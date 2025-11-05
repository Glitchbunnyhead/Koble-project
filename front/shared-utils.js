// ===== Shared Session Manager and Utilities =====
// This file contains shared utilities used across multiple pages

// Session storage with localStorage persistence
window.SessionManager = {
    currentUser: null,
    
    // Initialize session from storage on load
    init: function() {
        console.log('=== SESSIONMANAGER INIT ===');
        const savedUser = localStorage.getItem('koble_user_session');
        console.log('Raw localStorage data:', savedUser);
        
        if (savedUser) {
            try {
                this.currentUser = JSON.parse(savedUser);
                console.log('Session restored from localStorage:', this.currentUser.name || this.currentUser.email);
                console.log('Full restored user:', this.currentUser);
            } catch (error) {
                console.error('Error parsing saved session:', error);
                localStorage.removeItem('koble_user_session');
            }
        } else {
            console.log('No saved session found in localStorage');
        }
        console.log('SessionManager init complete. Current user:', this.currentUser);
    },
    
    setUser: function(userData) {
        this.currentUser = {
            ...userData,
            loginTime: new Date().toISOString()
        };
        
        // Persist to localStorage
        localStorage.setItem('koble_user_session', JSON.stringify(this.currentUser));
        
        this.updateHeader();
        console.log('User session set and saved:', this.currentUser.name || this.currentUser.email);
    },
    
    getUser: function() {
        return this.currentUser;
    },
    
    isLoggedIn: function() {
        const loggedIn = this.currentUser !== null;
        console.log('Checking isLoggedIn:', loggedIn, 'Current user:', this.currentUser);
        return loggedIn;
    },
    
    logout: function() {
        this.currentUser = null;
        localStorage.removeItem('koble_user_session');
        this.updateHeader();
        console.log('User logged out and session cleared');
        return true;
    },
    
    updateHeader: function() {
        // Dispatch event for header update
        const event = new CustomEvent(this.currentUser ? 'userLoggedIn' : 'userLoggedOut', { 
            detail: this.currentUser 
        });
        window.dispatchEvent(event);
    },
    
    // Authentication guard - redirects to login if not authenticated
    requireAuth: function() {
        if (!this.isLoggedIn()) {
            console.log('User not authenticated, redirecting to login');
            window.location.href = '/front/pages/login and sign in/login.html';
            return false;
        }
        return true;
    },
    
    // Check if current page requires authentication
    checkAuthRequirement: function() {
        console.log('=== CHECKING AUTH REQUIREMENT ===');
        const currentPath = window.location.pathname;
        console.log('Current path:', currentPath);
        
        const publicPaths = [
            '/front/pages/login and sign in/login.html',
            '/front/pages/login and sign in/cadastroExterno.html',
            '/front/pages/login and sign in/cadastroInstitucional.html',
            '/front/pages/welcome/welcome.html'
        ];
        
        const isPublicPage = publicPaths.some(path => currentPath.includes(path.split('/').pop()));
        console.log('Is public page:', isPublicPage);
        
        if (!isPublicPage && !this.isLoggedIn()) {
            console.log('Page requires auth but user not logged in - redirecting');
            this.requireAuth();
        } else {
            console.log('Auth check passed - user can access page');
        }
    }
};

// API configuration
window.API_CONFIG = {
    baseURL: (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')
        ? 'http://localhost:8080/api'  // Development
        : 'https://your-production-domain.com/api', // Production
    endpoints: {
        // User management endpoints (mapped to actual controllers)
        student: '/student',
        teacher: '/teacher', 
        company: '/company',
        external_person: '/external_person',
        
        // Project management endpoints
        project: '/project',
        company_project: '/company_project',
        
        // Ideas and participants (if needed later)
        idea: '/idea',
        participant: '/participant',
        fellow: '/fellow'
    }
};

// Simple fetch wrapper (modern browsers only)
window.apiCall = async function(endpoint, method = 'GET', data = null) {
    try {
        console.log(`=== API CALL DEBUG ===`);
        console.log(`Endpoint: ${endpoint}`);
        console.log(`Method: ${method}`);
        console.log(`Data:`, data);
        
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        };
        
        if (data) {
            options.body = JSON.stringify(data);
            console.log(`Request body:`, options.body);
        }
        
        console.log(`Making fetch request...`);
        const response = await fetch(endpoint, options);
        console.log(`Response status: ${response.status}`);
        console.log(`Response headers:`, [...response.headers.entries()]);
        
        // Check if response has content before trying to parse JSON
        const responseText = await response.text();
        console.log(`Response text:`, responseText);
        
        if (!responseText) {
            console.log('Empty response received');
            if (response.ok) {
                return { success: true, data: null };
            } else {
                // For 500 errors with empty response, provide more specific error info
                const errorMessage = response.status === 500 
                    ? 'Server error occurred' 
                    : 'Empty response from server';
                return { 
                    success: false, 
                    error: errorMessage,
                    status: response.status 
                };
            }
        }
        
        let result;
        try {
            result = JSON.parse(responseText);
            console.log(`Parsed JSON:`, result);
        } catch (jsonError) {
            console.error('JSON parse error:', jsonError);
            console.error('Response was not valid JSON:', responseText);
            
            // Handle non-JSON responses (like plain text success messages)
            if (response.ok && responseText.includes('sucesso')) {
                return { success: true, message: responseText };
            } else if (response.ok) {
                return { success: true, data: responseText };
            } else {
                return { success: false, error: responseText || 'Request failed' };
            }
        }
        
        if (response.ok) {
            return { success: true, data: result };
        } else {
            // Handle specific error codes
            let errorMessage = result.message || 'Request failed';
            
            if (response.status === 409) {
                errorMessage = 'Este email ou número de matrícula já está cadastrado. Tente usar valores diferentes.';
            } else if (response.status === 500) {
                errorMessage = 'Erro interno do servidor. Tente novamente em alguns momentos.';
            } else if (response.status === 400) {
                errorMessage = 'Dados inválidos. Verifique as informações e tente novamente.';
            }
            
            return { success: false, error: errorMessage, status: response.status };
        }
    } catch (error) {
        console.error('API call error:', error);
        return { success: false, error: error.message };
    }
};

