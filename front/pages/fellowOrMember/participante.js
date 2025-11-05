// Participation Management System - Comprehensive participation and fellowship manager

class ParticipationManager {
    constructor() {
        this.participations = [];
        this.fellowships = [];
        this.allProjects = [];
        this.filteredParticipations = [];
        this.currentUser = null;
        this.userType = null;
        this.userId = null;
        this.init();
    }

    init() {
        console.log('ParticipationManager initializing...');
        
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            window.location.href = '../welcome/index.html';
            return;
        }

        this.currentUser = SessionManager.getUser();
        this.userType = this.currentUser.userType;
        this.userId = this.currentUser.id;

        // Check if coming from a project page for application
        const selectedProject = sessionStorage.getItem('selectedProject');
        if (selectedProject) {
            this.showApplicationModal(JSON.parse(selectedProject));
            sessionStorage.removeItem('selectedProject');
        } else {
            this.setupEventListeners();
            this.loadParticipations();
        }
    }

    setupEventListeners() {
        // Search functionality
        const searchInput = document.getElementById('searchParticipations');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        // Filter controls
        document.getElementById('statusFilter')?.addEventListener('change', () => this.applyFilters());
        document.getElementById('typeFilter')?.addEventListener('change', () => this.applyFilters());
        document.getElementById('sortBy')?.addEventListener('change', () => this.applyFilters());
    }

    async loadParticipations() {
        this.showLoading();
        
        try {
            // Load all projects first
            const projectsResponse = await ProjectAPI.getAll();
            if (projectsResponse.success && projectsResponse.data) {
                this.allProjects = projectsResponse.data;
            }

            // Load user's participations and fellowships
            const userParticipations = await this.fetchUserParticipations();
            
            this.participations = userParticipations;
            this.filteredParticipations = [...userParticipations];
            
            this.updateStats();
            this.renderParticipations();
            this.hideLoading();
            
        } catch (error) {
            console.error('Error loading participations:', error);
            this.showError('Erro ao carregar participações. Tente novamente.');
            this.hideLoading();
        }
    }

    async fetchUserParticipations() {
        try {
            const user = this.currentUser;
            const userType = user.userType;
            const userName = user.name;
            const userId = user.id;
            
            console.log('🔍 Fetching participations for user:', userName, 'Type:', userType, 'ID:', userId);
            
            let userParticipations = [];
            
            if (userType === 'teacher') {
                // Teachers should not access this page, redirect them
                console.warn('⚠️ Teachers should not access participation management');
                alert('Esta página é destinada apenas para estudantes e pessoas externas.');
                window.location.href = '../userProject/myProjects.html';
                return [];
                
            } else if (userType === 'student') {
                // Students see their fellowships as participations
                console.log('🎓 Fetching fellowships for student ID:', userId);
                try {
                    const fellowships = await FellowAPI.getAll();
                    console.log('📊 Fellowships response:', fellowships);
                    
                    if (fellowships.success && fellowships.data) {
                        const studentFellowships = fellowships.data.filter(fellowship => 
                            fellowship.studentId === userId
                        );
                        console.log('🎓 Student fellowships found:', studentFellowships.length);
                        
                        userParticipations = studentFellowships.map(fellowship => ({
                            id: fellowship.id,
                            projectId: fellowship.projectId,
                            type: 'fellowship',
                            status: this.determineFellowshipStatus(fellowship),
                            startDate: fellowship.startDate,
                            endDate: fellowship.endDate,
                            salary: fellowship.salary || 0,
                            workload: fellowship.workload,
                            description: fellowship.description,
                            originalData: fellowship
                        }));
                    }
                } catch (fellowError) {
                    console.warn('⚠️ Error fetching fellowships:', fellowError);
                }
                
            } else if (userType === 'external') {
                // External users see their participations
                console.log('👤 Fetching participations for external user ID:', userId);
                try {
                    const participants = await ParticipantAPI.getAll();
                    console.log('📊 Participants response:', participants);
                    
                    if (participants.success && participants.data) {
                        const userParticipations_raw = participants.data.filter(participant => 
                            participant.externalPersonId === userId
                        );
                        console.log('👤 External participations found:', userParticipations_raw.length);
                        
                        userParticipations = userParticipations_raw.map(participation => ({
                            id: participation.id,
                            projectId: participation.projectId,
                            type: 'voluntary',
                            status: this.determineParticipationStatus(participation),
                            startDate: participation.startDate,
                            endDate: participation.endDate,
                            role: participation.role,
                            description: participation.description,
                            originalData: participation
                        }));
                    }
                } catch (participantError) {
                    console.warn('⚠️ Error fetching participants:', participantError);
                }
                
            } else if (userType === 'company') {
                // Companies should not have access to participations
                console.warn('⚠️ Companies should not access participation management');
                alert('Empresas não têm acesso a participações diretas em projetos.');
                window.location.href = '../../home.html';
                return [];
            }
            
            // Enrich participations with project data
            userParticipations = await this.enrichParticipationsWithProjectData(userParticipations);
            
            console.log('📋 Final user participations:', userParticipations.length, userParticipations);
            return userParticipations;
            
        } catch (error) {
            console.error('❌ Error fetching user participations:', error);
            
            // Only use fallback in development if there's a connection error
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                if (error.message && (error.message.includes('fetch') || error.message.includes('NetworkError') || error.message.includes('Connection'))) {
                    console.log('🔄 Connection error detected, falling back to mock data');
                    return this.getMockParticipationsForUser();
                }
            }
            
            console.warn('⚠️ Returning empty participations array due to error');
            return [];
        }
    }

    async enrichParticipationsWithProjectData(participations) {
        for (let participation of participations) {
            const project = this.allProjects.find(p => p.id === participation.projectId);
            if (project) {
                participation.project = project;
                participation.projectTitle = project.title;
                participation.projectCoordinator = project.coordinator;
                participation.projectType = project.type;
                participation.projectDescription = project.description;
            } else {
                // Try to fetch individual project if not in cache
                try {
                    const projectResponse = await ProjectAPI.getById(participation.projectId);
                    if (projectResponse.success && projectResponse.data) {
                        participation.project = projectResponse.data;
                        participation.projectTitle = projectResponse.data.title;
                        participation.projectCoordinator = projectResponse.data.coordinator;
                        participation.projectType = projectResponse.data.type;
                        participation.projectDescription = projectResponse.data.description;
                    }
                } catch (error) {
                    console.warn(`⚠️ Could not fetch project ${participation.projectId}:`, error);
                    participation.projectTitle = 'Projeto não encontrado';
                    participation.projectCoordinator = 'Não informado';
                }
            }
        }
        return participations;
    }

    determineFellowshipStatus(fellowship) {
        const now = new Date();
        const startDate = fellowship.startDate ? new Date(fellowship.startDate) : null;
        const endDate = fellowship.endDate ? new Date(fellowship.endDate) : null;
        
        if (fellowship.status) return fellowship.status;
        
        if (!startDate) return 'pending';
        if (startDate > now) return 'pending';
        if (endDate && endDate < now) return 'completed';
        return 'active';
    }

    determineParticipationStatus(participation) {
        const now = new Date();
        const startDate = participation.startDate ? new Date(participation.startDate) : null;
        const endDate = participation.endDate ? new Date(participation.endDate) : null;
        
        if (participation.status) return participation.status;
        
        if (!startDate) return 'pending';
        if (startDate > now) return 'pending';
        if (endDate && endDate < now) return 'completed';
        return 'active';
    }

    getMockParticipationsForUser() {
        const user = this.currentUser;
        const userType = user.userType;
        
        if (userType === 'student') {
            return [
                {
                    id: 1,
                    projectId: 2,
                    type: 'fellowship',
                    status: 'active',
                    startDate: '2024-03-01',
                    endDate: '2024-12-31',
                    salary: 600.00,
                    workload: '20h/semana',
                    projectTitle: "Análise de Dados Meteorológicos",
                    projectCoordinator: "Dra. Maria Santos",
                    projectType: "research",
                    projectDescription: "Pesquisa sobre padrões climáticos utilizando machine learning.",
                    originalData: { id: 1, studentId: user.id, projectId: 2 }
                }
            ];
        } else if (userType === 'external') {
            return [
                {
                    id: 1,
                    projectId: 3,
                    type: 'voluntary',
                    status: 'active',
                    startDate: '2024-02-15',
                    endDate: '2024-08-15',
                    role: 'Instrutor de informática',
                    projectTitle: "Programa de Alfabetização Digital",
                    projectCoordinator: "Prof. Ana Costa",
                    projectType: "extension",
                    projectDescription: "Extensão universitária para ensinar informática básica para idosos.",
                    originalData: { id: 1, externalPersonId: user.id, projectId: 3 }
                }
            ];
        }
        
        return [];
    }

    updateStats() {
        const totalParticipations = this.participations.length;
        const activeParticipations = this.participations.filter(p => p.status === 'active').length;
        const fellowships = this.participations.filter(p => p.type === 'fellowship').length;
        
        const pageTitle = document.querySelector('.page-header h1');
        const pageSubtitle = document.querySelector('.page-subtitle');
        
        if (this.userType === 'student') {
            pageTitle.textContent = 'Minhas Participações';
            pageSubtitle.textContent = `${totalParticipations} participação${totalParticipations !== 1 ? 'ões' : ''} • ${activeParticipations} ativa${activeParticipations !== 1 ? 's' : ''} • ${fellowships} bolsa${fellowships !== 1 ? 's' : ''}`;
        } else if (this.userType === 'external') {
            pageTitle.textContent = 'Minhas Participações';
            pageSubtitle.textContent = `${totalParticipations} participação${totalParticipations !== 1 ? 'ões' : ''} • ${activeParticipations} ativa${activeParticipations !== 1 ? 's' : ''}`;
        }
    }

    renderParticipations() {
        const participationsGrid = document.getElementById('participationsGrid');
        const emptyState = document.getElementById('emptyState');
        
        if (this.filteredParticipations.length === 0) {
            participationsGrid.style.display = 'none';
            emptyState.style.display = 'block';
            return;
        }
        
        participationsGrid.style.display = 'grid';
        emptyState.style.display = 'none';
        
        participationsGrid.innerHTML = this.filteredParticipations.map(participation => 
            this.createParticipationCard(participation)
        ).join('');
    }

    createParticipationCard(participation) {
        const typeLabels = {
            'research': 'Pesquisa',
            'extension': 'Extensão',
            'educational': 'Educacional',
            'company': 'Empresa'
        };

        const statusLabels = {
            'active': '🟢 Ativo',
            'pending': '🟡 Pendente',
            'completed': '✅ Concluído',
            'cancelled': '❌ Cancelado'
        };

        const participationTypeLabel = participation.type === 'fellowship' ? 
            `💰 Bolsista${participation.salary ? ` (R$ ${participation.salary.toFixed(2)})` : ''}` : 
            '💙 Voluntário';

        const workloadInfo = participation.workload ? 
            `<span class="participation-workload">⏱️ ${participation.workload}</span>` : '';

        const dateInfo = participation.startDate ? 
            `<span class="participation-dates">📅 ${new Date(participation.startDate).toLocaleDateString('pt-BR')} - ${participation.endDate ? new Date(participation.endDate).toLocaleDateString('pt-BR') : 'Em andamento'}</span>` : '';

        return `
            <div class="participation-card ${participation.status}" onclick="participationManager.showParticipationDetails(${participation.id})">
                <div class="participation-header">
                    <h3 class="participation-title">${participation.projectTitle || 'Projeto sem título'}</h3>
                    <div class="participation-badges">
                        <span class="status-badge ${participation.status}">${statusLabels[participation.status] || '❓ Desconhecido'}</span>
                        <span class="type-badge ${participation.type}">${participationTypeLabel}</span>
                    </div>
                </div>
                
                <div class="participation-type">${typeLabels[participation.projectType] || 'Tipo não informado'}</div>
                
                <p class="participation-description">${participation.projectDescription || 'Descrição não disponível'}</p>
                
                <div class="participation-meta">
                    <span class="participation-coordinator">👤 ${participation.projectCoordinator || 'Coordenador não informado'}</span>
                    ${workloadInfo}
                    ${dateInfo}
                </div>
                
                <div class="participation-action-hint">
                    <small>👆 Clique para ver detalhes da participação</small>
                </div>
            </div>
        `;
    }

    handleSearch(query) {
        if (!query.trim()) {
            this.filteredParticipations = [...this.participations];
        } else {
            const lowQuery = query.toLowerCase();
            this.filteredParticipations = this.participations.filter(participation => 
                participation.projectTitle?.toLowerCase().includes(lowQuery) ||
                participation.projectDescription?.toLowerCase().includes(lowQuery) ||
                participation.projectCoordinator?.toLowerCase().includes(lowQuery) ||
                participation.role?.toLowerCase().includes(lowQuery)
            );
        }
        
        this.applyFilters();
    }

    applyFilters() {
        let filtered = [...this.filteredParticipations];
        
        // Apply status filter
        const statusFilter = document.getElementById('statusFilter').value;
        if (statusFilter) {
            filtered = filtered.filter(participation => participation.status === statusFilter);
        }
        
        // Apply type filter
        const typeFilter = document.getElementById('typeFilter').value;
        if (typeFilter) {
            filtered = filtered.filter(participation => participation.type === typeFilter);
        }
        
        // Apply sorting
        const sortBy = document.getElementById('sortBy').value;
        filtered.sort((a, b) => {
            switch (sortBy) {
                case 'recent':
                    return new Date(b.startDate || '2024-01-01') - new Date(a.startDate || '2024-01-01');
                case 'oldest':
                    return new Date(a.startDate || '2024-01-01') - new Date(b.startDate || '2024-01-01');
                case 'project':
                    return (a.projectTitle || '').localeCompare(b.projectTitle || '');
                default:
                    return 0;
            }
        });
        
        this.filteredParticipations = filtered;
        this.renderParticipations();
    }

    showParticipationDetails(participationId) {
        const participation = this.participations.find(p => p.id === participationId);
        if (!participation || !participation.project) return;
        
        // Store project data and navigate to project page
        sessionStorage.setItem('selectedProject', JSON.stringify(participation.project));
        window.location.href = '../project page/project.html';
    }

    exploreProjects() {
            window.location.href = '../../home.html';
    }

    // Application Modal Functionality (when coming from project page)
    showApplicationModal(projectData) {
        document.getElementById('applicationModal').style.display = 'block';
        
        // Initialize application form
        this.applicationForm = new ProjectApplicationForm(projectData, this);
        
        // Setup modal close handlers
        const modal = document.getElementById('applicationModal');
        const closeBtn = document.querySelector('.close');
        const backBtn = document.getElementById('back-btn');
        
        closeBtn.onclick = () => this.closeApplicationModal();
        backBtn.onclick = () => this.closeApplicationModal();
        
        // Close modal when clicking outside
        window.onclick = (event) => {
            if (event.target === modal) {
                this.closeApplicationModal();
            }
        };
    }

    closeApplicationModal() {
        document.getElementById('applicationModal').style.display = 'none';
        // Continue with normal participation management view
        this.setupEventListeners();
        this.loadParticipations();
    }

    showLoading() {
        document.getElementById('loadingState').style.display = 'flex';
        document.getElementById('participationsGrid').style.display = 'none';
        document.getElementById('emptyState').style.display = 'none';
    }

    hideLoading() {
        document.getElementById('loadingState').style.display = 'none';
    }

    showError(message) {
        alert(message); // TODO: Implement better error display
    }
}

