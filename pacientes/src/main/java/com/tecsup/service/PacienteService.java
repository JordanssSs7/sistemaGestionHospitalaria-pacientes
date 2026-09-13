package com.tecsup.service;

import com.tecsup.model.Paciente;
import com.tecsup.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    // Inyección de dependencias (DI) mediante constructor
    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    // RF-PAC-01 y RF-PAC-04: Registrar paciente
    public Paciente registrarPaciente(Paciente paciente) {
        // RF-PAC-03: Generar código único automáticamente
        if (paciente.getCodigoPaciente() == null || paciente.getCodigoPaciente().isEmpty()) {
            paciente.setCodigoPaciente("PAC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        return pacienteRepository.save(paciente);
    }

    // RF-PAC-06: Listar todos
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    // RF-PAC-05: Buscar por documento
    public Optional<Paciente> buscarPorDocumento(String documento) {
        return pacienteRepository.findByNumeroDocumento(documento);
    }

    // RF-PAC-05: Buscar por nombres o apellidos
    public List<Paciente> buscarPorNombresOApellidos(String termino) {
        return pacienteRepository.findByNombresContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCase(termino, termino);
    }

    // RF-PAC-08: modificar datos del paciente
    public Paciente actualizarPaciente(Integer idPaciente, Paciente datosActualizados) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));

        paciente.setTipoDocumento(datosActualizados.getTipoDocumento());
        paciente.setNumeroDocumento(datosActualizados.getNumeroDocumento());
        paciente.setNombres(datosActualizados.getNombres());
        paciente.setApellidoPaterno(datosActualizados.getApellidoPaterno());
        paciente.setApellidoMaterno(datosActualizados.getApellidoMaterno());
        paciente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        paciente.setSexo(datosActualizados.getSexo());
        paciente.setEstadoCivil(datosActualizados.getEstadoCivil());
        paciente.setTelefono(datosActualizados.getTelefono());
        paciente.setCorreoElectronico(datosActualizados.getCorreoElectronico());
        paciente.setOcupacion(datosActualizados.getOcupacion());
        paciente.setTipoSangre(datosActualizados.getTipoSangre());
        paciente.setEstado(datosActualizados.getEstado());

        return pacienteRepository.save(paciente);
    }
}