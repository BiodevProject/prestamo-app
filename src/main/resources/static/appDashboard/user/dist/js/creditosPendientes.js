var allCreditos = [];
var currentPage = 1;
var creditosPerPage = 5;
var filteredData = [];
var selectedCreditoId = null;
var currentTab = 'activos'; // Default tab

document.addEventListener('DOMContentLoaded', function() {
    loadCreditoData();
    switchTab('activos'); // Start with activos tab
    setupContextMenu();
});

function loadCreditoData() {
    var creditoDataDivs = document.querySelectorAll('#creditoData');
    allCreditos = [];
    creditoDataDivs.forEach(function(div) {
        allCreditos.push({
            id: div.getAttribute('data-id'),
            monto: div.getAttribute('data-monto') || '',
            plazo: div.getAttribute('data-plazo') || '',
            estado: div.getAttribute('data-estado') || '',
            fecha: div.getAttribute('data-fecha') || ''
        });
    });
}

function switchTab(tabName) {
    currentTab = tabName;
    currentPage = 1; // Reset to first page when switching tabs
    
    // Update tab buttons
    document.querySelectorAll('.nav-tabs .nav-link').forEach(function(tab) {
        tab.classList.remove('active');
    });
    document.getElementById(tabName + '-tab').classList.add('active');
    
    // Filter data based on tab
    filterDataByTab();
    
    // Update table and pagination
    updateTable();
    updatePagination();
    
    // Clear search input
    document.getElementById('searchInput').value = '';
}

function filterDataByTab() {
    switch(currentTab) {
        case 'activos':
            filteredData = allCreditos.filter(function(credito) {
                return credito.estado.toLowerCase() === 'activo' || 
                       credito.estado.toLowerCase() === 'aceptado';
            });
            break;
        case 'pendientes':
            filteredData = allCreditos.filter(function(credito) {
                return credito.estado.toLowerCase() === 'pendiente';
            });
            break;
        case 'rechazados':
            filteredData = allCreditos.filter(function(credito) {
                return credito.estado.toLowerCase() === 'rechazado';
            });
            break;
        default:
    filteredData = [...allCreditos];
    }
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    
    // First filter by current tab
    filterDataByTab();
    
    // Then apply search filter
    filteredData = filteredData.filter(function(credito) {
        return credito.monto.toLowerCase().includes(filter) ||
               credito.plazo.toLowerCase().includes(filter) ||
               credito.estado.toLowerCase().includes(filter) ||
               credito.fecha.toLowerCase().includes(filter);
    });
    
    currentPage = 1;
    updateTable();
    updatePagination();
}

function getEstadoClass(estado) {
    var estadoLower = estado.toLowerCase();
    if (estadoLower === 'activo' || estadoLower === 'aceptado') {
        return 'estado-activo';
    } else if (estadoLower === 'pendiente') {
        return 'estado-pendiente';
    } else if (estadoLower === 'rechazado') {
        return 'estado-rechazado';
    }
    return '';
}

function updateTable() {
    var tbody = document.getElementById("creditoTableBody");
    var startIndex = (currentPage - 1) * creditosPerPage;
    var endIndex = startIndex + creditosPerPage;
    var pageCreditos = filteredData.slice(startIndex, endIndex);
    tbody.innerHTML = '';
    
    if (pageCreditos.length === 0) {
        var emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="5" class="text-center">No hay créditos en esta categoría</td>';
        tbody.appendChild(emptyRow);
        return;
    }
    
    pageCreditos.forEach(function(credito) {
        var row = document.createElement('tr');
        row.className = 'clickable-row';
        
        var estadoClass = getEstadoClass(credito.estado);
        var estadoDisplay = credito.estado.charAt(0).toUpperCase() + credito.estado.slice(1);
        
        row.innerHTML = `
            <td><span class="${estadoClass}">${estadoDisplay}</span></td>
            <td>${credito.monto}</td>
            <td>${credito.plazo}</td>
            <td>${credito.fecha}</td>
            <td><a href="#" class="btn btn-info btn-sm" onclick="showCreditoDetails('${credito.id}')">Ver Detalles</a></td>
        `;
        
        // Add right-click event for context menu
        row.addEventListener('contextmenu', function(e) {
            showContextMenu(e, credito.id);
        });
        
        // Add left-click event to also show context menu (optional)
        row.addEventListener('click', function(e) {
            // Only trigger if not clicking on the button
            if (!e.target.closest('.btn')) {
                showContextMenu(e, credito.id);
            }
        });
        
        tbody.appendChild(row);
    });
}