// Application Form Class (embedded within the modal)
class ProjectApplicationForm {
    constructor(projectData, parentManager) {
        this.parentManager = parentManager;
        this.form = document.getElementById('application-form');
        this.dynamicContent = document.getElementById('dynamic-form-content');
        this.projectData = projectData;
        this.userData = SessionManager.getUser();
        this.userType = this.userData?.userType;
        this.participationType = this.userType?.toLowerCase() === 'student' ? 'participant' : 'participant';
        this.missingFields = [];
        this.init();
    }

    init() {
        if (this.userType === 'company') {
            this.showAccessDenied();
            return;
        }
        
        this.loadProjectData();
        this.analyzeUserData();
        this.generateSmartForm();
        this.setupEventListeners();
    }

    showAccessDenied() {
        this.dynamicContent.innerHTML = `
            <div class="form-section access-denied">
                <h3>🚫 Acesso Negado</h3>
                <p>Empresas não podem se candidatar diretamente como participantes em projetos.</p>
                <p>Para estabelecer parcerias, entre em contato com a coordenação do projeto.</p>
            </div>
        `;
    }

    loadProjectData() {
        const elements = {
            title: document.getElementById('modal-project-title'),
            coordinator: document.getElementById('modal-project-coordinator')
        };
        
        if (this.projectData && elements.title && elements.coordinator) {
            elements.title.textContent = this.projectData.title || 'Projeto';
            elements.coordinator.textContent = `Coordenador: ${this.projectData.coordinator || this.projectData.coordenator || 'Não informado'}`;
        }
    }

