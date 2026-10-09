package com.tecsup.controller;

import com.tecsup.dto.EstadoDTO;
import com.tecsup.dto.RolDTO;
import com.tecsup.model.Rol;
import com.tecsup.service.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    @Autowired
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // Listar roles (GET)
    @GetMapping
    public ResponseEntity<List<Rol>> listar() {
        return new ResponseEntity<>(rolService.listar(), HttpStatus.OK);
    }

    // Obtener un rol (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtener(@PathVariable Integer id) {
        return new ResponseEntity<>(rolService.obtener(id), HttpStatus.OK);
    }

    // Registrar rol (POST)
    @PostMapping
    public ResponseEntity<Rol> registrar(@Valid @RequestBody RolDTO dto) {
        return new ResponseEntity<>(rolService.registrarRol(dto), HttpStatus.CREATED);
    }

    // Editar rol (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizar(@PathVariable Integer id, @Valid @RequestBody RolDTO dto) {
        return new ResponseEntity<>(rolService.actualizarRol(id, dto), HttpStatus.OK);
    }

    // Activar / desactivar rol (PATCH)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Rol> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody EstadoDTO dto) {
        return new ResponseEntity<>(rolService.cambiarEstadoRol(id, dto.getActivo()), HttpStatus.OK);
    }
}
