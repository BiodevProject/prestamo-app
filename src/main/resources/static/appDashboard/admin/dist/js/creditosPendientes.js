var allCreditos = [];
var currentPage = 1;
var creditosPerPage = 5;
var filteredData = [];
var selectedCreditoId = null;

document.addEventListener('DOMContentLoaded', function() {
    // Small delay to ensure all elements are rendered
    setTimeout(function() {
        loadCreditoData();
        updateTable();
        updatePagination();
        setupContextMenu();
    }, 100);
});

function loadCreditoData() {
    var creditoDataDivs = document.querySelectorAll('#creditoData');
    allCreditos = [];
    creditoDataDivs.forEach(function(div) {
        allCreditos.push({
            id: div.getAttribute('data-id'),
            usuario: div.getAttribute('data-usuario') || '',
            monto: div.getAttribute('data-monto') || '',
            plazo: div.getAttribute('data-plazo') || '',
            estado: div.getAttribute('data-estado') || ''
        });
    });
    filteredData = [...allCreditos];
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    filteredData = allCreditos.filter(function(credito) {
        return credito.usuario.toLowerCase().includes(filter) ||
               credito.monto.toLowerCase().includes(filter) ||
               credito.plazo.toLowerCase().includes(filter) ||
               credito.estado.toLowerCase().includes(filter);
    });
    currentPage = 1;
    updateTable();
    updatePagination();
}

function updateTable() {
    var tbody = document.getElementById("creditoTableBody");
    var startIndex = (currentPage - 1) * creditosPerPage;
    var endIndex = startIndex + creditosPerPage;
    var pageCreditos = filteredData.slice(startIndex, endIndex);
    tbody.innerHTML = '';
    pageCreditos.forEach(function(credito) {
        var row = document.createElement('tr');
        row.className = 'clickable-row';
        row.innerHTML = `
            <td>${credito.id}</td>
            <td>${credito.usuario}</td>
            <td>${credito.monto}</td>
            <td>${credito.plazo}</td>
            <td>${credito.estado}</td>
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
    
    // Fetch credit details via AJAX
    fetch(`/admin/creditos/detalle/${creditoId}/modal`)
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

function contextMenuAceptarCredito() {
    if (selectedCreditoId) {
        // Show confirmation dialog
        if (confirm('¿Está seguro de que desea aceptar este crédito?')) {
            // Make POST request to accept the credit
            fetch(`/credito/accept/${selectedCreditoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
            .then(response => {
                if (response.ok) {
                    // Refresh the page to show updated data
                    window.location.reload();
                } else {
                    alert('Error al aceptar el crédito. Por favor, intente nuevamente.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al aceptar el crédito. Por favor, intente nuevamente.');
            });
        }
        hideContextMenu();
    }
}

function contextMenuDeclinarCredito() {
    if (selectedCreditoId) {
        console.log('Declinar crédito:', selectedCreditoId);
        // TODO: Implement decline credit functionality
        alert('Función de declinar crédito será implementada próximamente');
        hideContextMenu();
    }
}

// Modal functions for Accept and Decline buttons
function modalAceptarCredito() {
    if (selectedCreditoId) {
        // Show confirmation dialog
        if (confirm('¿Está seguro de que desea aceptar este crédito?')) {
            // Make POST request to accept the credit
            fetch(`/credito/accept/${selectedCreditoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
            .then(response => {
                if (response.ok) {
                    // Close the modal
                    const modal = bootstrap.Modal.getInstance(document.getElementById('creditoModal'));
                    modal.hide();
                    // Refresh the page to show updated data
                    window.location.reload();
                } else {
                    alert('Error al aceptar el crédito. Por favor, intente nuevamente.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error al aceptar el crédito. Por favor, intente nuevamente.');
            });
        }
    }
}

function modalDeclinarCredito() {
    if (selectedCreditoId) {
        console.log('Declinar crédito desde modal:', selectedCreditoId);
        // TODO: Implement decline credit functionality
        alert('Función de declinar crédito será implementada próximamente');
    }
} 