    analyzeUserData() {
        if (!this.userData) return;
        
        const userType = this.userType?.toLowerCase();
        
        this.missingFields = [
            !this.userData.phoneNumber && 'phoneNumber',
            (this.participationType === 'fellow' && !this.userData.cpf) && 'cpf',
            (userType === 'student' && !this.userData.birthDate) && 'birthDate',
            (userType === 'student' && !this.userData.lattesCurriculum) && 'lattesCurriculum',
            (userType === 'external' && !this.userData.birthDate) && 'birthDate'
        ].filter(Boolean);
    }

    generateSmartForm() {
        if (!this.userData) {
            this.dynamicContent.innerHTML = '<div class="form-section"><h3>⚠️ Erro</h3><p>Faça login novamente.</p></div>';
            return;
        }

        const formHTML = [
            this.generateParticipationInfo(),
            this.missingFields.length > 0 ? this.generateMissingFieldsSection() : this.generateConfirmationSection()
        ].join('');

        this.dynamicContent.innerHTML = formHTML;
        this.setupDynamicEventListeners();
    }

    generateParticipationInfo() {
        const userTypeDisplay = {
            'student': 'Estudante',
            'teacher': 'Professor(a)',
            'external': 'Pessoa Externa'
        }[this.userType?.toLowerCase()] || 'Não identificado';

        const participationDisplay = this.participationType === 'fellow' 
            ? 'Bolsista (com bolsa de estudos)' 
            : 'Participante Voluntário';

        return `
            <div class="form-section">
                <h3>Informações da Candidatura</h3>
                <div class="info-display">
                    <div class="info-item"><strong>Seu tipo de usuário:</strong> ${userTypeDisplay}</div>
                    <div class="info-item"><strong>Tipo de participação:</strong> ${participationDisplay}</div>
                    ${this.userType?.toLowerCase() === 'student' ? this.generateStudentOptions() : ''}
                </div>
            </div>
        `;
    }

