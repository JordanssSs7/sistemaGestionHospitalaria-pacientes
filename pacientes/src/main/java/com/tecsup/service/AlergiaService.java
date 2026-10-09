package com.tecsup.service;

import com.tecsup.model.Alergia;
import com.tecsup.repository.AlergiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlergiaService {

    private final AlergiaRepository alergiaRepository;

    @Autowired
    public AlergiaService(AlergiaRepository alergiaRepository) {
        this.alergiaRepository = alergiaRepository;
    }

    public List<Alergia> listarTodas() {
        return alergiaRepository.findAll();
    }

    public Alergia registrar(Alergia alergia) {
        return alergiaRepository.save(alergia);
    }

    public Alergia actualizar(Integer idAlergia, Alergia datos) {
        Alergia alergia = alergiaRepository.findById(idAlergia)
                .orElseThrow(() -> new RuntimeException("Alergia no encontrada con id: " + idAlergia));
        alergia.setNombre(datos.getNombre());
        alergia.setTipo(datos.getTipo());
        return alergiaRepository.save(alergia);
    }

    @Transactional
    public void eliminar(Integer idAlergia) {
        Alergia alergia = alergiaRepository.findById(idAlergia)
                .orElseThrow(() -> new RuntimeException("Alergia no encontrada con id: " + idAlergia));
        alergiaRepository.quitarDePacientes(idAlergia);
        alergiaRepository.delete(alergia);
    }
}
