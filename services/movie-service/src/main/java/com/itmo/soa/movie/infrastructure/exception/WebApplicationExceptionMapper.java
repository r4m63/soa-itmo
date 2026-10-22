package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.response.FieldError;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ParamException;

import java.util.List;

/**
 * Ошибки самого JAX-RS (нет пути, не тот метод, не тот Content-Type) — тот же статус, но в формате спецификации.
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(WebApplicationException e) {
        // параметр пути не привёлся к типу DTO («abc» в int id): JAX-RS отвечает 404, по спецификации — 400
        if (e instanceof ParamException.PathParamException param) {
            String name = param.getParameterName();
            return ErrorResponses.validation("Некорректные параметры запроса", List.of(FieldError.path(name,
                    uriInfo.getPathParameters().getFirst(name), name + " должен быть положительным целым числом")));
        }
        Response.StatusType status = e.getResponse().getStatusInfo();
        // тело не удалось прочитать вовсе: его нет (400) или оно не JSON (415); по спецификации это 400 ValidationError
        if (status.getStatusCode() == 400) {
            return ErrorResponses.validation("Некорректное тело запроса",
                    List.of(FieldError.body(null, null, "Тело запроса обязательно")));
        }
        if (status.getStatusCode() == 415) {
            return ErrorResponses.validation("Некорректное тело запроса",
                    List.of(FieldError.body(null, null, "Тело запроса должно быть application/json")));
        }
        String message = switch (status.getStatusCode()) {
            case 404 -> "Ресурс не найден";
            case 405 -> "Метод не поддерживается";
            case 406 -> "Ответ доступен только в application/json";
            default -> status.getReasonPhrase();
        };
        return ErrorResponses.error(status, message);
    }
}
