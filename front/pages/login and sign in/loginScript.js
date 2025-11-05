document.addEventListener("DOMContentLoaded", () => {
    const isProfessor = document.getElementById("checkTeacher");
    const birthDateInput = document.getElementById("birthDateInput");
    const isCompany = document.getElementById("checkCompany");
    const cnpjInput = document.getElementById("cnpjInput");
    const loginForm = document.querySelector(".forms");

    // ===== Função auxiliar para animação =====
    function toggleAnimated(element, show) {
        if (!element) return;
        element.classList.remove("visibleField", "hiddenField");
        void element.offsetWidth; // reinicia a animação
        element.classList.add(show ? "visibleField" : "hiddenField");
        setTimeout(() => {
            element.style.display = show ? "block" : "none";
        }, show ? 0 : 300);
    }

    // ===== Institucional: esconder/mostrar data de nascimento =====
    if (isProfessor && birthDateInput) {
        toggleAnimated(birthDateInput, true);
        isProfessor.addEventListener("change", () => {
            toggleAnimated(birthDateInput, !isProfessor.checked);
        });
    }

    // ===== Externo: esconder/mostrar CNPJ =====
    if (isCompany && cnpjInput) {
        toggleAnimated(cnpjInput, false);
        isCompany.addEventListener("change", () => {
            toggleAnimated(cnpjInput, isCompany.checked);
        });
    }

    // ===== Redirecionar após login =====
    if (loginForm) {
        console.log('Login form found, adding event listener');
        loginForm.addEventListener("submit", async (e) => {
            console.log('Form submitted!');
            e.preventDefault();
            
            // Check if this is a registration form or login form
            const isRegistration = document.getElementById('nameInput') !== null;
            console.log('Is registration form:', isRegistration);
            
            if (isRegistration) {
                console.log('Handling registration');
                await handleRegistration(e.target);
            } else {
                console.log('Handling login');
                await handleLogin(e.target);
            }
        });
    } else {
        console.log('No login form found!');
    }
    
    // ===== Handle Registration =====
    async function handleRegistration(form) {
        try {
            // Get basic form data
            let userData = {
                name: document.getElementById('nameInput').value,
                email: document.getElementById('emailInput').value,
                password: document.getElementById('passwordInput').value,
                phoneNumber: document.getElementById('phoneNumberInput')?.value || document.getElementById('cellphoneInput')?.value
            };
            
            // Add specific fields based on user type
            const isTeacher = document.getElementById('checkTeacher')?.checked;
            const isCompany = document.getElementById('checkCompany')?.checked;
            const registrationInput = document.getElementById('registrationInput');
            const cnpjInput = document.getElementById('cnpjInput');
            const birthDateInput = document.getElementById('birthDateInput');
            
            // Determine user type and add specific fields matching the Java models
            let userType;
            if (isTeacher) {
                userType = 'teacher';
                // Teacher model expects: siape, email, name, password, phoneNumber
                if (registrationInput) userData.siape = registrationInput.value;
                // Teachers don't have birthDate in the model
            } else if (isCompany) {
                userType = 'company';
                // Company model expects: cnpj, email, name, password, phoneNumber
                if (cnpjInput && cnpjInput.value) userData.cnpj = cnpjInput.value;
            } else if (registrationInput) {
                userType = 'student';
                // SIMPLIFIED: Create new object with ONLY the exact fields Java expects
                const studentData = {
                    name: userData.name,
                    email: userData.email, 
                    password: userData.password,
                    phoneNumber: userData.phoneNumber,
                    registration: registrationInput.value,
                    birthDate: birthDateInput?.value || null
                };
                userData = studentData; // Replace userData with clean student object
                console.log('SIMPLIFIED Student data:', userData);
            } else {
                // External form logic: if CNPJ is filled, it's a company, otherwise external person
                const cnpjValue = cnpjInput?.value?.trim();
                if (cnpjValue) {
                    userType = 'company';
                    userData.cnpj = cnpjValue;
                } else {
                    userType = 'external';
                    // ExternalPerson model expects: name, email, password, phoneNumber
                }
            }
            
            console.log('Registering user:', userType, userData);
            console.log('=== REGISTRATION DEBUG ===');
            console.log('User type determined:', userType);
            console.log('User data to send:', JSON.stringify(userData, null, 2));
            
            // MINIMAL TEST: Try sending the simplest possible student object
            if (userType === 'student') {
                const minimalStudent = {
                    name: "Test Student",
                    email: "test@student.com", 
                    password: "password123",
                    phoneNumber: "1234567890",
                    registration: "12345"
                    // Temporarily removing birthDate to test
                };
                console.log('TESTING WITH MINIMAL STUDENT (no birthDate):', minimalStudent);
                const testResult = await AuthAPI.register(minimalStudent, userType);
                console.log('Minimal test result:', testResult);
                if (testResult.success) {
                    console.log('Minimal test SUCCEEDED! Issue might be with birthDate');
                    return; // Stop here if minimal test works
                } else {
                    console.log('Minimal test FAILED too - issue is deeper');
                }
            }
            
            // Call registration API - this will use the correct controller endpoints
            const result = await AuthAPI.register(userData, userType);
            console.log('Registration result:', result);
            
            if (result.success) {
                alert('Cadastro realizado com sucesso!');
                // Redirect to login page since login is not implemented yet
                window.location.href = '/front/pages/login and sign in/login.html';
            } else {
                alert('Erro no cadastro: ' + (result.error || 'Erro desconhecido'));
            }
        } catch (error) {
            console.error('Registration error:', error);
            alert('Erro no cadastro: ' + error.message);
        }
    }
    
    // ===== Handle Login =====
    async function handleLogin(form) {
        console.log('=== HANDLELOGIN CALLED ===');
        try {
            const email = document.getElementById('emailInput').value;
            const password = document.getElementById('passwordInput').value;
            
            console.log('Attempting login for:', email);
            console.log('Using password:', password); // Remove this in production
            
            const result = await AuthAPI.login(email, password);
            console.log('Login result:', result);
            
            if (result.success) {
                console.log('Login successful, setting user session:', result.data);
                SessionManager.setUser(result.data);
                console.log('Session set, waiting briefly then redirecting to home...');
                
                // Small delay to ensure localStorage is written
                setTimeout(() => {
                    window.location.href = '../../home.html';
                }, 100);
            } else {
                console.error('Login failed:', result.error);
                alert('Erro no login: ' + (result.error || 'Credenciais inválidas'));
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('Erro no login: ' + error.message);
        }
    }
});
