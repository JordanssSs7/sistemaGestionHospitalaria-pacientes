package com.tecsup.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alergia")
public class Alergia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alergia")
    private Integer idAlergia;

    @Column(nullable = false)
    private String nombre;

    // Tipo: Medicamento, Alimento, Otro
    private String tipo;

    public Integer getIdAlergia() { return idAlergia; }
    public void setIdAlergia(Integer idAlergia) { this.idAlergia = idAlergia; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
