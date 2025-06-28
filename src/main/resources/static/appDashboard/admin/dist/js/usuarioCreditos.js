var allCreditos = [];
var currentPage = 1;
var creditosPerPage = 5;
var filteredData = [];
var selectedCreditoId = null;
var currentTab = 'activos';

// Variables to store credit data for calculations
var currentCreditoData = null;

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
            monto: div.getAttribute('data-monto') || '',
            plazo: div.getAttribute('data-plazo') || '',
            estado: div.getAttribute('data-estado') || '',
            fecha: div.getAttribute('data-fecha') || ''
        });
    });
    
    // Apply initial filter based on the active tab (Activos)
    switchTab('activos');
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    filteredData = allCreditos.filter(function(credito) {
        return credito.monto.toLowerCase().includes(filter) ||
               credito.plazo.toLowerCase().includes(filter) ||
               credito.estado.toLowerCase().includes(filter) ||
               credito.fecha.toLowerCase().includes(filter);
    });
    currentPage = 1;
    updateTable();
    updatePagination();
}

function switchTab(tabName) {
    currentTab = tabName;
    
    // Update tab buttons
    document.querySelectorAll('.nav-link').forEach(btn => btn.classList.remove('active'));
    document.getElementById(tabName + '-tab').classList.add('active');
    
    // Filter data based on tab
    switch(tabName) {
        case 'activos':
            filteredData = allCreditos.filter(credito => 
                credito.estado.toLowerCase() === 'activo' || 
                credito.estado.toLowerCase() === 'aceptado'
            );
            break;
        case 'pendientes':
            filteredData = allCreditos.filter(credito => 
                credito.estado.toLowerCase() === 'pendiente'
            );
            break;
        case 'rechazados':
            filteredData = allCreditos.filter(credito => 
                credito.estado.toLowerCase() === 'rechazado'
            );
            break;
        case 'finalizados':
            filteredData = allCreditos.filter(credito => 
                credito.estado.toLowerCase() === 'finalizado'
            );
            break;
        default:
            filteredData = [...allCreditos];
    }
    
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
    } else if (estadoLower === 'finalizado') {
        return 'estado-finalizado';
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
    if (currentPage > 1) {
        currentPage--;
        updateTable();
        updatePagination();
    }
}

function nextPage() {
    var totalPages = Math.ceil(filteredData.length / creditosPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        updateTable();
        updatePagination();
    }
}

