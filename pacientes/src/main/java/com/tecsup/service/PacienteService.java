package com.tecsup.service;

import com.tecsup.model.Alergia;
import com.tecsup.model.Direccion;
import com.tecsup.model.Paciente;
import com.tecsup.repository.AlergiaRepository;
import com.tecsup.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;

    // Inyección de dependencias (DI) mediante constructor
    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, AlergiaRepository alergiaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.alergiaRepository = alergiaRepository;
    }

    // El front solo manda el id de cada alergia seleccionada; buscamos las entidades
    // ya persistidas para que el @ManyToMany (sin cascade) las enlace correctamente.
    private List<Alergia> resolverAlergias(List<Alergia> alergiasRecibidas) {
        // Hibernate necesita poder hacer clear()/limpiar esta lista al sincronizarla,
        // así que debe ser mutable (List.of() es inmutable y rompe con UnsupportedOperationException).
        if (alergiasRecibidas == null || alergiasRecibidas.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = alergiasRecibidas.stream()
                .map(Alergia::getIdAlergia)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        return new ArrayList<>(alergiaRepository.findAllById(ids));
    }

    // Copia los campos sobre la Direccion ya cargada en vez de reemplazar el objeto completo:
    // si se asigna un objeto Direccion nuevo con el mismo id que uno ya presente en el
    // contexto de persistencia, Hibernate lanza NonUniqueObjectException.
    private void actualizarDireccion(Paciente paciente, Direccion direccionRecibida) {
        if (direccionRecibida == null) {
            return;
        }
        Direccion direccionActual = paciente.getDireccion();
        if (direccionActual == null) {
            direccionActual = new Direccion();
            paciente.setDireccion(direccionActual);
        }
        direccionActual.setDireccion(direccionRecibida.getDireccion());
        direccionActual.setDistrito(direccionRecibida.getDistrito());
        direccionActual.setProvincia(direccionRecibida.getProvincia());
        direccionActual.setDepartamento(direccionRecibida.getDepartamento());
    }

    // RF-PAC-01 y RF-PAC-04: Registrar paciente
    public Paciente registrarPaciente(Paciente paciente) {
        // RF-PAC-03: Generar código único automáticamente
        if (paciente.getCodigoPaciente() == null || paciente.getCodigoPaciente().isEmpty()) {
            paciente.setCodigoPaciente("PAC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        paciente.setAlergias(resolverAlergias(paciente.getAlergias()));
        return pacienteRepository.save(paciente);
    }

    // Listar todos (menú "Listar pacientes")
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    // RF-PAC-06: Mostrar la información completa de un paciente puntual
    public Optional<Paciente> buscarPorId(Integer idPaciente) {
        return pacienteRepository.findById(idPaciente);
    }

    // RF-PAC-05: Buscar pacientes por documento, código, nombres y apellidos
    public List<Paciente> buscarGlobal(String termino) {
        return pacienteRepository.buscarPacienteGlobal(termino);
    }
    // RF-PAC-05: Buscar por documento específico
    public Optional<Paciente> buscarPorDocumento(String documento) {
        return pacienteRepository.findByNumeroDocumento(documento);
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
        actualizarDireccion(paciente, datosActualizados.getDireccion());
        paciente.setAlergias(resolverAlergias(datosActualizados.getAlergias()));

        return pacienteRepository.save(paciente);
    }
}