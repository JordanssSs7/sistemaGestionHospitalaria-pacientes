package com.tecsup.dto;

import jakarta.validation.constraints.NotNull;

public class EstadoDTO {

    @NotNull(message = "Debes indicar si queda activo o inactivo")
    private Boolean activo;

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
