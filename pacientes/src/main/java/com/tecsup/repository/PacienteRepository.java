package com.tecsup.repository;

import com.tecsup.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    // Spring Data JPA genera automáticamente las consultas por nombre de método
    Optional<Paciente> findByNumeroDocumento(String numeroDocumento);

    Optional<Paciente> findByCodigoPaciente(String codigoPaciente);

    // Búsqueda por nombres o apellidos (RF-PAC-05)
    List<Paciente> findByNombresContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCase(String nombres, String apellidoPaterno);
}