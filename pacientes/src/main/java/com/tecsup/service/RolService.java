package com.tecsup.service;

import com.tecsup.dto.RolDTO;
import com.tecsup.model.Rol;
import com.tecsup.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RolService {

    private static final List<String> ROLES_BASE = List.of("ADMINISTRADOR", "MEDICO", "RECEPCIONISTA");

    private final RolRepository rolRepository;

    @Autowired
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listar() {
        return rolRepository.findAll(Sort.by("idRol"));
    }

    public Rol obtener(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado con id: " + idRol));
    }

    // Registrar rol
    public Rol registrarRol(RolDTO dto) {
        String nombre = normalizar(dto.getNombre());
        if (rolRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalStateException("Ya existe un rol con el nombre " + nombre + ".");
        }
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(limpiar(dto.getDescripcion()));
        rol.setActivo(true);
        return rolRepository.save(rol);
    }

    // Editar rol
    public Rol actualizarRol(Integer idRol, RolDTO dto) {
        Rol rol = obtener(idRol);
        String nombre = normalizar(dto.getNombre());

        if (!rol.getNombre().equals(nombre)) {
            if (ROLES_BASE.contains(rol.getNombre())) {
                throw new IllegalArgumentException("El nombre de un rol base no se puede cambiar.");
            }
            if (rolRepository.existsByNombreIgnoreCaseAndIdRolNot(nombre, idRol)) {
                throw new IllegalStateException("Ya existe un rol con el nombre " + nombre + ".");
            }
            rol.setNombre(nombre);
        }
        rol.setDescripcion(limpiar(dto.getDescripcion()));
        return rolRepository.save(rol);
    }

    // Activar / desactivar rol
    public Rol cambiarEstadoRol(Integer idRol, boolean activo) {
        Rol rol = obtener(idRol);
        if (!activo && "ADMINISTRADOR".equals(rol.getNombre())) {
            throw new IllegalArgumentException("El rol ADMINISTRADOR no se puede desactivar.");
        }
        rol.setActivo(activo);
        return rolRepository.save(rol);
    }

    private String normalizar(String nombre) {
        return nombre.trim().toUpperCase();
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