// Authentication and Registration API
window.AuthAPI = {
    async register(userData, userType) {
        const endpointMap = {
            'student': API_CONFIG.endpoints.student,
            'teacher': API_CONFIG.endpoints.teacher,
            'external': API_CONFIG.endpoints.external_person,
            'company': API_CONFIG.endpoints.company
        };
        
        const endpoint = `${API_CONFIG.baseURL}${endpointMap[userType]}`;
        if (!endpointMap[userType]) {
            throw new Error('Invalid user type');
        }
        
        return await apiCall(endpoint, 'POST', userData);
    },
    
    async login(email, password) {
        try {
            console.log('=== LOGIN DEBUGGING ===');
            console.log('Attempting login for email:', email);
            console.log('Password provided:', password); // Remove this in production
            
            // Search for user across all user types
            const userTypes = [
                { type: 'student', endpoint: API_CONFIG.endpoints.student },
                { type: 'teacher', endpoint: API_CONFIG.endpoints.teacher },
                { type: 'company', endpoint: API_CONFIG.endpoints.company },
                { type: 'external', endpoint: API_CONFIG.endpoints.external_person }
            ];
            
            console.log('User types to check:', userTypes);
            
            for (const userTypeConfig of userTypes) {
                try {
                    const endpoint = `${API_CONFIG.baseURL}${userTypeConfig.endpoint}`;
                    console.log(`\n--- Checking ${userTypeConfig.type} at ${endpoint} ---`);
                    
                    const response = await apiCall(endpoint, 'GET');
                    console.log(`Response for ${userTypeConfig.type}:`, response);
                    
                    if (response.success && response.data) {
                        console.log(`Found ${response.data.length} users in ${userTypeConfig.type}`);
                        console.log('Users data:', response.data);
                        
                        // Find user with matching email
                        const user = response.data.find(u => u.email === email);
                        
                        if (user) {
                            console.log(`USER FOUND in ${userTypeConfig.type}:`, user);
                            console.log(`Password comparison - Input: "${password}", Stored: "${user.password}"`);
                            
                            // Verify password
                            if (user.password === password) {
                                console.log('PASSWORD MATCH! Login successful');
                                
                                // Return user data with type, excluding password
                                const { password: _, ...userWithoutPassword } = user;
                                const loginResult = {
                                    success: true,
                                    data: {
                                        ...userWithoutPassword,
                                        userType: userTypeConfig.type
                                    }
                                };
                                console.log('Login result:', loginResult);
                                return loginResult;
                            } else {
                                console.log('PASSWORD MISMATCH!');
                                return {
                                    success: false,
                                    error: 'Senha incorreta'
                                };
                            }
                        } else {
                            console.log(`User with email "${email}" not found in ${userTypeConfig.type}`);
                        }
                    } else {
                        console.log(`Failed to get data for ${userTypeConfig.type}:`, response);
                    }
                } catch (error) {
                    console.log(`Error checking ${userTypeConfig.type}:`, error.message);
                    console.log('Full error:', error);
                    // Continue to next user type
                }
            }
            
            console.log('USER NOT FOUND in any user type');
            return {
                success: false,
                error: 'Usuário não encontrado'
            };
            
        } catch (error) {
            console.error('Login error:', error);
            return {
                success: false,
                error: 'Erro interno do servidor'
            };
        }
    }
};

