const projectData = JSON.parse(sessionStorage.getItem('selectedProject'));
console.log('🎯 DEBUG: Retrieved project data from sessionStorage:', projectData);

// If no project data, set up a sample project for testing
if (!projectData) {
    console.log('📋 No project data found, setting up sample project for testing');
    const sampleProject = {
        id: 1,
        title: "Projeto de Teste - Plataforma Digital",
        subtitle: "Sistema de aprendizagem inovador",
        type: "educational",
        description: "Este é um projeto educacional focado no desenvolvimento de uma plataforma digital de aprendizagem para estudantes universitários.",
        duration: "8 meses",
        coordinator: "Prof. João Santos",
        timeline: "2024-2025",
        requirements: "Conhecimento em programação, experiência em educação",
        complementHours: 60,
        fellowship: true,
        fellowValue: 400.00,
        scholarshipAvailable: true,
        course: "Ciência da Computação",
        externalLink: "https://exemplo.com/inscricao"
    };
    
    sessionStorage.setItem('selectedProject', JSON.stringify(sampleProject));
    console.log('✅ Sample project data set for testing');
}

const finalProjectData = JSON.parse(sessionStorage.getItem('selectedProject'));

if (finalProjectData) {
    // Always load from session storage since it contains complete data
    console.log('📱 Loading project data from session storage');
    loadProjectData(finalProjectData);
    console.log(finalProjectData);
    
    // Check if fellowship exists and show/hide scholarship info
    if(finalProjectData.fellowship || finalProjectData.scholarshipAvailable){
        console.log("Fellowship available");
        const element = document.getElementById("info-bolsa"); 
        element.style.display = "block";
    }
} else {
    // No project data available
    document.getElementById('project-title').textContent = 'Projeto não encontrado';
    document.getElementById('project-description').textContent = 'Nenhum projeto foi selecionado.';
}

// Initialize SessionManager and check authentication
SessionManager.init();

// Wait for DOM to be fully loaded before initializing application functionality
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 DOM loaded, initializing application functionality');
    const currentProjectData = JSON.parse(sessionStorage.getItem('selectedProject'));
    if (currentProjectData) {
        // Small delay to ensure all elements are ready
        setTimeout(() => {
            initializeApplicationButtons();
        }, 100);
    }
});

async function loadProjectFromAPI(projectId) {
    try {
        const result = await ProjectAPI.getById(projectId);
        if (result.success) {
            const project = result.data;
            loadProjectData(project);
            
            // Load associated companies
            try {
                const companiesResult = await CompanyProjectAPI.findCompaniesByProjectId(projectId);
                if (companiesResult.success && companiesResult.data.length > 0) {
                    loadPartnerCompanies(companiesResult.data);
                }
            } catch (error) {
                console.warn('Could not load partner companies:', error);
            }
            
            // Check if fellowship exists and show/hide scholarship info
            if(project.scholarshipAvailable){
                console.log("Fellowship available");
                const element = document.getElementById("info-bolsa"); 
                element.style.display = "block";
            }
        } else {
            console.error('Failed to load project:', result.error);
            document.getElementById('project-title').textContent = 'Erro ao carregar projeto';
            document.getElementById('project-description').textContent = 'Não foi possível carregar os dados do projeto.';
        }
    } catch (error) {
        console.error('Error loading project from API:', error);
        // Fallback to session storage data if available
        if (projectData) {
            loadProjectData(projectData);
        }
    }
}

function loadProjectData(project) {
    console.log('🔧 DEBUG: Loading project data:', project);
    
    // Basic project information
    document.getElementById('project-title').textContent = project.title || 'Título do Projeto';
    document.getElementById('project-subtitle').textContent = project.subtitle || 'Subtítulo do projeto';
    document.getElementById('project-type').textContent = project.type ? project.type.toUpperCase() : 'TIPO';
    document.getElementById('project-description').textContent = project.description || 'Descrição não disponível';
    
    // Project details - fix field name mismatches
    document.getElementById('project-duration').textContent = project.duration || 'Não especificado';
    document.getElementById('project-coordinator').textContent = project.coordinator || project.coordenator || 'Não informado';
    document.getElementById('project-timeline').textContent = project.timeline || 'Não especificado';
    document.getElementById('project-requirements').textContent = project.requirements || 'Não especificado';
    document.getElementById('complement-hours').textContent = project.complementHours ? project.complementHours + 'h' : 'Não especificado';
    
    // Fellowship information
    if (project.fellowship) {
        document.getElementById('fellowship-value').textContent = project.fellowValue ? project.fellowValue.toFixed(2) : '0,00';
        // Load available slots in scholarship section
        loadAvailableSlots(project);
    }
    
    // Additional info
    document.getElementById('additional-info-text').textContent = 'Timeline: ' + (project.timeline || 'Não especificado') + 
        '. Tipo de projeto: ' + (project.type || 'Não especificado') + '.';
    
    // Project link - fix field name mismatch
    const linkElement = document.getElementById('project-link');
    const projectLink = project.externalLink || project.linkExtension;
    if (projectLink && projectLink !== '#') {
        linkElement.href = projectLink;
        linkElement.textContent = 'Clique aqui para se inscrever';
    } else {
        linkElement.textContent = 'Link não disponível';
        linkElement.href = '#';
    }
    
    // Load areas and target audience
    loadProjectAreas(project);
    loadTypeSpecificInfo(project);
    
    // Set up event listeners
    setupEventListeners();
}

