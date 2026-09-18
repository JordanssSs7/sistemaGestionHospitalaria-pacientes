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
import java.util.NoSuchElementException;
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

    // Nombres y apellidos: solo letras (con tildes/ñ), espacios, guion y apóstrofe. Vacío se permite (campo opcional).
    private void validarSoloLetras(String valor, String etiqueta) {
        if (valor == null || valor.isBlank()) {
            return;
        }
        if (!valor.trim().matches("^\\p{L}+([ '\\-]\\p{L}+)*$")) {
            throw new IllegalArgumentException(etiqueta + " solo pueden contener letras, sin números ni símbolos.");
        }
    }

    // Valida los datos obligatorios y que el documento no pertenezca a otro paciente
    private void validarPaciente(Paciente paciente, Integer idActual) {
        if (paciente.getNumeroDocumento() == null || paciente.getNumeroDocumento().isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio.");
        }
        if (paciente.getNombres() == null || paciente.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios.");
        }
        validarSoloLetras(paciente.getNombres(), "Los nombres");
        validarSoloLetras(paciente.getApellidoPaterno(), "El apellido paterno");
        validarSoloLetras(paciente.getApellidoMaterno(), "El apellido materno");
        String correo = paciente.getCorreoElectronico();
        if (correo != null && !correo.isBlank() && !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
        String documento = paciente.getNumeroDocumento().trim();
        paciente.setNumeroDocumento(documento);
        pacienteRepository.findByNumeroDocumento(documento).ifPresent(existente -> {
            if (!existente.getIdPaciente().equals(idActual)) {
                // RF-PAC-02: el documento ya está registrado en otro paciente
                throw new IllegalStateException("Ya existe un paciente registrado con el documento " + documento + ".");
            }
        });
    }

    // RF-PAC-01 y RF-PAC-04: Registrar paciente
    public Paciente registrarPaciente(Paciente paciente) {
        validarPaciente(paciente, null);
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
    // RF-PAC-05: Buscar limitando la búsqueda a un campo (null o "Todos" = búsqueda global)
    public List<Paciente> buscarPorCampo(String campo, String termino) {
        if (campo == null) {
            return pacienteRepository.buscarPacienteGlobal(termino);
        }
        return switch (campo) {
            case "Nombre" -> pacienteRepository.findByNombresContainingIgnoreCase(termino);
            case "Apellido" -> pacienteRepository
                    .findByApellidoPaternoContainingIgnoreCaseOrApellidoMaternoContainingIgnoreCase(termino, termino);
            case "Documento" -> pacienteRepository.findByNumeroDocumentoContaining(termino);
            case "Código" -> pacienteRepository.findByCodigoPacienteContainingIgnoreCase(termino);
            case "Todos" -> pacienteRepository.buscarPacienteGlobal(termino);
            default -> throw new IllegalArgumentException("Filtro de búsqueda no válido: " + campo);
        };
    }

    // RF-PAC-05: Buscar por documento específico
    public Optional<Paciente> buscarPorDocumento(String documento) {
        return pacienteRepository.findByNumeroDocumento(documento);
    }

    // RF-PAC-08: modificar datos del paciente
    public Paciente actualizarPaciente(Integer idPaciente, Paciente datosActualizados) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado con id: " + idPaciente));
        validarPaciente(datosActualizados, idPaciente);

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