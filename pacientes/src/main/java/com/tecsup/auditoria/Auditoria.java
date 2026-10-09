package com.tecsup.auditoria;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Bitácora: una fila por cada registro, modificación o eliminación sobre las entidades principales
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Integer idAuditoria;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // REGISTRO, MODIFICACION o ELIMINACION
    @Column(nullable = false, length = 20)
    private String operacion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "id_registro")
    private Integer idRegistro;

    public Integer getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Integer idAuditoria) { this.idAuditoria = idAuditoria; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public Integer getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
}
