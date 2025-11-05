// Teaching Home - Shows only Educational type projects
document.addEventListener('DOMContentLoaded', function() {
    console.log('Teaching Home loaded - filtering Educational projects');
    initializeSpecificCarousel('educational');
});

async function initializeSpecificCarousel(type) {
    console.log(`🎠 Initializing ${type} carousel`);
    
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
    
    // Load specific type projects
    try {
        console.log(`🚀 Loading ${type} projects...`);
        let result;
        switch (type.toLowerCase()) {
            case 'educational':
                result = await ProjectAPI.getEducationalProjects();
                break;
        }
        
        console.log(`📡 API Response for ${type}:`, result);
        
        if (result && result.success && Array.isArray(result.data)) {
            projects = result.data;
            console.log(`✅ Successfully loaded ${projects.length} ${type} projects`);
        } else {
            console.warn(`⚠️ API response invalid for ${type}:`, result);
            projects = [];
        }
        
        if (projects.length === 0) {
            console.log(`📋 No ${type} projects found, showing empty carousel`);
            showEmptyCarousel(type);
            return;
        }
        
        renderCarousel();
        
    } catch (error) {
        console.error(`💥 Critical error loading ${type} projects:`, error);
        showEmptyCarousel(type);
        return;
    }
    
    // Navigation
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
    
    function showEmptyCarousel(projectType) {
        track.innerHTML = `
            <div class="carousel-slide" style="width: 100vw;">
                <div class="slide-content">
                    <h3>📋 Nenhum projeto de ${projectType} disponível</h3>
                    <p>Não há projetos de ${projectType} para exibir no momento.</p>
                </div>
            </div>
        `;
        indicators.innerHTML = '<button class="indicator active"></button>';
        prevBtn.disabled = true;
        nextBtn.disabled = true;
    }
}