function updatePagination() {
    var totalPages = Math.ceil(filteredData.length / creditosPerPage);
    var pageInfo = document.getElementById("pageInfo");
    var totalPagesSpan = document.getElementById("totalPages");
    var prevBtn = document.getElementById("prevBtn");
    var nextBtn = document.getElementById("nextBtn");
    
    // Check if all required elements exist
    if (!pageInfo || !totalPagesSpan || !prevBtn || !nextBtn) {
        console.error("Pagination elements not found:", {
            pageInfo: !!pageInfo,
            totalPagesSpan: !!totalPagesSpan,
            prevBtn: !!prevBtn,
            nextBtn: !!nextBtn
        });
        return;
    }
    
    // Ensure currentPage doesn't exceed totalPages
    if (currentPage > totalPages && totalPages > 0) {
        currentPage = totalPages;
    }
    
    // Update the page info text (now just "Página X de ")
    pageInfo.textContent = `Página ${currentPage} de `;
    totalPagesSpan.textContent = totalPages;
    
    // Update button states
    var isFirstPage = currentPage <= 1;
    var isLastPage = currentPage >= totalPages;
    
    prevBtn.disabled = isFirstPage;
    nextBtn.disabled = isLastPage;
    
    // Update CSS classes for styling
    if (isFirstPage) {
        prevBtn.classList.add('disabled');
    } else {
        prevBtn.classList.remove('disabled');
    }
    
    if (isLastPage) {
        nextBtn.classList.add('disabled');
    } else {
        nextBtn.classList.remove('disabled');
    }
    
    console.log(`Pagination: Page ${currentPage} of ${totalPages}, Prev disabled: ${isFirstPage}, Next disabled: ${isLastPage}`);
}

function previousPage() {
    console.log(`Previous page clicked. Current page: ${currentPage}`);
    if (currentPage > 1) {
        currentPage--;
        console.log(`Going to page: ${currentPage}`);
        updateTable();
        updatePagination();
    } else {
        console.log('Already on first page');
    }
}

function nextPage() {
    var totalPages = Math.ceil(filteredData.length / creditosPerPage);
    console.log(`Next page clicked. Current page: ${currentPage}, Total pages: ${totalPages}`);
    if (currentPage < totalPages) {
        currentPage++;
        console.log(`Going to page: ${currentPage}`);
        updateTable();
        updatePagination();
    } else {
        console.log('Already on last page');
    }
}

function showCreditoDetails(creditoId) {
    // Set the selected credit ID for modal buttons
    selectedCreditoId = creditoId;
    
    // Show loading state
    document.getElementById('creditoModalBody').innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></i> Cargando...</div>';
    
    // Show the modal
    var modal = new bootstrap.Modal(document.getElementById('creditoModal'));
    modal.show();
    
    // Fetch credit details via AJAX - using user endpoint
    fetch(`/usuario/creditos/detalle/${creditoId}/modal`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('creditoModalBody').innerHTML = html;
            // Initialize collapsible sections after content is loaded
            initializeModalSections();
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('creditoModalBody').innerHTML = '<div class="alert alert-danger">Error al cargar los detalles del crédito</div>';
        });
}

// Collapsible section functions for modal content
function toggleSection(sectionId) {
    console.log('Toggling section:', sectionId);
    
    // Define section pairs - updated for new order
    const sectionPairs = {
        'personal-info': 'account-info',
        'account-info': 'personal-info',
        'work-info': 'contact-info',
        'contact-info': 'work-info'
    };
    
    const pairedSectionId = sectionPairs[sectionId];
    
    // Toggle the clicked section
    toggleSingleSection(sectionId);
    
    // If this section has a pair, toggle the paired section too
    if (pairedSectionId) {
        console.log('Toggling paired section:', pairedSectionId);
        toggleSingleSection(pairedSectionId);
    }
}