// Project Management API
window.ProjectAPI = {
    async create(projectData) {
        console.log('🚀 Attempting to create project via API...');
        console.log('📋 Project data:', projectData);
        
        // Determine which specific endpoint to use based on project type
        const projectType = projectData.type ? projectData.type.toLowerCase() : null;
        let endpoint;
        
        switch (projectType) {
            case 'research':
                endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/research`;
                console.log('🔬 Creating Research Project');
                break;
            case 'educational':
            case 'teaching':
                endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/educational`;
                console.log('🎓 Creating Educational Project');
                break;
            case 'extension':
                endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/extension`;
                console.log('🌍 Creating Extension Project');
                break;
            default:
                // Fallback to general endpoint
                endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}`;
                console.log('📝 Creating General Project (fallback)');
                break;
        }
        
        console.log('📡 Endpoint:', endpoint);
        
        try {
            // Try the specific API endpoint
            const result = await apiCall(endpoint, 'POST', projectData);
            console.log('✅ Real API success:', result);
            return result;
        } catch (error) {
            console.error('❌ Real API failed:', error);
            
            // Only use mock in development as fallback
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.log('🧪 Falling back to mock API due to error:', error.message);
                
                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve({
                            success: true,
                            data: {
                                id: Date.now(),
                                ...projectData,
                                createdAt: new Date().toISOString(),
                                status: 'active',
                                note: 'Created with mock API due to connection error'
                            }
                        });
                    }, 1500);
                });
            }
            
            // In production, return the error
            throw error;
        }
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}`;
        
        console.log('🚀 Fetching all projects from API...');
        console.log('📡 Endpoint:', endpoint);
        
        try {
            // Try real API first
            const result = await apiCall(endpoint, 'GET');
            console.log('✅ Projects fetched successfully:', result);
            return result;
        } catch (error) {
            console.error('❌ Error fetching projects:', error);
            
            // For development mode, provide mock response to avoid complete failure
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.log('🧪 ProjectAPI Development mode: falling back to mock projects due to error:', error.message);
                
                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve({
                            success: true,
                            data: this.getMockProjects() // Return some mock projects for development
                        });
                    }, 500);
                });
            }
            
            // In production, throw the error
            throw error;
        }
    },
    
    async getResearchProjects() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/research`;
        console.log('🔬 ProjectAPI: Fetching research projects from:', endpoint);
        
        // Add retry logic for database connection issues
        for (let attempt = 1; attempt <= 3; attempt++) {
            try {
                console.log(`🔬 Research projects attempt ${attempt}/3`);
                const result = await apiCall(endpoint, 'GET');
                console.log('🔬 Research projects result:', result);
                
                // Ensure we return consistent format
                if (result && result.success) {
                    return {
                        success: true,
                        data: Array.isArray(result.data) ? result.data : []
                    };
                } else if (result && result.status === 500 && attempt < 3) {
                    console.warn(`🔬 Database error on attempt ${attempt}, retrying...`);
                    await new Promise(resolve => setTimeout(resolve, 1000 * attempt)); // Progressive delay
                    continue;
                } else {
                    console.warn('🔬 Research projects API returned unsuccessful result');
                    return { success: false, data: [], error: result?.error || 'Unknown error' };
                }
            } catch (error) {
                console.error(`❌ Error fetching research projects (attempt ${attempt}):`, error);
                if (attempt === 3) {
                    return { success: false, data: [], error: error.message };
                }
                await new Promise(resolve => setTimeout(resolve, 1000 * attempt)); // Progressive delay
            }
        }
        
        return { success: false, data: [], error: 'Max retries exceeded' };
    },
    
    async getEducationalProjects() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/educational`;
        console.log('🎓 ProjectAPI: Fetching educational projects from:', endpoint);
        
        // Add retry logic for database connection issues
        for (let attempt = 1; attempt <= 3; attempt++) {
            try {
                console.log(`🎓 Educational projects attempt ${attempt}/3`);
                const result = await apiCall(endpoint, 'GET');
                console.log('🎓 Educational projects result:', result);
                
                // Ensure we return consistent format
                if (result && result.success) {
                    return {
                        success: true,
                        data: Array.isArray(result.data) ? result.data : []
                    };
                } else if (result && result.status === 500 && attempt < 3) {
                    console.warn(`🎓 Database error on attempt ${attempt}, retrying...`);
                    await new Promise(resolve => setTimeout(resolve, 1000 * attempt)); // Progressive delay
                    continue;
                } else {
                    console.warn('🎓 Educational projects API returned unsuccessful result');
                    return { success: false, data: [], error: result?.error || 'Unknown error' };
                }
            } catch (error) {
                console.error(`❌ Error fetching educational projects (attempt ${attempt}):`, error);
                if (attempt === 3) {
                    return { success: false, data: [], error: error.message };
                }
                await new Promise(resolve => setTimeout(resolve, 1000 * attempt)); // Progressive delay
            }
        }
        
        return { success: false, data: [], error: 'Max retries exceeded' };
    },
    
    async getExtensionProjects() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/extension`;
        console.log('🌍 ProjectAPI: Fetching extension projects from:', endpoint);
        
        // Add retry logic for database connection issues
        for (let attempt = 1; attempt <= 3; attempt++) {
            try {
                console.log(`🌍 Extension projects attempt ${attempt}/3`);
                const result = await apiCall(endpoint, 'GET');
                console.log('🌍 Extension projects result:', result);
                
                // Ensure we return consistent format
                if (result && result.success) {
                    return {
                        success: true,
                        data: Array.isArray(result.data) ? result.data : []
                    };
                } else if (result && result.status === 500 && attempt < 3) {
                    console.warn(`🌍 Database error on attempt ${attempt}, retrying...`);
                    await new Promise(resolve => setTimeout(resolve, 1000 * attempt)); // Progressive delay
                    continue;
                } else {
                    console.warn('🌍 Extension projects API returned unsuccessful result');
                    return { success: false, data: [], error: result?.error || 'Unknown error' };
                }
            } catch (error) {
                console.error(`❌ Error fetching extension projects (attempt ${attempt}):`, error);
                if (attempt === 3) {
                    return { success: false, data: [], error: error.message };
                }
                await new Promise(resolve => setTimeout(resolve, 1000 * attempt)); // Progressive delay
            }
        }
        
        return { success: false, data: [], error: 'Max retries exceeded' };
    },
    
    getMockProjects() {
        return [
            {
                id: 1,
                title: "Sistema de Gestão Acadêmica",
                subtitle: "Desenvolvimento de sistema integrado",
                coordinator: "324567890-",
                description: "Desenvolvimento de um sistema integrado para gestão de atividades acadêmicas.",
                timeline: "2024-2025",
                externalLink: "https://example.com/projeto1",
                duration: "12 meses",
                image: "default-project.jpg",
                complementHours: "60",
                scholarshipAvailable: true,
                scholarshipType: "Iniciação Científica",
                salary: 400.00,
                requirements: "Conhecimento em Java e SQL",
                scholarshipQuantity: 2,
                type: "educational",
                typeId: "EDU001"
            },
            {
                id: 2,
                title: "Análise de Dados Meteorológicos",
                subtitle: "Pesquisa em machine learning aplicado",
                coordinator: "324567890-",
                description: "Pesquisa sobre padrões climáticos utilizando machine learning.",
                timeline: "2023-2024",
                externalLink: "https://example.com/projeto2",
                duration: "18 meses",
                image: "default-project.jpg",
                complementHours: "80",
                scholarshipAvailable: true,
                scholarshipType: "Pesquisa",
                salary: 600.00,
                requirements: "Conhecimento em Python e estatística",
                scholarshipQuantity: 1,
                type: "research",
                typeId: "RES002"
            },
            {
                id: 3,
                title: "Programa de Alfabetização Digital",
                subtitle: "Extensão universitária para a comunidade",
                coordinator: "324567890-",
                description: "Extensão universitária para ensinar informática básica para idosos.",
                timeline: "2024",
                externalLink: "https://example.com/projeto3",
                duration: "6 meses",
                image: "default-project.jpg",
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
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async update(id, projectData) {
        // Determine the correct endpoint based on project type
        let typeEndpoint = '';
        if (projectData.type) {
            const type = projectData.type.toLowerCase();
            switch(type) {
                case 'research':
                    typeEndpoint = '/research';
                    break;
                case 'educational':
                case 'teaching':
                    typeEndpoint = '/educational';
                    break;
                case 'extension':
                    typeEndpoint = '/extension';
                    break;
                default:
                    typeEndpoint = '/research'; // Default fallback
            }
        } else {
            typeEndpoint = '/research'; // Default fallback
        }
        
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}${typeEndpoint}/${id}`;
        return await apiCall(endpoint, 'PUT', projectData);
    },

    // Specific create methods for different project types
    async createResearch(projectData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/research`;
        return await apiCall(endpoint, 'POST', projectData);
    },

    async createEducational(projectData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/educational`;
        return await apiCall(endpoint, 'POST', projectData);
    },

    async createExtension(projectData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/extension`;
        return await apiCall(endpoint, 'POST', projectData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.project}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    }
};

// Student Management API
window.StudentAPI = {
    async create(studentData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.student}`;
        return await apiCall(endpoint, 'POST', studentData);
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.student}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.student}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async update(id, studentData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.student}/${id}`;
        return await apiCall(endpoint, 'PUT', studentData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.student}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    }
};

