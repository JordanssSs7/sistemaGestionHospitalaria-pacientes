package com.tecsup.controller;

import com.tecsup.dto.EstadoDTO;
import com.tecsup.dto.UsuarioDTO;
import com.tecsup.model.Usuario;
import com.tecsup.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Listar usuarios (GET)
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return new ResponseEntity<>(usuarioService.listar(), HttpStatus.OK);
    }

    // Obtener un usuario (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Integer id) {
        return new ResponseEntity<>(usuarioService.obtener(id), HttpStatus.OK);
    }

    // Registrar usuario con su rol (POST)
    @PostMapping
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody UsuarioDTO dto) {
        return new ResponseEntity<>(usuarioService.registrarUsuario(dto), HttpStatus.CREATED);
    }

    // Editar usuario y/o cambiar su rol (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO dto) {
        return new ResponseEntity<>(usuarioService.actualizarUsuario(id, dto), HttpStatus.OK);
    }

    // Activar / desactivar usuario (PATCH)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Usuario> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoDTO dto) {
        return new ResponseEntity<>(usuarioService.cambiarEstadoUsuario(id, dto.getActivo()), HttpStatus.OK);
    }
}
