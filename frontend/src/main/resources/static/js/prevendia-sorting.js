document.addEventListener('DOMContentLoaded', function() {
    // Initialisation du tri des colonnes
    initTableSorting();
});

function initTableSorting() {
    const table = document.getElementById('patientsTable');
    if (!table) return;

    const sortableHeaders = table.querySelectorAll('th.sortable');
    
    sortableHeaders.forEach(header => {
        header.addEventListener('click', function() {
            const sortType = this.dataset.sort;
            const currentDirection = this.classList.contains('asc') ? 'asc' : 
                                    this.classList.contains('desc') ? 'desc' : 'asc';
            
            // Réinitialiser les classes de tri sur tous les en-têtes
            sortableHeaders.forEach(h => {
                h.classList.remove('asc', 'desc');
            });
            
            // Appliquer la classe de tri sur l'en-tête cliqué
            this.classList.add(currentDirection);
            
            // Trier le tableau
            sortTable(table, sortType, currentDirection);
        });
    });
}

function sortTable(table, sortType, direction) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    // Trier les lignes
    rows.sort((a, b) => {
        let valueA = a.dataset[sortType];
        let valueB = b.dataset[sortType];
        
        // Gestion spéciale pour les dates
        if (sortType === 'birthdate') {
            valueA = new Date(valueA);
            valueB = new Date(valueB);
        }
        
        // Comparaison
        if (valueA < valueB) {
            return direction === 'asc' ? -1 : 1;
        } else if (valueA > valueB) {
            return direction === 'asc' ? 1 : -1;
        } else {
            return 0;
        }
    });
    
    // Réorganiser les lignes dans le DOM
    rows.forEach(row => {
        tbody.appendChild(row);
    });
} 