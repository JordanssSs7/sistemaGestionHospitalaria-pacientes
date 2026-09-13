const API_BASE = '/api/pacientes';

function mostrarMensaje(elId, texto, tipo) {
    const el = document.getElementById(elId);
    if (!el) return;
    el.textContent = texto;
    el.className = 'mensaje ' + tipo;
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
