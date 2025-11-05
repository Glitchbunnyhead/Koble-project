// ===== Header Manager - Simplified =====

class HeaderManager {
    constructor() {
        this.init();
    }
    
    init() {
        console.log('HeaderManager initializing...');
        // Check authentication requirement first
        console.log('=== HEADER MANAGER DEBUGGING ===');
        console.log('Current user in header manager:', SessionManager.getUser());
        console.log('Is logged in in header manager:', SessionManager.isLoggedIn());
        
        SessionManager.checkAuthRequirement();
        this.checkLoginStatus();
        this.setupEventListeners();
        this.initializeSearchBar(); // Add search bar initialization
    }
    
    checkLoginStatus() {
        console.log('Checking login status...');
        if (SessionManager.isLoggedIn()) {
            const user = SessionManager.getUser();
            console.log('User is logged in:', user);
            this.updateHeaderForLoggedInUser(user);
        } else {
            console.log('User is not logged in');
            this.updateHeaderForGuest();
        }
    }
    
    updateHeaderForLoggedInUser(userData) {
        const configDiv = document.querySelector('.configDiv');
        if (!configDiv) return;
        
        configDiv.innerHTML = this.createUserMenu(userData);
        this.setupUserMenu();
        this.updateNavigationForUserType(userData.userType);
    }
    
    updateHeaderForGuest() {
        const configDiv = document.querySelector('.configDiv');
        if (!configDiv) return;
        
        // For protected pages, users should be redirected to login, not shown a button
        // This will only be shown on public pages
        configDiv.innerHTML = `
            <div class="auth-loading" style="display: none;">
                <span>Verificando autenticação...</span>
            </div>
            <div class="guest-actions">
                <span class="guest-message">Acesso restrito</span>
            </div>
        `;
        
        // Reset navigation to default
        this.resetNavigationToDefault();
    }
    
    resetNavigationToDefault() {
        const redirects = document.querySelectorAll('.redirects');
        
        // Reset to original navigation items
        if (redirects[0]) {
            redirects[0].innerHTML = '<p class="redirectsElements">ajuda</p>';
            redirects[0].onclick = null;
        }
        if (redirects[1]) {
            redirects[1].innerHTML = '<p class="redirectsElements">tutoriais</p>';
            redirects[1].onclick = null;
        }
        if (redirects[2]) {
            redirects[2].innerHTML = '<p class="redirectsElements">sobre</p>';
            redirects[2].onclick = null;
        }
    }
    
    createUserMenu(userData) {
        // Define menu items based on user type
        const userMenus = {
            student: `
                <div class="dropdown-item" onclick="headerManager.showProfile()">
                    👤 Perfil
                </div>
                <div class="dropdown-item" onclick="headerManager.showSettings()">
                    ⚙️ Configurações
                </div>
                <div class="dropdown-item" onclick="headerManager.myProjectsRedirect()">
                    📂 Meus Projetos
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToApplications()">
                    📋 Minhas Participações
                </div>
                <div class="dropdown-divider"></div>
                <div class="dropdown-item logout" onclick="headerManager.logout()">
                    🚪 Sair
                </div>`,
            
            teacher: `
                <div class="dropdown-item" onclick="headerManager.showProfile()">
                    👤 Perfil
                </div>
                <div class="dropdown-item" onclick="headerManager.showSettings()">
                    ⚙️ Configurações
                </div>
                <div class="dropdown-item" onclick="headerManager.myProjectsRedirect()">
                    📂 Meus Projetos
                </div>
                <div class="dropdown-item" onclick="headerManager.createProjectRedirect()">
                    ➕ Criar Projeto
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToManageApplications()">
                    📊 Gerenciar Candidaturas
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToReports()">
                    📈 Relatórios
                </div>
                <div class="dropdown-divider"></div>
                <div class="dropdown-item logout" onclick="headerManager.logout()">
                    🚪 Sair
                </div>`,
            
            company: `
                <div class="dropdown-item" onclick="headerManager.showProfile()">
                    🏢 Perfil Empresarial
                </div>
                <div class="dropdown-item" onclick="headerManager.showSettings()">
                    ⚙️ Configurações
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToPartnerships()">
                    🤝 Parcerias
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToCompanyProjects()">
                    📂 Projetos em Parceria
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToContacts()">
                    📞 Contatos
                </div>
                <div class="dropdown-divider"></div>
                <div class="dropdown-item logout" onclick="headerManager.logout()">
                    🚪 Sair
                </div>`,
            
            external: `
                <div class="dropdown-item" onclick="headerManager.showProfile()">
                    👤 Perfil
                </div>
                <div class="dropdown-item" onclick="headerManager.showSettings()">
                    ⚙️ Configurações
                </div>
                <div class="dropdown-item" onclick="headerManager.myProjectsRedirect()">
                    📂 Meus Projetos
                </div>
                <div class="dropdown-item" onclick="headerManager.navigateToApplications()">
                    📋 Minhas Participações
                </div>
                <div class="dropdown-divider"></div>
                <div class="dropdown-item logout" onclick="headerManager.logout()">
                    🚪 Sair
                </div>`
        };

        return `
            <div class="user-menu">
                <div class="user-info" id="userMenuToggle">
                    <span class="user-name" style="color: #ffffff !important;">${userData.name || userData.email || 'Usuário'}</span>
                    <span class="user-type-badge ${userData.userType}">${this.getUserTypeBadge(userData.userType)}</span>
                    <img src="/front/images/image.png" alt="perfil" class="config-icon">
                </div>
                <div class="user-dropdown" id="userDropdown" style="display: none;">
                    ${userMenus[userData.userType] || userMenus.external}
                </div>
            </div>
        `;
    }
    
