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
        
        // Add event listener for financial charges modal
        const financialModal = document.getElementById('cargosFinancierosModal');
        if (financialModal) {
            financialModal.addEventListener('shown.bs.modal', function() {
                // Trigger calculation when modal is shown
                setTimeout(function() {
                    calculateFinancialCharges();
                }, 100);
            });
        }
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
        emptyRow.innerHTML = '<td colspan="5" class="text-center">No hay créditos para mostrar</td>';
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
            <td>${credito.usuario}</td>
            <td>${credito.monto}</td>
            <td>${credito.plazo}</td>
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
    var currentPageSpan = document.getElementById("currentPage");
    var totalPagesSpan = document.getElementById("totalPages");
    var prevBtn = document.getElementById("prevBtn");
    var nextBtn = document.getElementById("nextBtn");
    if (currentPageSpan) currentPageSpan.textContent = currentPage;
    if (totalPagesSpan) totalPagesSpan.textContent = totalPages;
    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage === totalPages;
    if (prevBtn.disabled) { prevBtn.classList.add('disabled'); } else { prevBtn.classList.remove('disabled'); }
    if (nextBtn.disabled) { nextBtn.classList.add('disabled'); } else { nextBtn.classList.remove('disabled'); }
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
        // Store the ID before hiding the context menu
        const creditoIdToAccept = selectedCreditoId;
        
        // Hide the context menu
        hideContextMenu();
        
        // Show confirmation dialog
        if (confirm('¿Está seguro de que desea aceptar este crédito? Esta acción no se puede deshacer.')) {
            // Set the creditoId in the hidden input of the new modal
            document.getElementById('creditoIdInput').value = creditoIdToAccept;
            
            // Set credit data for calculations
            setCreditoDataForCalculation(creditoIdToAccept);
            
            // Show the financial charges modal
            var financialModal = new bootstrap.Modal(document.getElementById('cargosFinancierosModal'));
            financialModal.show();
        }
    }
}

function contextMenuRechazarCredito() {
    if (selectedCreditoId) {
        // Store the ID before hiding the context menu
        const creditoIdToRechazar = selectedCreditoId;
        
        // Hide the context menu
        hideContextMenu();
        
        // Show confirmation dialog
        if (confirm('¿Está seguro de que desea rechazar este crédito? Esta acción no se puede deshacer.')) {
            rechazarCredito(creditoIdToRechazar);
        }
    }
}

// Modal functions for Accept and Reject buttons
function modalAceptarCredito() {
    if (selectedCreditoId) {
        // Hide the details modal
        var detailsModal = bootstrap.Modal.getInstance(document.getElementById('creditoModal'));
        detailsModal.hide();
        
        // Show confirmation dialog
        if (confirm('¿Está seguro de que desea aceptar este crédito? Esta acción no se puede deshacer.')) {
            // Set the creditoId in the hidden input of the new modal
            document.getElementById('creditoIdInput').value = selectedCreditoId;
            
            // Set credit data for calculations
            setCreditoDataForCalculation(selectedCreditoId);
            
            // Show the financial charges modal
            var financialModal = new bootstrap.Modal(document.getElementById('cargosFinancierosModal'));
            financialModal.show();
        }
    }
}

function modalRechazarCredito() {
    if (selectedCreditoId) {
        // Hide the details modal
        var detailsModal = bootstrap.Modal.getInstance(document.getElementById('creditoModal'));
        detailsModal.hide();
        
        // Show confirmation dialog
        if (confirm('¿Está seguro de que desea rechazar este crédito? Esta acción no se puede deshacer.')) {
            rechazarCredito(selectedCreditoId);
        }
    }
}

function rechazarCredito(creditoId) {
    // Show loading state
    console.log('Rechazando crédito:', creditoId);
    
    // Send request to reject the credit
    fetch(`/admin/creditos/${creditoId}/rechazar`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (response.ok) {
            alert('Crédito rechazado exitosamente');
            // Reload the page to refresh the data
            window.location.reload();
        } else {
            throw new Error('Error al rechazar el crédito');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al rechazar el crédito. Por favor, intente nuevamente.');
    });
}

// Variables to store credit data for calculations
var currentCreditoData = null;

// Function to set credit data when opening the financial charges modal
function setCreditoDataForCalculation(creditoId) {
    // Find the credit data from the hidden divs
    const creditoDataDiv = document.querySelector(`#creditoData[data-id="${creditoId}"]`);
    if (creditoDataDiv) {
        currentCreditoData = {
            id: creditoDataDiv.getAttribute('data-id'),
            monto: parseFloat(creditoDataDiv.getAttribute('data-monto').replace(/,/g, '')),
            plazo: parseInt(creditoDataDiv.getAttribute('data-plazo'))
        };
        console.log('Credit data set for calculations:', currentCreditoData);
    }
}

// Real-time calculation function for financial charges
function calculateFinancialCharges() {
    if (!currentCreditoData) {
        console.log('No credit data available for calculations');
        return;
    }

    // Get input values
    const porcentajeInteres = parseFloat(document.getElementById('porcentajeInteres').value) || 0;
    const porcentajeMora = parseFloat(document.getElementById('porcentajeMora').value) || 0;
    const porcentajeIva = parseFloat(document.getElementById('porcentajeIva').value) || 0;
    const comisionFija = parseFloat(document.getElementById('comisionFija').value) || 0;

    // Get credit data
    const monto = currentCreditoData.monto;
    const plazoEnMeses = currentCreditoData.plazo;

    // Perform calculations (exact same logic as in CreditoController)
    const cien = 100;
    const doce = 12;

    // Calculate monthly interest rate (with 10 decimal precision like server)
    const tasaMensual = (porcentajeInteres / cien) / doce;

    // (1 + r)^n
    const unoMasTasa = 1 + tasaMensual;
    const potencia = Math.pow(unoMasTasa, plazoEnMeses);

    // Amortization formula: cuota = P * [ r * (1 + r)^n ] / [ (1 + r)^n - 1 ]
    // Round to 2 decimal places like server
    const cuotaMensual = Math.round((monto * tasaMensual * potencia / (potencia - 1)) * 100) / 100;

    // Total repayment = cuota mensual * meses + fixed commission + IVA
    const totalCuotas = cuotaMensual * plazoEnMeses;
    const subtotal = totalCuotas + comisionFija;

    // IVA calculation with 4 decimal precision like server
    const iva = Math.round(subtotal * (porcentajeIva / cien) * 10000) / 10000;
    const total = subtotal + iva;

    // Interest calculation (totalCuotas - monto) rounded to 2 decimals like server
    const interes = Math.round((totalCuotas - monto) * 100) / 100;

    // Update display values with proper formatting
    document.getElementById('cuotaMensualCalculada').textContent = '$' + cuotaMensual.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    document.getElementById('interesCalculado').textContent = '$' + interes.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    document.getElementById('ivaCalculado').textContent = '$' + iva.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    document.getElementById('totalCalculado').textContent = '$' + total.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
} 