function toggleSingleSection(sectionId) {
    const content = document.getElementById(sectionId + '-content');
    const toggle = document.getElementById(sectionId + '-toggle');
    
    if (!content || !toggle) {
        console.error('Elements not found for section:', sectionId);
        return;
    }
    
    // Check if collapsed by looking at inline styles
    const isCollapsed = content.style.maxHeight === '0px' || content.style.maxHeight === '';
    
    console.log('Section collapsed state:', isCollapsed);
    
    if (isCollapsed) {
        // Expand
        content.classList.remove('collapsed');
        toggle.classList.remove('collapsed');
        content.style.maxHeight = '1000px';
        content.style.paddingTop = '20px';
        content.style.paddingBottom = '20px';
        content.style.overflow = 'visible';
        console.log('Expanding section:', sectionId);
    } else {
        // Collapse
        content.classList.add('collapsed');
        toggle.classList.add('collapsed');
        content.style.maxHeight = '0px';
        content.style.paddingTop = '0px';
        content.style.paddingBottom = '0px';
        content.style.overflow = 'hidden';
        console.log('Collapsing section:', sectionId);
    }
}

// Initialize sections in modal - all collapsed except credit summary
function initializeModalSections() {
    console.log('Initializing modal sections...');
    
    // Collapse all sections except credit summary
    const sectionsToCollapse = [
        'personal-info',
        'contact-info', 
        'work-info',
        'account-info',
        'references',
        'application-details'
    ];
    
    sectionsToCollapse.forEach(function(sectionId) {
        const content = document.getElementById(sectionId + '-content');
        const toggle = document.getElementById(sectionId + '-toggle');
        
        if (content && toggle) {
            content.classList.add('collapsed');
            toggle.classList.add('collapsed');
            content.style.maxHeight = '0px';
            content.style.paddingTop = '0px';
            content.style.paddingBottom = '0px';
            content.style.overflow = 'hidden';
            console.log('Collapsed section:', sectionId);
        }
    });
    
    // Ensure credit summary is expanded
    const creditSummaryContent = document.getElementById('credit-summary-content');
    const creditSummaryToggle = document.getElementById('credit-summary-toggle');
    
    if (creditSummaryContent && creditSummaryToggle) {
        creditSummaryContent.classList.remove('collapsed');
        creditSummaryToggle.classList.remove('collapsed');
        creditSummaryContent.style.maxHeight = '1000px';
        creditSummaryContent.style.paddingTop = '20px';
        creditSummaryContent.style.paddingBottom = '20px';
        creditSummaryContent.style.overflow = 'visible';
        console.log('Expanded credit summary');
    }
}

function setupContextMenu() {
    // Hide context menu when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.context-menu') && !e.target.closest('.clickable-row')) {
            hideContextMenu();
        }
    });
    
    // Hide context menu on escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            hideContextMenu();
        }
    });
}

function showContextMenu(e, creditoId) {
    e.preventDefault();
    e.stopPropagation();
    
    selectedCreditoId = creditoId;
    const contextMenu = document.getElementById('contextMenu');
    
    // Position the context menu
    contextMenu.style.left = e.pageX + 'px';
    contextMenu.style.top = e.pageY + 'px';
    contextMenu.style.display = 'block';
    
    // Ensure menu doesn't go off-screen
    const rect = contextMenu.getBoundingClientRect();
    const windowWidth = window.innerWidth;
    const windowHeight = window.innerHeight;
    
    if (rect.right > windowWidth) {
        contextMenu.style.left = (e.pageX - rect.width) + 'px';
    }
    
    if (rect.bottom > windowHeight) {
        contextMenu.style.top = (e.pageY - rect.height) + 'px';
    }
}

function hideContextMenu() {
    const contextMenu = document.getElementById('contextMenu');
    contextMenu.style.display = 'none';
    selectedCreditoId = null;
}

function contextMenuVerDetalles() {
    if (selectedCreditoId) {
        showCreditoDetails(selectedCreditoId);
        hideContextMenu();
    }
} 