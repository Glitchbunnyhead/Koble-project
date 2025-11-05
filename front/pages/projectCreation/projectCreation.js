// ===== Project Creation Manager =====
class ProjectCreationManager {
    constructor() {
        this.currentProject = null;
        this.formData = {};
        this.init();
    }

    init() {
        console.log('🚀 ProjectCreationManager initializing...');
        
        // Check authentication and user type
        if (!this.validateUserAccess()) {
            return;
        }

        this.setupEventListeners();
        this.setupFormValidation();
        this.loadUserInfo();
        
        // Check if this is from an adopted idea
        this.checkForAdoptedIdea();
        
        console.log('✅ ProjectCreationManager initialized successfully');
    }

    validateUserAccess() {
        const user = SessionManager.getUser();
        
        if (!user) {
            alert('Você precisa estar logado para criar projetos.');
            window.location.href = '../../pages/login and sign in/login.html';
            return false;
        }

        if (user.userType !== 'teacher') {
            alert('Apenas professores podem criar projetos.');
            window.location.href = '../../index.html';
            return false;
        }

        console.log('✅ User access validated:', user.name);
        return true;
    }

    loadUserInfo() {
        const user = SessionManager.getUser();
        if (user) {
            // Auto-fill coordinator field
            document.getElementById('projectDescription').placeholder = 
                `Olá Prof. ${user.name}! Descreva os objetivos, metodologia e resultados esperados do seu projeto.`;
        }
    }

    checkForAdoptedIdea() {
        console.log('🔍 Checking for adopted idea data...');
        
        try {
            const adoptedIdeaData = localStorage.getItem('koble_adopted_idea');
            
            if (adoptedIdeaData) {
                const ideaData = JSON.parse(adoptedIdeaData);
                console.log('✅ Found adopted idea data:', ideaData);
                
                // Show notification about adopted idea
                this.showNotification(
                    `Preenchendo projeto com dados da ideia adotada: "${ideaData.title}"`, 
                    'info'
                );
                
                // Pre-fill form with idea data
                this.prePopulateFormFromIdea(ideaData);
                
                // Clean up localStorage
                localStorage.removeItem('koble_adopted_idea');
                
                console.log('🎯 Form pre-populated from adopted idea');
            } else {
                console.log('No adopted idea data found');
            }
        } catch (error) {
            console.error('❌ Error loading adopted idea data:', error);
        }
    }

    prePopulateFormFromIdea(ideaData) {
        console.log('📝 Pre-populating form with idea data...', ideaData);
        
        // Basic project information
        if (ideaData.title) {
            document.getElementById('projectTitle').value = ideaData.title;
        }
        if (ideaData.subtitle) {
            document.getElementById('projectSubtitle').value = ideaData.subtitle;
        }
        if (ideaData.description) {
            document.getElementById('projectDescription').value = ideaData.description;
        }
        if (ideaData.type) {
            document.getElementById('projectType').value = ideaData.type;
            this.handleProjectTypeChange(ideaData.type);
        }
        if (ideaData.discipline) {
            document.getElementById('projectDiscipline').value = ideaData.discipline;
        }
        if (ideaData.objective) {
            document.getElementById('projectObjective').value = ideaData.objective;
        }
        if (ideaData.justification) {
            document.getElementById('projectJustification').value = ideaData.justification;
        }
        
        // Set other defaults from idea
        if (ideaData.timeline) {
            document.getElementById('projectTimeline').value = ideaData.timeline;
        }
        if (ideaData.duration) {
            document.getElementById('projectDuration').value = ideaData.duration;
        }
        
        // Store the original student information for later use
        this.adoptedIdeaStudent = {
            name: ideaData.originalStudent,
            id: ideaData.originalStudentId
        };
        
        // Add a note about the original student
        const noteElement = document.createElement('div');
        noteElement.className = 'adopted-idea-note';
        noteElement.innerHTML = `
            <div class="alert alert-info">
                <strong>📝 Ideia Adotada:</strong> Esta projeto foi criado a partir de uma ideia proposta por 
                <strong>${ideaData.originalStudent}</strong>. O estudante será automaticamente adicionado 
                como participante do projeto.
            </div>
        `;
        
        // Insert note after the form title
        const formTitle = document.querySelector('.page-title');
        if (formTitle && formTitle.parentNode) {
            formTitle.parentNode.insertBefore(noteElement, formTitle.nextSibling);
        }
        
        console.log('✅ Form pre-populated successfully');
    }