    generateStudentOptions() {
        return `
            <div class="form-group">
                <label class="main-question">Como você gostaria de participar do projeto?</label>
                <div class="radio-group">
                    <label class="radio-option ${this.participationType === 'participant' ? 'selected' : ''}" data-tooltip="Ideal para ganhar experiência sem compromisso financeiro">
                        <input type="radio" name="fellowshipChoice" value="participant" ${this.participationType === 'participant' ? 'checked' : ''}>
                        <div class="option-content">
                            <span class="option-title">💙 Participante Voluntário</span>
                            <span class="option-description">Sem bolsa de estudos - Participação por experiência e aprendizado</span>
                            <span class="option-benefits">✓ Experiência prática ✓ Networking ✓ Certificado de participação</span>
                            <span class="option-extra">📚 Perfeito para iniciantes e quem busca experiência</span>
                        </div>
                    </label>
                    <label class="radio-option ${this.participationType === 'fellow' ? 'selected' : ''}" data-tooltip="Requer maior dedicação e passa por processo seletivo">
                        <input type="radio" name="fellowshipChoice" value="fellow" ${this.participationType === 'fellow' ? 'checked' : ''}>
                        <div class="option-content">
                            <span class="option-title">💰 Bolsista</span>
                            <span class="option-description">Com bolsa de estudos - Participação remunerada (sujeito à seleção)</span>
                            <span class="option-benefits">✓ Todos os benefícios do voluntário ✓ Bolsa mensal ✓ Experiência profissional</span>
                            <span class="option-extra">🎯 Ideal para quem pode dedicar mais tempo ao projeto</span>
                        </div>
                    </label>
                </div>
                <div class="selection-info">
                    <small>💡 <strong>Dica:</strong> Você pode começar como voluntário e, dependendo da disponibilidade e desempenho, ser considerado para bolsa posteriormente. Ambas as opções oferecem excelente experiência!</small>
                </div>
            </div>
        `;
    }

