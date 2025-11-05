// My Projects Page Manager
class MyProjectsManager {
    constructor() {
        this.projects = [];
        this.filteredProjects = [];
        this.currentProject = null;
        this.userType = null;
        this.userId = null;
        this.init();
    }

    init() {
        console.log('MyProjectsManager initializing...');
        
        // Check if user is logged in
        if (!SessionManager.isLoggedIn()) {
            window.location.href = '../../pages/welcome/welcome.html';
            return;
        }

        const user = SessionManager.getUser();
        this.userType = user.userType;
        this.userId = user.id;
        
        this.setupEventListeners();
        this.loadProjects();
    }

    setupEventListeners() {
        // Search functionality
        const searchInput = document.getElementById('searchProjects');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        // Filter controls
        document.getElementById('typeFilter')?.addEventListener('change', () => this.applyFilters());
        document.getElementById('sortBy')?.addEventListener('change', () => this.applyFilters());

        // Create project button
        document.getElementById('createProjectBtn')?.addEventListener('click', () => this.createProject());
    }

    async loadProjects() {
        this.showLoading();
        
        try {
            // Load projects based on user type
            const projects = await this.fetchUserProjects();
            this.projects = projects;
            this.filteredProjects = [...projects];
            
            this.updateStats();
            this.renderProjects();
            this.hideLoading();
            
        } catch (error) {
            console.error('Error loading projects:', error);
            this.showError('Erro ao carregar projetos. Tente novamente.');
            this.hideLoading();
        }
    }

