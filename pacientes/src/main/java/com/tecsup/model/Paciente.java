package com.tecsup.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Integer idPaciente;

    @Column(name = "codigo_paciente", unique = true)
    private String codigoPaciente;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento", unique = true, nullable = false)
    private String numeroDocumento;

    private String nombres;

    @Column(name = "apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    private String sexo;

    @Column(name = "estado_civil")
    private String estadoCivil;

    private String telefono;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    private String ocupacion;

    @Column(name = "tipo_sangre")
    private String tipoSangre;

    private String estado = "Activo";

    // RELACIONES (Asegúrate de crear las clases Direccion, ContactoEmergencia y Alergia después)

    // Relación 1 a 1
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id_direccion")
    private Direccion direccion;

    // Relación 1 a N (Para el requerimiento de tu compañera)
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<ContactoEmergencia> contactos;

    // Relación N a M
    @ManyToMany
    @JoinTable(
            name = "paciente_alergia",
            joinColumns = @JoinColumn(name = "id_paciente"),
            inverseJoinColumns = @JoinColumn(name = "id_alergia")
    )
    private List<Alergia> alergias;

    // Genera aquí los Getters y Setters vacíos o usa la anotación @Data si utilizas Lombok
}