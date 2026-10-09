package com.tecsup.auditoria;

import com.tecsup.model.Alergia;
import com.tecsup.model.ContactoEmergencia;
import com.tecsup.model.Paciente;
import com.tecsup.model.Rol;
import com.tecsup.model.Usuario;
import jakarta.persistence.Id;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;

// Intercepta save() y delete() de los repositorios y registra la operacion en la tabla auditoria,
// sin que los servicios tengan que llamar a la bitacora.
@Aspect
@Component
public class AuditoriaAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaAspect.class);

    // Entidades principales del sistema que se auditan
    private static final Set<Class<?>> ENTIDADES_AUDITADAS =
            Set.of(Paciente.class, ContactoEmergencia.class, Alergia.class, Usuario.class, Rol.class);

    private final AuditoriaRepository auditoriaRepository;

    @Autowired
    public AuditoriaAspect(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // save(): sin id es un REGISTRO, con id es una MODIFICACION
    @Around("execution(* org.springframework.data.repository.CrudRepository+.save(..)) && args(entidad)")
    public Object auditarGuardado(ProceedingJoinPoint punto, Object entidad) throws Throwable {
        boolean esNueva = auditada(entidad) && obtenerId(entidad) == null;
        Object resultado = punto.proceed();
        if (auditada(entidad)) {
            registrar(esNueva ? "REGISTRO" : "MODIFICACION", resultado, obtenerId(resultado));
        }
        return resultado;
    }

    @Around("execution(* org.springframework.data.repository.CrudRepository+.delete(..)) && args(entidad)")
    public Object auditarEliminacion(ProceedingJoinPoint punto, Object entidad) throws Throwable {
        Integer id = auditada(entidad) ? obtenerId(entidad) : null;
        Object resultado = punto.proceed();
        if (auditada(entidad)) {
            registrar("ELIMINACION", entidad, id);
        }
        return resultado;
    }

    // Un fallo al auditar no debe impedir la operacion original
    private void registrar(String operacion, Object entidad, Integer id) {
        try {
            Auditoria fila = new Auditoria();
            fila.setUsuario(usuarioActual());
            fila.setFechaHora(LocalDateTime.now());
            fila.setOperacion(operacion);
            fila.setEntidad(entidad.getClass().getSimpleName());
            fila.setIdRegistro(id);
            auditoriaRepository.save(fila);
        } catch (RuntimeException e) {
            log.error("No se pudo registrar la auditoria de {}", operacion, e);
        }
    }

    // Usuario autenticado; si no hay sesion (p. ej. carga inicial de datos) se registra como "sistema"
    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "sistema";
        }
        return auth.getName();
    }

    private boolean auditada(Object entidad) {
        return entidad != null && ENTIDADES_AUDITADAS.contains(entidad.getClass());
    }

    // Lee el campo anotado con @Id (idPaciente, idUsuario, idRol, ...)
    private Integer obtenerId(Object entidad) {
        for (Field campo : entidad.getClass().getDeclaredFields()) {
            if (campo.isAnnotationPresent(Id.class)) {
                try {
                    campo.setAccessible(true);
                    Object valor = campo.get(entidad);
                    return valor instanceof Integer ? (Integer) valor : null;
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
