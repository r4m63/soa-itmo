package com.itmo.soa.movie.api.http.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Ошибки самого JAX-RS (нет пути, не тот метод, не тот Content-Type) — тот же статус, но в формате спецификации.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException e) {
        Response.StatusType status = e.getResponse().getStatusInfo();
        String message = switch (status.getStatusCode()) {
            case 404 -> "Ресурс не найден";
            case 405 -> "Метод не поддерживается";
            case 406 -> "Ответ доступен только в application/json";
            case 415 -> "Тело запроса должно быть application/json";
            default -> status.getReasonPhrase();
        };
        return ErrorResponses.error(status, message);
    }
}
