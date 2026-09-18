package com.tecsup.repository;

import com.tecsup.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    // Método necesario para buscar por documento específico
    Optional<Paciente> findByNumeroDocumento(String numeroDocumento);

    // Búsqueda global por documento, código, nombres o apellidos
    @Query("SELECT p FROM Paciente p WHERE p.numeroDocumento LIKE %:termino% " +
            "OR p.codigoPaciente LIKE %:termino% " +
            "OR p.nombres LIKE %:termino% " +
            "OR p.apellidoPaterno LIKE %:termino% " +
            "OR p.apellidoMaterno LIKE %:termino%")
    List<Paciente> buscarPacienteGlobal(@Param("termino") String termino);

    // Búsquedas filtradas por un solo campo
    List<Paciente> findByNumeroDocumentoContaining(String termino);

    List<Paciente> findByCodigoPacienteContainingIgnoreCase(String termino);

    List<Paciente> findByNombresContainingIgnoreCase(String termino);

    List<Paciente> findByApellidoPaternoContainingIgnoreCaseOrApellidoMaternoContainingIgnoreCase(
            String apellidoPaterno, String apellidoMaterno);

}