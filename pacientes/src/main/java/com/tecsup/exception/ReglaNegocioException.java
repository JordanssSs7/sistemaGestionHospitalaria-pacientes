package com.tecsup.exception;

import org.springframework.http.HttpStatus;

// Error de negocio con el código HTTP que debe devolver la API (400, 404, 409...)
public class ReglaNegocioException extends RuntimeException {

    private final HttpStatus status;

    public ReglaNegocioException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
