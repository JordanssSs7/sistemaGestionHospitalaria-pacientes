package com.tecsup.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    // Filtros opcionales: si un parametro llega null no se aplica
    @Query("SELECT a FROM Auditoria a WHERE " +
            "(:usuario IS NULL OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :usuario, '%'))) AND " +
            "(:entidad IS NULL OR a.entidad = :entidad) AND " +
            "(:operacion IS NULL OR a.operacion = :operacion) " +
            "ORDER BY a.fechaHora DESC, a.idAuditoria DESC")
    List<Auditoria> buscar(@Param("usuario") String usuario,
                           @Param("entidad") String entidad,
                           @Param("operacion") String operacion);
}
