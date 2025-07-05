var allTrabajadores = [];
var currentPage = 1;
var trabajadoresPerPage = 5;
var filteredData = [];
var selectedTrabajadorId = null;

document.addEventListener('DOMContentLoaded', function() {
    // Small delay to ensure all elements are rendered
    setTimeout(function() {
        loadTrabajadorData();
        updateTable();
        updatePagination();
        setupContextMenu();
    }, 100);
});

function loadTrabajadorData() {
    var trabajadorDataDivs = document.querySelectorAll('#workerData');
    allTrabajadores = [];
    trabajadorDataDivs.forEach(function(div) {
        allTrabajadores.push({
            id: div.getAttribute('data-id'),
            nombre: div.getAttribute('data-nombre') || '',
            email: div.getAttribute('data-email') || '',
        });
    });
    filteredData = [...allTrabajadores];
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    filteredData = allTrabajadores.filter(function(trabajador) {
        return trabajador.nombre.toLowerCase().includes(filter) ||
               trabajador.email.toLowerCase().includes(filter);
    });
    currentPage = 1;
    updateTable();
    updatePagination();
}

function updateTable() {
    var tbody = document.getElementById("workerTableBody");
    var startIndex = (currentPage - 1) * trabajadoresPerPage;
    var endIndex = startIndex + trabajadoresPerPage;
    var pageTrabajadores = filteredData.slice(startIndex, endIndex);
    tbody.innerHTML = '';
    
    if (pageTrabajadores.length === 0) {
        var emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="4" class="text-center">No hay trabajadores para mostrar</td>';
        tbody.appendChild(emptyRow);
        return;
    }
    
    pageTrabajadores.forEach(function(trabajador) {
        var row = document.createElement('tr');
        row.className = 'clickable-row';
        
        row.innerHTML = `
            <td>${trabajador.nombre}</td>
            <td>${trabajador.email}</td>
            <td><a href="#" class="btn btn-info btn-sm" onclick="showTrabajadorDetails('${trabajador.id}')">Ver Detalles</a></td>
        `;
        
        // Add right-click event for context menu
        row.addEventListener('contextmenu', function(e) {
            showContextMenu(e, trabajador.id);
        });
        
        // Add left-click event to also show context menu (optional)
        row.addEventListener('click', function(e) {
            // Only trigger if not clicking on the button
            if (!e.target.closest('.btn')) {
                showContextMenu(e, trabajador.id);
            }
        });
        
        tbody.appendChild(row);
    });
}

function updatePagination() {
    var totalPages = Math.ceil(filteredData.length / trabajadoresPerPage);
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
    var totalPages = Math.ceil(filteredData.length / trabajadoresPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        updateTable();
        updatePagination();
    }
}

function showTrabajadorDetails(trabajadorId) {
    selectedTrabajadorId = trabajadorId;
    
    // Find the worker data
    var trabajador = allTrabajadores.find(t => t.id === trabajadorId);
    if (!trabajador) {
        alert('Trabajador no encontrado');
        return;
    }
    
    // Populate modal with worker data
    document.getElementById('trabajadorId').value = trabajador.id;
    document.getElementById('trabajadorIdDisplay').textContent = trabajador.id;
    document.getElementById('modalNombre').value = trabajador.nombre;
    document.getElementById('modalEmail').value = trabajador.email;
    
    // Reset form state (all fields disabled, edit button visible)
    var modalInputs = document.querySelectorAll('#trabajadorModal input:not([type="hidden"]), #trabajadorModal select');
    modalInputs.forEach(input => input.disabled = true);
    document.getElementById('modalEditBtn').style.display = 'inline-block';
    document.getElementById('modalSaveBtn').style.display = 'none';
    
    // Show the modal
    var modal = new bootstrap.Modal(document.getElementById('trabajadorModal'));
    modal.show();
    
    // Setup modal edit functionality
    setupModalEditFunctionality();
}

function setupModalEditFunctionality() {
    const editBtn = document.getElementById('modalEditBtn');
    const saveBtn = document.getElementById('modalSaveBtn');
    const form = document.getElementById('trabajadorForm');
    const inputs = form.querySelectorAll("input:not([type='hidden']), select");

    // Remove existing event listeners to prevent duplicates
    editBtn.removeEventListener('click', editHandler);
    form.removeEventListener('submit', submitHandler);
    
    function editHandler() {
        inputs.forEach(input => input.removeAttribute("disabled"));
        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
    }
    
    function submitHandler(e) {
        e.preventDefault();
        
        // Collect form data
        const formData = new FormData(form);
        
        // Send update request
        fetch('/trabajador/update', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Error al actualizar trabajador');
        })
        .then(result => {
            alert('Trabajador actualizado exitosamente');
            
            // Update local data
            var trabajador = allTrabajadores.find(t => t.id === selectedTrabajadorId);
            if (trabajador) {
                trabajador.nombre = formData.get('nombre');
                trabajador.email = formData.get('email');
                
                // Refresh table
                updateTable();
            }
            
            // Close modal
            var modal = bootstrap.Modal.getInstance(document.getElementById('trabajadorModal'));
            modal.hide();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al actualizar trabajador: ' + error.message);
        });
    }

    editBtn.addEventListener('click', editHandler);
    form.addEventListener('submit', submitHandler);
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

function showContextMenu(e, trabajadorId) {
    e.preventDefault();
    selectedTrabajadorId = trabajadorId;
    
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

function hideContextMenu() {
    var contextMenu = document.getElementById('contextMenu');
    contextMenu.style.display = 'none';
}

function contextMenuVerDetalles() {
    hideContextMenu();
    if (selectedTrabajadorId) {
        showTrabajadorDetails(selectedTrabajadorId);
    }
}

function contextMenuEditarTrabajador() {
    hideContextMenu();
    if (selectedTrabajadorId) {
        // TODO: Implement edit worker functionality
        alert('Editar trabajador ID: ' + selectedTrabajadorId);
    }
}

function contextMenuEliminarTrabajador() {
    hideContextMenu();
    if (selectedTrabajadorId) {
        if (confirm('¿Está seguro de que desea eliminar este trabajador?')) {
            // TODO: Implement delete worker functionality
            alert('Eliminar trabajador ID: ' + selectedTrabajadorId);
        }
    }
} 