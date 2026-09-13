package com.tecsup.controller;

import com.tecsup.model.ContactoEmergencia;
import com.tecsup.service.ContactoEmergenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ContactoEmergencia> registrarContacto(@PathVariable Integer idPaciente,
                                                                @RequestBody ContactoEmergencia contacto) {
        ContactoEmergencia nuevo = contactoEmergenciaService.registrarContacto(idPaciente, contacto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // RF-PAC-10: actualizar contacto (PUT)
    @PutMapping("/{idContacto}")
    public ResponseEntity<ContactoEmergencia> actualizarContacto(@PathVariable Integer idPaciente,
                                                                 @PathVariable Integer idContacto,
                                                                 @RequestBody ContactoEmergencia contacto) {
        ContactoEmergencia actualizado = contactoEmergenciaService.actualizarContacto(idContacto, contacto);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }

    // Listar contactos de un paciente (GET)
    @GetMapping
    public ResponseEntity<List<ContactoEmergencia>> listarContactos(@PathVariable Integer idPaciente) {
        return new ResponseEntity<>(contactoEmergenciaService.listarPorPaciente(idPaciente), HttpStatus.OK);
    }
}