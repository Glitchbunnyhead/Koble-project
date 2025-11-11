// ===== Idea Creation Manager =====
class IdeaCreationManager {
    constructor() {
        this.currentIdea = null;
        this.formData = {};
        this.init();
    }

    init() {
        console.log('🚀 IdeaCreationManager initializing...');
        
        // Check authentication and user type
        if (!this.validateUserAccess()) {
            return;
        }

        this.setupEventListeners();
        this.setupFormValidation();
        this.loadUserInfo();
        
        console.log('✅ IdeaCreationManager initialized successfully');
    }

    validateUserAccess() {
        const user = SessionManager.getUser();
        
        if (!user) {
            alert('Você precisa estar logado para criar ideias.');
            window.location.href = '../../pages/login and sign in/login.html';
            return false;
        }

        // Based on RF-013: Only students should publish ideas
        // Teachers can view and adopt ideas, but students create them
        if (user.userType !== 'student') {
            alert('Apenas estudantes podem criar ideias. Professores podem explorar e adotar ideias existentes.');
            window.location.href = '../ideas/ideas.html';
            return false;
        }

        return true;
    }

    loadUserInfo() {
        const user = SessionManager.getUser();
        if (user) {
            // Auto-fill proposer field
            const proposerField = document.getElementById('ideaProposer');
            if (proposerField) {
                proposerField.value = user.name || user.email || 'Usuário Logado';
            }
        }
    }

    setupEventListeners() {
        // Form submission
        const form = document.getElementById('ideaCreationForm');
        if (form) {
            form.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }

        // Preview button
        const previewBtn = document.getElementById('previewBtn');
        if (previewBtn) {
            previewBtn.addEventListener('click', () => this.showPreview());
        }

        // Modal controls
        this.setupModalControls();

        // Real-time validation
        this.setupRealTimeValidation();
    }

    setupModalControls() {
        // Preview modal controls
        const closePreviewBtn = document.getElementById('closePreviewBtn');
        const confirmCreateBtn = document.getElementById('confirmCreateBtn');
        const previewModal = document.getElementById('previewModal');
        
        if (closePreviewBtn) {
            closePreviewBtn.addEventListener('click', () => this.closeModal('previewModal'));
        }
        
        if (confirmCreateBtn) {
            confirmCreateBtn.addEventListener('click', () => this.createIdeaFromPreview());
        }

        // Close modal on backdrop click
        if (previewModal) {
            previewModal.addEventListener('click', (e) => {
                if (e.target === previewModal) {
                    this.closeModal('previewModal');
                }
            });
        }

        // Close buttons
        document.querySelectorAll('.close').forEach(closeBtn => {
            closeBtn.addEventListener('click', (e) => {
                const modal = e.target.closest('.modal');
                if (modal) {
                    this.closeModal(modal.id);
                }
            });
        });

        // Success modal buttons
        const viewIdeasBtn = document.getElementById('viewIdeasBtn');
        const createAnotherBtn = document.getElementById('createAnotherBtn');
        
        if (viewIdeasBtn) {
            viewIdeasBtn.addEventListener('click', () => {
                // Navigate to ideas list page (to be created)
                window.location.href = '../ideas/ideas.html';
            });
        }
        
        if (createAnotherBtn) {
            createAnotherBtn.addEventListener('click', () => {
                this.closeModal('successModal');
                this.resetForm();
            });
        }
    }

