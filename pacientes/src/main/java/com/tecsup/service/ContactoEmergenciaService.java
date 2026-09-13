package com.tecsup.service;

import com.tecsup.model.ContactoEmergencia;
import com.tecsup.model.Paciente;
import com.tecsup.repository.ContactoEmergenciaRepository;
import com.tecsup.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoEmergenciaService {

    private final ContactoEmergenciaRepository contactoEmergenciaRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public ContactoEmergenciaService(ContactoEmergenciaRepository contactoEmergenciaRepository,
                                     PacienteRepository pacienteRepository) {
        this.contactoEmergenciaRepository = contactoEmergenciaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // RF-PAC-10: registrar contacto de emergencia
    public ContactoEmergencia registrarContacto(Integer idPaciente, ContactoEmergencia contacto) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + idPaciente));
        contacto.setPaciente(paciente);
        return contactoEmergenciaRepository.save(contacto);
    }

    // RF-PAC-10: actualizar contacto de emergencia
    public ContactoEmergencia actualizarContacto(Integer idContacto, ContactoEmergencia datosActualizados) {
        ContactoEmergencia contacto = contactoEmergenciaRepository.findById(idContacto)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado con id: " + idContacto));

        contacto.setNombreCompleto(datosActualizados.getNombreCompleto());
        contacto.setParentesco(datosActualizados.getParentesco());
        contacto.setTelefono(datosActualizados.getTelefono());
        contacto.setDireccion(datosActualizados.getDireccion());
        contacto.setCorreo(datosActualizados.getCorreo());
        contacto.setContactoPrincipal(datosActualizados.getContactoPrincipal());

        return contactoEmergenciaRepository.save(contacto);
    }

    // Listar contactos de un paciente
    public List<ContactoEmergencia> listarPorPaciente(Integer idPaciente) {
        return contactoEmergenciaRepository.findByPaciente_IdPaciente(idPaciente);
    }
}