    getUserTypeBadge(userType) {
        const badges = {
            student: 'Estudante',
            teacher: 'Professor',
            company: 'Empresa',
            external: 'Externo'
        };
        return badges[userType] || 'Usuário';
    }
    
    
    
    updateNavigationForUserType(userType) {
        console.log('🔧 Updating navigation for user type:', userType);
        
        // Use setTimeout to ensure DOM elements are available
        setTimeout(() => {
            const redirects = document.querySelectorAll('.redirects');
            console.log('� Found redirects elements:', redirects.length);
            
            if (redirects.length === 0) {
                console.warn('⚠️ No navigation elements found, retrying...');
                return;
            }
            
            // Clear all navigation items first
            redirects.forEach((redirect, index) => {
                redirect.onclick = null;
                redirect.innerHTML = '';
            });
            
            const navigationConfigs = {
                'student': {
                    items: [
                        { text: '🏠 Home', action: () => this.navigateToHome() },
                        { text: 'Explorar Ideias', action: () => this.navigateToIdeas() },
                        { text: 'Criar Ideia', action: () => this.navigateToCreateIdea() },
                        { text: 'Minhas Participações', action: () => this.navigateToMyApplications() }
                    ]
                },
                'teacher': {
                    items: [
                        { text: '🏠 Home', action: () => this.navigateToHome() },
                        { text: 'Criar Projeto', action: () => this.navigateToCreateProject() },
                        { text: 'Gerenciar Candidaturas', action: () => this.navigateToManageApplications() },
                        { text: 'Explorar Ideias', action: () => this.navigateToIdeas() }
                    ]
                },
                'company': {
                    items: [
                        { text: '🏠 Home', action: () => this.navigateToHome() },
                        { text: 'Parcerias', action: () => this.navigateToPartnerships() },
                        { text: 'Projetos em Parceria', action: () => this.navigateToCompanyProjects() },
                        { text: 'Ideias Inovadoras', action: () => this.navigateToIdeas() }
                    ]
                },
                'external': {
                    items: [
                        { text: '🏠 Home', action: () => this.navigateToHome() },
                        { text: 'Explorar Ideias', action: () => this.navigateToIdeas() },
                        { text: 'Minhas Participações', action: () => this.navigateToMyApplications() },
                        { text: 'Sobre', action: () => this.navigateToHelp() }
                    ]
                }
            };
            
            const config = navigationConfigs[userType];
            console.log('⚙️ Applying navigation config for', userType);
            
            if (config && config.items) {
                config.items.forEach((item, index) => {
                    if (redirects[index]) {
                        console.log(`🔗 Setting up navigation: "${item.text}" at index ${index}`);
                        redirects[index].innerHTML = `<p class="redirectsElements">${item.text}</p>`;
                        redirects[index].onclick = item.action;
                    }
                });
                console.log('✅ Navigation setup completed');
            }
        }, 100); // Small delay to ensure DOM is ready
    }
    