    setupRealTimeValidation() {
        const requiredFields = ['ideaTitle', 'ideaArea', 'ideaType', 'ideaDescription', 'ideaAim', 'ideaJustification', 'ideaTargetAudience'];
        
        requiredFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field) {
                field.addEventListener('blur', () => this.validateField(fieldId));
                field.addEventListener('input', () => this.clearFieldError(fieldId));
            }
        });
    }

    setupFormValidation() {
        // Add character counters for text areas
        const textAreas = document.querySelectorAll('textarea');
        textAreas.forEach(textarea => {
            this.addCharacterCounter(textarea);
        });
    }

    addCharacterCounter(textarea) {
        const maxLength = textarea.hasAttribute('maxlength') ? parseInt(textarea.getAttribute('maxlength')) : 1000;
        
        const counter = document.createElement('div');
        counter.className = 'char-counter';
        counter.textContent = `0/${maxLength}`;
        
        textarea.parentNode.appendChild(counter);
        
        textarea.addEventListener('input', () => {
            const currentLength = textarea.value.length;
            counter.textContent = `${currentLength}/${maxLength}`;
            
            if (currentLength > maxLength * 0.9) {
                counter.className = 'char-counter warning';
            } else if (currentLength > maxLength) {
                counter.className = 'char-counter error';
            } else {
                counter.className = 'char-counter';
            }
        });
    }

    validateField(fieldId) {
        const field = document.getElementById(fieldId);
        const inputGroup = field.closest('.input-group');
        
        if (!field.value.trim()) {
            this.showFieldError(inputGroup, 'Este campo é obrigatório');
            return false;
        }
        
        // Specific validations
        switch (fieldId) {
            case 'ideaTitle':
                if (field.value.length < 5) {
                    this.showFieldError(inputGroup, 'O título deve ter pelo menos 5 caracteres');
                    return false;
                }
                break;
            case 'ideaDescription':
                if (field.value.length < 50) {
                    this.showFieldError(inputGroup, 'A descrição deve ter pelo menos 50 caracteres');
                    return false;
                }
                break;
        }
        
        this.showFieldSuccess(inputGroup);
        return true;
    }

    showFieldError(inputGroup, message) {
        inputGroup.classList.remove('success');
        inputGroup.classList.add('error');
        
        let errorMsg = inputGroup.querySelector('.error-message');
        if (!errorMsg) {
            errorMsg = document.createElement('div');
            errorMsg.className = 'error-message';
            inputGroup.appendChild(errorMsg);
        }
        errorMsg.textContent = message;
    }

    showFieldSuccess(inputGroup) {
        inputGroup.classList.remove('error');
        inputGroup.classList.add('success');
        
        const errorMsg = inputGroup.querySelector('.error-message');
        if (errorMsg) {
            errorMsg.style.display = 'none';
        }
    }

    clearFieldError(fieldId) {
        const field = document.getElementById(fieldId);
        const inputGroup = field.closest('.input-group');
        inputGroup.classList.remove('error');
        
        const errorMsg = inputGroup.querySelector('.error-message');
        if (errorMsg) {
            errorMsg.style.display = 'none';
        }
    }

    collectFormData() {
        const user = SessionManager.getUser();
        console.log('🔍 DEBUGGING - collectFormData - Current user:', JSON.stringify(user, null, 2));
        
        // Collect all form field values
        const title = document.getElementById('ideaTitle')?.value?.trim();
        const subtitle = document.getElementById('ideaSubtitle')?.value?.trim();
        const area = document.getElementById('ideaArea')?.value;
        const type = document.getElementById('ideaType')?.value;
        const description = document.getElementById('ideaDescription')?.value?.trim();
        const aim = document.getElementById('ideaAim')?.value?.trim();
        const justification = document.getElementById('ideaJustification')?.value?.trim();
        const targetAudience = document.getElementById('ideaTargetAudience')?.value?.trim();
        const proposer = document.getElementById('ideaProposer')?.value?.trim();
        
        console.log('🔍 DEBUGGING - collectFormData - Raw form values:', {
            title, subtitle, area, type, description, aim, justification, targetAudience, proposer
        });
        
        const formData = {
            title: title || '',
            subtitle: subtitle || '',
            area: area || '',
            type: type || '',
            description: description || '',
            aim: aim || '',
            justification: justification || '',
            targetAudience: targetAudience || '',
            proposer: proposer || '',
            // Only students create ideas (RF-013) - use null for teacher_id initially
            teacherId: null, // Will be set when a teacher adopts the idea
            studentId: (user && user.userType === 'student' && user.id) ? user.id : null
        };

        console.log('📋 DEBUGGING - Final collected form data:', JSON.stringify(formData, null, 2));
        console.log('👤 DEBUGGING - Current user info for reference:', JSON.stringify(user, null, 2));
        
        // Validation check
        const requiredFields = ['title', 'area', 'type', 'description', 'aim', 'justification', 'targetAudience'];
        const missingFields = requiredFields.filter(field => !formData[field] || formData[field].trim() === '');
        
        if (missingFields.length > 0) {
            console.warn('⚠️ DEBUGGING - Missing required fields in collectFormData:', missingFields);
        } else {
            console.log('✅ DEBUGGING - All required fields collected successfully');
        }
        
        return formData;
    }

    validateForm() {
        const requiredFields = [
            'ideaTitle', 'ideaArea', 'ideaType', 'ideaDescription', 
            'ideaAim', 'ideaJustification', 'ideaTargetAudience'
        ];
        
        let isValid = true;
        
        requiredFields.forEach(fieldId => {
            if (!this.validateField(fieldId)) {
                isValid = false;
            }
        });
        
        if (!isValid) {
            this.showError('Por favor, corrija os erros no formulário antes de continuar.');
        }
        
        return isValid;
    }

    handleFormSubmit(e) {
        e.preventDefault();
        console.log('📝 Form submitted');
        
        if (this.validateForm()) {
            this.formData = this.collectFormData();
            this.showPreview();
        }
    }

    showPreview() {
        console.log('👁️ Showing preview...');
        
        // Collect form data first, then validate
        this.formData = this.collectFormData();
        console.log('📋 Form data for preview:', this.formData);
        
        // Validate required fields for preview
        const requiredFields = ['title', 'area', 'type', 'description', 'aim', 'justification', 'targetAudience'];
        const missingFields = [];
        
        requiredFields.forEach(field => {
            if (!this.formData[field] || this.formData[field].trim() === '') {
                missingFields.push(field);
            }
        });
        
        if (missingFields.length > 0) {
            alert(`Por favor, preencha os seguintes campos obrigatórios: ${missingFields.join(', ')}`);
            return;
        }
        
        this.generatePreviewContent();
        this.showModal('previewModal');
    }

    generatePreviewContent() {
        const previewContent = document.getElementById('previewContent');
        
        if (!previewContent) {
            console.error('❌ Preview content element not found');
            return;
        }
        
        console.log('🔍 Generating preview with data:', this.formData);
        
        previewContent.innerHTML = `
            <div class="preview-section">
                <h3>📋 Informações Básicas</h3>
                <p><span class="label">Título:</span> ${this.escapeHtml(this.formData.title || 'Não informado')}</p>
                ${this.formData.subtitle ? `<p><span class="label">Subtítulo:</span> ${this.escapeHtml(this.formData.subtitle)}</p>` : ''}
                <p><span class="label">Área:</span> ${this.escapeHtml(this.formData.area || 'Não informada')}</p>
                <p><span class="label">Tipo:</span> ${this.getTypeLabel(this.formData.type)}</p>
                <p><span class="label">Propositor:</span> ${this.escapeHtml(this.formData.proposer || 'Não informado')}</p>
            </div>
            
            <div class="preview-section">
                <h3>📝 Descrição</h3>
                <p>${this.escapeHtml(this.formData.description || 'Nenhuma descrição fornecida')}</p>
            </div>
            
            <div class="preview-section">
                <h3>🎯 Objetivo</h3>
                <p>${this.escapeHtml(this.formData.aim || 'Nenhum objetivo especificado')}</p>
            </div>
            
            <div class="preview-section">
                <h3>💡 Justificativa</h3>
                <p>${this.escapeHtml(this.formData.justification || 'Nenhuma justificativa fornecida')}</p>
            </div>
            
            <div class="preview-section">
                <h3>👥 Público-Alvo</h3>
                <p>${this.escapeHtml(this.formData.targetAudience || 'Público-alvo não especificado')}</p>
            </div>
        `;
        
        console.log('✅ Preview content generated successfully');
    }

    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    getTypeLabel(type) {
        const types = {
            'Research': 'Pesquisa',
            'Extension': 'Extensão',
            'Teaching': 'Ensino'
        };
        return types[type] || type;
    }

    async createIdeaFromPreview() {
        this.closeModal('previewModal');
        await this.createIdea();
    }

    async createIdea() {
        if (!this.formData) {
            this.formData = this.collectFormData();
        }
        
        console.log('🚀 Creating idea with data:', this.formData);
        console.log('🔍 DEBUGGING - Full form data structure:', JSON.stringify(this.formData, null, 2));
        
        // Additional validation before sending
        const user = SessionManager.getUser();
        console.log('🔍 DEBUGGING - Current user:', JSON.stringify(user, null, 2));
        
        if (!user || user.userType !== 'student') {
            console.error('❌ DEBUGGING - User validation failed:', { user, userType: user?.userType });
            this.showError('Apenas estudantes podem criar ideias.');
            return;
        }
        
        // Check required fields
        const requiredFields = ['title', 'area', 'type', 'description', 'aim', 'justification', 'targetAudience'];
        const missingFields = requiredFields.filter(field => !this.formData[field] || this.formData[field].trim() === '');
        
        if (missingFields.length > 0) {
            console.error('❌ DEBUGGING - Missing required fields:', missingFields);
            this.showError(`Campos obrigatórios em falta: ${missingFields.join(', ')}`);
            return;
        }
        
        console.log('✅ DEBUGGING - Validation passed, proceeding with API call');
        this.showLoading();
        
        try {
            console.log('🔍 DEBUGGING - About to call IdeaAPI.create...');
            const result = await IdeaAPI.create(this.formData);
            console.log('✅ DEBUGGING - Idea creation result:', JSON.stringify(result, null, 2));
            
            this.hideLoading();
            
            if (result && result.success) {
                console.log('✅ DEBUGGING - Success! Idea created:', result.data);
                this.currentIdea = result.data;
                this.showSuccessModal();
            } else {
                console.error('❌ DEBUGGING - API returned failure:', result);
                const errorMessage = result?.error || result?.message || 'Erro desconhecido ao criar ideia';
                this.showError(`Erro ao criar ideia: ${errorMessage}`);
            }
        } catch (error) {
            console.error('❌ DEBUGGING - Exception during idea creation:', error);
            console.error('❌ DEBUGGING - Error stack:', error.stack);
            this.hideLoading();
            this.showError(`Erro ao conectar com o servidor: ${error.message}`);
        }
    }

    showSuccessModal() {
        this.showModal('successModal');
    }

    resetForm() {
        const form = document.getElementById('ideaCreationForm');
        if (form) {
            form.reset();
            
            // Clear validation states
            document.querySelectorAll('.input-group').forEach(group => {
                group.classList.remove('error', 'success');
            });
            
            // Reset user info
            this.loadUserInfo();
        }
        
        this.formData = {};
        this.currentIdea = null;
    }

    showModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        }
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = '';
        }
    }

    showLoading() {
        const loading = document.getElementById('loadingOverlay');
        if (loading) {
            loading.style.display = 'flex';
        }
    }

    hideLoading() {
        const loading = document.getElementById('loadingOverlay');
        if (loading) {
            loading.style.display = 'none';
        }
    }

    showError(message) {
        // Create and show error notification
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-notification';
        errorDiv.innerHTML = `
            <div class="error-content">
                <span class="error-icon">⚠️</span>
                <span class="error-message">${message}</span>
                <button class="error-close">&times;</button>
            </div>
        `;
        
        // Add styles
        errorDiv.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #dc3545;
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(220, 53, 69, 0.3);
            z-index: 3000;
            animation: slideInRight 0.3s ease-out;
        `;
        
        document.body.appendChild(errorDiv);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (errorDiv.parentNode) {
                errorDiv.remove();
            }
        }, 5000);
        
        // Manual close
        errorDiv.querySelector('.error-close').addEventListener('click', () => {
            errorDiv.remove();
        });
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    console.log('🌟 Idea Creation page loaded');
    window.ideaCreationManager = new IdeaCreationManager();
});

// Add CSS for error notifications
const style = document.createElement('style');
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
    
    .error-content {
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .error-close {
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        cursor: pointer;
        padding: 0;
        margin-left: 10px;
    }
    
    .error-close:hover {
        opacity: 0.8;
    }
`;
document.head.appendChild(style);