// Teacher Management API
window.TeacherAPI = {
    async create(teacherData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.teacher}`;
        return await apiCall(endpoint, 'POST', teacherData);
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.teacher}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.teacher}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async update(id, teacherData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.teacher}/${id}`;
        return await apiCall(endpoint, 'PUT', teacherData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.teacher}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    }
};

// Company Management API
window.CompanyAPI = {
    async create(companyData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company}`;
        return await apiCall(endpoint, 'POST', companyData);
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async update(id, companyData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company}/${id}`;
        return await apiCall(endpoint, 'PUT', companyData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    }
};

// External Person Management API
window.ExternalPersonAPI = {
    async create(externalPersonData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.external_person}`;
        return await apiCall(endpoint, 'POST', externalPersonData);
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.external_person}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.external_person}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async update(id, externalPersonData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.external_person}/${id}`;
        return await apiCall(endpoint, 'PUT', externalPersonData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.external_person}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    }
};

// Company-Project Relationship API
window.CompanyProjectAPI = {
    async create(companyProjectData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}`;
        return await apiCall(endpoint, 'POST', companyProjectData);
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async findProjectsByCompanyId(companyId) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}/company/${companyId}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async findCompaniesByProjectId(projectId) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}/project/${projectId}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async deleteRelationship(companyId, projectId) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}/${companyId}/${projectId}`;
        return await apiCall(endpoint, 'DELETE');
    },
    
    async deleteAllProjectsByCompanyId(companyId) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}/company/${companyId}`;
        return await apiCall(endpoint, 'DELETE');
    },
    
    async deleteAllCompaniesByProjectId(projectId) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.company_project}/project/${projectId}`;
        return await apiCall(endpoint, 'DELETE');
    }
};

