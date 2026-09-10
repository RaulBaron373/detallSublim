package com.detallsublim.app.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Excepción controlada para errores funcionales de Historias.
 *
 * Permite devolver al cliente el código HTTP adecuado
 * sin convertir errores esperados en un 500.
 */
public class HistoriaServiceException extends ErrorResponseException {

    private HistoriaServiceException(HttpStatus status, String detail) {
        super(status, ProblemDetail.forStatusAndDetail(status, detail), null);
    }

    public static HistoriaServiceException badRequest(String detail) {
        return new HistoriaServiceException(HttpStatus.BAD_REQUEST, detail);
    }

    public static HistoriaServiceException notFound(String detail) {
        return new HistoriaServiceException(HttpStatus.NOT_FOUND, detail);
    }

    public static HistoriaServiceException conflict(String detail) {
        return new HistoriaServiceException(HttpStatus.CONFLICT, detail);
    }
}
