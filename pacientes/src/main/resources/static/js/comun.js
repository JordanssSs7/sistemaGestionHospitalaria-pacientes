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
