package com.tecsup.service;

import com.tecsup.model.Alergia;
import com.tecsup.repository.AlergiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