function showCreditoDetails(creditoId) {
    selectedCreditoId = creditoId;
    
    // Show/hide modal buttons based on current tab
    updateModalButtonsForTab();
    
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

function updateModalButtonsForTab() {
    const aceptarBtn = document.getElementById('modalAceptarBtn');
    const rechazarBtn = document.getElementById('modalRechazarBtn');
    const finalizarBtn = document.getElementById('modalFinalizarBtn');
    
    // Hide all action buttons initially
    aceptarBtn.style.display = 'none';
    rechazarBtn.style.display = 'none';
    finalizarBtn.style.display = 'none';
    
    // Show appropriate buttons based on current tab
    switch(currentTab) {
        case 'pendientes':
            aceptarBtn.style.display = 'inline-block';
            rechazarBtn.style.display = 'inline-block';
            break;
        case 'activos':
            finalizarBtn.style.display = 'inline-block';
            break;
        case 'rechazados':
        case 'finalizados':
            // Only "Cerrar" button is available
            break;
    }
}

// Context Menu Functions
function setupContextMenu() {
    // Hide context menu when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.context-menu')) {
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
    selectedCreditoId = creditoId;
    
    // Show/hide context menu items based on current tab
    updateContextMenuForTab();
    
    var contextMenu = document.getElementById('contextMenu');
    contextMenu.style.display = 'block';
    contextMenu.style.left = e.pageX + 'px';
    contextMenu.style.top = e.pageY + 'px';
    
    // Ensure menu doesn't go off screen
    var rect = contextMenu.getBoundingClientRect();
    if (rect.right > window.innerWidth) {
        contextMenu.style.left = (e.pageX - rect.width) + 'px';
    }
    if (rect.bottom > window.innerHeight) {
        contextMenu.style.top = (e.pageY - rect.height) + 'px';
    }
}

function updateContextMenuForTab() {
    const divider = document.getElementById('contextMenuDivider1');
    const aceptarBtn = document.getElementById('contextMenuAceptar');
    const rechazarBtn = document.getElementById('contextMenuRechazar');
    const finalizarBtn = document.getElementById('contextMenuFinalizar');
    
    // Hide all action buttons initially
    divider.style.display = 'none';
    aceptarBtn.style.display = 'none';
    rechazarBtn.style.display = 'none';
    finalizarBtn.style.display = 'none';
    
    // Show appropriate buttons based on current tab
    switch(currentTab) {
        case 'pendientes':
            divider.style.display = 'block';
            aceptarBtn.style.display = 'block';
            rechazarBtn.style.display = 'block';
            break;
        case 'activos':
            divider.style.display = 'block';
            finalizarBtn.style.display = 'block';
            break;
        case 'rechazados':
        case 'finalizados':
            // Only "Ver Detalles" is available
            break;
    }
}

function hideContextMenu() {
    var contextMenu = document.getElementById('contextMenu');
    contextMenu.style.display = 'none';
}

function contextMenuVerDetalles() {
    hideContextMenu();
    if (selectedCreditoId) {
        showCreditoDetails(selectedCreditoId);
    }
}

function contextMenuAceptarCredito() {
    hideContextMenu();
    if (selectedCreditoId) {
        setCreditoDataForCalculation(selectedCreditoId);
        var modal = new bootstrap.Modal(document.getElementById('cargosFinancierosModal'));
        modal.show();
    }
}

function contextMenuRechazarCredito() {
    hideContextMenu();
    if (selectedCreditoId) {
        if (confirm('¿Está seguro de que desea rechazar este crédito?')) {
            rechazarCredito(selectedCreditoId);
        }
    }
}

function contextMenuFinalizarCredito() {
    hideContextMenu();
    if (selectedCreditoId) {
        if (confirm('¿Está seguro de que desea finalizar este crédito? Esta acción no se puede deshacer.')) {
            finalizarCredito(selectedCreditoId);
        }
    }
}

function modalAceptarCredito() {
    if (selectedCreditoId) {
        setCreditoDataForCalculation(selectedCreditoId);
        var modal = new bootstrap.Modal(document.getElementById('cargosFinancierosModal'));
        modal.show();
    }
}

function modalRechazarCredito() {
    if (selectedCreditoId) {
        if (confirm('¿Está seguro de que desea rechazar este crédito?')) {
            rechazarCredito(selectedCreditoId);
        }
    }
}

function modalFinalizarCredito() {
    if (selectedCreditoId) {
        if (confirm('¿Está seguro de que desea finalizar este crédito? Esta acción no se puede deshacer.')) {
            finalizarCredito(selectedCreditoId);
        }
    }
}

function rechazarCredito(creditoId) {
    fetch(`/admin/creditos/${creditoId}/rechazar`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(result => {
        alert('Crédito rechazado exitosamente');
        // Refresh the page to update the data
        location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al rechazar el crédito');
    });
}

function finalizarCredito(creditoId) {
    // Send request to finalize the credit
    fetch(`/admin/creditos/${creditoId}/finalizar`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(result => {
        alert('Crédito finalizado exitosamente');
        // Refresh the page to update the data
        location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al finalizar el crédito');
    });
}

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

// Collapsible section functions for modal content
function toggleSection(sectionId) {
    const content = document.getElementById(sectionId + '-content');
    const icon = document.getElementById(sectionId + '-icon');
    
    if (content.style.display === 'none' || content.style.display === '') {
        content.style.display = 'block';
        icon.className = 'fas fa-chevron-up';
    } else {
        content.style.display = 'none';
        icon.className = 'fas fa-chevron-down';
    }
}

function initializeModalSections() {
    // Initialize all collapsible sections
    const sections = ['personal-info', 'account-info', 'work-info', 'contact-info'];
    sections.forEach(sectionId => {
        const content = document.getElementById(sectionId + '-content');
        const icon = document.getElementById(sectionId + '-icon');
        if (content && icon) {
            content.style.display = 'none';
            icon.className = 'fas fa-chevron-down';
        }
    });
} 