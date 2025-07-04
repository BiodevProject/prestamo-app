var allUsuarios = [];
var currentPage = 1;
var usuariosPerPage = 5;
var filteredData = [];
var selectedUsuarioId = null;

document.addEventListener('DOMContentLoaded', function() {
    // Small delay to ensure all elements are rendered
    setTimeout(function() {
        loadUsuarioData();
        updateTable();
        updatePagination();
        setupContextMenu();
    }, 100);
});

function loadUsuarioData() {
    var usuarioDataDivs = document.querySelectorAll('#userData');
    allUsuarios = [];
    usuarioDataDivs.forEach(function(div) {
        allUsuarios.push({
            id: div.getAttribute('data-id'),
            nombre: div.getAttribute('data-nombre') || '',
            email: div.getAttribute('data-email') || '',
            celular: div.getAttribute('data-celular') || '',
            apellido: div.getAttribute('data-apellido') || '',
            codigo: div.getAttribute('data-codigo') || ''
        });
    });
    filteredData = [...allUsuarios];
}

function filterTable() {
    var input = document.getElementById("searchInput");
    var filter = input.value.toLowerCase();
    filteredData = allUsuarios.filter(function(usuario) {
        return usuario.nombre.toLowerCase().includes(filter) ||
               usuario.email.toLowerCase().includes(filter) ||
               usuario.celular.toLowerCase().includes(filter) ||
               usuario.rol.toLowerCase().includes(filter) ||
               usuario.codigo.toLowerCase().includes(filter);
    });
    currentPage = 1;
    updateTable();
    updatePagination();
}

function updateTable() {
    var tbody = document.getElementById("userTableBody");
    var startIndex = (currentPage - 1) * usuariosPerPage;
    var endIndex = startIndex + usuariosPerPage;
    var pageUsuarios = filteredData.slice(startIndex, endIndex);
    tbody.innerHTML = '';
    
    if (pageUsuarios.length === 0) {
        var emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="6" class="text-center">No hay usuarios para mostrar</td>';
        tbody.appendChild(emptyRow);
        return;
    }
    
    pageUsuarios.forEach(function(usuario) {
        var row = document.createElement('tr');
        row.className = 'clickable-row';
        
        row.innerHTML = `
            <td>${usuario.codigo}</td>
            <td>${usuario.nombre}</td>
            <td>${usuario.apellido}</td>
            <td>${usuario.email}</td>
            <td>${usuario.celular}</td>
            <td>
                <a href="#" class="btn btn-info btn-sm" onclick="showUsuarioDetails('${usuario.id}')">Ver Detalles</a>
                <a href="/admin/usuarios/${usuario.id}/creditos" class="btn btn-primary btn-sm">Ver Créditos</a>
            </td>
        `;
        
        // Add right-click event for context menu
        row.addEventListener('contextmenu', function(e) {
            showContextMenu(e, usuario.id);
        });
        
        // Add left-click event to also show context menu (optional)
        row.addEventListener('click', function(e) {
            // Only trigger if not clicking on the button
            if (!e.target.closest('.btn')) {
                showContextMenu(e, usuario.id);
            }
        });
        
        tbody.appendChild(row);
    });
}

function updatePagination() {
    var totalPages = Math.ceil(filteredData.length / usuariosPerPage);
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
    var totalPages = Math.ceil(filteredData.length / usuariosPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        updateTable();
        updatePagination();
    }
}

function showUsuarioDetails(usuarioId) {
    selectedUsuarioId = usuarioId;
    
    // Find the user data
    var usuario = allUsuarios.find(u => u.id === usuarioId);
    if (!usuario) {
        alert('Usuario no encontrado');
        return;
    }
    
    // Populate modal with user data
    document.getElementById('usuarioId').value = usuario.id;
    document.getElementById('usuarioIdDisplay').textContent = usuario.id;
    document.getElementById('modalNombre').value = usuario.nombre;
    document.getElementById('modalEmail').value = usuario.email;
    document.getElementById('modalCelular').value = usuario.celular;
    document.getElementById('modalCodigo').value = usuario.codigo;
    
    // Reset form state (all fields disabled, edit button visible)
    var modalInputs = document.querySelectorAll('#usuarioModal input:not([type="hidden"]), #usuarioModal select');
    modalInputs.forEach(input => input.disabled = true);
    document.getElementById('modalEditBtn').style.display = 'inline-block';
    document.getElementById('modalSaveBtn').style.display = 'none';
    
    // Show the modal
    var modal = new bootstrap.Modal(document.getElementById('usuarioModal'));
    modal.show();
    
    // Setup modal edit functionality
    setupModalEditFunctionality();
}

function setupModalEditFunctionality() {
    const editBtn = document.getElementById('modalEditBtn');
    const saveBtn = document.getElementById('modalSaveBtn');
    const form = document.getElementById('usuarioForm');
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
        fetch('/admin/usuarios/update', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                return response.text();
            }
            throw new Error('Error al actualizar usuario');
        })
        .then(result => {
            alert('Usuario actualizado exitosamente');
            
            // Update local data
            var usuario = allUsuarios.find(u => u.id === selectedUsuarioId);
            if (usuario) {
                usuario.nombre = formData.get('nombre');
                usuario.email = formData.get('email');
                usuario.celular = formData.get('celular');
                usuario.rol = formData.get('rol');
                usuario.codigo = formData.get('codigo');
                
                // Refresh table
                updateTable();
            }
            
            // Close modal
            var modal = bootstrap.Modal.getInstance(document.getElementById('usuarioModal'));
            modal.hide();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al actualizar usuario: ' + error.message);
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

function showContextMenu(e, usuarioId) {
    e.preventDefault();
    selectedUsuarioId = usuarioId;
    
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
    if (selectedUsuarioId) {
        showUsuarioDetails(selectedUsuarioId);
    }
}

function contextMenuEditarUsuario() {
    hideContextMenu();
    if (selectedUsuarioId) {
        // TODO: Implement edit user functionality
        alert('Editar usuario ID: ' + selectedUsuarioId);
    }
}

function contextMenuEliminarUsuario() {
    hideContextMenu();
    if (selectedUsuarioId) {
        if (confirm('¿Está seguro de que desea eliminar este usuario?')) {
            // TODO: Implement delete user functionality
            alert('Eliminar usuario ID: ' + selectedUsuarioId);
        }
    }
} 