document.addEventListener('DOMContentLoaded', function() {
    // Search Bar Elements
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const searchResults = document.getElementById('searchResults');

    // Sample data - replace with your actual data or API calls
    const sampleData = [
        { type: 'project', title: 'AI Research Project', description: 'Machine Learning Applications' },
        { type: 'project', title: 'Web Development Project', description: 'Full Stack Development' },
        { type: 'project', title: 'Data Science Initiative', description: 'Big Data Analytics' },
        { type: 'teacher', title: 'Dr. Smith', description: 'Computer Science Professor' },
        { type: 'teacher', title: 'Prof. Johnson', description: 'Data Science Researcher' },
        { type: 'company', title: 'Tech Corp', description: 'Software Development Company' },
        { type: 'company', title: 'Innovation Labs', description: 'AI Research Company' }
    ];

    // Search function
    async function performSearch(query) {
        if (query.trim() === '') {
            hideSearchResults();
            return;
        }

        // Filter data based on query
        try{
            results = sampleData;
            displaySearchResults(results);
        }catch(error){
            console.log(error);
            displaySearchResults([]);
        }

        /*try {
            const response = await fetch(`http://localhost:8080/api/search?q=${encodeURIComponent(query)}`);
            const results = await response.json();
            displaySearchResults(results);
        } catch (error) {
            console.error('Search error:', error);
            displaySearchResults([]);
        }
        */
    }

    // Display search results
    function displaySearchResults(results) {
        searchResults.innerHTML = '';

        if (results.length === 0) {
            searchResults.innerHTML = '<div class="result-item">No results found</div>';
        } else {
            results.forEach(result => {
                const resultItem = document.createElement('div');
                resultItem.className = 'result-item';
                resultItem.innerHTML = `
                    <strong>${result.title}</strong> <span style="color: #666;">(${result.type})</span>
                    <br>
                    <small style="color: #888;">${result.description}</small>
                `;
                
                // Add click handler for each result
                resultItem.addEventListener('click', () => {
                    selectSearchResult(result);
                });

                searchResults.appendChild(resultItem);
            });
        }

        showSearchResults();
    }

    // Show search results
    function showSearchResults() {
        searchResults.classList.remove('hidden');
    }

    // Hide search results
    function hideSearchResults() {
        searchResults.classList.add('hidden');
    }

    // Handle result selection
    function selectSearchResult(result) {
        searchInput.value = result.title;
        hideSearchResults();
        
        // You can add navigation logic here
        console.log('Selected:', result);
        
        // Example: redirect based on type
        switch(result.type) {
            case 'project':
                // window.location.href = `pages/project-details.html?id=${result.id}`;
                alert(`Opening project: ${result.title}`);
                break;
            case 'teacher':
                // window.location.href = `pages/teacher-profile.html?id=${result.id}`;
                alert(`Opening teacher profile: ${result.title}`);
                break;
            case 'company':
                // window.location.href = `pages/company-profile.html?id=${result.id}`;
                alert(`Opening company profile: ${result.title}`);
                break;
        }
    }

    // Event Listeners
    searchInput.addEventListener('input', function() {
        const query = this.value;
        performSearch(query);
    });

    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            const query = this.value;
            performSearch(query);
        }
    });

    searchBtn.addEventListener('click', function() {
        const query = searchInput.value;
        performSearch(query);
    });

    // Hide search results when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.search-container')) {
            hideSearchResults();
        }
    });

});

/* Optional: Function to integrate with your Spring Boot API
async function searchAPI(query) {
    try {
        // Example API call to your Spring Boot backend
        const response = await fetch(`http://localhost:8080/api/search?q=${encodeURIComponent(query)}`);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Search API error:', error);
        return [];
    }
}
*/