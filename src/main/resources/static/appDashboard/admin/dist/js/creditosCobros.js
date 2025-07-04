var allCuotas = [];
var allCuotasAvencer = [];
var allCuotasVencidas = [];
var currentPage = 1;
var cuotasPerPage = 5;
var filteredData = [];
var selectedCuotaId = null;
var currentTab = 'enrevision';

document.addEventListener('DOMContentLoaded', function() {
    loadCuotaData();
    setupTabHandlers();
    updateTable();
    updatePagination();
    setupContextMenu();
});

function loadCuotaData() {
    // Load EnRevision cuotas
    var cuotaDataDivs = document.querySelectorAll('#cuotaData');
    allCuotas = [];
    cuotaDataDivs.forEach(function(div) {
        allCuotas.push({
            id: div.getAttribute('data-id'),
            usuario: div.getAttribute('data-usuario') || '',
            codigo: div.getAttribute('data-codigo') || '',
            fechaVencimiento: div.getAttribute('data-fecha-vencimiento') || '',
            fechaPago: div.getAttribute('data-fecha-pago') || '',
            monto: div.getAttribute('data-monto') || '',
            estado: div.getAttribute('data-estado') || ''
        });
    });

    // Load Avencer cuotas
    var cuotaDataAvencerDivs = document.querySelectorAll('#cuotaDataAvencer');
    allCuotasAvencer = [];
    cuotaDataAvencerDivs.forEach(function(div) {
        allCuotasAvencer.push({
            id: div.getAttribute('data-id'),
            usuario: div.getAttribute('data-usuario') || '',
            codigo: div.getAttribute('data-codigo') || '',
            fechaVencimiento: div.getAttribute('data-fecha-vencimiento') || '',
            fechaPago: div.getAttribute('data-fecha-pago') || '',
            monto: div.getAttribute('data-monto') || '',
            estado: div.getAttribute('data-estado') || ''
        });
    });

    // Load Vencidas cuotas
    var cuotaDataVencidasDivs = document.querySelectorAll('#cuotaDataVencidas');
    allCuotasVencidas = [];
    cuotaDataVencidasDivs.forEach(function(div) {
        allCuotasVencidas.push({
            id: div.getAttribute('data-id'),
            usuario: div.getAttribute('data-usuario') || '',
            codigo: div.getAttribute('data-codigo') || '',
            fechaVencimiento: div.getAttribute('data-fecha-vencimiento') || '',
            fechaPago: div.getAttribute('data-fecha-pago') || '',
            monto: div.getAttribute('data-monto') || '',
            estado: div.getAttribute('data-estado') || '',
            pagoMora: div.getAttribute('data-pago-mora') || '0.00'
        });
    });

    filteredData = [...allCuotas];
}

function setupTabHandlers() {
    // Add click handlers for tabs
    document.getElementById('enrevision-tab').addEventListener('click', function() {
        switchTab('enrevision');
    });
    
    document.getElementById('avencer-tab').addEventListener('click', function() {
        switchTab('avencer');
    });
    
    document.getElementById('vencidas-tab').addEventListener('click', function() {
        switchTab('vencidas');
    });
}

function switchTab(tabName) {
    // Update active tab
    document.querySelectorAll('.nav-link').forEach(tab => tab.classList.remove('active'));
    document.getElementById(tabName + '-tab').classList.add('active');
    
    // Update current tab and data
    currentTab = tabName;
    currentPage = 1;
    
    // Clear search input
    document.getElementById('searchInput').value = '';
    
    // Update table headers first (this clears the table body)
    updateTableHeaders(tabName);
    
    // Load appropriate data
    switch(tabName) {
        case 'enrevision':
            filteredData = [...allCuotas];
            break;
        case 'avencer':
            filteredData = [...allCuotasAvencer];
            break;
        case 'vencidas':
            filteredData = [...allCuotasVencidas];
            break;
    }
    
    // Update table and pagination
    updateTable();
    updatePagination();
}

