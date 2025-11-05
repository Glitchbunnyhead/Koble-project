// ===== Ideas Manager =====
class IdeasManager {
    constructor() {
        this.allIdeas = [];
        this.filteredIdeas = [];
        this.currentView = 'grid';
        this.currentSort = 'newest';
        this.currentUser = null;
        this.viewMode = 'all'; // 'all' or 'my'
        this.filters = {
            search: '',
            area: '',
            type: ''
        };
        this.init();
    }

    init() {
        console.log('🚀 IdeasManager initializing...');
        
        // Check authentication
        SessionManager.checkAuthRequirement();
        
        // Get current user
        this.currentUser = SessionManager.getUser();
        console.log('👤 Current user:', this.currentUser);
        
        this.setupUserInterface();
        this.setupEventListeners();
        this.loadIdeas();
        
        console.log('✅ IdeasManager initialized successfully');
    }

    setupUserInterface() {
        // Show/hide buttons based on user role
        const createBtn = document.getElementById('createIdeaBtn');
        const myIdeasBtn = document.getElementById('myIdeasBtn');
        const createFirstBtn = document.getElementById('createFirstIdeaBtn');
        
        // Only students can create ideas (RF-013: Students publish ideas)
        if (this.currentUser && this.currentUser.userType === 'student') {
            if (createBtn) createBtn.style.display = 'block';
            if (myIdeasBtn) myIdeasBtn.style.display = 'block';
            if (createFirstBtn) createFirstBtn.style.display = 'block';
        } else {
            // Hide create buttons for teachers and other users
            if (createBtn) createBtn.style.display = 'none';
            if (myIdeasBtn) myIdeasBtn.style.display = 'none';
            if (createFirstBtn) createFirstBtn.style.display = 'none';
        }
        
        // Update page title based on view mode
        this.updatePageTitle();
    }

    updatePageTitle() {
        const pageHeader = document.querySelector('.page-header h1');
        const pageSubtitle = document.querySelector('.page-subtitle');
        
        if (this.viewMode === 'my') {
            if (pageHeader) pageHeader.textContent = '📝 Minhas Ideias';
            if (pageSubtitle) pageSubtitle.textContent = 'Gerencie suas ideias e acompanhe o interesse dos professores';
        } else {
            if (pageHeader) pageHeader.textContent = '💡 Explorar Ideias';
            if (pageSubtitle) pageSubtitle.textContent = 'Descubra ideias inovadoras e encontre oportunidades de colaboração';
        }
    }