function loadAvailableSlots(project) {
    const slotsElement = document.getElementById('available-slots');
    let slotsText = '';
    
    switch(project.type) {
        case 'research':
            slotsText = 'Vagas para pesquisadores e bolsistas';
            break;
        case 'educational':
            slotsText = project.slots ? project.slots + ' vagas disponíveis' : 'Consultar coordenação';
            break;
        case 'extension':
            slotsText = project.slots ? project.slots + ' vagas para participação' : 'Consultar coordenação';
            break;
        default:
            slotsText = 'Informações em breve';
    }
    
    slotsElement.textContent = slotsText;
}

function loadProjectAreas(project) {
    const areasElement = document.getElementById('project-areas');
    let areasText = '';
    
    if (project.course) {
        areasText = project.course;
    }
    
    if (project.type) {
        areasText += areasText ? ', ' + project.type : project.type;
    }
    
    areasElement.textContent = areasText || 'Não especificado';
}

function loadTypeSpecificInfo(project) {
    // Load actual project members (participants and fellows)
    loadProjectMembers(project.id);
    
    // Load partner institutions (will be replaced by real data from CompanyProjectAPI)
    document.getElementById('partner-institutions').textContent = 'Carregando parcerias...';
    
    // Handle type-specific information
    switch(project.type) {
        case 'research':
            if (project.aim) {
                document.getElementById('research-specific').style.display = 'block';
                document.getElementById('research-aim').textContent = project.aim;
            }
            if (project.justification) {
                document.getElementById('research-justification').style.display = 'block';
                document.getElementById('research-just').textContent = project.justification;
            }
            document.getElementById('target-audience').textContent = 'Estudantes de graduação e pós-graduação';
            break;
            
        case 'educational':
            if (project.justification) {
                document.getElementById('research-justification').style.display = 'block';
                document.getElementById('research-just').textContent = project.justification;
            }
            document.getElementById('target-audience').textContent = 'Estudantes e educadores';
            break;
            
        case 'extension':
            if (project.targetAudience) {
                document.getElementById('target-audience').textContent = project.targetAudience;
            }
            if (project.selectionProcess) {
                document.getElementById('extension-specific').style.display = 'block';
                document.getElementById('selection-process').textContent = project.selectionProcess;
            }
            break;
            
        default:
            document.getElementById('target-audience').textContent = 'Público geral';
    }
}

function loadPartnerCompanies(companyProjects) {
    if (companyProjects && companyProjects.length > 0) {
        // Extract company information from the relationships
        const companyNames = companyProjects.map(cp => cp.companyName || `Empresa ID: ${cp.companyId}`).join(', ');
        document.getElementById('partner-institutions').textContent = companyNames;
    } else {
        document.getElementById('partner-institutions').textContent = 'Nenhuma empresa parceira cadastrada';
    }
}

function setupEventListeners() {
    // Back button
    document.getElementById('back-btn').addEventListener('click', () => {
        // Check if we came from My Projects page
        const referrer = document.referrer;
        if (referrer.includes('myProjects.html')) {
            window.location.href = '../userProject/myProjects.html';
        } else {
            window.location.href = '/front/home.html';
        }
    });
    
    // Like button
    const likeBtn = document.getElementById('like-button');
    let isLiked = false;
    likeBtn.addEventListener('click', () => {
        isLiked = !isLiked;
        likeBtn.textContent = isLiked ? '❤️ Curtido' : '👍 Curtir';
        likeBtn.style.backgroundColor = isLiked ? '#e322f5' : '#34f76f';
    });
    
    // Apply button (old scholarship application)
    const applyBtn = document.getElementById('apply-button');
    if (applyBtn) {
        applyBtn.addEventListener('click', () => {
            applyForFellowship();
        });
    }
    
    // New application buttons
    const participantBtn = document.getElementById('apply-participant-btn');
    const fellowBtn = document.getElementById('apply-fellow-btn');
    
    if (participantBtn) {
        participantBtn.addEventListener('click', () => {
            applyForParticipant();
        });
    }
    
    if (fellowBtn) {
        fellowBtn.addEventListener('click', () => {
            applyForFellowship();
        });
    }
    
    // Initialize application buttons based on user type and project
    initializeApplicationButtons();
}