function updateTableHeaders(tabName) {
    const table = document.getElementById('cuotaTable');
    const thead = table.querySelector('thead tr');
    
    if (tabName === 'vencidas') {
        thead.innerHTML = `
            <th>Usuario</th>
            <th>Código</th>
            <th>Fecha Vencimiento</th>
            <th>Monto</th>
            <th>Mora</th>
            <th>Estado</th>
            <th></th>
        `;
    } else if (tabName === 'enrevision') {
        thead.innerHTML = `
            <th>Usuario</th>
            <th>Código</th>
            <th>Fecha Vencimiento</th>
            <th id="fechaPagoHeader">Fecha Pago</th>
            <th>Monto</th>
            <th>Estado</th>
            <th></th>
        `;
    } else {
        thead.innerHTML = `
            <th>Usuario</th>
            <th>Código</th>
            <th>Fecha Vencimiento</th>
            <th>Monto</th>
            <th>Estado</th>
            <th></th>
        `;
    }
    
    // Clear the table body to prevent glitches
    const tbody = table.querySelector('tbody');
    tbody.innerHTML = '';
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    
    filteredData = allCuotas.filter(function(cuota) {
        return cuota.usuario.toLowerCase().includes(filter) ||
               cuota.codigo.toLowerCase().includes(filter) ||
               cuota.monto.toLowerCase().includes(filter) ||
               cuota.estado.toLowerCase().includes(filter);
    });
    
    currentPage = 1;
    updateTable();
    updatePagination();
}