// Participant Management API
window.ParticipantAPI = {
    async create(participantData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.participant}`;
        
        console.log('🚀 ParticipantAPI: Creating participant application...');
        console.log('📋 Original data:', participantData);
        
        // Transform modern application data to legacy Participant format
        const legacyParticipantData = {
            name: participantData.name || `${participantData.personType} Application`, // Use provided name or default
            cpf: participantData.cpf || '', // Use provided CPF or empty
            phoneNumber: participantData.phoneNumber || '', // Use provided phone or empty
            role: participantData.personType || 'participant', // Use personType as role
            projectId: participantData.projectId
        };
        
        console.log('🔄 Transformed to legacy format:', legacyParticipantData);
        
        try {
            const result = await apiCall(endpoint, 'POST', legacyParticipantData);
            console.log('📨 Participant API Response:', result);
            
            // Check if we got a successful response
            if (result.success && result.data) {
                console.log('✅ Participant created successfully via real API:', result);
                
                // Track this application in localStorage for future checks
                if (participantData.personId && participantData.personType) {
                    this.trackApplication(participantData.projectId, participantData.personId, participantData.personType);
                }
                
                return result;
            } else if (result.status === 500 || !result.success) {
                // Backend returned 500 or other error, fall back to development mode
                console.warn('⚠️ Backend error (status: ' + result.status + '), falling back to development mode');
                throw new Error('Backend error, using fallback');
            }
            
            return result;
        } catch (error) {
            console.error('❌ Error creating participant via real API:', error);
            console.log('🔍 Error details:', error.message, error.stack);
            
            // Always use mock data as fallback when there are errors
            console.log('🧪 Falling back to mock data due to API error');
            console.log('Participant data:', participantData);
            
            // Simulate API response
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve({
                        success: true,
                        data: {
                            id: Math.floor(Math.random() * 1000),
                            ...legacyParticipantData,
                            createdAt: new Date().toISOString()
                        }
                    });
                }, 1000); // Simulate network delay
            });
        }
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.participant}`;
        
        console.log('🚀 ParticipantAPI: Fetching all participants from backend...');
        console.log('📡 Endpoint:', endpoint);
        
        try {
            // Try real API first
            const result = await apiCall(endpoint, 'GET');
            console.log('✅ Participants fetched successfully from backend:', result);
            return result;
        } catch (error) {
            console.error('❌ Error fetching participants from backend:', error);
            
            // Only use development mode as fallback if backend is truly unavailable
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.log('🧪 ParticipantAPI Development mode: Backend unavailable, using empty array fallback');
                
                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve({
                            success: true,
                            data: [] // Return empty array for development
                        });
                    }, 500);
                });
            }
            
            // In production, throw the error
            throw error;
        }
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.participant}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getByProjectId(projectId) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.participant}/byProject/${projectId}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getByPersonId(personId, personType) {
        // This endpoint doesn't exist in the backend, so we'll use the general endpoint
        // and filter results or fall back to development mode
        console.warn('⚠️ ParticipantAPI getByPersonId: Backend endpoint not available, using fallback');
        
        // Fallback for development - always return empty array since endpoint doesn't exist
        console.log('🧪 ParticipantAPI Development mode: Using empty array for getByPersonId');
        return {
            success: true,
            data: [] // Return empty array to indicate no existing applications
        };
    },
    
    async update(id, participantData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.participant}/${id}`;
        return await apiCall(endpoint, 'PUT', participantData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.participant}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    },
    
    async checkExistingApplication(projectId, personId, personType) {
        try {
            // Since the specific endpoint doesn't exist, we'll use localStorage to track applications
            // This is a frontend-only solution that tracks applications per session
            const storageKey = `participant_applications_${personId}`;
            const existingApplications = JSON.parse(localStorage.getItem(storageKey) || '[]');
            
            const hasApplied = existingApplications.some(app => 
                app.projectId === projectId && app.personType === personType
            );
            
            console.log(`🔍 ParticipantAPI: Checking existing application for project ${projectId}, person ${personId}:`, hasApplied);
            return hasApplied;
        } catch (error) {
            console.error('Error checking existing participant application:', error);
            return false;
        }
    },
    
    // Helper method to track applications in localStorage
    trackApplication(projectId, personId, personType) {
        try {
            const storageKey = `participant_applications_${personId}`;
            const existingApplications = JSON.parse(localStorage.getItem(storageKey) || '[]');
            
            // Add this application to the tracking list
            existingApplications.push({
                projectId: projectId,
                personId: personId,
                personType: personType,
                timestamp: new Date().toISOString()
            });
            
            localStorage.setItem(storageKey, JSON.stringify(existingApplications));
            console.log(`📝 ParticipantAPI: Tracked application for project ${projectId}`);
        } catch (error) {
            console.error('Error tracking participant application:', error);
        }
    },

    async addToProject(projectId, participantData) {
        console.log(`🎯 ParticipantAPI: Adding participant directly to project ${projectId}`, participantData);
        
        // For adopted ideas, we want to directly add the student as a confirmed participant
        // This would typically be handled differently than applications
        const directParticipantData = {
            name: participantData.name,
            cpf: participantData.cpf || '',
            phoneNumber: participantData.phoneNumber || '',
            role: participantData.role || 'participant',
            projectId: projectId,
            status: participantData.status || 'accepted', // Direct addition, not pending
            studentId: participantData.studentId // Include student ID for tracking
        };
        
        console.log('📝 Direct participant data:', directParticipantData);
        
        try {
            // Try to use the regular create endpoint but with accepted status
            const result = await this.create(directParticipantData);
            
            if (result.success) {
                console.log('✅ Participant added directly to project successfully');
                
                // Track this as a confirmed participation, not just an application
                const confirmationKey = `koble_confirmed_participations`;
                const existingConfirmations = JSON.parse(localStorage.getItem(confirmationKey) || '[]');
                existingConfirmations.push({
                    projectId: projectId,
                    studentId: participantData.studentId,
                    name: participantData.name,
                    role: participantData.role || 'participant',
                    addedAt: new Date().toISOString(),
                    source: 'adopted_idea'
                });
                localStorage.setItem(confirmationKey, JSON.stringify(existingConfirmations));
                
                return result;
            } else {
                throw new Error(result.error || 'Failed to add participant');
            }
        } catch (error) {
            console.error('❌ Error adding participant to project:', error);
            
            // Fallback: simulate successful addition
            console.log('🧪 Using fallback simulation for participant addition');
            
            return {
                success: true,
                data: {
                    id: Date.now(),
                    ...directParticipantData,
                    createdAt: new Date().toISOString()
                },
                message: 'Participant added successfully (simulated)'
            };
        }
    }
};

// Fellow Management API
window.FellowAPI = {
    async create(fellowData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.fellow}`;
        
        console.log('🚀 FellowAPI: Creating fellow application...');
        console.log('📡 Endpoint:', endpoint);
        console.log('📝 Original Fellow data:', fellowData);
        
        // Transform fellowData to match the backend Fellow model
        // Backend expects: studentId, projectId, cpf, lattesCurriculum
        const backendFellowData = {
            studentId: fellowData.studentId,
            projectId: fellowData.projectId,
            cpf: fellowData.cpf || '',
            lattesCurriculum: fellowData.lattesCurriculum || ''
        };
        
        console.log('🔄 Transformed for backend:', backendFellowData);
        
        try {
            // Try real API first - database schema is now fixed!
            const result = await apiCall(endpoint, 'POST', backendFellowData);
            console.log('📨 API Response:', result);
            
            // Check if we got a successful response
            if (result.success && result.data) {
                console.log('✅ Fellow application created successfully via real API:', result);
                
                // Track this application for future checks
                this.trackApplication(fellowData.projectId, fellowData.studentId);
                
                return result;
            } else if (result.status === 500 || !result.success) {
                // Backend returned 500 or other error, fall back to development mode
                console.warn('⚠️ Backend error (status: ' + result.status + '), falling back to development mode');
                throw new Error('Backend error, using fallback');
            }
            
            return result;
        } catch (error) {
            console.error('❌ Error creating fellow application:', error);
            
            // Fallback to simulation mode if real API fails
            console.log('🧪 FellowAPI Development mode: Backend unavailable, simulating fellow creation');
            
            // Track this application for future checks
            this.trackApplication(fellowData.projectId, fellowData.studentId);
            
            // Create a notification for the teacher
            this.createTeacherNotification(fellowData, backendFellowData);
            
            // Simulate API response
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve({
                        success: true,
                        data: {
                            id: Math.floor(Math.random() * 1000),
                            ...backendFellowData,
                            status: 'PENDING',
                            createdAt: new Date().toISOString()
                        },
                        message: 'Candidatura para bolsa criada! (Modo simulação devido a erro na API)'
                    });
                }, 1000); // Simulate network delay
            });
        }
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.fellow}`;
        
        console.log('🚀 FellowAPI: Fetching all fellows from backend...');
        console.log('📡 Endpoint:', endpoint);
        
        try {
            // Try real API first
            const result = await apiCall(endpoint, 'GET');
            console.log('✅ Fellows fetched successfully from backend:', result);
            return result;
        } catch (error) {
            console.error('❌ Error fetching fellows from backend:', error);
            
            // Only use development mode as fallback if backend is truly unavailable
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.log('🧪 FellowAPI Development mode: Backend unavailable, using empty array fallback');
                
                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve({
                            success: true,
                            data: [] // Return empty array for development
                        });
                    }, 500);
                });
            }
            
            // In production, throw the error
            throw error;
        }
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.fellow}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getByProjectId(projectId) {
        // This endpoint doesn't exist in the backend, use fallback
        console.warn('⚠️ FellowAPI getByProjectId: Backend endpoint not available, using fallback');
        console.log('🧪 FellowAPI Development mode: Using empty array for getByProjectId');
        return {
            success: true,
            data: [] // Return empty array since endpoint doesn't exist
        };
    },
    
    async getByStudentId(studentId) {
        // This endpoint doesn't exist in the backend, so we'll skip the API call
        // and go directly to fallback mode
        console.warn('⚠️ FellowAPI getByStudentId: Backend endpoint not available, using fallback');
        
        // Fallback for development - always return empty array since endpoint doesn't exist
        console.log('🧪 FellowAPI Development mode: Using empty array for getByStudentId');
        return {
            success: true,
            data: [] // Return empty array to indicate no existing applications
        };
    },
    
    async update(id, fellowData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.fellow}/${id}`;
        return await apiCall(endpoint, 'PUT', fellowData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.fellow}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    },
    
    async checkExistingApplication(projectId, studentId) {
        try {
            // Since the endpoint doesn't exist and fellow creation has database issues,
            // we'll use localStorage to track fellow applications
            const storageKey = `fellow_applications_${studentId}`;
            const existingApplications = JSON.parse(localStorage.getItem(storageKey) || '[]');
            
            const hasApplied = existingApplications.some(app => app.projectId === projectId);
            
            console.log(`🔍 FellowAPI: Checking existing fellow application for project ${projectId}, student ${studentId}:`, hasApplied);
            return hasApplied;
        } catch (error) {
            console.error('Error checking existing fellow application:', error);
            return false;
        }
    },
    
    // Helper method to track fellow applications in localStorage
    trackApplication(projectId, studentId) {
        try {
            const storageKey = `fellow_applications_${studentId}`;
            const existingApplications = JSON.parse(localStorage.getItem(storageKey) || '[]');
            
            // Add this application to the tracking list
            existingApplications.push({
                projectId: projectId,
                studentId: studentId,
                timestamp: new Date().toISOString()
            });
            
            localStorage.setItem(storageKey, JSON.stringify(existingApplications));
            console.log(`📝 FellowAPI: Tracked fellow application for project ${projectId}`);
        } catch (error) {
            console.error('Error tracking fellow application:', error);
        }
    },
    
    // Create a notification for teachers about fellow applications
    createTeacherNotification(originalData, fellowData) {
        try {
            const projectId = fellowData.projectId;
            const studentId = fellowData.studentId;
            
            // Get current user info for better application data
            const currentUser = SessionManager.getUser();
            
            // Create application data for teacher management
            const application = {
                id: `fellow_sim_${projectId}_${studentId}_${Date.now()}`,
                type: 'fellow',
                candidateName: currentUser?.name || `Estudante ID: ${studentId}`,
                candidateEmail: currentUser?.email || 'email@exemplo.com',
                candidateType: 'student',
                projectId: projectId,
                projectTitle: originalData.projectTitle || `Projeto ${projectId}`,
                status: 'PENDING',
                applicationDate: new Date().toISOString(),
                originalData: fellowData,
                personData: {
                    id: studentId,
                    name: currentUser?.name || `Estudante ID: ${studentId}`,
                    email: currentUser?.email || 'email@exemplo.com'
                },
                isSimulated: true // Flag to indicate this is a simulated application
            };
            
            // Store in localStorage for teacher access
            const storageKey = `teacher_fellow_applications`;
            const existingApps = JSON.parse(localStorage.getItem(storageKey) || '[]');
            existingApps.push(application);
            localStorage.setItem(storageKey, JSON.stringify(existingApps));
            
            console.log('📧 FellowAPI: Created teacher notification for fellow application');
        } catch (error) {
            console.error('Error creating teacher notification:', error);
        }
    }
};