// Initialize application buttons based on user type and project requirements
async function initializeApplicationButtons() {
    const user = SessionManager.getUser();
    const projectData = JSON.parse(sessionStorage.getItem('selectedProject'));
    
    if (!user || !projectData) {
        console.log('No user or project data available');
        return;
    }
    
    console.log('🔧 Initializing application buttons for user:', user.userType);
    console.log('📋 Project data:', projectData);
    
    const participantBtn = document.getElementById('apply-participant-btn');
    const fellowBtn = document.getElementById('apply-fellow-btn');
    
    // Teachers cannot participate in projects as participants
    if (user.userType === 'teacher') {
        // Hide participant buttons for teachers
        participantBtn.style.display = 'none';
        fellowBtn.style.display = 'none';
        
    } else if (user.userType === 'company') {
        // Companies cannot participate
        participantBtn.style.display = 'none';
        fellowBtn.style.display = 'none';
        
    } else {
        // Students and external users can be participants
        
        // Show participant button for students and external users
        participantBtn.style.display = 'inline-block';
        
        // Check if user already applied as participant
        await checkExistingParticipantApplication(user, projectData.id);
        
        // Show fellowship button only if project has fellowship and user is student
        if (projectData.fellowship && user.userType === 'student') {
            fellowBtn.style.display = 'inline-block';
            
            // Check if user already applied for fellowship
            await checkExistingFellowApplication(user, projectData.id);
        }
    }
}

// Check if user already applied as participant
async function checkExistingParticipantApplication(user, projectId) {
    try {
        const personId = getUserPersonId(user);
        const hasApplied = await ParticipantAPI.checkExistingApplication(projectId, personId, user.userType);
        
        const participantBtn = document.getElementById('apply-participant-btn');
        if (hasApplied) {
            participantBtn.textContent = '✅ Candidatura Enviada - Participante';
            participantBtn.disabled = true;
            participantBtn.style.cursor = 'not-allowed';
        }
    } catch (error) {
        console.error('Error checking participant application:', error);
    }
}

// Check if user already applied for fellowship
async function checkExistingFellowApplication(user, projectId) {
    try {
        const studentId = user.id;
        const hasApplied = await FellowAPI.checkExistingApplication(projectId, studentId);
        
        const fellowBtn = document.getElementById('apply-fellow-btn');
        if (hasApplied) {
            fellowBtn.textContent = '✅ Candidatura Enviada - Bolsista';
            fellowBtn.disabled = true;
            fellowBtn.style.cursor = 'not-allowed';
        }
    } catch (error) {
        console.error('Error checking fellow application:', error);
    }
}

