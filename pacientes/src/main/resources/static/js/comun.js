const API_BASE = '/api/pacientes';

function mostrarMensaje(elId, texto, tipo) {
    const el = document.getElementById(elId);
    if (!el) return;
    const icono = tipo === 'ok'
        ? '<i class="fa-solid fa-circle-check"></i>'
        : '<i class="fa-solid fa-circle-exclamation"></i>';
    el.innerHTML = icono + '<span>' + texto + '</span>';
    el.className = 'mensaje ' + tipo;
}

// Devuelve el mensaje que envía la API ({mensaje: "..."}) o uno genérico con el código HTTP
async function leerErrorApi(resp) {
    try {
        const cuerpo = await resp.json();
        if (cuerpo && cuerpo.mensaje) return cuerpo.mensaje;
    } catch (e) { /* respuesta sin JSON */ }
    return 'El servidor respondió con error ' + resp.status;
}

function obtenerIdPacienteDeUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

function irAPaciente(id) {
    window.location.href = `paciente.html?id=${id}`;
}

function irAContactos(id) {
    window.location.href = `contactos.html?id=${id}`;
}

function iniciales(nombres, apellido) {
    const a = (nombres ?? '').trim().charAt(0);
    const b = (apellido ?? '').trim().charAt(0);
    return (a + b).toUpperCase() || '?';
}

function escapeHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

function insertarFooter() {
    const placeholder = document.getElementById('pieDePagina');
    if (!placeholder) return;
    placeholder.outerHTML = `
        <footer class="pie">
            <div class="marca">
                <i class="fa-solid fa-hospital"></i>
                Sistema de Gestión Hospitalaria · Módulo Pacientes
            </div>
            <div class="estado">
                <span class="punto"></span>
                Conectado a la API local
            </div>
        </footer>
    `;
}

document.addEventListener('DOMContentLoaded', insertarFooter);

// ---- Catalogo de alergias: editar y eliminar (usado en registrar.html y paciente.html) ----
async function editarAlergia(event, boton) {
    event.preventDefault();
    event.stopPropagation();
    const id = boton.dataset.id;
    const nombreActual = decodeURIComponent(boton.dataset.nombre);
    const tipoActual = decodeURIComponent(boton.dataset.tipo);

    const nombre = prompt('Nombre de la alergia:', nombreActual);
    if (nombre === null || nombre.trim() === '') return;
    const tipo = prompt('Tipo (Medicamento, Alimento u Otro):', tipoActual);
    if (tipo === null) return;

    try {
        const resp = await fetch('/api/alergias/' + id, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre: nombre.trim(), tipo: tipo.trim() })
        });
        if (!resp.ok) throw new Error('No se pudo editar la alergia');
        await recargarCatalogoAlergias();
    } catch (err) {
        alert(err.message);
    }
}

async function eliminarAlergia(event, id) {
    event.preventDefault();
    event.stopPropagation();
    if (!confirm('¿Eliminar esta alergia del catálogo? También se quitará de los pacientes que la tengan.')) return;

    try {
        const resp = await fetch('/api/alergias/' + id, { method: 'DELETE' });
        if (!resp.ok) throw new Error('No se pudo eliminar la alergia');
        await recargarCatalogoAlergias();
    } catch (err) {
        alert(err.message);
    }
}

// Vuelve a pintar el catalogo conservando las alergias marcadas en el formulario
async function recargarCatalogoAlergias() {
    const marcadas = Array.from(document.querySelectorAll('#listaAlergias input[type="checkbox"]:checked'))
        .map(chk => chk.value);
    await cargarCatalogoAlergias();
    document.querySelectorAll('#listaAlergias input[type="checkbox"]').forEach(chk => {
        chk.checked = marcadas.includes(chk.value);
    });
}
