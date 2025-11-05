document.addEventListener('DOMContentLoaded', function() {
    // Check authentication requirement for this page
    console.log('=== HOME PAGE LOADED ===');
    console.log('Current user on page load:', SessionManager.getUser());
    console.log('Is logged in on page load:', SessionManager.isLoggedIn());
    
    SessionManager.checkAuthRequirement();
    
    //start of new simple carousel
    initializeSimpleCarousel();

    // Simple, reliable carousel implementation
    async function initializeSimpleCarousel() {
        console.log('🎠 Initializing Simple Carousel');
        
        const track = document.getElementById('carouselTrack');
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');
        const indicators = document.getElementById('carouselIndicators');
        
        if (!track || !prevBtn || !nextBtn || !indicators) {
            console.error('Carousel elements not found');
            return;
        }
        
        let projects = [];
        let currentPage = 0;
        
        // Load projects
        try {
            projects = await loadAllProjects();
            if (projects.length === 0) {
                showEmptyCarousel();
                return;
            }
        } catch (error) {
            console.error('Failed to load projects:', error);
            showEmptyCarousel();
            return;
        }
        
        // Render carousel
        renderCarousel();
        
        // Set up navigation
        prevBtn.onclick = () => {
            if (currentPage > 0) {
                currentPage--;
                updateCarousel();
            }
        };
        
        nextBtn.onclick = () => {
            const totalPages = Math.ceil(projects.length / 3);
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateCarousel();
            }
        };
        
        // Load all projects from API
        async function loadAllProjects() {
            try {
                console.log('🚀 Starting to load all projects...');
                
                const [research, educational, extension] = await Promise.all([
                    ProjectAPI.getResearchProjects()
                        .then(result => {
                            console.log('🔬 Research API response:', result);
                            return result;
                        })
                        .catch(error => {
                            console.error('❌ Research API error:', error);
                            return { success: false, data: [], error: error.message };
                        }),
                    ProjectAPI.getEducationalProjects()
                        .then(result => {
                            console.log('🎓 Educational API response:', result);
                            return result;
                        })
                        .catch(error => {
                            console.error('❌ Educational API error:', error);
                            return { success: false, data: [], error: error.message };
                        }),
                    ProjectAPI.getExtensionProjects()
                        .then(result => {
                            console.log('🌍 Extension API response:', result);
                            return result;
                        })
                        .catch(error => {
                            console.error('❌ Extension API error:', error);
                            return { success: false, data: [], error: error.message };
                        })
                ]);
                
                let allProjects = [];
                
                if (research.success && Array.isArray(research.data)) {
                    console.log(`✅ Adding ${research.data.length} research projects`);
                    allProjects.push(...research.data);
                } else {
                    console.warn('⚠️ Research projects failed or empty:', research);
                }
                
                if (educational.success && Array.isArray(educational.data)) {
                    console.log(`✅ Adding ${educational.data.length} educational projects`);
                    allProjects.push(...educational.data);
                } else {
                    console.warn('⚠️ Educational projects failed or empty:', educational);
                }
                
                if (extension.success && Array.isArray(extension.data)) {
                    console.log(`✅ Adding ${extension.data.length} extension projects`);
                    allProjects.push(...extension.data);
                } else {
                    console.warn('⚠️ Extension projects failed or empty:', extension);
                }
                
                console.log(`📋 Total loaded projects: ${allProjects.length}`);
                console.log('🔍 All projects array:', allProjects);
                return allProjects;
            } catch (error) {
                console.error('💥 Critical error loading projects:', error);
                return [];
            }
        }
        
        // Render the carousel HTML
        function renderCarousel() {
            track.innerHTML = '';
            indicators.innerHTML = '';
            
            // Create slides
            projects.forEach((project, index) => {
                const slide = document.createElement('div');
                slide.className = `carousel-slide slide-bg-${(index % 3) + 1}`;
                slide.style.width = '33.33vw';
                slide.style.cursor = 'pointer';
                
                slide.innerHTML = `
                    <div class="slide-content">
                        <h2>${project.title || 'Untitled Project'}</h2>
                        <h3>${project.subtitle || ''}</h3>
                        <p>${(project.description || '').substring(0, 100)}${project.description && project.description.length > 100 ? '...' : ''}</p>
                        <div class="project-details">
                            <span class="coordinator">Coordenador: ${project.coordinator || 'N/A'}</span>
                            <span class="duration">Duração: ${project.duration || 'N/A'}</span>
                            ${project.scholarshipAvailable ? `<span class="fellowship">Bolsa: R$ ${(project.salary || 0).toFixed(2)}</span>` : ''}
                        </div>
                    </div>
                `;
                
                slide.onclick = () => {
                    sessionStorage.setItem('selectedProject', JSON.stringify(project));
                    window.location.href = '/front/pages/project page/project.html';
                };
                
                track.appendChild(slide);
            });
            
            // Set track width based on number of elements (each 33.33vw)
            track.style.width = `${projects.length * 33.33}vw`;
            
            // Create indicators
            const totalPages = Math.ceil(projects.length / 3);
            for (let i = 0; i < totalPages; i++) {
                const indicator = document.createElement('button');
                indicator.className = `indicator ${i === 0 ? 'active' : ''}`;
                indicator.onclick = () => {
                    currentPage = i;
                    updateCarousel();
                };
                indicators.appendChild(indicator);
            }
            
            updateCarousel();
        }
        
        // Update carousel position and indicators
        function updateCarousel() {
            // Move by 100vw (3 slides × 33.33vw each) per page to match slide widths
            const translateX = -(currentPage * 100);
            track.style.transform = `translateX(${translateX}vw)`;
            
            // Update indicators
            const allIndicators = indicators.querySelectorAll('.indicator');
            allIndicators.forEach((indicator, index) => {
                indicator.classList.toggle('active', index === currentPage);
            });
            
            // Update button states
            const totalPages = Math.ceil(projects.length / 3);
            prevBtn.disabled = currentPage === 0;
            nextBtn.disabled = currentPage === totalPages - 1;
        }
        
        // Show empty state
        function showEmptyCarousel() {
            track.innerHTML = `
                <div class="carousel-slide" style="width: 100vw;">
                    <div class="slide-content">
                        <h3>📋 Nenhum projeto disponível</h3>
                        <p>Não há projetos para exibir no momento.</p>
                    </div>
                </div>
            `;
            indicators.innerHTML = '<button class="indicator active"></button>';
            prevBtn.disabled = true;
            nextBtn.disabled = true;
        }
    }

    function redirectExtension(){
        div = document.getElementById("extensionHomeRedirect");

        if (div) {
            div.addEventListener('click', function(){
                window.location.href = '/front/pages/specific homes/extensionHome.html'
            })
        }
    }

    function redirectEnsino(){
        div = document.getElementById("teachingHomeRedirect");

        if (div) {
            div.addEventListener('click', function(){
                window.location.href = '/front/pages/specific homes/teachingHome.html'
            })
        }
    }

    function redirectResearch(){
        div = document.getElementById("researchHomeRedirect");

        if (div) {
            div.addEventListener('click', function(){
                window.location.href = '/front/pages/specific homes/researchHome.html'
            })
        }
    }

    //redirects
    redirectResearch();
    redirectEnsino();
    redirectExtension();
});