    setupEventListeners() {
        // Search functionality
        const searchInput = document.getElementById('searchInput');
        const searchBtn = document.getElementById('searchBtn');
        
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.filters.search = e.target.value;
                this.debounceFilter();
            });
            
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.applyFilters();
                }
            });
        }
        
        if (searchBtn) {
            searchBtn.addEventListener('click', () => this.applyFilters());
        }

        // Filter controls
        const areaFilter = document.getElementById('areaFilter');
        const typeFilter = document.getElementById('typeFilter');
        
        if (areaFilter) {
            areaFilter.addEventListener('change', (e) => {
                this.filters.area = e.target.value;
                this.applyFilters();
            });
        }
        
        if (typeFilter) {
            typeFilter.addEventListener('change', (e) => {
                this.filters.type = e.target.value;
                this.applyFilters();
            });
        }

        // Clear filters
        const clearFiltersBtn = document.getElementById('clearFiltersBtn');
        const clearFiltersEmptyBtn = document.getElementById('clearFiltersEmptyBtn');
        
        [clearFiltersBtn, clearFiltersEmptyBtn].forEach(btn => {
            if (btn) {
                btn.addEventListener('click', () => this.clearFilters());
            }
        });

        // Sort functionality
        const sortSelect = document.getElementById('sortSelect');
        if (sortSelect) {
            sortSelect.addEventListener('change', (e) => {
                this.currentSort = e.target.value;
                this.applySort();
            });
        }

        // View toggle
        const gridViewBtn = document.getElementById('gridViewBtn');
        const listViewBtn = document.getElementById('listViewBtn');
        
        if (gridViewBtn) {
            gridViewBtn.addEventListener('click', () => this.setView('grid'));
        }
        
        if (listViewBtn) {
            listViewBtn.addEventListener('click', () => this.setView('list'));
        }

        // Create idea buttons
        const createIdeaBtn = document.getElementById('createIdeaBtn');
        const createFirstIdeaBtn = document.getElementById('createFirstIdeaBtn');
        
        [createIdeaBtn, createFirstIdeaBtn].forEach(btn => {
            if (btn) {
                btn.addEventListener('click', () => {
                    window.location.href = '../ideaCreation/ideaCreation.html';
                });
            }
        });

        // My Ideas button
        const myIdeasBtn = document.getElementById('myIdeasBtn');
        if (myIdeasBtn) {
            myIdeasBtn.addEventListener('click', () => this.toggleMyIdeas());
        }

        // Modal controls
        this.setupModalControls();

        // Adoption confirmation
        const confirmAdoptionBtn = document.getElementById('confirmAdoptionBtn');
        if (confirmAdoptionBtn) {
            confirmAdoptionBtn.addEventListener('click', () => this.confirmAdoption());
        }

        // Save idea button
        const saveIdeaBtn = document.getElementById('saveIdeaBtn');
        if (saveIdeaBtn) {
            saveIdeaBtn.addEventListener('click', () => this.saveIdeaChanges());
        }
    }

    toggleMyIdeas() {
        this.viewMode = this.viewMode === 'my' ? 'all' : 'my';
        
        // Update button state
        const myIdeasBtn = document.getElementById('myIdeasBtn');
        if (myIdeasBtn) {
            myIdeasBtn.textContent = this.viewMode === 'my' ? '🌐 Todas as Ideias' : '📝 Minhas Ideias';
            myIdeasBtn.classList.toggle('active', this.viewMode === 'my');
        }
        
        this.updatePageTitle();
        this.applyFilters();
    }

    setupModalControls() {
        const modal = document.getElementById('ideaDetailModal');
        const closeBtn = document.getElementById('closeDetailBtn');
        const contactBtn = document.getElementById('contactProposerBtn');
        
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeModal());
        }
        
        if (contactBtn) {
            contactBtn.addEventListener('click', () => this.contactProposer());
        }
        
        // Close modal on backdrop click
        if (modal) {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeModal();
                }
            });
        }
        
        // Close button in header
        document.querySelectorAll('.close').forEach(closeBtn => {
            closeBtn.addEventListener('click', () => this.closeModal());
        });
    }

    // Debounce search input
    debounceFilter() {
        clearTimeout(this.searchTimeout);
        this.searchTimeout = setTimeout(() => {
            this.applyFilters();
        }, 300);
    }

    async loadIdeas() {
        console.log('📥 Loading ideas...');
        this.showLoading();
        
        try {
            const result = await IdeaAPI.getAll();
            console.log('📊 Ideas API result:', result);
            
            if (result.success) {
                this.allIdeas = result.data || [];
                this.filteredIdeas = [...this.allIdeas];
                
                console.log(`✅ Loaded ${this.allIdeas.length} ideas`);
                
                if (this.allIdeas.length === 0) {
                    this.showNoIdeasState();
                } else {
                    this.applySort();
                    this.renderIdeas();
                }
            } else {
                console.error('❌ Failed to load ideas:', result);
                this.showError('Erro ao carregar ideias. Tente novamente.');
                this.showNoIdeasState();
            }
        } catch (error) {
            console.error('❌ Error loading ideas:', error);
            this.showError('Erro ao conectar com o servidor. Verifique sua conexão.');
            this.showNoIdeasState();
        } finally {
            this.hideLoading();
        }
    }

    applyFilters() {
        console.log('🔍 Applying filters:', this.filters, 'View mode:', this.viewMode);
        
        this.filteredIdeas = this.allIdeas.filter(idea => {
            // "My Ideas" filter - only show ideas from current user
            if (this.viewMode === 'my' && this.currentUser) {
                // Check if current user is the proposer (by name or ID)
                const isMyIdea = idea.proposer === this.currentUser.name || 
                               (this.currentUser.userType === 'student' && idea.studentId === this.currentUser.id) ||
                               (this.currentUser.userType === 'teacher' && idea.teacherId === this.currentUser.id);
                               
                if (!isMyIdea) {
                    return false;
                }
            }
            
            // Search filter
            if (this.filters.search) {
                const searchTerm = this.filters.search.toLowerCase();
                const searchableText = [
                    idea.title,
                    idea.description,
                    idea.area,
                    idea.proposer,
                    idea.targetAudience
                ].join(' ').toLowerCase();
                
                if (!searchableText.includes(searchTerm)) {
                    return false;
                }
            }
            
            // Area filter
            if (this.filters.area && idea.area !== this.filters.area) {
                return false;
            }
            
            // Type filter
            if (this.filters.type && idea.type !== this.filters.type) {
                return false;
            }
            
            return true;
        });
        
        console.log(`📊 Filtered to ${this.filteredIdeas.length} ideas`);
        
        this.applySort();
        this.renderIdeas();
    }

    applySort() {
        console.log('📋 Applying sort:', this.currentSort);
        
        this.filteredIdeas.sort((a, b) => {
            switch (this.currentSort) {
                case 'newest':
                    return (b.id || 0) - (a.id || 0);
                case 'oldest':
                    return (a.id || 0) - (b.id || 0);
                case 'title':
                    return (a.title || '').localeCompare(b.title || '');
                case 'area':
                    return (a.area || '').localeCompare(b.area || '');
                case 'type':
                    return (a.type || '').localeCompare(b.type || '');
                default:
                    return 0;
            }
        });
    }

    clearFilters() {
        console.log('🧹 Clearing filters');
        
        // Reset filter values
        this.filters = {
            search: '',
            area: '',
            type: ''
        };
        
        // Reset UI elements
        const searchInput = document.getElementById('searchInput');
        const areaFilter = document.getElementById('areaFilter');
        const typeFilter = document.getElementById('typeFilter');
        
        if (searchInput) searchInput.value = '';
        if (areaFilter) areaFilter.value = '';
        if (typeFilter) typeFilter.value = '';
        
        // Reapply with cleared filters
        this.applyFilters();
    }

    setView(viewType) {
        console.log('👁️ Setting view to:', viewType);
        
        this.currentView = viewType;
        
        // Update UI buttons
        const gridBtn = document.getElementById('gridViewBtn');
        const listBtn = document.getElementById('listViewBtn');
        
        if (gridBtn && listBtn) {
            gridBtn.classList.toggle('active', viewType === 'grid');
            listBtn.classList.toggle('active', viewType === 'list');
        }
        
        // Update container class
        const container = document.getElementById('ideasContainer');
        if (container) {
            container.classList.toggle('list-view', viewType === 'list');
        }
    }

    renderIdeas() {
        const container = document.getElementById('ideasContainer');
        const emptyState = document.getElementById('emptyState');
        const noIdeasState = document.getElementById('noIdeasState');
        
        if (!container) return;
        
        // Hide states
        if (emptyState) emptyState.style.display = 'none';
        if (noIdeasState) noIdeasState.style.display = 'none';
        
        if (this.filteredIdeas.length === 0) {
            container.innerHTML = '';
            container.style.display = 'none';
            
            if (this.allIdeas.length === 0) {
                if (noIdeasState) noIdeasState.style.display = 'block';
            } else {
                if (emptyState) emptyState.style.display = 'block';
            }
            return;
        }
        
        container.style.display = 'grid';
        container.innerHTML = '';
        
        this.filteredIdeas.forEach(idea => {
            const ideaCard = this.createIdeaCard(idea);
            container.appendChild(ideaCard);
        });
        
        console.log(`✅ Rendered ${this.filteredIdeas.length} ideas`);
    }

    createIdeaCard(idea) {
        const card = document.createElement('div');
        card.className = 'idea-card';
        card.addEventListener('click', () => this.showIdeaDetail(idea));
        
        const typeClass = (idea.type || '').toLowerCase();
        const typeLabel = this.getTypeLabel(idea.type);
        
        // Check if current user owns this idea
        const isMyIdea = this.currentUser && (
            idea.proposer === this.currentUser.name || 
            (this.currentUser.userType === 'student' && idea.studentId === this.currentUser.id) ||
            (this.currentUser.userType === 'teacher' && idea.teacherId === this.currentUser.id)
        );

        // Generate action buttons based on user role and ownership
        let actionButtons = '';
        
        if (isMyIdea && this.currentUser.userType === 'student') {
            // Owner can edit/delete
            actionButtons = `
                <button class="action-btn edit-btn" onclick="event.stopPropagation(); window.ideasManager.editIdea(${JSON.stringify(idea).replace(/"/g, '&quot;')})">
                    ✏️ Editar
                </button>
                <button class="action-btn delete-btn" onclick="event.stopPropagation(); window.ideasManager.deleteIdea(${idea.id})">
                    🗑️ Excluir
                </button>
            `;
        } else if (this.currentUser && this.currentUser.userType === 'teacher' && !isMyIdea) {
            // Teachers can adopt ideas (but not their own)
            actionButtons = `
                <button class="action-btn adopt-btn" onclick="event.stopPropagation(); window.ideasManager.adoptIdea(${JSON.stringify(idea).replace(/"/g, '&quot;')})">
                    🎯 Adotar Ideia
                </button>
            `;
        }
        
        actionButtons += `
            <button class="action-btn detail-btn" onclick="event.stopPropagation(); window.ideasManager.showIdeaDetail(${JSON.stringify(idea).replace(/"/g, '&quot;')})">
                👁️ Ver Detalhes
            </button>
        `;
        
        card.innerHTML = `
            <div class="idea-header">
                <h3 class="idea-title">${this.escapeHtml(idea.title || 'Sem título')}</h3>
                ${idea.subtitle ? `<p class="idea-subtitle">${this.escapeHtml(idea.subtitle)}</p>` : ''}
            </div>
            
            <div class="idea-meta">
                <div class="meta-item">
                    <span>📚</span>
                    <span>${this.escapeHtml(idea.area || 'Área não informada')}</span>
                </div>
                <div class="meta-item">
                    <span class="type-badge ${typeClass}">${typeLabel}</span>
                </div>
            </div>
            
            <div class="idea-description">
                ${this.escapeHtml(this.truncateText(idea.description || 'Sem descrição disponível', 120))}
            </div>
            
            <div class="idea-footer">
                <div class="proposer-info">
                    <span>👤</span>
                    <span>${this.escapeHtml(idea.proposer || 'Propositor não informado')}</span>
                    ${isMyIdea ? '<span class="my-idea-badge">Minha Ideia</span>' : ''}
                </div>
                <div class="idea-actions">
                    ${actionButtons}
                </div>
            </div>
        `;
        
        return card;
    }

    truncateText(text, maxLength) {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    }

    showIdeaDetail(idea) {
        console.log('👁️ Showing idea detail:', idea);
        
        const modal = document.getElementById('ideaDetailModal');
        const title = document.getElementById('modalIdeaTitle');
        const content = document.getElementById('ideaDetailContent');
        const actionsContainer = document.getElementById('ideaDetailActions');
        
        if (!modal || !content) return;
        
        // Set title
        if (title) {
            title.textContent = idea.title || 'Detalhes da Ideia';
        }
        
        // Generate content
        const typeLabel = this.getTypeLabel(idea.type);
        
        content.innerHTML = `
            <div class="detail-section">
                <h3>📋 Informações Básicas</h3>
                <p><span class="label">Título:</span> ${this.escapeHtml(idea.title || '')}</p>
                ${idea.subtitle ? `<p><span class="label">Subtítulo:</span> ${this.escapeHtml(idea.subtitle)}</p>` : ''}
                <p><span class="label">Área:</span> ${this.escapeHtml(idea.area || '')}</p>
                <p><span class="label">Tipo:</span> ${typeLabel}</p>
                <p><span class="label">Propositor:</span> ${this.escapeHtml(idea.proposer || '')}</p>
            </div>
            
            <div class="detail-section">
                <h3>📝 Descrição</h3>
                <p>${this.escapeHtml(idea.description || 'Nenhuma descrição disponível.')}</p>
            </div>
            
            <div class="detail-section">
                <h3>🎯 Objetivo</h3>
                <p>${this.escapeHtml(idea.aim || 'Objetivo não especificado.')}</p>
            </div>
            
            <div class="detail-section">
                <h3>💡 Justificativa</h3>
                <p>${this.escapeHtml(idea.justification || 'Justificativa não fornecida.')}</p>
            </div>
            
            <div class="detail-section">
                <h3>👥 Público-Alvo</h3>
                <p>${this.escapeHtml(idea.targetAudience || 'Público-alvo não especificado.')}</p>
            </div>
        `;
        
        // Generate action buttons based on user role and ownership
        const isMyIdea = this.currentUser && (
            idea.proposer === this.currentUser.name || 
            (this.currentUser.userType === 'student' && idea.studentId === this.currentUser.id) ||
            (this.currentUser.userType === 'teacher' && idea.teacherId === this.currentUser.id)
        );

        let actionButtons = `
            <button onclick="window.ideasManager.closeModal()" class="btn-secondary">
                Fechar
            </button>
        `;

        if (isMyIdea && this.currentUser.userType === 'student') {
            // Owner can edit/delete
            actionButtons += `
                <button onclick="window.ideasManager.editIdea(${JSON.stringify(idea).replace(/"/g, '&quot;')})" class="btn-secondary">
                    ✏️ Editar
                </button>
                <button onclick="window.ideasManager.deleteIdea(${idea.id})" class="btn-danger">
                    🗑️ Excluir
                </button>
            `;
        } else if (this.currentUser && this.currentUser.userType === 'teacher' && !isMyIdea) {
            // Teachers can adopt ideas (but not their own)
            actionButtons += `
                <button onclick="window.ideasManager.adoptIdea(${JSON.stringify(idea).replace(/"/g, '&quot;')})" class="btn-primary">
                    🎯 Adotar Ideia
                </button>
            `;
        }

        if (actionsContainer) {
            actionsContainer.innerHTML = actionButtons;
        }
        
        // Store current idea for actions
        this.currentIdea = idea;
        
        // Show modal
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }

    closeModal() {
        const modals = ['ideaDetailModal', 'adoptionModal', 'editIdeaModal'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.style.display = 'none';
            }
        });
        document.body.style.overflow = '';
        this.currentIdea = null;
    }

    // ===== CRUD OPERATIONS =====

    async editIdea(idea) {
        console.log('✏️ Editing idea:', idea);
        
        // Check if current user owns this idea
        const isMyIdea = this.currentUser && (
            idea.proposer === this.currentUser.name || 
            (this.currentUser.userType === 'student' && idea.studentId === this.currentUser.id) ||
            (this.currentUser.userType === 'teacher' && idea.teacherId === this.currentUser.id)
        );

        if (!isMyIdea) {
            this.showNotification('Você só pode editar suas próprias ideias.', 'error');
            return;
        }

        // Only students who created the idea can edit it
        if (this.currentUser.userType !== 'student') {
            this.showNotification('Apenas estudantes podem editar ideias.', 'error');
            return;
        }
        
        this.closeModal();
        
        const modal = document.getElementById('editIdeaModal');
        const form = document.getElementById('editIdeaForm');
        
        if (!modal || !form) return;

        // Populate form with current idea data
        form.innerHTML = `
            <div class="input-group">
                <label for="editTitle">Título *</label>
                <input type="text" id="editTitle" value="${this.escapeHtml(idea.title || '')}" required>
            </div>
            
            <div class="input-group">
                <label for="editSubtitle">Subtítulo</label>
                <input type="text" id="editSubtitle" value="${this.escapeHtml(idea.subtitle || '')}">
            </div>
            
            <div class="input-group">
                <label for="editArea">Área *</label>
                <select id="editArea" required>
                    <option value="">Selecione uma área</option>
                    <option value="Tecnologia da Informação" ${idea.area === 'Tecnologia da Informação' ? 'selected' : ''}>Tecnologia da Informação</option>
                    <option value="Engenharia" ${idea.area === 'Engenharia' ? 'selected' : ''}>Engenharia</option>
                    <option value="Ciências Exatas" ${idea.area === 'Ciências Exatas' ? 'selected' : ''}>Ciências Exatas</option>
                    <option value="Ciências Humanas" ${idea.area === 'Ciências Humanas' ? 'selected' : ''}>Ciências Humanas</option>
                    <option value="Ciências Biológicas" ${idea.area === 'Ciências Biológicas' ? 'selected' : ''}>Ciências Biológicas</option>
                    <option value="Ciências da Saúde" ${idea.area === 'Ciências da Saúde' ? 'selected' : ''}>Ciências da Saúde</option>
                    <option value="Ciências Sociais" ${idea.area === 'Ciências Sociais' ? 'selected' : ''}>Ciências Sociais</option>
                    <option value="Artes e Design" ${idea.area === 'Artes e Design' ? 'selected' : ''}>Artes e Design</option>
                    <option value="Educação" ${idea.area === 'Educação' ? 'selected' : ''}>Educação</option>
                    <option value="Meio Ambiente" ${idea.area === 'Meio Ambiente' ? 'selected' : ''}>Meio Ambiente</option>
                    <option value="Sustentabilidade" ${idea.area === 'Sustentabilidade' ? 'selected' : ''}>Sustentabilidade</option>
                    <option value="Inovação Social" ${idea.area === 'Inovação Social' ? 'selected' : ''}>Inovação Social</option>
                </select>
            </div>
            
            <div class="input-group">
                <label for="editType">Tipo *</label>
                <select id="editType" required>
                    <option value="">Selecione o tipo</option>
                    <option value="Research" ${idea.type === 'Research' ? 'selected' : ''}>Pesquisa</option>
                    <option value="Extension" ${idea.type === 'Extension' ? 'selected' : ''}>Extensão</option>
                    <option value="Teaching" ${idea.type === 'Teaching' ? 'selected' : ''}>Ensino</option>
                </select>
            </div>
            
            <div class="input-group">
                <label for="editDescription">Descrição *</label>
                <textarea id="editDescription" rows="4" required>${this.escapeHtml(idea.description || '')}</textarea>
            </div>
            
            <div class="input-group">
                <label for="editAim">Objetivo *</label>
                <textarea id="editAim" rows="3" required>${this.escapeHtml(idea.aim || '')}</textarea>
            </div>
            
            <div class="input-group">
                <label for="editJustification">Justificativa *</label>
                <textarea id="editJustification" rows="3" required>${this.escapeHtml(idea.justification || '')}</textarea>
            </div>
            
            <div class="input-group">
                <label for="editTargetAudience">Público-Alvo *</label>
                <textarea id="editTargetAudience" rows="3" required>${this.escapeHtml(idea.targetAudience || '')}</textarea>
            </div>
        `;

        this.currentIdea = idea;
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
    }

    async saveIdeaChanges() {
        if (!this.currentIdea) return;

        console.log('💾 Saving idea changes...');

        // Get form data
        const formData = {
            title: document.getElementById('editTitle')?.value,
            subtitle: document.getElementById('editSubtitle')?.value,
            area: document.getElementById('editArea')?.value,
            type: document.getElementById('editType')?.value,
            description: document.getElementById('editDescription')?.value,
            aim: document.getElementById('editAim')?.value,
            justification: document.getElementById('editJustification')?.value,
            targetAudience: document.getElementById('editTargetAudience')?.value
        };

        // Validate required fields
        const requiredFields = ['title', 'area', 'type', 'description', 'aim', 'justification', 'targetAudience'];
        for (let field of requiredFields) {
            if (!formData[field] || formData[field].trim() === '') {
                this.showNotification(`O campo ${field} é obrigatório.`, 'error');
                return;
            }
        }

        try {
            this.showNotification('Salvando alterações...', 'info');

            // Create updated idea object
            const updatedIdea = {
                ...this.currentIdea,
                ...formData,
                proposer: this.currentIdea.proposer // Keep original proposer
            };

            const result = await IdeaAPI.update(this.currentIdea.id, updatedIdea);

            if (result.success) {
                this.showNotification('Ideia atualizada com sucesso!', 'success');
                this.closeModal();
                
                // Reload ideas to show updated data
                await this.loadIdeas();
            } else {
                throw new Error(result.error || 'Erro ao atualizar ideia');
            }
        } catch (error) {
            console.error('❌ Error updating idea:', error);
            this.showNotification('Erro ao atualizar ideia. Tente novamente.', 'error');
        }
    }

    async deleteIdea(ideaId) {
        console.log('🗑️ Deleting idea:', ideaId);

        // First, get the idea details to check ownership
        let ideaToDelete;
        try {
            const idea = this.allIdeas.find(idea => idea.id === ideaId);
            if (!idea) {
                this.showNotification('Ideia não encontrada.', 'error');
                return;
            }
            ideaToDelete = idea;
        } catch (error) {
            this.showNotification('Erro ao encontrar a ideia.', 'error');
            return;
        }

        // Check if current user owns this idea
        const isMyIdea = this.currentUser && (
            ideaToDelete.proposer === this.currentUser.name || 
            (this.currentUser.userType === 'student' && ideaToDelete.studentId === this.currentUser.id) ||
            (this.currentUser.userType === 'teacher' && ideaToDelete.teacherId === this.currentUser.id)
        );

        if (!isMyIdea) {
            this.showNotification('Você só pode excluir suas próprias ideias.', 'error');
            return;
        }

        // Only students who created the idea can delete it
        if (this.currentUser.userType !== 'student') {
            this.showNotification('Apenas estudantes podem excluir ideias.', 'error');
            return;
        }

        if (!confirm('Tem certeza de que deseja excluir esta ideia? Esta ação não pode ser desfeita.')) {
            return;
        }

        try {
            this.showNotification('Excluindo ideia...', 'info');

            const result = await IdeaAPI.delete(ideaId);

            if (result.success) {
                this.showNotification('Ideia excluída com sucesso!', 'success');
                this.closeModal();
                
                // Reload ideas to remove deleted idea
                await this.loadIdeas();
            } else {
                throw new Error(result.error || 'Erro ao excluir ideia');
            }
        } catch (error) {
            console.error('❌ Error deleting idea:', error);
            this.showNotification('Erro ao excluir ideia. Tente novamente.', 'error');
        }
    }

    async adoptIdea(idea) {
        console.log('🎯 Starting idea adoption:', idea);

        // Populate adoption modal
        document.getElementById('adoptionIdeaTitle').textContent = idea.title || 'Sem título';
        document.getElementById('adoptionProposer').textContent = idea.proposer || 'Não informado';
        document.getElementById('adoptionArea').textContent = idea.area || 'Não informada';
        document.getElementById('adoptionType').textContent = this.getTypeLabel(idea.type);

        this.currentIdea = idea;
        document.getElementById('adoptionModal').style.display = 'block';
        document.body.style.overflow = 'hidden';
    }

    async confirmAdoption() {
        if (!this.currentIdea) return;

        console.log('✅ Confirming idea adoption:', this.currentIdea);

        try {
            this.showNotification('Adotando ideia...', 'info');

            // Step 1: Save idea information to localStorage for project creation
            const ideaForProject = {
                // Basic project information from idea
                title: this.currentIdea.title,
                subtitle: this.currentIdea.subtitle || '',
                description: this.currentIdea.description,
                objective: this.currentIdea.aim,
                justification: this.currentIdea.justification,
                discipline: this.currentIdea.area,
                type: this.currentIdea.type,
                
                // Coordinator and participant information
                coordinator: this.currentUser.name,
                coordinatorId: this.currentUser.id,
                originalStudent: this.currentIdea.proposer,
                originalStudentId: this.currentIdea.studentId,
                
                // Project defaults that teacher will complete
                timeline: new Date().getFullYear() + ' - ' + (new Date().getFullYear() + 1),
                duration: '12 meses',
                complementHours: '0',
                scholarshipAvailable: false,
                scholarshipType: '',
                salary: 0,
                requirements: '',
                scholarshipQuantity: 0,
                externalLink: '',
                image: 'default-project.jpg',
                
                // Flag to indicate this comes from an adopted idea
                adoptedFromIdea: true,
                originalIdeaId: this.currentIdea.id
            };

            console.log('💾 Saving adopted idea data to localStorage:', ideaForProject);
            localStorage.setItem('koble_adopted_idea', JSON.stringify(ideaForProject));

            // Step 2: Remove idea from database
            console.log('🗑️ Removing idea from database...');
            const deleteResult = await IdeaAPI.delete(this.currentIdea.id);
            
            if (!deleteResult.success) {
                throw new Error('Erro ao remover ideia do banco de dados');
            }

            console.log('✅ Idea removed from database successfully');

            // Step 3: Navigate to project creation page
            this.showNotification('Ideia adotada! Redirecionando para criação do projeto...', 'success');
            this.closeModal();

            // Wait a moment for user to see the success message, then redirect
            setTimeout(() => {
                console.log('🚀 Redirecting to project creation page...');
                window.location.href = '../projectCreation/projectCreation.html';
            }, 1500);

        } catch (error) {
            console.error('❌ Error adopting idea:', error);
            this.showNotification(`Erro ao adotar ideia: ${error.message}`, 'error');
        }
    }

    getTypeLabel(type) {
        const types = {
            'Research': 'Pesquisa',
            'Extension': 'Extensão',
            'Teaching': 'Ensino'
        };
        return types[type] || type || 'Não especificado';
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    showLoading() {
        const loading = document.getElementById('loadingState');
        const container = document.getElementById('ideasContainer');
        const emptyState = document.getElementById('emptyState');
        const noIdeasState = document.getElementById('noIdeasState');
        
        if (loading) loading.style.display = 'block';
        if (container) container.style.display = 'none';
        if (emptyState) emptyState.style.display = 'none';
        if (noIdeasState) noIdeasState.style.display = 'none';
    }

    hideLoading() {
        const loading = document.getElementById('loadingState');
        if (loading) loading.style.display = 'none';
    }

    showNoIdeasState() {
        const container = document.getElementById('ideasContainer');
        const emptyState = document.getElementById('emptyState');
        const noIdeasState = document.getElementById('noIdeasState');
        
        if (container) container.style.display = 'none';
        if (emptyState) emptyState.style.display = 'none';
        if (noIdeasState) noIdeasState.style.display = 'block';
    }

    showError(message) {
        this.showNotification(message, 'error');
    }

    showNotification(message, type = 'info') {
        const container = document.getElementById('notifications');
        if (!container) return;
        
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        
        const icon = type === 'error' ? '❌' : type === 'success' ? '✅' : 'ℹ️';
        
        notification.innerHTML = `
            <span>${icon}</span>
            <span>${this.escapeHtml(message)}</span>
        `;
        
        container.appendChild(notification);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 5000);
        
        // Manual close on click
        notification.addEventListener('click', () => {
            notification.remove();
        });
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    console.log('🌟 Ideas page loaded');
    window.ideasManager = new IdeasManager();
});

// Export for global access
window.IdeasManager = IdeasManager;