    async fetchUserProjects() {
        try {
            const user = SessionManager.getUser();
            const userType = user.userType;
            const userName = user.name;
            const userId = user.id;
            
            console.log('🔍 Fetching projects for user:', userName, 'Type:', userType, 'ID:', userId);
            
            let userProjects = [];
            
            if (userType === 'teacher') {
                // Teachers see projects they coordinate
                console.log('👨‍🏫 Fetching projects coordinated by teacher:', userName);
                const allProjects = await ProjectAPI.getAll();
                console.log('📊 All projects response:', allProjects);
                
                if (allProjects.success && allProjects.data) {
                    // Filter projects by coordinator name
                    userProjects = allProjects.data.filter(project => {
                        const coordinatorMatch = project.coordinator === userName || 
                                               project.coordinator === user.siape ||
                                               project.coordinator === user.email;
                        console.log(`🔍 Checking project "${project.title}": coordinator="${project.coordinator}" vs user="${userName}" = ${coordinatorMatch}`);
                        return coordinatorMatch;
                    });
                    console.log('👨‍🏫 Teacher projects found:', userProjects.length);
                } else {
                    console.warn('⚠️ No projects data received from API');
                }
                
            } else if (userType === 'student') {
                // Students see projects they are fellows in
                console.log('🎓 Fetching fellowships for student ID:', userId);
                try {
                    const fellowships = await FellowAPI.getAll();
                    console.log('📊 Fellowships response:', fellowships);
                    
                    if (fellowships.success && fellowships.data) {
                        const studentFellowships = fellowships.data.filter(fellowship => 
                            fellowship.studentId === userId
                        );
                        console.log('🎓 Student fellowships found:', studentFellowships.length);
                        
                        if (studentFellowships.length > 0) {
                            // Get projects for these fellowships
                            const allProjects = await ProjectAPI.getAll();
                            if (allProjects.success && allProjects.data) {
                                const fellowProjectIds = studentFellowships.map(f => f.projectId);
                                userProjects = allProjects.data.filter(project => 
                                    fellowProjectIds.includes(project.id)
                                );
                                console.log('🎓 Student projects found:', userProjects.length);
                            }
                        }
                    }
                } catch (fellowError) {
                    console.warn('⚠️ Error fetching fellowships:', fellowError);
                    // Continue with empty array for students
                }
                
            } else if (userType === 'external') {
                // External users see projects they participate in
                console.log('👤 Fetching participations for external user ID:', userId);
                try {
                    const participants = await ParticipantAPI.getAll();
                    console.log('📊 Participants response:', participants);
                    
                    if (participants.success && participants.data) {
                        const userParticipations = participants.data.filter(participant => 
                            participant.externalPersonId === userId
                        );
                        console.log('👤 External participations found:', userParticipations.length);
                        
                        if (userParticipations.length > 0) {
                            // Get projects for these participations
                            const allProjects = await ProjectAPI.getAll();
                            if (allProjects.success && allProjects.data) {
                                const participantProjectIds = userParticipations.map(p => p.projectId);
                                userProjects = allProjects.data.filter(project => 
                                    participantProjectIds.includes(project.id)
                                );
                                console.log('👤 External user projects found:', userProjects.length);
                            }
                        }
                    }
                } catch (participantError) {
                    console.warn('⚠️ Error fetching participants:', participantError);
                    // Continue with empty array for external users
                }
                
            } else if (userType === 'company') {
                // Companies should not have access to "My Projects"
                throw new Error('Companies do not have access to project management');
            }
            
            console.log('📋 Final user projects:', userProjects.length, userProjects);
            return userProjects;
            
        } catch (error) {
            console.error('❌ Error fetching user projects:', error);
            
            // Only use fallback in development if there's a connection error
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                if (error.message && (error.message.includes('fetch') || error.message.includes('NetworkError') || error.message.includes('Connection'))) {
                    console.log('🔄 Connection error detected, falling back to mock data');
                    return this.getMockProjectsForUser();
                }
            }
            
            // For other errors or production, return empty array
            console.warn('⚠️ Returning empty projects array due to error');
            return [];
        }
    }

    getMockProjectsForUser() {
        const user = SessionManager.getUser();
        const userType = user.userType;
        
        // Mock data based on user type
        if (userType === 'teacher') {
            // Mock teacher coordinated projects
            return [
                {
                    id: 1,
                    title: "Sistema de Gestão Acadêmica",
                    subtitle: "Desenvolvimento de sistema integrado",
                    coordinator: user.name,
                    description: "Desenvolvimento de um sistema integrado para gestão de atividades acadêmicas.",
                    timeline: "2024-2025",
                    externalLink: "https://example.com/projeto1",
                    duration: "12 meses",
                    image: null,
                    complementHours: "60",
                    scholarshipAvailable: true,
                    scholarshipType: "Iniciação Científica",
                    salary: 400.00,
                    requirements: "Conhecimento em Java e SQL",
                    scholarshipQuantity: 2,
                    type: "educational",
                    typeId: "EDU001"
                }
            ];
        } else if (userType === 'student') {
            // Mock student fellowship projects
            return [
                {
                    id: 2,
                    title: "Análise de Dados Meteorológicos",
                    subtitle: "Pesquisa em machine learning aplicado",
                    coordinator: "Dra. Maria Santos",
                    description: "Pesquisa sobre padrões climáticos utilizando machine learning.",
                    timeline: "2023-2024",
                    externalLink: "https://example.com/projeto2",
                    duration: "18 meses",
                    image: null,
                    complementHours: "80",
                    scholarshipAvailable: true,
                    scholarshipType: "Pesquisa",
                    salary: 600.00,
                    requirements: "Conhecimento em Python e estatística",
                    scholarshipQuantity: 1,
                    type: "research",
                    typeId: "RES002"
                }
            ];
        } else if (userType === 'external') {
            // Mock external user participation projects
            return [
                {
                    id: 3,
                    title: "Programa de Alfabetização Digital",
                    subtitle: "Extensão universitária para a comunidade",
                    coordinator: "Prof. Ana Costa",
                    description: "Extensão universitária para ensinar informática básica para idosos.",
                    timeline: "2024",
                    externalLink: "https://example.com/projeto3",
                    duration: "6 meses",
                    image: null,
                    complementHours: "40",
                    scholarshipAvailable: false,
                    scholarshipType: null,
                    salary: 0.00,
                    requirements: "Disponibilidade para trabalho comunitário",
                    scholarshipQuantity: 0,
                    type: "extension",
                    typeId: "EXT003"
                }
            ];
        }
        
        return [];
    }

    updateStats() {
        // Simple count of total projects only
        const totalProjects = this.projects.length;
        const user = SessionManager.getUser();
        
        // Update page title and subtitle based on user role
        const pageTitle = document.querySelector('.page-header h1');
        const pageSubtitle = document.querySelector('.page-subtitle');
        const createButton = document.getElementById('createProjectBtn');
        
        if (user.userType === 'teacher') {
            pageTitle.textContent = 'Meus Projetos';
            pageSubtitle.textContent = `Você coordena ${totalProjects} projeto${totalProjects !== 1 ? 's' : ''}`;
            createButton.style.display = 'block'; // Show create button for teachers
        } else if (user.userType === 'student') {
            pageTitle.textContent = 'Meus Projetos';
            pageSubtitle.textContent = `Você participa como bolsista em ${totalProjects} projeto${totalProjects !== 1 ? 's' : ''}`;
            createButton.style.display = 'none'; // Hide create button for students
        } else if (user.userType === 'external') {
            pageTitle.textContent = 'Meus Projetos';
            pageSubtitle.textContent = `Você participa em ${totalProjects} projeto${totalProjects !== 1 ? 's' : ''}`;
            createButton.style.display = 'none'; // Hide create button for external users
        }
    }

    renderProjects() {
        const projectsGrid = document.getElementById('projectsGrid');
        const emptyState = document.getElementById('emptyState');
        
        if (this.filteredProjects.length === 0) {
            projectsGrid.style.display = 'none';
            emptyState.style.display = 'block';
            return;
        }
        
        projectsGrid.style.display = 'grid';
        emptyState.style.display = 'none';
        
        projectsGrid.innerHTML = this.filteredProjects.map(project => this.createProjectCard(project)).join('');
        
        // Load member information for each project
        this.filteredProjects.forEach(project => {
            this.loadProjectMemberCount(project.id);
        });
    }

    createProjectCard(project) {
        const user = SessionManager.getUser();
        const isCoordinator = user.userType === 'teacher' && project.coordinator === user.name;
        
        const typeLabels = {
            'research': 'Pesquisa',
            'extension': 'Extensão',
            'educational': 'Educacional',
            'company': 'Empresa'
        };

        // Different card styling based on user role
        const cardClass = isCoordinator ? 'project-card coordinator' : 'project-card participant';
        const roleIndicator = isCoordinator ? 
            '<span class="role-badge coordinator">👑 Coordenador</span>' : 
            '<span class="role-badge participant">👥 Participante</span>';

        return `
            <div class="${cardClass}" onclick="myProjectsManager.showProjectDetails(${project.id})">
                <div class="project-header">
                    <h3 class="project-title">${project.title}</h3>
                    <div class="project-badges">
                        ${project.scholarshipAvailable ? '<span class="scholarship-badge">💰 Bolsa</span>' : ''}
                        ${roleIndicator}
                    </div>
                </div>
                
                <div class="project-type">${typeLabels[project.type]}</div>
                
                <p class="project-subtitle">${project.subtitle}</p>
                <p class="project-description">${project.description}</p>
                
                <div class="project-meta">
                    <span class="project-coordinator">👤 ${project.coordinator}</span>
                    <span class="project-duration">⏱️ ${project.duration}</span>
                </div>
                
                <div class="project-members-info" id="members-info-${project.id}">
                    <span class="members-loading">👥 Carregando membros...</span>
                </div>
                
                <div class="project-action-hint">
                    <small>👆 ${isCoordinator ? 'Clique para gerenciar projeto' : 'Clique para ver detalhes'}</small>
                </div>
            </div>
        `;
    }

    async loadProjectMemberCount(projectId) {
        const membersInfoElement = document.getElementById(`members-info-${projectId}`);
        if (!membersInfoElement) return;
        
        try {
            let totalMembers = 0; // Don't count coordinator since it's displayed separately
            let membersText = [];
            
            // Count fellows
            const fellowsResult = await FellowAPI.getAll();
            if (fellowsResult.success && fellowsResult.data) {
                const projectFellows = fellowsResult.data.filter(fellow => 
                    fellow.projectId === projectId
                );
                if (projectFellows.length > 0) {
                    totalMembers += projectFellows.length;
                    membersText.push(`${projectFellows.length} bolsista${projectFellows.length !== 1 ? 's' : ''}`);
                }
            }
            
            // Count participants
            const participantsResult = await ParticipantAPI.getAll();
            if (participantsResult.success && participantsResult.data) {
                const projectParticipants = participantsResult.data.filter(participant => 
                    participant.projectId === projectId
                );
                if (projectParticipants.length > 0) {
                    totalMembers += projectParticipants.length;
                    membersText.push(`${projectParticipants.length} participante${projectParticipants.length !== 1 ? 's' : ''}`);
                }
            }
            
            // Update display
            if (totalMembers > 0) {
                membersInfoElement.innerHTML = `<span class="members-count">👥 ${totalMembers} membro${totalMembers !== 1 ? 's' : ''} (${membersText.join(', ')})</span>`;
            } else {
                membersInfoElement.innerHTML = `<span class="members-count">👥 Nenhum membro cadastrado</span>`;
            }
            
        } catch (error) {
            console.warn('Error loading member count for project', projectId, error);
            membersInfoElement.innerHTML = `<span class="members-error">👥 Erro ao carregar membros</span>`;
        }
    }

    handleSearch(query) {
        if (!query.trim()) {
            this.filteredProjects = [...this.projects];
        } else {
            const lowQuery = query.toLowerCase();
            this.filteredProjects = this.projects.filter(project => 
                project.title.toLowerCase().includes(lowQuery) ||
                project.description.toLowerCase().includes(lowQuery) ||
                project.coordinator?.toLowerCase().includes(lowQuery) ||
                project.department?.toLowerCase().includes(lowQuery)
            );
        }
        
        this.applyFilters();
    }

    applyFilters() {
        let filtered = [...this.filteredProjects];
        
        // Apply type filter
        const typeFilter = document.getElementById('typeFilter').value;
        if (typeFilter) {
            filtered = filtered.filter(project => project.type === typeFilter);
        }
        
        // Apply sorting
        const sortBy = document.getElementById('sortBy').value;
        filtered.sort((a, b) => {
            switch (sortBy) {
                case 'recent':
                    return new Date(b.timeline?.split('-')[0] || '2024') - new Date(a.timeline?.split('-')[0] || '2024');
                case 'oldest':
                    return new Date(a.timeline?.split('-')[0] || '2024') - new Date(b.timeline?.split('-')[0] || '2024');
                case 'name':
                    return a.title.localeCompare(b.title);
                default:
                    return 0;
            }
        });
        
        this.filteredProjects = filtered;
        this.renderProjects();
    }

    showProjectDetails(projectId) {
        const project = this.projects.find(p => p.id === projectId);
        if (!project) return;
        
        // Convert project data to match the expected format for the project page
        const projectData = {
            id: project.id,
            title: project.title,
            subtitle: project.subtitle,
            description: project.description,
            type: project.type,
            coordinator: project.coordinator,
            coordenator: project.coordinator, // backup field name
            duration: project.duration,
            timeline: project.timeline,
            requirements: project.requirements,
            complementHours: project.complementHours,
            course: project.type, // map type to course field
            fellowship: project.scholarshipAvailable,
            fellowValue: project.salary,
            scholarshipAvailable: project.scholarshipAvailable,
            scholarshipType: project.scholarshipType,
            scholarshipQuantity: project.scholarshipQuantity,
            slots: project.scholarshipQuantity || 0,
            externalLink: project.externalLink,
            linkExtension: project.externalLink,
            targetAudience: this.getTargetAudienceByType(project.type),
            aim: project.type === 'research' ? 'Desenvolver pesquisa inovadora' : null,
            justification: 'Projeto aprovado pelo comitê científico da instituição',
            selectionProcess: project.type === 'extension' ? 'Inscrição online seguida de entrevista' : null
        };
        
        // Store in sessionStorage for the project page
        sessionStorage.setItem('selectedProject', JSON.stringify(projectData));
        
        // Redirect to project page
        window.location.href = '../project page/project.html';
    }

    getTargetAudienceByType(type) {
        const audiences = {
            'research': 'Estudantes de graduação e pós-graduação interessados em pesquisa',
            'extension': 'Comunidade acadêmica e sociedade em geral',
            'educational': 'Estudantes, professores e profissionais da educação',
            'company': 'Estudantes interessados em experiência profissional'
        };
        return audiences[type] || 'Público geral';
    }

    createProject() {
        const user = SessionManager.getUser();
        
        // Only teachers can create projects
        if (user.userType !== 'teacher') {
            alert('Apenas professores podem criar projetos.');
            return;
        }
        
        console.log('🎯 Redirecting to project creation page...');
        
        // Get the current path to determine correct redirect
        const currentPath = window.location.pathname;
        console.log('Current path:', currentPath);
        
        let targetUrl;
        
        if (currentPath.includes('/pages/userProject/')) {
            // From myProjects page, navigate to projectCreation
            targetUrl = '../projectCreation/projectCreation.html';
        } else if (currentPath.includes('/pages/')) {
            // Other pages folder
            targetUrl = '../projectCreation/projectCreation.html';
        } else {
            // We're in root
            targetUrl = 'pages/projectCreation/projectCreation.html';
        }
        
        console.log('🎯 Create Project redirect target:', targetUrl);
        window.location.href = targetUrl;
    }

    showLoading() {
        document.getElementById('loadingState').style.display = 'flex';
        document.getElementById('projectsGrid').style.display = 'none';
        document.getElementById('emptyState').style.display = 'none';
    }

    hideLoading() {
        document.getElementById('loadingState').style.display = 'none';
    }

    showError(message) {
        alert(message); // TODO: Implement better error display
    }
}

// Initialize the manager when DOM is loaded
let myProjectsManager;
document.addEventListener('DOMContentLoaded', () => {
    myProjectsManager = new MyProjectsManager();
});