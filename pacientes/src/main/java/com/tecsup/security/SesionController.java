package com.tecsup.security;

import com.tecsup.model.Usuario;
import com.tecsup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Datos del usuario con sesion iniciada; el front los usa para armar el menu segun el rol
@RestController
@RequestMapping("/api/me")
public class SesionController {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public SesionController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> usuarioActual(Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(auth.getName()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of(
                "username", usuario.getUsername(),
                "nombreCompleto", usuario.getNombreCompleto(),
                "rol", usuario.getRol().getNombre()));
    }
}
