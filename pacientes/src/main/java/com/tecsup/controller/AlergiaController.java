package com.tecsup.controller;

import com.tecsup.model.Alergia;
import com.tecsup.service.AlergiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alergias")
public class AlergiaController {

    private final AlergiaService alergiaService;

    @Autowired
    public AlergiaController(AlergiaService alergiaService) {
        this.alergiaService = alergiaService;
    }

    // Catalogo de alergias disponibles, para seleccionar al registrar/editar un paciente
    @GetMapping
    public ResponseEntity<List<Alergia>> listarAlergias() {
        return new ResponseEntity<>(alergiaService.listarTodas(), HttpStatus.OK);
    }

    // Agregar una alergia nueva al catalogo
    @PostMapping
    public ResponseEntity<Alergia> registrarAlergia(@RequestBody Alergia alergia) {
        return new ResponseEntity<>(alergiaService.registrar(alergia), HttpStatus.CREATED);
    }
}