    setupEventListeners() {
        // Form type change listener
        document.getElementById('projectType').addEventListener('change', (e) => {
            this.handleProjectTypeChange(e.target.value);
        });

        // Scholarship checkbox listener
        document.getElementById('scholarshipAvailable').addEventListener('change', (e) => {
            this.handleScholarshipChange(e.target.checked);
        });

        // Form action buttons
        document.getElementById('cancelBtn').addEventListener('click', () => {
            this.cancelCreation();
        });

        document.getElementById('previewBtn').addEventListener('click', () => {
            this.previewProject();
        });

        document.getElementById('submitBtn').addEventListener('click', (e) => {
            e.preventDefault();
            this.handleFormSubmission();
        });

        // Form submission
        document.getElementById('projectCreationForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleFormSubmission();
        });

        // Auto-save functionality (every 30 seconds)
        setInterval(() => {
            this.autoSave();
        }, 30000);

        console.log('✅ Event listeners set up');
    }

    setupFormValidation() {
        const inputs = document.querySelectorAll('input[required], select[required], textarea[required]');
        
        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                this.validateField(input);
            });

            input.addEventListener('input', () => {
                this.clearFieldError(input);
            });
        });

        console.log('✅ Form validation set up');
    }

    handleProjectTypeChange(type) {
        console.log('🔄 Project type changed to:', type);
        
        // Hide all type-specific fields
        document.getElementById('typeSpecificFields').style.display = 'none';
        document.querySelectorAll('.type-fields').forEach(field => {
            field.style.display = 'none';
        });

        // Show relevant fields based on type
        if (type) {
            document.getElementById('typeSpecificFields').style.display = 'block';
            
            switch(type) {
                case 'research':
                    document.getElementById('researchFields').style.display = 'block';
                    this.setRequiredFields(['researchObjective', 'researchJustification', 'researchDiscipline']);
                    break;
                case 'educational':
                    document.getElementById('educationalFields').style.display = 'block';
                    this.setRequiredFields([]);
                    break;
                case 'extension':
                    document.getElementById('extensionFields').style.display = 'block';
                    this.setRequiredFields([]);
                    break;
            }
        }
    }

    setRequiredFields(fieldIds) {
        // Remove required from all type-specific fields
        document.querySelectorAll('.type-fields input, .type-fields textarea, .type-fields select').forEach(field => {
            field.removeAttribute('required');
        });

        // Add required to specified fields
        fieldIds.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field) {
                field.setAttribute('required', '');
            }
        });
    }

    handleScholarshipChange(isChecked) {
        const scholarshipDetails = document.getElementById('scholarshipDetails');
        scholarshipDetails.style.display = isChecked ? 'block' : 'none';
        
        // Set required for scholarship fields
        const scholarshipFields = scholarshipDetails.querySelectorAll('input');
        scholarshipFields.forEach(field => {
            if (isChecked) {
                field.setAttribute('required', '');
            } else {
                field.removeAttribute('required');
                field.value = '';
            }
        });

        console.log('💰 Scholarship fields', isChecked ? 'shown' : 'hidden');
    }

    validateField(field) {
        const fieldGroup = field.closest('.input-group');
        this.clearFieldError(field);

        if (field.hasAttribute('required') && !field.value.trim()) {
            this.showFieldError(field, 'Este campo é obrigatório');
            return false;
        }

        // Specific validations
        switch(field.type) {
            case 'email':
                if (field.value && !this.isValidEmail(field.value)) {
                    this.showFieldError(field, 'Email inválido');
                    return false;
                }
                break;
            case 'url':
                if (field.value && !this.isValidUrl(field.value)) {
                    this.showFieldError(field, 'URL inválida');
                    return false;
                }
                break;
            case 'number':
                if (field.value && isNaN(field.value)) {
                    this.showFieldError(field, 'Valor numérico inválido');
                    return false;
                }
                break;
        }

        this.showFieldSuccess(field);
        return true;
    }

    showFieldError(field, message) {
        const fieldGroup = field.closest('.input-group');
        fieldGroup.classList.remove('success');
        fieldGroup.classList.add('error');
        
        let errorElement = fieldGroup.querySelector('.error-message');
        if (!errorElement) {
            errorElement = document.createElement('span');
            errorElement.className = 'error-message';
            fieldGroup.appendChild(errorElement);
        }
        errorElement.textContent = message;
    }

    showFieldSuccess(field) {
        const fieldGroup = field.closest('.input-group');
        fieldGroup.classList.remove('error');
        fieldGroup.classList.add('success');
    }

    clearFieldError(field) {
        const fieldGroup = field.closest('.input-group');
        fieldGroup.classList.remove('error', 'success');
        
        const errorElement = fieldGroup.querySelector('.error-message');
        if (errorElement) {
            errorElement.remove();
        }
    }

    isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    isValidUrl(url) {
        try {
            new URL(url);
            return true;
        } catch {
            return false;
        }
    }

    collectFormData() {
        const form = document.getElementById('projectCreationForm');
        const formData = new FormData(form);
        const data = {};

        // Basic form data
        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }

        // Add user as coordinator
        const user = SessionManager.getUser();
        data.coordinator = user.name || user.email;

        // Handle checkboxes
        data.scholarshipAvailable = document.getElementById('scholarshipAvailable').checked;

        // Handle type-specific fields properly
        const projectType = data.type;
        if (projectType === 'research') {
            // For research projects, get the specific research fields
            data.objective = document.getElementById('researchObjective').value || '';
            data.justification = document.getElementById('researchJustification').value || '';
            data.discipline = document.getElementById('researchDiscipline').value || '';
        } else if (projectType === 'educational') {
            // For educational projects, get the specific educational fields
            data.slots = document.getElementById('educationalSlots').value ? parseInt(document.getElementById('educationalSlots').value) : null;
            data.course = document.getElementById('educationalCourse').value || '';
            data.justification = document.getElementById('educationalJustification').value || '';
        } else if (projectType === 'extension') {
            // For extension projects, get the specific extension fields
            data.slots = document.getElementById('extensionSlots').value ? parseInt(document.getElementById('extensionSlots').value) : null;
            data.targetAudience = document.getElementById('targetAudience').value || '';
            data.selectionProcess = document.getElementById('selectionProcess').value || '';
        }

        // Convert numeric fields
        if (data.salary) data.salary = parseFloat(data.salary);
        if (data.scholarshipQuantity) data.scholarshipQuantity = parseInt(data.scholarshipQuantity);
        if (data.complementHours) data.complementHours = data.complementHours.toString();

        // Generate unique ID for the project
        data.id = Date.now();
        data.typeId = `${data.type.toUpperCase()}_${data.id}`;

        // Set default image
        data.image = 'default-project.jpg';

        console.log('📋 Form data collected:', data);
        return data;
    }

    validateFormData(data) {
        const errors = [];

        // Required fields validation
        const requiredFields = ['title', 'description', 'type', 'duration', 'timeline', 'requirements'];
        
        requiredFields.forEach(field => {
            if (!data[field] || !data[field].trim()) {
                errors.push(`Campo obrigatório: ${this.getFieldLabel(field)}`);
            }
        });

        // Type-specific validations
        if (data.type === 'research') {
            if (!data.objective || !data.objective.trim()) {
                errors.push('Objetivo da pesquisa é obrigatório para projetos de pesquisa');
            }
            if (!data.justification || !data.justification.trim()) {
                errors.push('Justificativa é obrigatória para projetos de pesquisa');
            }
            if (!data.discipline || !data.discipline.trim()) {
                errors.push('Disciplina é obrigatória para projetos de pesquisa');
            }
        }

        if (data.type === 'educational') {
            if (!data.justification || !data.justification.trim()) {
                errors.push('Justificativa é obrigatória para projetos educacionais');
            }
            if (!data.course || !data.course.trim()) {
                errors.push('Curso é obrigatório para projetos educacionais');
            }
            if (!data.slots || data.slots <= 0) {
                errors.push('Número de vagas deve ser maior que zero');
            }
        }

        // Extension projects don't require justification field

        // Scholarship validation
        if (data.scholarshipAvailable) {
            if (!data.salary || data.salary <= 0) {
                errors.push('Valor da bolsa deve ser maior que zero');
            }
            if (!data.scholarshipQuantity || data.scholarshipQuantity <= 0) {
                errors.push('Quantidade de bolsas deve ser maior que zero');
            }
        }

        console.log('🔍 Validation errors found:', errors);
        return errors;
    }

    getFieldLabel(fieldName) {
        const labels = {
            title: 'Título',
            description: 'Descrição',
            type: 'Tipo de Projeto',
            duration: 'Duração',
            timeline: 'Cronograma',
            requirements: 'Requisitos'
        };
        return labels[fieldName] || fieldName;
    }

    previewProject() {
        console.log('👁️ Generating project preview...');
        
        const data = this.collectFormData();
        const errors = this.validateFormData(data);

        if (errors.length > 0) {
            alert('Por favor, corrija os seguintes erros:\n\n' + errors.join('\n'));
            return;
        }

        this.currentProject = data;
        this.showPreviewModal(data);
    }

    showPreviewModal(data) {
        const modal = document.getElementById('previewModal');
        const content = document.getElementById('previewContent');
        
        content.innerHTML = this.generatePreviewHTML(data);
        modal.style.display = 'flex';
    }

    generatePreviewHTML(data) {
        const user = SessionManager.getUser();
        
        return `
            <div class="preview-project">
                <div class="preview-section">
                    <h3>📋 Informações Básicas</h3>
                    <p><span class="label">Título:</span> ${data.title}</p>
                    ${data.subtitle ? `<p><span class="label">Subtítulo:</span> ${data.subtitle}</p>` : ''}
                    <p><span class="label">Tipo:</span> ${this.getProjectTypeLabel(data.type)}</p>
                    <p><span class="label">Coordenador:</span> ${data.coordinator}</p>
                    <p><span class="label">Duração:</span> ${data.duration}</p>
                    <p><span class="label">Cronograma:</span> ${data.timeline}</p>
                    <p><span class="label">Descrição:</span> ${data.description}</p>
                </div>

                <div class="preview-section">
                    <h3>📅 Requisitos e Informações</h3>
                    <p><span class="label">Requisitos:</span> ${data.requirements}</p>
                    ${data.complementHours ? `<p><span class="label">Horas Complementares:</span> ${data.complementHours}h</p>` : ''}
                    ${data.externalLink ? `<p><span class="label">Link Externo:</span> <a href="${data.externalLink}" target="_blank">${data.externalLink}</a></p>` : ''}
                </div>

                ${data.scholarshipAvailable ? `
                <div class="preview-section">
                    <h3>💰 Informações da Bolsa</h3>
                    <p><span class="label">Valor:</span> R$ ${data.salary.toFixed(2)}</p>
                    <p><span class="label">Quantidade:</span> ${data.scholarshipQuantity} bolsa(s)</p>
                    ${data.scholarshipType ? `<p><span class="label">Tipo:</span> ${data.scholarshipType}</p>` : ''}
                </div>
                ` : ''}

                ${this.generateTypeSpecificPreview(data)}
            </div>
        `;
    }

    generateTypeSpecificPreview(data) {
        switch(data.type) {
            case 'research':
                return `
                    <div class="preview-section">
                        <h3>🔬 Detalhes da Pesquisa</h3>
                        <p><span class="label">Objetivo:</span> ${data.objective}</p>
                        <p><span class="label">Justificativa:</span> ${data.justification}</p>
                        <p><span class="label">Disciplina:</span> ${data.discipline}</p>
                    </div>
                `;
            case 'educational':
                return `
                    <div class="preview-section">
                        <h3>📚 Detalhes Educacionais</h3>
                        ${data.slots ? `<p><span class="label">Vagas:</span> ${data.slots}</p>` : ''}
                        ${data.course ? `<p><span class="label">Curso:</span> ${data.course}</p>` : ''}
                        ${data.justification ? `<p><span class="label">Justificativa:</span> ${data.justification}</p>` : ''}
                    </div>
                `;
            case 'extension':
                return `
                    <div class="preview-section">
                        <h3>🌍 Detalhes da Extensão</h3>
                        ${data.slots ? `<p><span class="label">Vagas:</span> ${data.slots}</p>` : ''}
                        ${data.targetAudience ? `<p><span class="label">Público-alvo:</span> ${data.targetAudience}</p>` : ''}
                        ${data.selectionProcess ? `<p><span class="label">Processo Seletivo:</span> ${data.selectionProcess}</p>` : ''}
                    </div>
                `;
            default:
                return '';
        }
    }

    getProjectTypeLabel(type) {
        const types = {
            research: 'Pesquisa',
            educational: 'Educacional',
            extension: 'Extensão'
        };
        return types[type] || type;
    }

    closePreview() {
        document.getElementById('previewModal').style.display = 'none';
    }

    handleFormSubmission() {
        console.log('📝 Handling form submission...');
        
        if (!this.currentProject) {
            // Generate data if not previewed
            const data = this.collectFormData();
            const errors = this.validateFormData(data);

            if (errors.length > 0) {
                alert('Por favor, corrija os seguintes erros:\n\n' + errors.join('\n'));
                return;
            }

            this.currentProject = data;
        }

        this.submitProject();
    }

    async submitProject() {
        console.log('🚀 Submitting project...');
        this.showLoading();
        this.closePreview();

        try {
            // Create the specific project type
            const result = await this.createProjectByType(this.currentProject);

            if (result.success) {
                console.log('✅ Project created successfully:', result.data);
                
                // If this project was created from an adopted idea, add the original student as participant
                if (this.adoptedIdeaStudent && this.adoptedIdeaStudent.id) {
                    console.log('👤 Adding original student as participant:', this.adoptedIdeaStudent);
                    await this.addStudentAsParticipant(result.data.id, this.adoptedIdeaStudent);
                }
                
                this.hideLoading();
                this.showSuccessModal();
                this.clearAutoSave();
                
                // Store the created project for viewing
                this.createdProject = result.data;
            } else {
                console.error('❌ Project creation failed:', result.error);
                this.hideLoading();
                this.showErrorMessage('Erro ao criar projeto: ' + (result.error || 'Erro desconhecido'));
            }
        } catch (error) {
            console.error('❌ Error creating project:', error);
            this.hideLoading();
            
            // Provide specific error messages based on the error type
            let errorMessage = 'Erro interno do sistema. ';
            
            if (error.message && error.message.includes('fetch')) {
                errorMessage = 'Erro de conexão com o servidor. Projeto será criado localmente. ';
                // Still try to proceed with mock data
                this.showSuccessModal();
                this.clearAutoSave();
                return;
            } else if (error.name === 'TypeError') {
                errorMessage = 'Erro de configuração. Verifique os dados e tente novamente. ';
            }
            
            this.showErrorMessage(errorMessage + 'Tente novamente em alguns momentos.');
        }
    }

    async addStudentAsParticipant(projectId, student) {
        try {
            console.log(`👤 Adding student ${student.name} (ID: ${student.id}) as participant to project ${projectId}`);
            
            // Create participant data
            const participantData = {
                studentId: student.id,
                name: student.name,
                role: 'participant', // or 'member' depending on your system
                status: 'accepted' // Auto-accept since this is from idea adoption
            };
            
            // Add participant to project (you may need to adjust this based on your API)
            const result = await ParticipantAPI.addToProject(projectId, participantData);
            
            if (result.success) {
                console.log('✅ Student added as participant successfully');
                this.showNotification(`${student.name} foi adicionado automaticamente como participante do projeto.`, 'success');
            } else {
                console.warn('⚠️ Failed to add student as participant:', result.error);
                this.showNotification(`Projeto criado, mas não foi possível adicionar ${student.name} automaticamente como participante.`, 'warning');
            }
        } catch (error) {
            console.error('❌ Error adding student as participant:', error);
            this.showNotification(`Projeto criado, mas erro ao adicionar ${student.name} como participante.`, 'warning');
        }
    }
    
    showErrorMessage(message) {
        alert('⚠️ ' + message);
    }

    async createProjectByType(data) {
        console.log('🚀 Creating project of type:', data.type);
        console.log('📋 Project data:', data);
        
        // Validate project type
        const projectType = data.type ? data.type.toLowerCase() : null;
        if (!projectType || !['research', 'educational', 'extension'].includes(projectType)) {
            return {
                success: false,
                error: 'Tipo de projeto inválido. Tipos aceitos: research, educational, extension'
            };
        }
        
        try {
            // Prepare data specific to project type
            let projectData;
            
            switch (projectType) {
                case 'research':
                    console.log('🔬 Preparing Research Project data...');
                    projectData = this.prepareResearchProjectData(data);
                    break;
                    
                case 'educational':
                    console.log('🎓 Preparing Educational Project data...');
                    projectData = this.prepareEducationalProjectData(data);
                    break;
                    
                case 'extension':
                    console.log('🌍 Preparing Extension Project data...');
                    projectData = this.prepareExtensionProjectData(data);
                    break;
                    
                default:
                    console.log('📝 Preparing General Project data...');
                    projectData = this.prepareProjectDataForAPI(data);
                    break;
            }
            
            console.log('📡 Attempting API call with prepared data:', projectData);
            
            const result = await ProjectAPI.create(projectData);
            console.log('✅ API Response:', result);
            
            if (result.success) {
                return result;
            } else {
                console.error('❌ API returned error:', result.error);
                return {
                    success: false,
                    error: result.error || 'Erro na resposta da API'
                };
            }
        } catch (error) {
            console.error('❌ API Error:', error);
            
            // If we're in development and there's a connection error, return a more informative error
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                if (error.message && error.message.includes('fetch')) {
                    return {
                        success: false,
                        error: 'Não foi possível conectar ao servidor backend. Verifique se o servidor está rodando na porta 8080.\n\nDetalhes: ' + error.message
                    };
                }
            }
            
            return {
                success: false,
                error: 'Erro de conexão com o servidor: ' + error.message
            };
        }
    }

    prepareProjectDataForAPI(data) {
        // Remove undefined fields and prepare for API
        const cleanData = {};
        
        Object.keys(data).forEach(key => {
            if (data[key] !== undefined && data[key] !== null && data[key] !== '') {
                cleanData[key] = data[key];
            }
        });

        return cleanData;
    }

    prepareResearchProjectData(data) {
        console.log('🔬 Preparing data for Research Project...');
        
        const baseData = this.prepareProjectDataForAPI(data);
        
        // Research-specific fields
        const researchData = {
            ...baseData,
            type: 'research',
            // Research projects need objective, justification, and discipline
            objective: data.objective || '',
            justification: data.justification || '',
            discipline: data.discipline || ''
        };
        
        console.log('🔬 Research project data prepared:', researchData);
        return researchData;
    }

    prepareEducationalProjectData(data) {
        console.log('🎓 Preparing data for Educational Project...');
        
        const baseData = this.prepareProjectDataForAPI(data);
        
        // Educational-specific fields
        const educationalData = {
            ...baseData,
            type: 'Teaching', // Map frontend 'educational' to database 'Teaching'
            // Educational projects need justification, course, and slots
            justification: data.justification || '',
            course: data.course || data.discipline || '', // Use course or fallback to discipline
            slots: data.slots || data.complementHours || 0 // Use slots or fallback to complementHours
        };
        
        console.log('🎓 Educational project data prepared:', educationalData);
        return educationalData;
    }

    prepareExtensionProjectData(data) {
        console.log('🌍 Preparing data for Extension Project...');
        
        const baseData = this.prepareProjectDataForAPI(data);
        
        // Extension-specific fields
        const extensionData = {
            ...baseData,
            type: 'extension',
            // Extension projects need targetAudience, selectionProcess, and slots
            targetAudience: data.targetAudience || '',
            selectionProcess: data.selectionProcess || '',
            slots: data.slots || 0
        };
        
        console.log('🌍 Extension project data prepared:', extensionData);
        return extensionData;
    }

    showLoading() {
        document.getElementById('loadingOverlay').style.display = 'flex';
    }

    hideLoading() {
        document.getElementById('loadingOverlay').style.display = 'none';
    }

    showSuccessModal() {
        document.getElementById('successModal').style.display = 'flex';
    }

    viewProject() {
        // Store the created project in session storage
        const projectToView = this.createdProject || this.currentProject;
        sessionStorage.setItem('selectedProject', JSON.stringify(projectToView));
        console.log('📱 Navigating to view project:', projectToView.title);
        window.location.href = '../project page/project.html';
    }

    createAnother() {
        document.getElementById('successModal').style.display = 'none';
        this.resetForm();
        this.currentProject = null;
    }

    goToMyProjects() {
        window.location.href = '../userProject/myProjects.html';
    }

    resetForm() {
        document.getElementById('projectCreationForm').reset();
        document.getElementById('typeSpecificFields').style.display = 'none';
        document.getElementById('scholarshipDetails').style.display = 'none';
        
        // Clear validation states
        document.querySelectorAll('.input-group').forEach(group => {
            group.classList.remove('error', 'success');
        });
        
        // Remove error messages
        document.querySelectorAll('.error-message').forEach(msg => {
            msg.remove();
        });
        
        console.log('📝 Form reset');
    }

    cancelCreation() {
        if (confirm('Tem certeza que deseja cancelar? Todos os dados não salvos serão perdidos.')) {
            window.location.href = '../../index.html';
        }
    }

    // Auto-save functionality
    autoSave() {
        try {
            const data = this.collectFormData();
            localStorage.setItem('project_draft', JSON.stringify(data));
            console.log('💾 Auto-save completed');
        } catch (error) {
            console.warn('Auto-save failed:', error);
        }
    }

    loadDraft() {
        try {
            const draft = localStorage.getItem('project_draft');
            if (draft) {
                const data = JSON.parse(draft);
                this.populateForm(data);
                console.log('📄 Draft loaded');
                return true;
            }
        } catch (error) {
            console.warn('Failed to load draft:', error);
        }
        return false;
    }

    clearAutoSave() {
        localStorage.removeItem('project_draft');
        console.log('🗑️ Auto-save cleared');
    }

    populateForm(data) {
        Object.keys(data).forEach(key => {
            const element = document.getElementById(key) || document.querySelector(`[name="${key}"]`);
            if (element) {
                if (element.type === 'checkbox') {
                    element.checked = data[key];
                } else {
                    element.value = data[key];
                }
            }
        });

        // Trigger change events
        if (data.type) {
            this.handleProjectTypeChange(data.type);
        }
        if (data.scholarshipAvailable) {
            this.handleScholarshipChange(true);
        }
    }

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <span class="notification-message">${message}</span>
                <button class="notification-close">&times;</button>
            </div>
        `;
        
        // Add styles
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${type === 'success' ? '#28a745' : type === 'warning' ? '#ffc107' : type === 'error' ? '#dc3545' : '#007bff'};
            color: ${type === 'warning' ? '#000' : '#fff'};
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            z-index: 3000;
            animation: slideInRight 0.3s ease-out;
        `;
        
        // Add animation CSS if not already added
        if (!document.querySelector('#notification-animations')) {
            const style = document.createElement('style');
            style.id = 'notification-animations';
            style.textContent = `
                @keyframes slideInRight {
                    from {
                        opacity: 0;
                        transform: translateX(100%);
                    }
                    to {
                        opacity: 1;
                        transform: translateX(0);
                    }
                }
                .notification-content {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }
                .notification-close {
                    background: none;
                    border: none;
                    color: inherit;
                    font-size: 18px;
                    cursor: pointer;
                    padding: 0;
                    margin-left: 10px;
                }
                .notification-close:hover {
                    opacity: 0.8;
                }
            `;
            document.head.appendChild(style);
        }
        
        document.body.appendChild(notification);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 5000);
        
        // Manual close
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
        });
    }
}

// Modal functionality accessible globally
window.projectCreationManager = null;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Initializing Project Creation Manager...');
    window.projectCreationManager = new ProjectCreationManager();
    
    // Check for draft on load
    if (window.projectCreationManager.loadDraft()) {
        if (confirm('Foi encontrado um rascunho de projeto. Deseja continuar de onde parou?')) {
            // Draft is already loaded
        } else {
            window.projectCreationManager.clearAutoSave();
            window.projectCreationManager.resetForm();
        }
    }
});

console.log('✅ ProjectCreationManager class loaded');