    addTeacherOptions(redirects) {
        if (redirects[0]) {
            redirects[0].innerHTML = '<p class="redirectsElements">Criar Projeto</p>';
            redirects[0].onclick = () => this.createProjectRedirect();
        }
    }
    
    addStudentOptions(redirects) {
        if (redirects[1]) {
            redirects[1].innerHTML = '<p class="redirectsElements">Minhas Candidaturas</p>';
            redirects[1].onclick = () => this.navigateToApplications();
        }
    }
    
    addExternalOptions(redirects) {
        if (redirects[2]) {
            redirects[2].innerHTML = '<p class="redirectsElements">Parcerias</p>';
            redirects[2].onclick = () => this.navigateToPartnerships();
        }
    }
    
    setupUserMenu() {
        const userMenuToggle = document.getElementById('userMenuToggle');
        const userDropdown = document.getElementById('userDropdown');
        
        if (userMenuToggle && userDropdown) {
            userMenuToggle.addEventListener('click', (e) => {
                e.stopPropagation();
                const isVisible = userDropdown.style.display !== 'none';
                userDropdown.style.display = isVisible ? 'none' : 'block';
            });
            
            document.addEventListener('click', () => {
                userDropdown.style.display = 'none';
            });
        }
    }
    
    setupEventListeners() {
        console.log('Setting up header event listeners...');
        window.addEventListener('userLoggedIn', (e) => {
            console.log('UserLoggedIn event received:', e.detail);
            this.updateHeaderForLoggedInUser(e.detail);
        });
        
        window.addEventListener('userLoggedOut', () => {
            console.log('UserLoggedOut event received');
            this.updateHeaderForGuest();
        });
    }
    
    // Navigation methods
    showProfile() { 
        console.log('👤 Navigating to profile...');
        this.myProjectsRedirect();
    }
    showSettings() { 
        console.log('⚙️ Navigating to settings...');
        this.myProjectsRedirect();
    }
    myProjectsRedirect() { 
        console.log('📂 Navigating to my projects...');
        
        // Get the current path to determine correct redirect
        const currentPath = window.location.pathname;
        console.log('Current path:', currentPath);
        
        let targetUrl;
        
        if (currentPath.includes('/pages/')) {
            // We're in a subfolder, need to go up and then navigate
            if (currentPath.includes('/pages/userProject/')) {
                // Already in userProject folder
                targetUrl = 'myProjects.html';
            } else if (currentPath.includes('/pages/projectCreation/')) {
                // From project creation folder
                targetUrl = '../userProject/myProjects.html';
            } else if (currentPath.includes('/pages/project page/')) {
                // From project page
                targetUrl = '../userProject/myProjects.html';
            } else if (currentPath.includes('/pages/login and sign in/')) {
                // From login pages
                targetUrl = '../userProject/myProjects.html';
            } else if (currentPath.includes('/pages/welcome/')) {
                // From welcome pages
                targetUrl = '../userProject/myProjects.html';
            } else {
                // Other pages folder
                targetUrl = '../userProject/myProjects.html';
            }
        } else {
            // We're in the root
            targetUrl = 'pages/userProject/myProjects.html';
        }
        
        console.log('🎯 My Projects redirect target:', targetUrl);
        window.location.href = targetUrl;
    }

    showMyProjects() { 
        console.log('📂 Navigating to my projects...');
        this.myProjectsRedirect();
    }
    