    generateMissingFieldsSection() {
        const fieldsHTML = this.missingFields.map(field => this.getFieldHTML(field)).join('');
        return `
            <div class="form-section">
                <h3>Informações Adicionais Necessárias</h3>
                <p class="section-description">Complete as informações abaixo:</p>
                ${fieldsHTML}
            </div>
        `;
    }

    generateConfirmationSection() {
        const summary = Object.entries({
            'Nome': this.userData.name,
            'Email': this.userData.email,
            'Telefone': this.userData.phoneNumber,
            'CPF': this.userData.cpf && this.formatCPF(this.userData.cpf),
            'Data de Nascimento': this.userData.birthDate && new Date(this.userData.birthDate).toLocaleDateString('pt-BR')
        }).filter(([_, value]) => value)
          .map(([label, value]) => `<div class="summary-item"><strong>${label}:</strong> ${value}</div>`)
          .join('');

        return `
            <div class="form-section">
                <h3>✅ Informações Completas</h3>
                <p class="success-message">Todas as suas informações estão completas!</p>
                <div class="data-summary">
                    <h4>Seus dados:</h4>
                    ${summary}
                </div>
            </div>
        `;
    }

    getFieldHTML(fieldName) {
        const userType = this.userType?.toLowerCase();
        
        const fields = {
            phoneNumber: `<input type="tel" id="phoneNumber" name="phoneNumber" required value="${this.userData.phoneNumber || ''}" placeholder="(11) 99999-9999"><small>Número para contato</small>`,
            cpf: `<input type="text" id="cpf" name="cpf" ${this.participationType === 'fellow' ? 'required' : ''} value="${this.userData.cpf || ''}" placeholder="000.000.000-00" maxlength="14"><small>Formato: 000.000.000-00</small>`,
            birthDate: `<input type="date" id="birthDate" name="birthDate" required value="${this.userData.birthDate || ''}"><small>${userType === 'student' ? 'Obrigatório para estudantes' : 'Obrigatório para participação'}</small>`,
            lattesCurriculum: `<input type="url" id="lattesCurriculum" name="lattesCurriculum" value="${this.userData.lattesCurriculum || ''}" placeholder="http://lattes.cnpq.br/..."><small>Link do Currículo Lattes</small>`
        };

        const labels = {
            phoneNumber: 'Telefone *',
            cpf: `CPF ${this.participationType === 'fellow' ? '*' : ''}`,
            birthDate: 'Data de Nascimento *',
            lattesCurriculum: 'Currículo Lattes'
        };

        return `<div class="form-group"><label for="${fieldName}">${labels[fieldName]}</label>${fields[fieldName]}</div>`;
    }

