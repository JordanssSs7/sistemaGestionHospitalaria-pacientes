package com.tecsup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RolDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 30, message = "El nombre del rol no puede superar 30 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_]*$",
            message = "El nombre del rol solo puede tener letras sin tildes, números y guion bajo")
    private String nombre;

    @Size(max = 150, message = "La descripción no puede superar 150 caracteres")
    private String descripcion;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