    // Teacher-specific navigation
    createProjectRedirect() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'teacher') {
            alert('Apenas professores podem criar projetos.');
            return;
        }
        console.log('🎯 Redirecting to project creation page...');
        
        // Get the current path to determine correct redirect
        const currentPath = window.location.pathname;
        console.log('Current path:', currentPath);
        
        let targetUrl;
        
        if (currentPath.includes('/pages/')) {
            // We're in a subfolder, need to go up and then navigate
            if (currentPath.includes('/pages/userProject/')) {
                // From myProjects or similar pages
                targetUrl = '../projectCreation/projectCreation.html';
            } else if (currentPath.includes('/pages/projectCreation/')) {
                // Already in project creation folder
                targetUrl = 'projectCreation.html';
            } else if (currentPath.includes('/pages/project page/')) {
                // From project page
                targetUrl = '../projectCreation/projectCreation.html';
            } else {
                // Other pages folder
                targetUrl = '../projectCreation/projectCreation.html';
            }
        } else {
            // We're in root
            targetUrl = 'pages/projectCreation/projectCreation.html';
        }
        
        console.log('Navigating to:', targetUrl);
        window.location.href = targetUrl;
    }
    navigateToManageApplications() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'teacher') {
            alert('Apenas professores podem gerenciar candidaturas.');
            return;
        }
        console.log('📊 Navigating to manage applications...');
        
        // Get the current path to determine correct redirect
        const currentPath = window.location.pathname;
        console.log('Current path:', currentPath);
        
        let targetUrl;
        
        if (currentPath.includes('/pages/')) {
            // We're in a subfolder, need to go up and then navigate
            if (currentPath.includes('/pages/manageApplications/')) {
                // Already in manage applications folder
                targetUrl = 'manageApplications.html';
            } else if (currentPath.includes('/pages/userProject/')) {
                // From myProjects or similar pages
                targetUrl = '../manageApplications/manageApplications.html';
            } else if (currentPath.includes('/pages/projectCreation/')) {
                // From project creation folder
                targetUrl = '../manageApplications/manageApplications.html';
            } else if (currentPath.includes('/pages/project page/')) {
                // From project page
                targetUrl = '../manageApplications/manageApplications.html';
            } else {
                // Other pages folder
                targetUrl = '../manageApplications/manageApplications.html';
            }
        } else {
            // We're in root
            targetUrl = 'pages/manageApplications/manageApplications.html';
        }
        
        console.log('🎯 Manage Applications redirect target:', targetUrl);
        window.location.href = targetUrl;
    }
    navigateToReports() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'teacher') {
            alert('Apenas professores podem acessar relatórios.');
            return;
        }
        console.log('📈 Navigating to reports...');
        this.myProjectsRedirect();
    }
    
    // Student-specific navigation
    navigateToApplications() { 
        const user = SessionManager.getUser();
        if (!['student', 'external'].includes(user.userType)) {
            alert('Função disponível apenas para estudantes e usuários externos.');
            return;
        }
        console.log('📋 Navigating to participations...');
        this.redirectToPage('pages/fellowOrMember/participante.html');
    }
    navigateToExploreProjects() {
        console.log('🔍 Navigating to explore projects...');
        this.redirectToPage('index.html');
    }
    navigateToScholarships() {
        console.log('🎓 Navigating to scholarships...');
        this.redirectToPage('index.html');
    }
    
    // Company-specific navigation
    navigateToPartnerships() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'company') {
            alert('Função disponível apenas para empresas.');
            return;
        }
        console.log('🤝 Navigating to partnerships...');
        this.redirectToPage('index.html');
    }
    navigateToCompanyProjects() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'company') {
            alert('Função disponível apenas para empresas.');
            return;
        }
        console.log('📂 Navigating to company projects...');
        this.myProjectsRedirect();
    }
    navigateToContacts() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'company') {
            alert('Função disponível apenas para empresas.');
            return;
        }
        console.log('📞 Navigating to contacts...');
        this.redirectToPage('index.html');
    }
    
    // Idea-specific navigation
    navigateToIdeas() {
        console.log('💡 Navigating to ideas...');
        this.redirectToPage('pages/ideas/ideas.html');
    }
    
    createIdeaRedirect() {
        const user = SessionManager.getUser();
        if (!['teacher', 'student'].includes(user.userType)) {
            alert('Função disponível apenas para professores e estudantes.');
            return;
        }
        console.log('✨ Navigating to idea creation...');
        this.redirectToPage('pages/ideaCreation/ideaCreation.html');
    }
    
    // Helper method for navigation - ABSOLUTE PATH SYSTEM
    redirectToPage(targetPage) {
        console.log('🧭 Navigating to:', targetPage);
        
        // Get the base URL by finding the 'front' directory
        const currentPath = window.location.pathname;
        const frontIndex = currentPath.indexOf('/front/');
        
        let basePath;
        if (frontIndex !== -1) {
            // Extract everything up to and including '/front/'
            basePath = currentPath.substring(0, frontIndex + 7); // +7 to include '/front/'
        } else {
            // Fallback: assume we're already in the front directory
            basePath = '/front/';
        }
        
        // Create absolute path
        const absolutePath = basePath + targetPage;
        
        console.log('🎯 Absolute navigation path:', absolutePath);
        window.location.href = absolutePath;
    }
    
    showPlaceholder(feature) {
        console.log(`🚧 ${feature} feature placeholder - redirecting to main page`);
        this.redirectToPage('home.html');
    }
    
    // ===== STANDARDIZED NAVIGATION METHODS - Location Independent =====
    
    navigateToHome() {
        console.log('🏠 Navigating to home...');
        this.redirectToPage('home.html');
    }
    
    navigateToExploreProjects() {
        console.log('🔍 Navigating to explore projects...');
        this.redirectToPage('home.html'); // Main project exploration page
    }
    
    navigateToMyProjects() {
        console.log('📁 Navigating to my projects...');
        this.redirectToPage('pages/userProject/myProjects.html');
    }
    
    navigateToCreateProject() {
        const user = SessionManager.getUser();
        if (user.userType !== 'teacher') {
            alert('Função disponível apenas para professores.');
            return;
        }
        console.log('➕ Navigating to create project...');
        this.redirectToPage('pages/projectCreation/projectCreation.html');
    }
    
    navigateToManageApplications() {
        const user = SessionManager.getUser();
        if (user.userType !== 'teacher') {
            alert('Função disponível apenas para professores.');
            return;
        }
        console.log('📋 Navigating to manage applications...');
        this.redirectToPage('pages/manageApplications/manageApplications.html');
    }
    
    navigateToMyApplications() {
        const user = SessionManager.getUser();
        if (!['student', 'external'].includes(user.userType)) {
            alert('Função disponível apenas para estudantes e usuários externos.');
            return;
        }
        console.log('📄 Navigating to my participations...');
        this.redirectToPage('pages/fellowOrMember/participante.html');
    }
    
    navigateToIdeas() {
        console.log('💡 Navigating to ideas...');
        this.redirectToPage('pages/ideas/ideas.html');
    }
    
    navigateToCreateIdea() {
        const user = SessionManager.getUser();
        if (!['teacher', 'student'].includes(user.userType)) {
            alert('Função disponível apenas para professores e estudantes.');
            return;
        }
        console.log('✨ Navigating to create idea...');
        this.redirectToPage('pages/ideaCreation/ideaCreation.html');
    }
    
    navigateToPartnerships() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'company') {
            alert('Função disponível apenas para empresas.');
            return;
        }
        console.log('🤝 Navigating to partnerships...');
        this.redirectToPage('home.html'); // Placeholder - implement company partnerships page
    }
    
    navigateToCompanyProjects() { 
        const user = SessionManager.getUser();
        if (user.userType !== 'company') {
            alert('Função disponível apenas para empresas.');
            return;
        }
        console.log('🏢 Navigating to company projects...');
        this.redirectToPage('pages/userProject/myProjects.html');
    }
    
    navigateToHelp() {
        console.log('❓ Navigating to help...');
        alert('Página de ajuda será implementada em breve');
    }
    
    navigateToLogin() {
        console.log('🔐 Navigating to login...');
        this.redirectToPage('pages/login and sign in/login.html');
    }
    
    navigateToRegister() {
        console.log('📝 Navigating to register...');
        this.redirectToPage('pages/login and sign in/cadastroExterno.html');
    }
    
    logout() {
        if (confirm('Tem certeza que deseja sair?')) {
            SessionManager.logout();
            console.log('User logged out, redirecting to welcome page');
            // Use the new redirectToPage method for consistency
            this.redirectToPage('pages/welcome/index.html');
        }
    }

    // ===== SEARCH BAR FUNCTIONALITY =====
    initializeSearchBar() {
        console.log('🔍 Initializing search bar...');
        
        // Use setTimeout to ensure DOM elements are loaded
        setTimeout(() => {
            this.searchInput = document.getElementById('searchInput');
            this.searchBtn = document.getElementById('searchBtn');
            this.searchResults = document.getElementById('searchResults');

            if (!this.searchInput || !this.searchBtn || !this.searchResults) {
                console.warn('⚠️ Search bar elements not found, search functionality disabled');
                return;
            }

            this.setupSearchEventListeners();
            console.log('✅ Search bar initialized successfully');
        }, 100);
    }

    setupSearchEventListeners() {
        // Event Listeners
        this.searchInput.addEventListener('input', (e) => {
            const query = e.target.value;
            this.performSearch(query);
        });

        this.searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                const query = e.target.value;
                this.performSearch(query);
            }
        });

        this.searchBtn.addEventListener('click', () => {
            const query = this.searchInput.value;
            this.performSearch(query);
        });

        // Hide search results when clicking outside
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.search-container')) {
                this.hideSearchResults();
            }
        });
    }

    // Search function
    async performSearch(query) {
        if (query.trim() === '') {
            this.hideSearchResults();
            return;
        }

        try {
            const results = [];
            
            // Search projects
            const projectResult = await ProjectAPI.getAll();
            if (projectResult.success) {
                const matchingProjects = projectResult.data.filter(p => 
                    p.title?.toLowerCase().includes(query.toLowerCase()) ||
                    p.description?.toLowerCase().includes(query.toLowerCase()) ||
                    p.coordinator?.toLowerCase().includes(query.toLowerCase())
                );
                results.push(...matchingProjects.map(p => ({...p, type: 'project'})));
            }
            
            // Search teachers
            const teacherResult = await TeacherAPI.getAll();
            if (teacherResult.success) {
                const matchingTeachers = teacherResult.data.filter(t => 
                    t.name?.toLowerCase().includes(query.toLowerCase())
                );
                results.push(...matchingTeachers.map(t => ({...t, type: 'teacher'})));
            }
            
            // Search companies
            const companyResult = await CompanyAPI.getAll();
            if (companyResult.success) {
                const matchingCompanies = companyResult.data.filter(c => 
                    c.name?.toLowerCase().includes(query.toLowerCase())
                );
                results.push(...matchingCompanies.map(c => ({...c, type: 'company'})));
            }

            this.displaySearchResults(results);
            
        } catch (error) {
            console.error('Search error:', error);
            this.searchResults.innerHTML = '<div class="result-item">Erro na busca. Verifique se o backend está rodando.</div>';
            this.searchResults.classList.remove('hidden');
        }
    }

    // Display search results
    displaySearchResults(results) {
        this.searchResults.innerHTML = '';

        if (results.length === 0) {
            this.searchResults.innerHTML = '<div class="result-item">No results found</div>';
        } else {
            results.forEach(result => {
                const resultItem = document.createElement('div');
                resultItem.className = 'result-item';
                resultItem.innerHTML = `
                    <strong>${result.title || result.name}</strong> <span style="color: #666;">(${result.type})</span>
                    <br>
                    <small style="color: #888;">${result.description || ''}</small>
                `;
                
                // Add click handler for each result
                resultItem.addEventListener('click', () => {
                    this.selectSearchResult(result);
                });

                this.searchResults.appendChild(resultItem);
            });
        }

        this.showSearchResults();
    }

    // Show search results
    showSearchResults() {
        this.searchResults.classList.remove('hidden');
    }

    // Hide search results
    hideSearchResults() {
        this.searchResults.classList.add('hidden');
    }

    // Handle result selection
    selectSearchResult(result) {
        this.searchInput.value = result.title || result.name;
        this.hideSearchResults();
        
        console.log('Selected search result:', result);
        
        // Navigate based on type
        switch(result.type) {
            case 'project':
                // Store project data and navigate to project page
                sessionStorage.setItem('selectedProject', JSON.stringify(result));
                this.redirectToPage('pages/project page/project.html');
                break;
            case 'teacher':
                alert(`Professor: ${result.name || result.title}\nEmail: ${result.email || 'N/A'}`);
                break;
            case 'company':
                alert(`Empresa: ${result.name || result.title}\nEmail: ${result.email || 'N/A'}`);
                break;
            default:
                console.log('Unknown result type:', result.type);
        }
    }
}

// Initialize header manager
let headerManager;
document.addEventListener('DOMContentLoaded', () => {
    headerManager = new HeaderManager();
    console.log('Header manager initialized');
});