package com.tecsup.service;

import com.tecsup.dto.UsuarioDTO;
import com.tecsup.model.Rol;
import com.tecsup.model.Usuario;
import com.tecsup.repository.RolRepository;
import com.tecsup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("idUsuario"));
    }

    public Usuario obtener(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + idUsuario));
    }

    // Registrar usuario y asignarle un rol
    public Usuario registrarUsuario(UsuarioDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        String username = dto.getUsername().trim();
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalStateException("Ya existe un usuario con el nombre " + username + ".");
        }
        Rol rol = buscarRol(dto.getRolId());
        if (!Boolean.TRUE.equals(rol.getActivo())) {
            throw new IllegalArgumentException("El rol " + rol.getNombre() + " está desactivado.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setCorreo(limpiar(dto.getCorreo()));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    // Editar usuario (el nombre de usuario no se modifica; la contraseña solo si se envía una nueva)
    public Usuario actualizarUsuario(Integer idUsuario, UsuarioDTO dto) {
        Usuario usuario = obtener(idUsuario);
        Rol rol = buscarRol(dto.getRolId());

        boolean cambiaRol = !rol.getIdRol().equals(usuario.getRol().getIdRol());
        if (cambiaRol) {
            if (esUsuarioActual(usuario)) {
                throw new IllegalArgumentException("No puedes cambiar tu propio rol.");
            }
            if (!Boolean.TRUE.equals(rol.getActivo())) {
                throw new IllegalArgumentException("El rol " + rol.getNombre() + " está desactivado.");
            }
        }

        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setCorreo(limpiar(dto.getCorreo()));
        usuario.setRol(rol);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    // Activar / desactivar usuario
    public Usuario cambiarEstadoUsuario(Integer idUsuario, boolean activo) {
        Usuario usuario = obtener(idUsuario);
        if (!activo && esUsuarioActual(usuario)) {
            throw new IllegalArgumentException("No puedes desactivar tu propio usuario.");
        }
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    private Rol buscarRol(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado con id: " + idRol));
    }

    // Se activa cuando exista el login (P5); sin sesión no coincide con ningún usuario
    private boolean esUsuarioActual(Usuario usuario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName().equalsIgnoreCase(usuario.getUsername());
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
