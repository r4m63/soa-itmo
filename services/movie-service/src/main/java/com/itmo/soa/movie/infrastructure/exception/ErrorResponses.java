package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.response.ErrorResponse;
import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.contract.error.response.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.List;

/**
 * Тела ошибок в формате спецификации: Error и ValidationError.
 */
final class ErrorResponses {

    private ErrorResponses() {
    }

    static Response error(Response.StatusType status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(message, Instant.now()))
                .build();
    }

    static Response validation(String message, List<FieldError> errors) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ValidationErrorResponse(message, Instant.now(), errors))
                .build();
    }
}
