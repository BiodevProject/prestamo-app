var allCuotas = [];
var currentPage = 1;
var cuotasPerPage = 5;
var filteredData = [];
var selectedCuotaId = null;
var currentTab = 'todas'; // Default tab
var paymentCuotaId = null; // Store cuota ID for payment modal

document.addEventListener('DOMContentLoaded', function() {
    loadCuotaData();
    switchTab('todas'); // Start with todas tab
    setupContextMenu();
});

function loadCuotaData() {
    var cuotaDataDivs = document.querySelectorAll('#cuotaData');
    allCuotas = [];
    cuotaDataDivs.forEach(function(div) {
        allCuotas.push({
            id: div.getAttribute('data-id'),
            codigo: div.getAttribute('data-codigo') || '',
            fechaVencimiento: div.getAttribute('data-fecha-vencimiento') || '',
            fechaPago: div.getAttribute('data-fecha-pago') || '',
            monto: div.getAttribute('data-monto') || '',
            estado: div.getAttribute('data-estado') || ''
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
        case 'todas':
            filteredData = [...allCuotas];
            break;
        case 'pendientes':
            filteredData = allCuotas.filter(function(cuota) {
                return cuota.estado.toLowerCase() === 'pendiente';
            });
            break;
        case 'pagadas':
            filteredData = allCuotas.filter(function(cuota) {
                return cuota.estado.toLowerCase() === 'pagado';
            });
            break;
        case 'vencidas':
            filteredData = allCuotas.filter(function(cuota) {
                return cuota.estado.toLowerCase() === 'vencido';
            });
            break;
        default:
            filteredData = [...allCuotas];
    }
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    
    // First filter by current tab
    filterDataByTab();
    
    // Then apply search filter
    filteredData = filteredData.filter(function(cuota) {
        return cuota.codigo.toLowerCase().includes(filter) ||
               cuota.fechaVencimiento.toLowerCase().includes(filter) ||
               cuota.fechaPago.toLowerCase().includes(filter) ||
               cuota.monto.toLowerCase().includes(filter) ||
               cuota.estado.toLowerCase().includes(filter);
    });
    
    currentPage = 1;
    updateTable();
    updatePagination();
}

function getEstadoClass(estado) {
    var estadoLower = estado.toLowerCase();
    if (estadoLower === 'pendiente') {
        return 'estado-pendiente';
    } else if (estadoLower === 'pagado') {
        return 'estado-pagado';
    } else if (estadoLower === 'vencido') {
        return 'estado-vencido';
    }
    return '';
}

function formatDate(dateString) {
    if (!dateString || dateString === 'null' || dateString === '') {
        return 'N/A';
    }
    
    try {
        var date = new Date(dateString);
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (e) {
        return dateString;
    }
}

function updateTable() {
    var tbody = document.getElementById("cuotaTableBody");
    var startIndex = (currentPage - 1) * cuotasPerPage;
    var endIndex = startIndex + cuotasPerPage;
    var pageCuotas = filteredData.slice(startIndex, endIndex);
    tbody.innerHTML = '';
    
    if (pageCuotas.length === 0) {
        var emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="6" class="text-center">No hay cuotas en esta categoría</td>';
        tbody.appendChild(emptyRow);
        return;
    }
    
    pageCuotas.forEach(function(cuota) {
        var row = document.createElement('tr');
        row.className = 'clickable-row';
        
        var estadoClass = getEstadoClass(cuota.estado);
        var estadoDisplay = cuota.estado.charAt(0).toUpperCase() + cuota.estado.slice(1);
        
        row.innerHTML = `
            <td>${cuota.codigo || 'N/A'}</td>
            <td>${formatDate(cuota.fechaVencimiento)}</td>
            <td>${formatDate(cuota.fechaPago)}</td>
            <td>${cuota.monto}</td>
            <td><span class="${estadoClass}">${estadoDisplay}</span></td>
            <td><a href="#" class="btn btn-info btn-sm" onclick="showCuotaDetails('${cuota.id}')">Ver Detalles</a></td>
        `;
        
        // Add right-click event for context menu
        row.addEventListener('contextmenu', function(e) {
            showContextMenu(e, cuota.id);
        });
        
        // Add left-click event to also show context menu (optional)
        row.addEventListener('click', function(e) {
            // Only trigger if not clicking on the button
            if (!e.target.closest('.btn')) {
                showContextMenu(e, cuota.id);
            }
        });
        
        tbody.appendChild(row);
    });
}

function updatePagination() {
    var totalPages = Math.ceil(filteredData.length / cuotasPerPage);
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
    
    // Update the page info text
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
    var totalPages = Math.ceil(filteredData.length / cuotasPerPage);
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

function showCuotaDetails(cuotaId) {
    // Set the selected cuota ID for modal buttons
    selectedCuotaId = cuotaId;
    
    // Show loading state
    document.getElementById('cuotaModalBody').innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></i> Cargando...</div>';
    
    // Show the modal
    var modal = new bootstrap.Modal(document.getElementById('cuotaModal'));
    modal.show();
    
    // Fetch cuota details via AJAX
    fetch(`/usuario/cuotas/detalle/${cuotaId}/modal`)
        .then(response => response.text())
        .then(html => {
            document.getElementById('cuotaModalBody').innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('cuotaModalBody').innerHTML = '<div class="alert alert-danger">Error al cargar los detalles de la cuota</div>';
        });
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
    
    // Prevent context menu from being hidden when clicking on context menu items
    document.addEventListener('click', function(e) {
        if (e.target.closest('.context-menu-item')) {
            e.stopPropagation();
        }
    });
}

function showContextMenu(e, cuotaId) {
    e.preventDefault();
    e.stopPropagation();
    
    selectedCuotaId = cuotaId;
    const contextMenu = document.getElementById('contextMenu');
    
    // Show/hide context menu items based on current tab
    updateContextMenuForTab();
    
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

function updateContextMenuForTab() {
    const divider = document.getElementById('contextMenuDivider1');
    const realizarPagoBtn = document.getElementById('contextMenuRealizarPago');
    
    // Hide all action buttons initially
    divider.style.display = 'none';
    realizarPagoBtn.style.display = 'none';
    
    // Show appropriate buttons based on current tab
    switch(currentTab) {
        case 'pendientes':
            divider.style.display = 'block';
            realizarPagoBtn.style.display = 'block';
            break;
        case 'todas':
        case 'pagadas':
        case 'vencidas':
            // Only "Ver Detalles" is available
            break;
    }
}

function hideContextMenu() {
    const contextMenu = document.getElementById('contextMenu');
    contextMenu.style.display = 'none';
    selectedCuotaId = null;
}

function contextMenuVerDetalles() {
    if (selectedCuotaId) {
        showCuotaDetails(selectedCuotaId);
        hideContextMenu();
    }
}

function contextMenuRealizarPago() {
    if (selectedCuotaId) {
        paymentCuotaId = selectedCuotaId;
        showPagoModal(selectedCuotaId);
        document.getElementById('contextMenu').style.display = 'none';
    }
}

function showPagoModal(cuotaId) {
    // Find the cuota data to get the payment amount
    const cuotaDataDiv = document.querySelector(`#cuotaData[data-id="${cuotaId}"]`);
    if (cuotaDataDiv) {
        const cuotaAmount = cuotaDataDiv.getAttribute('data-monto');
        document.getElementById('montoCuota').textContent = cuotaAmount;
        
        // Set the custom amount input to the cuota amount by default
        const amountValue = cuotaAmount.replace(/[$,]/g, '');
        document.getElementById('montoPersonalizado').value = amountValue;
        
        // Update the final payment amount
        updateMontoPago();
    }
    
    // Show the payment modal
    var modal = new bootstrap.Modal(document.getElementById('pagoModal'));
    modal.show();
}

function updateMontoPago() {
    const customAmount = document.getElementById('montoPersonalizado').value;
    const montoPagoElement = document.getElementById('montoPago');
    
    if (customAmount && customAmount > 0) {
        // Format the amount with proper currency formatting
        const formattedAmount = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 2
        }).format(parseFloat(customAmount));
        
        montoPagoElement.textContent = formattedAmount;
    } else {
        montoPagoElement.textContent = '$0.00';
    }
}

function realizarPago() {
    const customAmount = document.getElementById('montoPersonalizado').value;
    
    if (!customAmount || customAmount <= 0) {
        alert('Por favor, ingrese un monto válido para realizar el pago.');
        return;
    }
    
    if (!paymentCuotaId) {
        alert('Error: No se ha seleccionado una cuota válida.');
        return;
    }
    
    // Send payment request
    fetch(`/cuota/pagar/${paymentCuotaId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `monto=${customAmount}`
    })
    .then(response => {
        if (response.ok) {
            alert('Pago realizado exitosamente');
            // Clear the payment cuota ID
            paymentCuotaId = null;
            // Close the modal
            var modal = bootstrap.Modal.getInstance(document.getElementById('pagoModal'));
            modal.hide();
            // Refresh the page to show updated totals
            location.reload();
        } else {
            alert('Error al realizar el pago');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al realizar el pago');
    });
} 