    setupDynamicEventListeners() {
        // Fellowship choice change with enhanced visual feedback
        document.querySelectorAll('input[name="fellowshipChoice"]').forEach(radio => {
            radio.addEventListener('change', (e) => {
                // Update selected state classes
                document.querySelectorAll('.radio-option').forEach(option => {
                    option.classList.remove('selected');
                });
                e.target.closest('.radio-option').classList.add('selected');
                
                // Update participation type and regenerate form
                this.participationType = e.target.value;
                console.log('Participation type changed to:', this.participationType);
                this.analyzeUserData();
                this.generateSmartForm();
            });
        });

        // Field validations
        const validators = {
            cpf: this.setupCPFValidation.bind(this),
            birthDate: this.setupDateValidation.bind(this),
            lattesCurriculum: this.setupURLValidation.bind(this)
        };

        Object.entries(validators).forEach(([id, validator]) => {
            const field = document.getElementById(id);
            if (field) validator(field);
        });
    }

    setupCPFValidation(field) {
        field.addEventListener('input', (e) => {
            const value = e.target.value.replace(/\D/g, '');
            e.target.value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        });
        
        field.addEventListener('blur', (e) => {
            const cpf = e.target.value.replace(/\D/g, '');
            if (cpf.length === 11 && this.isValidCPF(cpf)) {
                this.clearError(e.target);
            } else if (cpf.length > 0) {
                this.showError(e.target, 'CPF inválido');
            }
        });
    }

