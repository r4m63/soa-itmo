package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.response.FieldError;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

    @Override
    public Response toResponse(ProcessingException e) {
        return ErrorResponses.validation("Некорректное тело запроса", List.of(FieldError.body(null, null,
                "Некорректный JSON: неверный тип значения, неизвестное значение enum или лишнее поле")));
    }
}
