package com.tecsup.auditoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    @Autowired
    public AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // Consulta de la bitacora, mas reciente primero. Filtros opcionales por usuario, entidad y operacion.
    @GetMapping
    public List<Auditoria> consultar(@RequestParam(required = false) String usuario,
                                     @RequestParam(required = false) String entidad,
                                     @RequestParam(required = false) String operacion) {
        return auditoriaRepository.buscar(vacioANulo(usuario), vacioANulo(entidad), vacioANulo(operacion));
    }

    private String vacioANulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
