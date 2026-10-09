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

// ---- Sesion y control de acceso (P5) ----

// Si la sesion caduca, cualquier llamada a la API que responda 401 lleva de nuevo al login
const fetchOriginal = window.fetch.bind(window);
window.fetch = async (...args) => {
    const resp = await fetchOriginal(...args);
    if (resp.status === 401) {
        window.location.href = '/login.html';
    }
    return resp;
};

// Opciones del menu y los roles que pueden verlas
const OPCIONES_MENU = [
    { href: 'registrar.html', icono: 'fa-user-plus', texto: 'Registrar', roles: ['ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA'] },
    { href: 'buscar.html', icono: 'fa-magnifying-glass', texto: 'Buscar', roles: ['ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA'] },
    { href: 'usuarios.html', icono: 'fa-users', texto: 'Usuarios', roles: ['ADMINISTRADOR'] },
    { href: 'roles.html', icono: 'fa-user-shield', texto: 'Roles', roles: ['ADMINISTRADOR'] },
    { href: 'auditoria.html', icono: 'fa-clipboard-list', texto: 'Bitácora', roles: ['ADMINISTRADOR'] }
];

// Arma el menu solo con las opciones permitidas para el rol del usuario con sesion iniciada
async function iniciarSesionUI() {
    const nav = document.querySelector('.navbar nav');
    if (!nav) return;

    try {
        const resp = await fetch('/api/me');
        if (!resp.ok) return;
        const usuario = await resp.json();
        document.body.dataset.rol = usuario.rol;

        const paginaActual = window.location.pathname.split('/').pop() || 'index.html';
        // Pestaña contextual de la pagina (Ficha, Contactos), que no es un enlace del menu
        const contextual = nav.querySelector('a[href="#"]');

        let html = OPCIONES_MENU
            .filter(o => o.roles.includes(usuario.rol))
            .map(o => `<a href="${o.href}"${o.href === paginaActual ? ' class="activo"' : ''}>
                <i class="fa-solid ${o.icono}"></i> ${o.texto}</a>`)
            .join('');
        if (contextual) html += contextual.outerHTML;
        html += `
            <span class="usuario-nav" title="Rol: ${escapeHtml(usuario.rol)}">
                <i class="fa-solid fa-circle-user"></i> ${escapeHtml(usuario.nombreCompleto)}
                <small>${escapeHtml(usuario.rol)}</small>
            </span>
            <a href="#" onclick="cerrarSesion(event)"><i class="fa-solid fa-right-from-bracket"></i> Salir</a>`;
        nav.innerHTML = html;
    } finally {
        nav.classList.add('listo');
    }
}

async function cerrarSesion(event) {
    event.preventDefault();
    await fetchOriginal('/logout', { method: 'POST' });
    window.location.href = '/login.html';
}

document.addEventListener('DOMContentLoaded', iniciarSesionUI);

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