// Apply for participant role
async function applyForParticipant() {
    const user = SessionManager.getUser();
    const projectData = JSON.parse(sessionStorage.getItem('selectedProject'));
    
    if (!user) {
        showAccessDenied('Você precisa estar logado para enviar uma candidatura.');
        return;
    }
    
    if (user.userType === 'company') {
        showAccessDenied('Empresas não podem se candidatar como participantes. Entre em contato diretamente com o coordenador do projeto.');
        return;
    }
    
    if (!projectData) {
        showApplicationStatus('Erro', 'Dados do projeto não encontrados.');
        return;
    }
    
    console.log('🔧 Applying for participant role');
    console.log('User:', user);
    console.log('Project:', projectData);
    
    showApplicationStatus('Enviando Candidatura', 'Processando sua candidatura como participante...', true);
    
    try {
        const personId = getUserPersonId(user);
        
        // Check if already applied
        const hasApplied = await ParticipantAPI.checkExistingApplication(projectData.id, personId, user.userType);
        if (hasApplied) {
            showApplicationStatus('Candidatura Já Enviada', 'Você já enviou uma candidatura para participar deste projeto.');
            return;
        }
        
        const participantData = {
            projectId: projectData.id,
            personId: personId,
            personType: user.userType,
            applicationDate: new Date().toISOString(),
            status: 'PENDING',
            // Add user details for backend compatibility
            name: user.name,
            email: user.email,
            phoneNumber: user.phoneNumber || '',
            cpf: user.cpf || user.registration || '' // Use registration if cpf not available
        };
        
        const result = await ParticipantAPI.create(participantData);
        
        if (result.success) {
            showApplicationStatus('Candidatura Enviada com Sucesso!', 
                'Sua candidatura para participar do projeto foi enviada. Redirecionando para suas participações...');
            
            // Update button state
            const participantBtn = document.getElementById('apply-participant-btn');
            participantBtn.textContent = '✅ Candidatura Enviada - Participante';
            participantBtn.disabled = true;
            participantBtn.style.cursor = 'not-allowed';
            
            // Redirect to participante page after 3 seconds
            setTimeout(() => {
                window.location.href = '../fellowOrMember/participante.html';
            }, 3000);
        } else {
            showApplicationStatus('Erro ao Enviar Candidatura', 
                result.error || 'Ocorreu um erro ao processar sua candidatura. Tente novamente.');
        }
    } catch (error) {
        console.error('Error applying for participant:', error);
        showApplicationStatus('Erro', 'Erro interno do sistema. Tente novamente mais tarde.');
    }
}

// Apply for fellowship role
async function applyForFellowship() {
    const user = SessionManager.getUser();
    const projectData = JSON.parse(sessionStorage.getItem('selectedProject'));
    
    if (!user) {
        showAccessDenied('Você precisa estar logado para enviar uma candidatura.');
        return;
    }
    
    if (user.userType !== 'student') {
        showAccessDenied('Apenas estudantes podem se candidatar para bolsas de estudo.');
        return;
    }
    
    if (!projectData) {
        showApplicationStatus('Erro', 'Dados do projeto não encontrados.');
        return;
    }
    
    if (!projectData.fellowship) {
        showAccessDenied('Este projeto não oferece bolsas de estudo.');
        return;
    }
    
    console.log('🔧 Applying for fellowship role');
    console.log('User:', user);
    console.log('Project:', projectData);
    
    showApplicationStatus('Enviando Candidatura', 'Processando sua candidatura para bolsa de estudo...', true);
    
    try {
        // Check if already applied
        const hasApplied = await FellowAPI.checkExistingApplication(projectData.id, user.id);
        if (hasApplied) {
            showApplicationStatus('Candidatura Já Enviada', 'Você já enviou uma candidatura para bolsa neste projeto.');
            return;
        }
        
        const fellowData = {
            projectId: projectData.id,
            studentId: user.id,
            cpf: user.cpf || user.registration || '', // Use registration if cpf not available
            lattesCurriculum: '' // Optional, can be empty for now
        };
        
        const result = await FellowAPI.create(fellowData);
        
        if (result.success) {
            showApplicationStatus('Candidatura para Bolsa Enviada!', 
                'Sua candidatura para bolsa de estudo foi enviada com sucesso. Redirecionando para suas participações...');
            
            // Update button state
            const fellowBtn = document.getElementById('apply-fellow-btn');
            if (fellowBtn) {
                fellowBtn.textContent = '✅ Candidatura Enviada - Bolsista';
                fellowBtn.disabled = true;
                fellowBtn.style.cursor = 'not-allowed';
            }
            
            // Redirect to participante page after 3 seconds
            setTimeout(() => {
                window.location.href = '../fellowOrMember/participante.html';
            }, 3000);
            
        } else {
            showApplicationStatus('Erro ao Enviar Candidatura', 
                result.error || 'Ocorreu um erro ao processar sua candidatura. Tente novamente.');
        }
    } catch (error) {
        console.error('Error applying for fellowship:', error);
        showApplicationStatus('Erro', 'Erro interno do sistema. Tente novamente mais tarde.');
    }
}

// Get person ID based on user type
function getUserPersonId(user) {
    switch(user.userType) {
        case 'student':
            return user.id;
        case 'teacher':
            return user.id;
        case 'external':
            return user.id;
        default:
            throw new Error('Invalid user type for participant application');
    }
}