// ===== IDEAS API =====
window.IdeaAPI = {
    async create(ideaData) {
        console.log('🚀 IdeaAPI.create called with data:', ideaData);
        console.log('🔍 DEBUGGING - IdeaAPI.create detailed data:', JSON.stringify(ideaData, null, 2));
        
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}`;
        console.log('🔍 DEBUGGING - Using endpoint:', endpoint);
        console.log('🔍 DEBUGGING - API_CONFIG:', JSON.stringify(API_CONFIG, null, 2));
        
        try {
            console.log('🔍 DEBUGGING - About to call apiCall function...');
            const result = await apiCall(endpoint, 'POST', ideaData);
            console.log('✅ DEBUGGING - apiCall returned:', JSON.stringify(result, null, 2));
            console.log('✅ Idea created successfully:', result);
            return result;
        } catch (error) {
            console.error('❌ DEBUGGING - Error in IdeaAPI.create:', error);
            console.error('❌ DEBUGGING - Error details:', {
                message: error.message,
                stack: error.stack,
                name: error.name
            });
            
            // Development mode fallback
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.log('🧪 IdeaAPI Development mode: Using mock success response');
                console.log('🔍 DEBUGGING - Returning mock response for:', ideaData);
                
                return new Promise((resolve) => {
                    setTimeout(() => {
                        const mockResult = {
                            success: true,
                            data: {
                                id: Date.now(),
                                ...ideaData,
                                proposer: ideaData.proposer || 'Mock Proposer'
                            }
                        };
                        console.log('🧪 DEBUGGING - Mock response:', mockResult);
                        resolve(mockResult);
                    }, 1000);
                });
            }
            
            throw error;
        }
    },
    
    async getAll() {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}`;
        
        try {
            const result = await apiCall(endpoint, 'GET');
            console.log('✅ Ideas fetched successfully from backend:', result);
            return result;
        } catch (error) {
            console.error('❌ Error fetching ideas from backend:', error);
            
            // Development mode fallback
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                console.log('🧪 IdeaAPI Development mode: Using mock ideas');
                
                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve({
                            success: true,
                            data: this.getMockIdeas()
                        });
                    }, 500);
                });
            }
            
            throw error;
        }
    },
    
    async getById(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async update(id, ideaData) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}/${id}`;
        return await apiCall(endpoint, 'PUT', ideaData);
    },
    
    async delete(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}/${id}`;
        return await apiCall(endpoint, 'DELETE');
    },
    
    async getStudentForIdea(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}/student/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    async getTeacherForIdea(id) {
        const endpoint = `${API_CONFIG.baseURL}${API_CONFIG.endpoints.idea}/teacher/${id}`;
        return await apiCall(endpoint, 'GET');
    },
    
    // Development mode fallback with mock ideas
    getMockIdeas() {
        return [
            {
                id: 1,
                title: "Sistema de Gestão Acadêmica Inteligente",
                subtitle: "Automatização de processos universitários",
                area: "Tecnologia da Informação",
                description: "Desenvolvimento de um sistema web para automatizar processos acadêmicos utilizando inteligência artificial para otimização de recursos.",
                aim: "Melhorar a eficiência dos processos administrativos da universidade",
                type: "Research",
                justification: "A necessidade de digitalização e otimização dos processos acadêmicos é crescente",
                proposer: "João Silva",
                targetAudience: "Administradores e estudantes universitários",
                teacherId: 1,
                studentId: 1
            },
            {
                id: 2,
                title: "Aplicativo para Inclusão Digital de Idosos",
                subtitle: "Tecnologia acessível para terceira idade",
                area: "Tecnologia Social",
                description: "Criação de uma aplicação mobile com interface simplificada para ensinar idosos a utilizarem tecnologias digitais básicas.",
                aim: "Promover inclusão digital e reduzir o isolamento social de idosos",
                type: "Extension",
                justification: "O envelhecimento populacional exige soluções tecnológicas inclusivas",
                proposer: "Maria Santos",
                targetAudience: "Idosos com pouco conhecimento tecnológico",
                teacherId: 2,
                studentId: 2
            },
            {
                id: 3,
                title: "Plataforma de Monitoramento Ambiental",
                subtitle: "IoT para preservação ambiental",
                area: "Meio Ambiente e Tecnologia",
                description: "Desenvolvimento de sensores IoT para monitoramento em tempo real da qualidade do ar e água em áreas urbanas.",
                aim: "Criar ferramenta de monitoramento ambiental acessível",
                type: "Research",
                justification: "A crescente preocupação ambiental demanda ferramentas de monitoramento eficazes",
                proposer: "Carlos Oliveira",
                targetAudience: "Órgãos ambientais e comunidade em geral",
                teacherId: 1,
                studentId: 3
            }
        ];
    }
};

console.log('Shared utilities loaded with updated API endpoints including ParticipantAPI, FellowAPI, and IdeaAPI');

// Initialize SessionManager on page load
SessionManager.init();

// Usage examples:
// ProjectAPI.getAll().then(result => console.log(result));
// StudentAPI.create(studentData).then(result => console.log(result));
// CompanyProjectAPI.findProjectsByCompanyId(1).then(result => console.log(result));
// ParticipantAPI.create(participantData).then(result => console.log(result));
// FellowAPI.create(fellowData).then(result => console.log(result));
// IdeaAPI.create(ideaData).then(result => console.log(result));