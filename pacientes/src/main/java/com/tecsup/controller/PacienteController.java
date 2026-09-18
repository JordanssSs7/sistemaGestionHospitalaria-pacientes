package com.tecsup.controller;

import com.tecsup.model.Paciente;
import com.tecsup.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Endpoint para registrar un paciente (POST)
    @PostMapping
    public ResponseEntity<?> registrarPaciente(@RequestBody Paciente paciente) {
        try {
            Paciente nuevoPaciente = pacienteService.registrarPaciente(paciente);
            return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // RF-PAC-02: documento ya registrado
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(Map.of("mensaje",
                    "Los datos entran en conflicto con un registro existente (documento o código duplicado)."),
                    HttpStatus.CONFLICT);
        }
    }

    // Endpoint para listar todos los pacientes (GET)
    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        return new ResponseEntity<>(pacienteService.listarTodos(), HttpStatus.OK);
    }

    // RF-PAC-06: mostrar la información completa de un paciente (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPaciente(@PathVariable Integer id) {
        Optional<Paciente> paciente = pacienteService.buscarPorId(id);
        return paciente.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint para buscar por documento (GET)
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<Paciente> buscarPorDocumento(@PathVariable String numeroDocumento) {
        Optional<Paciente> paciente = pacienteService.buscarPorDocumento(numeroDocumento);

        if (paciente.isPresent()) {
            // Si lo encuentra, devuelve el paciente y un estado 200 (OK)
            return new ResponseEntity<>(paciente.get(), HttpStatus.OK);
        } else {
            // Si no lo encuentra, devuelve un estado 404 (NOT FOUND)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para RF-PAC-05
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPaciente(@RequestParam String termino,
                                             @RequestParam(required = false) String campo) {
        try {
            List<Paciente> pacientes = pacienteService.buscarPorCampo(campo, termino);
            if (pacientes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(pacientes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // RF-PAC-08: actualizar datos del paciente (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPaciente(@PathVariable Integer id, @RequestBody Paciente paciente) {
        try {
            Paciente actualizado = pacienteService.actualizarPaciente(id, paciente);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("mensaje", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(Map.of("mensaje",
                    "Los datos entran en conflicto con un registro existente (documento o código duplicado)."),
                    HttpStatus.CONFLICT);
        }
    }
}