// Show application status modal
function showApplicationStatus(title, message, isLoading = false) {
    const statusDiv = document.getElementById('application-status');
    const statusTitle = document.getElementById('status-title');
    const statusMessage = document.getElementById('status-message');
    
    statusTitle.textContent = title;
    statusMessage.innerHTML = isLoading ? `<div class="loading"></div>${message}` : message;
    statusDiv.style.display = 'flex';
    
    // Auto-hide after 5 seconds if not loading
    if (!isLoading) {
        setTimeout(() => {
            statusDiv.style.display = 'none';
        }, 5000);
    }
    
    // Click outside to close
    statusDiv.addEventListener('click', (e) => {
        if (e.target === statusDiv) {
            statusDiv.style.display = 'none';
        }
    });
}

// Show access denied modal
function showAccessDenied(message) {
    const accessDiv = document.getElementById('access-denied');
    const accessMessage = document.getElementById('access-message');
    
    accessMessage.textContent = message;
    accessDiv.style.display = 'flex';
}

// Hide access denied modal
function hideAccessDenied() {
    const accessDiv = document.getElementById('access-denied');
    accessDiv.style.display = 'none';
}

// Function to load actual project members from APIs
async function loadProjectMembers(projectId) {
    console.log('👥 Loading project members for project ID:', projectId);
    
    const membersElement = document.getElementById('project-members');
    if (!membersElement) {
        console.warn('⚠️ Project members element not found');
        return;
    }
    
    // Show loading state
    membersElement.innerHTML = '<em>🔄 Carregando membros...</em>';
    
    let membersHTML = '';
    
    try {
        let totalMembers = 0;
        
        // Load fellows (bolsistas)
        console.log('🎓 Loading fellows for project:', projectId);
        const fellowsResult = await FellowAPI.getAll();
        
        if (fellowsResult.success && fellowsResult.data) {
            const projectFellows = fellowsResult.data.filter(fellow => 
                fellow.projectId === projectId
            );
            
            console.log('🎓 Found fellows:', projectFellows.length);
            
            if (projectFellows.length > 0) {
                totalMembers += projectFellows.length;
                
                for (const fellow of projectFellows) {
                    // Try to get student information
                    try {
                        const studentResult = await StudentAPI.getById(fellow.studentId);
                        const studentName = studentResult.success && studentResult.data 
                            ? studentResult.data.name 
                            : `Estudante ID: ${fellow.studentId}`;
                        
                        membersHTML += `💰 <strong>${studentName}</strong> - Bolsista<br>`;
                    } catch (error) {
                        console.warn('Could not load student details for fellow:', fellow.studentId);
                        membersHTML += `💰 <strong>Estudante ID: ${fellow.studentId}</strong> - Bolsista<br>`;
                    }
                }
            }
        } else {
            console.log('📊 No fellows data or API call failed');
        }
        
        // Load participants
        console.log('👤 Loading participants for project:', projectId);
        const participantsResult = await ParticipantAPI.getAll();
        
        if (participantsResult.success && participantsResult.data) {
            const projectParticipants = participantsResult.data.filter(participant => 
                participant.projectId === projectId
            );
            
            console.log('👤 Found participants:', projectParticipants.length);
            
            if (projectParticipants.length > 0) {
                totalMembers += projectParticipants.length;
                
                for (const participant of projectParticipants) {
                    let participantName = participant.name || 'Nome não disponível';
                    let participantRole = 'Participante';
                    
                    // Determine participant type and role
                    if (participant.role) {
                        participantRole = participant.role;
                    } else if (participant.studentId) {
                        participantRole = 'Estudante Participante';
                        
                        // Try to get more detailed student information if available
                        try {
                            const studentResult = await StudentAPI.getById(participant.studentId);
                            if (studentResult.success && studentResult.data && studentResult.data.name) {
                                participantName = studentResult.data.name;
                            }
                        } catch (error) {
                            console.warn('Could not load student details for participant:', participant.studentId);
                        }
                        
                    } else if (participant.externalPersonId) {
                        participantRole = 'Participante Externo';
                    }
                    
                    membersHTML += `👥 <strong>${participantName}</strong> - ${participantRole}<br>`;
                }
            }
        } else {
            console.log('📊 No participants data or API call failed');
        }
        
        // Add summary line if there are members
        if (totalMembers > 0) {
            membersHTML += `<br><em>Total: ${totalMembers} membros</em>`;
        }
        
        // If no members found, show appropriate message
        if (totalMembers === 0) {
            membersHTML = '<em>Nenhum membro cadastrado</em>';
        }
        
        membersElement.innerHTML = membersHTML;
        console.log('✅ Project members loaded successfully');
        
    } catch (error) {
        console.error('❌ Error loading project members:', error);
        membersElement.innerHTML = '<em>❌ Erro ao carregar informações de membros</em>';
    }
}

