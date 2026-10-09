package com.tecsup.controller;

import com.tecsup.model.ContactoEmergencia;
import com.tecsup.service.ContactoEmergenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/pacientes/{idPaciente}/contactos")
public class ContactoEmergenciaController {

    private final ContactoEmergenciaService contactoEmergenciaService;

    @Autowired
    public ContactoEmergenciaController(ContactoEmergenciaService contactoEmergenciaService) {
        this.contactoEmergenciaService = contactoEmergenciaService;
    }

    // RF-PAC-10: registrar contacto (POST)
    @PostMapping
    public ResponseEntity<?> registrarContacto(@PathVariable Integer idPaciente,
                                               @RequestBody ContactoEmergencia contacto) {
        try {
            ContactoEmergencia nuevo = contactoEmergenciaService.registrarContacto(idPaciente, contacto);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // RF-PAC-10: actualizar contacto (PUT)
    @PutMapping("/{idContacto}")
    public ResponseEntity<?> actualizarContacto(@PathVariable Integer idPaciente,
                                                @PathVariable Integer idContacto,
                                                @RequestBody ContactoEmergencia contacto) {
        try {
            ContactoEmergencia actualizado = contactoEmergenciaService.actualizarContacto(idContacto, contacto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar contacto (DELETE)
    @DeleteMapping("/{idContacto}")
    public ResponseEntity<?> eliminarContacto(@PathVariable Integer idPaciente,
                                              @PathVariable Integer idContacto) {
        try {
            contactoEmergenciaService.eliminarContacto(idPaciente, idContacto);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Listar contactos de un paciente (GET)
    @GetMapping
    public ResponseEntity<List<ContactoEmergencia>> listarContactos(@PathVariable Integer idPaciente) {
        return new ResponseEntity<>(contactoEmergenciaService.listarPorPaciente(idPaciente), HttpStatus.OK);
    }
}
