// Manage Applications Page Manager
class ManageApplicationsManager {
    constructor() {
        this.applications = [];
        this.coordinatorProjects = [];
        this.currentApplication = null;
        this.init();
    }

    async init() {
        console.log('ManageApplicationsManager initializing...');
        
        // Check if user is logged in and is a teacher
        if (!SessionManager.isLoggedIn()) {
            window.location.href = '../../pages/welcome/index.html';
            return;
        }

        const user = SessionManager.getUser();
        if (user.userType !== 'teacher') {
            alert('Apenas professores podem gerenciar candidaturas.');
            window.location.href = '../../index.html';
            return;
        }

        await this.loadCoordinatorProjects();
        await this.loadApplications();
    }

    async loadCoordinatorProjects() {
        try {
            const user = SessionManager.getUser();
            console.log('Current user:', user);
            const allProjects = await ProjectAPI.getAll();
            
            if (allProjects.success && allProjects.data) {
                console.log('All projects from API:', allProjects.data);
                
                // Filter projects coordinated by this teacher
                // More flexible matching - check if any of the user identifiers match the coordinator field
                this.coordinatorProjects = allProjects.data.filter(project => {
                    console.log(`Checking project "${project.title}" with coordinator "${project.coordinator}"`);
                    console.log(`Against user: name="${user.name}", siape="${user.siape}", email="${user.email}"`);
                    
                    const match = project.coordinator === user.name || 
                                 project.coordinator === user.siape || 
                                 project.coordinator === user.email ||
                                 project.coordinator.toLowerCase().includes(user.name.toLowerCase()) ||
                                 (user.siape && project.coordinator.includes(user.siape));
                    
                    console.log(`Match result: ${match}`);
                    return match;
                });
                
                console.log('Coordinator projects loaded:', this.coordinatorProjects.length);
                console.log('Coordinator projects:', this.coordinatorProjects);
                
                // For development/testing: if no projects found, use all projects
                if (this.coordinatorProjects.length === 0 && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
                    console.log('🧪 Development mode: No coordinator match found, using all projects for testing');
                    this.coordinatorProjects = allProjects.data;
                }
                
                this.populateProjectFilter();
            }
        } catch (error) {
            console.error('Error loading coordinator projects:', error);
        }
    }

    populateProjectFilter() {
        const projectFilter = document.getElementById('projectFilter');
        if (projectFilter) {
            // Clear existing options except the first one
            projectFilter.innerHTML = '<option value="">Todos os projetos</option>';
            
            // Add coordinator's projects
            this.coordinatorProjects.forEach(project => {
                const option = document.createElement('option');
                option.value = project.id;
                option.textContent = project.title;
                projectFilter.appendChild(option);
            });
        }
    }

    async loadApplications() {
        this.showLoading();
        
        try {
            const applications = await this.fetchAllApplications();
            this.applications = applications;
            
            // Load visual-only changes that might have been made previously
            this.loadVisualChangesFromStorage();
            
            // Update clear button visibility
            this.updateClearButtonState();
            
            this.updateStats();
            this.renderApplications();
            this.hideLoading();
            
        } catch (error) {
            console.error('Error loading applications:', error);
            this.showError('Erro ao carregar candidaturas. Tente novamente.');
            this.hideLoading();
        }
    }

