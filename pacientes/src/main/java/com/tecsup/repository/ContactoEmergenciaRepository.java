package com.tecsup.repository;

import com.tecsup.model.ContactoEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoEmergenciaRepository extends JpaRepository<ContactoEmergencia, Integer> {

    // RF-PAC-10: listar contactos de un paciente
    List<ContactoEmergencia> findByPaciente_IdPaciente(Integer idPaciente);
}