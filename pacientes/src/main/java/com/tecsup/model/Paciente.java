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

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(String codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public List<ContactoEmergencia> getContactos() {
        return contactos;
    }

    public void setContactos(List<ContactoEmergencia> contactos) {
        this.contactos = contactos;
    }

    public List<Alergia> getAlergias() {
        return alergias;
    }

    public void setAlergias(List<Alergia> alergias) {
        this.alergias = alergias;
    }

    // Este campo no existirá en la tabla de la base de datos
    @Transient
    public Integer getEdad() {
        if (this.fechaNacimiento == null) {
            return null;
        }
        // Usamos getTime() en vez de toInstant(): cuando Hibernate lee la fecha desde MySQL
        // la devuelve como java.sql.Date, y java.sql.Date.toInstant() siempre lanza
        // UnsupportedOperationException (no tiene información de hora/zona horaria).
        java.time.LocalDate nacimiento = java.time.Instant.ofEpochMilli(this.fechaNacimiento.getTime())
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
        java.time.LocalDate hoy = java.time.LocalDate.now();

        // Calculamos y retornamos los años exactos de diferencia
        return java.time.Period.between(nacimiento, hoy).getYears();
    }

}