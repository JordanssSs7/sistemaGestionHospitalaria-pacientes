package com.tecsup.controller;

import com.tecsup.model.Paciente;
import com.tecsup.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<Paciente> registrarPaciente(@RequestBody Paciente paciente) {
        Paciente nuevoPaciente = pacienteService.registrarPaciente(paciente);
        return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
    }

    // Endpoint para listar todos los pacientes (GET)
    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        return new ResponseEntity<>(pacienteService.listarTodos(), HttpStatus.OK);
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
    public ResponseEntity<List<Paciente>> buscarPaciente(@RequestParam String termino) {
        List<Paciente> pacientes = pacienteService.buscarGlobal(termino);
        if(pacientes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    // RF-PAC-08: actualizar datos del paciente (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Integer id, @RequestBody Paciente paciente) {
        Paciente actualizado = pacienteService.actualizarPaciente(id, paciente);
        return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }
}