    setupDateValidation(field) {
        field.addEventListener('blur', (e) => {
            const date = new Date(e.target.value);
            const age = new Date().getFullYear() - date.getFullYear();
            
            if (date > new Date()) {
                this.showError(e.target, 'Data não pode ser no futuro');
            } else if (age < 16) {
                this.showError(e.target, 'Idade mínima: 16 anos');
            } else {
                this.clearError(e.target);
            }
        });
    }

    setupURLValidation(field) {
        field.addEventListener('blur', (e) => {
            const url = e.target.value.trim();
            if (url && !url.includes('lattes.cnpq.br')) {
                this.showError(e.target, 'Deve ser URL do Lattes');
            } else {
                this.clearError(e.target);
            }
        });
    }

    setupEventListeners() {
        this.form.addEventListener('submit', this.handleSubmit.bind(this));
    }

    async handleSubmit(event) {
        event.preventDefault();
        
        if (!this.validateForm()) {
            this.showMessage('Corrija os erros no formulário', 'error');
            return;
        }
        
        const submitBtn = document.getElementById('submit-btn');
        const originalText = submitBtn.textContent;
        
        try {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Enviando...';
            
            const formData = this.getFormData();
            console.log('🚀 Submitting application with data:', formData);
            console.log('📋 Participation type:', this.participationType);
            
            let response;
            if (this.participationType === 'fellow') {
                console.log('💰 Creating fellow application...');
                response = await FellowAPI.create(formData);
            } else {
                console.log('💙 Creating participant application...');
                response = await ParticipantAPI.create(formData);
            }
            
            console.log('📨 Response received:', response);
            
            if (response.success) {
                const participationText = this.participationType === 'fellow' ? 'bolsista' : 'participante voluntário';
                this.showMessage(`Candidatura como ${participationText} enviada com sucesso!`, 'success');
                this.form.reset();
                
                // Close modal and refresh participations
                setTimeout(() => {
                    this.parentManager.closeApplicationModal();
                }, 2000);
            } else {
                throw new Error(response.error || 'Erro desconhecido');
            }
            
        } catch (error) {
            console.error('❌ Error submitting application:', error);
            this.showMessage('Erro ao enviar candidatura. Tente novamente.', 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        }
    }

    getFormData() {
        const formData = new FormData(this.form);
        const data = Object.fromEntries(formData);
        
        const completeData = { 
            ...this.userData, 
            ...data,
            participationType: this.participationType,
            projectId: this.projectData?.id
        };
        
        if (completeData.cpf) {
            completeData.cpf = completeData.cpf.replace(/\D/g, '');
        }
        
        // For fellow applications, always set studentId
        if (this.participationType === 'fellow') {
            completeData.studentId = this.userData.id;
        } else {
            // For participant applications, set user type fields
            const userTypeFields = {
                'student': 'isStudent',
                'teacher': 'isTeacher',
                'external': 'isExternalPerson'
            };
            
            const field = userTypeFields[this.userType?.toLowerCase()];
            if (field) completeData[field] = this.userData.id;
        }
        
        delete completeData.loginTime;
        delete completeData.userType;
        
        return completeData;
    }

    // Utility methods
    isValidCPF(cpf) {
        if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;
        
        const digits = cpf.split('').map(Number);
        const check = (slice, factor) => {
            const sum = slice.reduce((acc, digit, i) => acc + digit * (factor - i), 0);
            const remainder = sum % 11;
            return remainder < 2 ? 0 : 11 - remainder;
        };
        
        return check(digits.slice(0, 9), 10) === digits[9] && 
               check(digits.slice(0, 10), 11) === digits[10];
    }

    formatCPF(cpf) {
        const clean = cpf.replace(/\D/g, '');
        return clean.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }

    validateForm() {
        const fields = this.form.querySelectorAll('input[required]');
        return Array.from(fields).every(field => {
            if (!field.value.trim()) {
                this.showError(field, 'Campo obrigatório');
                return false;
            }
            return true;
        });
    }

    showError(field, message) {
        field.classList.add('error');
        let errorEl = field.parentNode.querySelector('.error-message');
        if (!errorEl) {
            errorEl = document.createElement('div');
            errorEl.className = 'error-message';
            field.parentNode.appendChild(errorEl);
        }
        errorEl.textContent = message;
        errorEl.style.display = 'block';
    }

    clearError(field) {
        field.classList.remove('error');
        const errorEl = field.parentNode.querySelector('.error-message');
        if (errorEl) errorEl.style.display = 'none';
    }

    showMessage(message, type = 'info') {
        const existingMsg = document.querySelector('.form-message');
        if (existingMsg) existingMsg.remove();
        
        const msgEl = document.createElement('div');
        msgEl.className = `form-message ${type}`;
        msgEl.textContent = message;
        msgEl.style.cssText = `
            padding: 15px; margin: 15px 0; border-radius: 8px; text-align: center; font-weight: 500;
            background: ${type === 'error' ? 'linear-gradient(135deg, rgba(255, 71, 87, 0.2), rgba(255, 71, 87, 0.1))' : 'linear-gradient(135deg, rgba(39, 174, 96, 0.2), rgba(46, 204, 113, 0.1))'};
            border: 2px solid ${type === 'error' ? '#ff4757' : '#27ae60'};
            color: ${type === 'error' ? '#ff4757' : '#27ae60'};
        `;
        
        this.form.appendChild(msgEl);
        msgEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
}

// Global variable for manager
let participationManager;

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    participationManager = new ParticipationManager();
});