function getEstadoClass(estado) {
    var estadoLower = estado.toLowerCase();
    if (estadoLower === 'enrevision') {
        return 'estado-enrevision';
    } else if (estadoLower === 'avencer') {
        return 'estado-avencer';
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
        var emptyMessage = '';
        switch(currentTab) {
            case 'enrevision':
                emptyMessage = 'No hay cuotas en revisión';
                break;
            case 'avencer':
                emptyMessage = 'No hay cuotas a vencer';
                break;
            case 'vencidas':
                emptyMessage = 'No hay cuotas vencidas';
                break;
            default:
                emptyMessage = 'No hay cuotas';
        }
        var colspan;
        if (currentTab === 'enrevision') {
            colspan = '7';
        } else if (currentTab === 'vencidas') {
            colspan = '7';
        } else {
            colspan = '6';
        }
        emptyRow.innerHTML = '<td colspan="' + colspan + '" class="text-center">' + emptyMessage + '</td>';
        tbody.appendChild(emptyRow);
        return;
    }
    
    pageCuotas.forEach(function(cuota) {
        var row = document.createElement('tr');
        row.className = 'clickable-row';
        
        var estadoClass = getEstadoClass(cuota.estado);
        var estadoDisplay = cuota.estado.charAt(0).toUpperCase() + cuota.estado.slice(1);
        
        var actionButtons = '';
        if (currentTab === 'enrevision') {
            actionButtons = `
                <a href="#" class="btn btn-info btn-sm" onclick="showCuotaDetails('${cuota.id}')">Ver Detalles</a>
                <a href="#" class="btn btn-success btn-sm ms-1" onclick="aceptarCuota('${cuota.id}')">Aceptar</a>
            `;
        } else if (currentTab === 'avencer') {
            actionButtons = `
                <a href="#" class="btn btn-info btn-sm" onclick="showCuotaDetails('${cuota.id}')">Ver Detalles</a>
            `;
        } else if (currentTab === 'vencidas') {
            actionButtons = `
                <a href="#" class="btn btn-info btn-sm" onclick="showCuotaDetails('${cuota.id}')">Ver Detalles</a>
                <span class="badge bg-danger ms-1">Vencida</span>
            `;
        }

        var fechaPagoCell = currentTab === 'enrevision' ? `<td>${formatDate(cuota.fechaPago)}</td>` : '';
        var moraCell = currentTab === 'vencidas' ? `<td>$${cuota.pagoMora}</td>` : '';
        
        row.innerHTML = `
            <td>${cuota.usuario || 'N/A'}</td>
            <td>${cuota.codigo || 'N/A'}</td>
            <td>${formatDate(cuota.fechaVencimiento)}</td>
            ${fechaPagoCell}
            <td>${cuota.monto}</td>
            ${moraCell}
            <td><span class="${estadoClass}">${estadoDisplay}</span></td>
            <td>${actionButtons}</td>
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
    var currentPageSpan = document.getElementById("currentPage");
    var totalPagesSpan = document.getElementById("totalPages");
    var prevBtn = document.getElementById("prevBtn");
    var nextBtn = document.getElementById("nextBtn");
    
    // Check if all required elements exist
    if (!pageInfo || !currentPageSpan || !totalPagesSpan || !prevBtn || !nextBtn) {
        console.error("Pagination elements not found");
        return;
    }
    
    // Ensure currentPage doesn't exceed totalPages
    if (currentPage > totalPages && totalPages > 0) {
        currentPage = totalPages;
    }
    
    // Update the page info text
    currentPageSpan.textContent = currentPage;
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
}

function previousPage() {
    if (currentPage > 1) {
        currentPage--;
        updateTable();
        updatePagination();
    }
}

function nextPage() {
    var totalPages = Math.ceil(filteredData.length / cuotasPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        updateTable();
        updatePagination();
    }
}

function showCuotaDetails(cuotaId) {
    selectedCuotaId = cuotaId;
    
    // Show loading state
    document.getElementById('cuotaModalBody').innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></i> Cargando...</div>';
    
    // Show the modal
    var modal = new bootstrap.Modal(document.getElementById('cuotaModal'));
    modal.show();
    
    // For now, show basic cuota info since we don't have a cuota details endpoint
    const cuotaDataDiv = document.querySelector(`#cuotaData[data-id="${cuotaId}"]`);
    if (cuotaDataDiv) {
        const cuotaInfo = `
            <div class="cuota-detail">
                <div class="section-card full-width">
                    <div class="section-header">
                        <div class="section-header-left">
                            <i class="fas fa-info-circle"></i>
                            Detalle de Cuota
                        </div>
                    </div>
                    <div class="section-content">
                        <div class="info-grid" style="grid-template-columns: 1fr 1fr;">
                            <div class="info-item">
                                <span class="info-label">Usuario</span>
                                <div class="info-value">${cuotaDataDiv.getAttribute('data-usuario') || 'N/A'}</div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Código</span>
                                <div class="info-value">${cuotaDataDiv.getAttribute('data-codigo') || 'N/A'}</div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Estado</span>
                                <div class="info-value">${cuotaDataDiv.getAttribute('data-estado') || 'N/A'}</div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Monto</span>
                                <div class="info-value">${cuotaDataDiv.getAttribute('data-monto') || 'N/A'}</div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Fecha de Vencimiento</span>
                                <div class="info-value">${formatDate(cuotaDataDiv.getAttribute('data-fecha-vencimiento'))}</div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Fecha de Pago</span>
                                <div class="info-value">${formatDate(cuotaDataDiv.getAttribute('data-fecha-pago'))}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        document.getElementById('cuotaModalBody').innerHTML = cuotaInfo;
    } else {
        document.getElementById('cuotaModalBody').innerHTML = '<div class="alert alert-danger">Error al cargar los detalles de la cuota</div>';
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
    selectedCuotaId = null;
}

function contextMenuVerDetalles() {
    if (selectedCuotaId) {
        showCuotaDetails(selectedCuotaId);
        hideContextMenu();
    }
}

function contextMenuAceptarCuota() {
    if (selectedCuotaId) {
        aceptarCuota(selectedCuotaId);
        hideContextMenu();
    }
}

function modalAceptarCuota() {
    if (selectedCuotaId) {
        aceptarCuota(selectedCuotaId);
        var modal = bootstrap.Modal.getInstance(document.getElementById('cuotaModal'));
        modal.hide();
    }
}

function aceptarCuota(cuotaId) {
    if (confirm('¿Está seguro de que desea aceptar esta cuota?')) {
        // Send accept request
        fetch(`/cuota/aceptar/${cuotaId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
        .then(response => {
            if (response.ok) {
                alert('Cuota aceptada exitosamente');
                // Refresh the page to show updated data
                location.reload();
            } else {
                alert('Error al aceptar la cuota');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al aceptar la cuota');
        });
    }
} 