    async fetchAllApplications() {
        const projectIds = this.coordinatorProjects.map(p => p.id);
        console.log('Fetching applications for project IDs:', projectIds);
        const allApplications = [];
        
        try {
            // Fetch participant applications (these work with real API)
            console.log('Fetching participant applications...');
            const participantResult = await ParticipantAPI.getAll();
            console.log('Participant API result:', participantResult);
            
            if (participantResult.success && participantResult.data && participantResult.data.length > 0) {
                const relevantParticipants = participantResult.data.filter(p => 
                    projectIds.includes(p.projectId)
                );
                
                for (const participant of relevantParticipants) {
                    const application = await this.enrichParticipantApplication(participant);
                    if (application) allApplications.push(application);
                }
            }
            
            // Fetch fellow applications (these are simulated due to database issues)
            console.log('Fetching fellow applications...');
            
            // Try real API first
            const fellowResult = await FellowAPI.getAll();
            console.log('Fellow API result:', fellowResult);
            
            if (fellowResult.success && fellowResult.data && fellowResult.data.length > 0) {
                const relevantFellows = fellowResult.data.filter(f => 
                    projectIds.includes(f.projectId)
                );
                
                for (const fellow of relevantFellows) {
                    const application = await this.enrichFellowApplication(fellow);
                    if (application) allApplications.push(application);
                }
            }
            
            // Also fetch simulated fellow applications from localStorage
            console.log('Fetching simulated fellow applications from localStorage...');
            const simulatedFellows = JSON.parse(localStorage.getItem('teacher_fellow_applications') || '[]');
            const relevantSimulatedFellows = simulatedFellows.filter(app => 
                projectIds.includes(app.projectId) && app.isSimulated
            );
            
            for (const simApp of relevantSimulatedFellows) {
                allApplications.push(simApp);
            }
            
            // For development: if no applications found and we have projects, create some mock applications
            if (allApplications.length === 0 && this.coordinatorProjects.length > 0 && 
                (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
                console.log('🧪 Development mode: Creating mock applications for testing');
                return this.getMockApplicationsForProjects();
            }
            
        } catch (error) {
            console.error('Error fetching applications:', error);
            
            // For development, return mock applications
            if (window.location.hostname === 'localhost') {
                console.log('🧪 Development mode: Using mock applications due to error');
                return this.getMockApplicationsForProjects();
            }
        }
        
        console.log('Total applications loaded:', allApplications.length);
        return allApplications;
    }

    async enrichParticipantApplication(participant) {
        try {
            // Find the project
            const project = this.coordinatorProjects.length > 0 
                ? this.coordinatorProjects.find(p => p.id === participant.projectId)
                : { id: participant.projectId, title: `Project ${participant.projectId}` };
            
            if (!project) return null;
            
            console.log('🔧 Enriching participant application:', participant);
            
            // Use the participant data directly since it now contains the user information
            // The participant table stores name, phoneNumber, cpf directly
            const candidateName = participant.name || 'Nome não disponível';
            // Since participant table doesn't store email, we'll show phone as contact info
            const candidateEmail = participant.email || `Telefone: ${participant.phoneNumber || 'Não informado'}`;
            const candidatePhone = participant.phoneNumber || '';
            const candidateType = participant.role || 'participant'; // Use role if available
            
            return {
                id: participant.id || `participant_${participant.projectId}_${Date.now()}`,
                type: 'participant',
                candidateName: candidateName,
                candidateEmail: candidateEmail,
                candidatePhone: candidatePhone,
                candidateType: candidateType,
                projectId: participant.projectId,
                projectTitle: project.title,
                status: participant.status || 'PENDING',
                applicationDate: participant.applicationDate || new Date().toISOString(),
                originalData: participant,
                personData: {
                    id: participant.id,
                    name: candidateName,
                    email: candidateEmail,
                    phoneNumber: candidatePhone
                }
            };
        } catch (error) {
            console.error('Error enriching participant application:', error);
            return null;
        }
    }

    async enrichFellowApplication(fellow) {
        try {
            // Find the project
            const project = this.coordinatorProjects.find(p => p.id === fellow.projectId);
            if (!project) return null;
            
            // Get student details
            let student = null;
            try {
                const studentResult = await StudentAPI.getById(fellow.studentId);
                student = studentResult.success ? studentResult.data : null;
            } catch (error) {
                console.warn(`Could not fetch student details for ${fellow.studentId}`);
                student = {
                    id: fellow.studentId,
                    name: `Estudante #${fellow.studentId}`,
                    email: 'email@example.com'
                };
            }
            
            return {
                id: fellow.id || `fellow_${fellow.projectId}_${fellow.studentId}`,
                type: 'fellow',
                candidateName: student?.name || 'Nome não disponível',
                candidateEmail: student?.email || 'Email não disponível',
                candidateType: 'student',
                projectId: fellow.projectId,
                projectTitle: project.title,
                fellowshipValue: fellow.fellowshipValue || 0,
                status: fellow.status || 'PENDING',
                applicationDate: fellow.applicationDate || new Date().toISOString(),
                originalData: fellow,
                personData: student
            };
        } catch (error) {
            console.error('Error enriching fellow application:', error);
            return null;
        }
    }

    getMockApplications() {
        const user = SessionManager.getUser();
        return [
            {
                id: 'mock_1',
                type: 'participant',
                candidateName: 'João Silva',
                candidateEmail: 'joao.silva@email.com',
                candidateType: 'student',
                projectId: 1,
                projectTitle: 'Sistema de Gestão Acadêmica',
                status: 'PENDING',
                applicationDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString()
            },
            {
                id: 'mock_2',
                type: 'fellow',
                candidateName: 'Maria Santos',
                candidateEmail: 'maria.santos@email.com',
                candidateType: 'student',
                projectId: 2,
                projectTitle: 'Análise de Dados Meteorológicos',
                fellowshipValue: 600,
                status: 'PENDING',
                applicationDate: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString()
            },
            {
                id: 'mock_3',
                type: 'participant',
                candidateName: 'Carlos Oliveira',
                candidateEmail: 'carlos.oliveira@email.com',
                candidateType: 'external',
                projectId: 3,
                projectTitle: 'Programa de Alfabetização Digital',
                status: 'APPROVED',
                applicationDate: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString()
            }
        ];
    }

    getMockApplicationsForProjects() {
        if (this.coordinatorProjects.length === 0) {
            return this.getMockApplications();
        }

        const mockApplications = [];
        const candidateNames = ['Ana Silva', 'João Santos', 'Maria Oliveira', 'Pedro Costa', 'Carla Ferreira'];
        const candidateTypes = ['student', 'external', 'teacher'];
        const statuses = ['PENDING', 'PENDING', 'PENDING', 'APPROVED', 'REJECTED'];

        // Create 2-3 applications per project
        this.coordinatorProjects.forEach((project, projectIndex) => {
            const numApplications = Math.min(3, Math.max(2, Math.floor(Math.random() * 4) + 1));
            
            for (let i = 0; i < numApplications; i++) {
                const candidateIndex = (projectIndex * 3 + i) % candidateNames.length;
                const isScholarship = project.scholarshipAvailable && Math.random() > 0.5;
                
                mockApplications.push({
                    id: `mock_${project.id}_${i}`,
                    type: isScholarship ? 'fellow' : 'participant',
                    candidateName: candidateNames[candidateIndex],
                    candidateEmail: `${candidateNames[candidateIndex].toLowerCase().replace(' ', '.')}@email.com`,
                    candidateType: candidateTypes[i % candidateTypes.length],
                    projectId: project.id,
                    projectTitle: project.title,
                    fellowshipValue: isScholarship ? project.salary : undefined,
                    status: statuses[Math.floor(Math.random() * statuses.length)],
                    applicationDate: new Date(Date.now() - Math.floor(Math.random() * 10) * 24 * 60 * 60 * 1000).toISOString(),
                    originalData: {
                        id: `mock_${project.id}_${i}`,
                        projectId: project.id,
                        personId: candidateIndex + 1,
                        personType: candidateTypes[i % candidateTypes.length],
                        status: statuses[Math.floor(Math.random() * statuses.length)],
                        applicationDate: new Date().toISOString()
                    },
                    personData: {
                        id: candidateIndex + 1,
                        name: candidateNames[candidateIndex],
                        email: `${candidateNames[candidateIndex].toLowerCase().replace(' ', '.')}@email.com`
                    }
                });
            }
        });

        console.log('Generated mock applications:', mockApplications);
        return mockApplications;
    }

    updateStats() {
        const total = this.applications.length;
        const pending = this.applications.filter(app => app.status === 'PENDING').length;
        const approved = this.applications.filter(app => app.status === 'APPROVED').length;
        const rejected = this.applications.filter(app => app.status === 'REJECTED').length;
        
        document.getElementById('totalApplications').textContent = `Total: ${total}`;
        document.getElementById('pendingApplications').textContent = `Pendentes: ${pending}`;
        document.getElementById('approvedApplications').textContent = `Aprovados: ${approved}`;
        document.getElementById('rejectedApplications').textContent = `Rejeitados: ${rejected}`;
    }

    renderApplications() {
        const applicationsGrid = document.getElementById('applicationsGrid');
        const emptyState = document.getElementById('emptyState');
        
        if (this.applications.length === 0) {
            applicationsGrid.style.display = 'none';
            emptyState.style.display = 'block';
            return;
        }
        
        applicationsGrid.style.display = 'grid';
        emptyState.style.display = 'none';
        
        applicationsGrid.innerHTML = this.applications.map(app => this.createApplicationCard(app)).join('');
    }

    createApplicationCard(application) {
        const statusClass = application.status.toLowerCase();
        const typeLabel = application.type === 'participant' ? 'Participante' : 'Bolsista';
        const applicationDate = new Date(application.applicationDate).toLocaleDateString('pt-BR');
        
        const fellowshipInfo = application.type === 'fellow' ? 
            `<p><strong>Valor da Bolsa:</strong> R$ ${application.fellowshipValue?.toFixed(2) || '0,00'}</p>` : '';

        return `
            <div class="application-card ${statusClass}" onclick="manageApplicationsManager.showApplicationDetails('${application.id}')">
                <div class="application-header">
                    <div class="candidate-info">
                        <h3>${application.candidateName}</h3>
                        <p>${application.candidateEmail}</p>
                    </div>
                    <div class="application-badges">
                        <span class="status-badge ${statusClass}">${application.status}</span>
                        <span class="type-badge ${application.type}">${typeLabel}</span>
                    </div>
                </div>
                
                <div class="project-info">
                    <h4>${application.projectTitle}</h4>
                    ${fellowshipInfo}
                </div>
                
                <div class="application-meta">
                    <span>📅 ${applicationDate}</span>
                    <span>👤 ${application.candidateType}</span>
                </div>
                
                <div class="quick-actions" onclick="event.stopPropagation()">
                    <button class="btn-approve ${application.status === 'APPROVED' ? 'current-status' : ''}" 
                            onclick="manageApplicationsManager.quickApprove('${application.id}')"
                            ${application.status === 'APPROVED' ? 'disabled' : ''}>
                        ✅ ${application.status === 'APPROVED' ? 'Aprovado' : 'Aprovar'}
                    </button>
                    <button class="btn-reject ${application.status === 'REJECTED' ? 'current-status' : ''}" 
                            onclick="manageApplicationsManager.quickReject('${application.id}')"
                            ${application.status === 'REJECTED' ? 'disabled' : ''}>
                        ❌ ${application.status === 'REJECTED' ? 'Rejeitado' : 'Rejeitar'}
                    </button>
                </div>
                
                ${application.lastModified ? `
                <div class="visual-change-indicator">
                    <span class="indicator-badge">💭 Alteração Visual</span>
                    <small class="indicator-time">Modificado: ${new Date(application.lastModified).toLocaleString('pt-BR')}</small>
                </div>
                ` : ''}
            </div>
        `;
    }

    showApplicationDetails(applicationId) {
        const application = this.applications.find(app => app.id === applicationId);
        if (!application) return;
        
        this.currentApplication = application;
        
        // Populate modal with application details
        document.getElementById('modalTitle').textContent = `Candidatura - ${application.candidateName}`;
        document.getElementById('candidateName').textContent = application.candidateName;
        document.getElementById('candidateEmail').textContent = application.candidateEmail;
        document.getElementById('candidateType').textContent = this.getTypeLabel(application.candidateType);
        document.getElementById('applicationDate').textContent = new Date(application.applicationDate).toLocaleDateString('pt-BR');
        
        document.getElementById('projectTitle').textContent = application.projectTitle;
        document.getElementById('applicationType').textContent = application.type === 'participant' ? 'Participante' : 'Bolsista';
        
        const fellowshipInfo = document.getElementById('fellowshipInfo');
        if (application.type === 'fellow') {
            document.getElementById('fellowshipValue').textContent = application.fellowshipValue?.toFixed(2) || '0,00';
            fellowshipInfo.style.display = 'block';
        } else {
            fellowshipInfo.style.display = 'none';
        }
        
        const currentStatus = document.getElementById('currentStatus');
        currentStatus.textContent = application.status;
        currentStatus.className = `status-badge ${application.status.toLowerCase()}`;
        
        // Show/hide action buttons based on status - Always show for visual-only mode
        const approveBtn = document.getElementById('approveBtn');
        const rejectBtn = document.getElementById('rejectBtn');
        
        // Always show buttons but update their text and style based on current status
        approveBtn.style.display = 'inline-block';
        rejectBtn.style.display = 'inline-block';
        
        if (application.status === 'APPROVED') {
            approveBtn.textContent = '✅ Aprovado';
            approveBtn.disabled = true;
            approveBtn.classList.add('current-status');
            rejectBtn.textContent = '❌ Rejeitar';
            rejectBtn.disabled = false;
            rejectBtn.classList.remove('current-status');
        } else if (application.status === 'REJECTED') {
            rejectBtn.textContent = '❌ Rejeitado';
            rejectBtn.disabled = true;
            rejectBtn.classList.add('current-status');
            approveBtn.textContent = '✅ Aprovar';
            approveBtn.disabled = false;
            approveBtn.classList.remove('current-status');
        } else {
            approveBtn.textContent = '✅ Aprovar';
            approveBtn.disabled = false;
            approveBtn.classList.remove('current-status');
            rejectBtn.textContent = '❌ Rejeitar';
            rejectBtn.disabled = false;
            rejectBtn.classList.remove('current-status');
        }
        
        // Show modal
        document.getElementById('applicationModal').style.display = 'flex';
    }

    getTypeLabel(type) {
        const labels = {
            'student': 'Estudante',
            'teacher': 'Professor',
            'external': 'Externo',
            'company': 'Empresa'
        };
        return labels[type] || type;
    }

    async quickApprove(applicationId) {
        await this.updateApplicationStatus(applicationId, 'APPROVED');
    }

    async quickReject(applicationId) {
        await this.updateApplicationStatus(applicationId, 'REJECTED');
    }

    async approveApplication() {
        if (this.currentApplication) {
            await this.updateApplicationStatus(this.currentApplication.id, 'APPROVED');
            closeApplicationModal();
        }
    }

    async rejectApplication() {
        if (this.currentApplication) {
            await this.updateApplicationStatus(this.currentApplication.id, 'REJECTED');
            closeApplicationModal();
        }
    }

    async updateApplicationStatus(applicationId, newStatus) {
        const application = this.applications.find(app => app.id == applicationId); // Using == for type coercion
        if (!application) {
            console.error('Application not found with ID:', applicationId);
            return;
        }
        
        // VISUAL-ONLY MODE: No database updates, only UI changes
        console.log(`💭 Visual update: ${application.candidateName} → ${newStatus}`);
        
        try {
            // Update local application data (visual only)
            application.status = newStatus;
            application.lastModified = new Date().toISOString();
            
            // Store visual changes in localStorage for persistence across page reloads
            this.saveVisualChangesToStorage();
            
            // Refresh display immediately
            this.updateStats();
            this.renderApplications();
            this.updateClearButtonState();
            
            const statusText = newStatus === 'APPROVED' ? 'aprovada' : 
                              newStatus === 'REJECTED' ? 'rejeitada' : 'atualizada';
            
            // Show success message
            this.showStatusUpdateMessage(`Candidatura ${statusText} com sucesso!`, 'success');
            
        } catch (error) {
            console.error('Error updating visual application status:', error);
            this.showStatusUpdateMessage('Erro ao atualizar status da candidatura.', 'error');
        }
    }

    // Save visual-only changes to localStorage for persistence
    saveVisualChangesToStorage() {
        const visualChanges = {};
        
        this.applications.forEach(app => {
            if (app.lastModified) {
                visualChanges[app.id] = {
                    status: app.status,
                    lastModified: app.lastModified
                };
            }
        });
        
        localStorage.setItem('teacher_visual_application_changes', JSON.stringify(visualChanges));
        console.log('💾 Visual changes saved to localStorage');
    }

    // Load visual-only changes from localStorage
    loadVisualChangesFromStorage() {
        try {
            const visualChanges = JSON.parse(localStorage.getItem('teacher_visual_application_changes') || '{}');
            
            this.applications.forEach(app => {
                if (visualChanges[app.id]) {
                    app.status = visualChanges[app.id].status;
                    app.lastModified = visualChanges[app.id].lastModified;
                }
            });
            
            if (Object.keys(visualChanges).length > 0) {
                console.log('📂 Loaded visual changes from localStorage:', Object.keys(visualChanges).length, 'applications');
            }
        } catch (error) {
            console.error('Error loading visual changes:', error);
        }
    }

    // Show status update message with styling
    showStatusUpdateMessage(message, type = 'success') {
        // Remove any existing message
        const existingMessage = document.querySelector('.status-update-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // Create new message element
        const messageElement = document.createElement('div');
        messageElement.className = `status-update-message ${type}`;
        messageElement.innerHTML = `
            <div class="message-content">
                ${type === 'success' ? '✅' : '❌'} ${message}
            </div>
        `;
        
        // Style the message
        messageElement.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${type === 'success' ? '#4CAF50' : '#f44336'};
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 10000;
            animation: slideIn 0.3s ease-out;
        `;

        // Add animation styles if not already present
        if (!document.querySelector('#status-message-styles')) {
            const styleSheet = document.createElement('style');
            styleSheet.id = 'status-message-styles';
            styleSheet.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOut {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
                .status-update-message.removing {
                    animation: slideOut 0.3s ease-in forwards;
                }
            `;
            document.head.appendChild(styleSheet);
        }

        // Add to document
        document.body.appendChild(messageElement);

        // Auto-remove after 3 seconds
        setTimeout(() => {
            messageElement.classList.add('removing');
            setTimeout(() => {
                if (messageElement.parentNode) {
                    messageElement.remove();
                }
            }, 300);
        }, 3000);
    }

    showLoading() {
        document.getElementById('loadingState').style.display = 'flex';
        document.getElementById('applicationsGrid').style.display = 'none';
        document.getElementById('emptyState').style.display = 'none';
    }

    hideLoading() {
        document.getElementById('loadingState').style.display = 'none';
    }

    showError(message) {
        alert(message); // TODO: Implement better error display
    }

    // Clear all visual-only changes and return to original database states
    clearAllVisualChanges() {
        if (confirm('⚠️ Tem certeza que deseja desfazer todas as alterações visuais? Isso irá restaurar os status originais.')) {
            // Remove visual changes from localStorage
            localStorage.removeItem('teacher_visual_application_changes');
            
            // Reset all application statuses to their original values
            this.applications.forEach(app => {
                delete app.lastModified;
                // You might want to reload from the API here to get original status
                // For now, let's assume PENDING is the default original status
                if (app.status === 'APPROVED' || app.status === 'REJECTED') {
                    app.status = 'PENDING';
                }
            });
            
            // Refresh display
            this.updateStats();
            this.renderApplications();
            this.updateClearButtonState();
            
            this.showStatusUpdateMessage('Todas as alterações visuais foram removidas!', 'success');
            console.log('🔄 All visual changes cleared');
        }
    }

    // Update the clear button state based on whether there are visual changes
    updateClearButtonState() {
        const clearButton = document.getElementById('clearVisualChanges');
        if (clearButton) {
            const hasChanges = this.hasVisualChanges();
            clearButton.disabled = !hasChanges;
            
            if (hasChanges) {
                clearButton.style.display = 'flex';
                clearButton.title = 'Desfazer todas as alterações visuais';
            } else {
                clearButton.style.display = 'none';
                clearButton.title = 'Nenhuma alteração visual para desfazer';
            }
        }
    }

    // Check if there are any visual changes pending
    hasVisualChanges() {
        try {
            const visualChanges = JSON.parse(localStorage.getItem('teacher_visual_application_changes') || '{}');
            return Object.keys(visualChanges).length > 0;
        } catch {
            return false;
        }
    }
}

// Modal functions
function closeApplicationModal() {
    document.getElementById('applicationModal').style.display = 'none';
}

function approveApplication() {
    if (window.manageApplicationsManager) {
        window.manageApplicationsManager.approveApplication();
    }
}

function rejectApplication() {
    if (window.manageApplicationsManager) {
        window.manageApplicationsManager.rejectApplication();
    }
}

// Initialize the manager when DOM is loaded
let manageApplicationsManager;
document.addEventListener('DOMContentLoaded', () => {
    manageApplicationsManager = new ManageApplicationsManager();
    window.manageApplicationsManager = manageApplicationsManager;
});