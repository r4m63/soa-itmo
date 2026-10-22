package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.movie.domain.movie.model.MovieRuleViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

/**
 * Нарушено ограничение предметной области → 400 с ошибкой поля тела.
 * Обычно до домена не доходит: тот же вход раньше отклоняют аннотации DTO контракта.
 */
@Provider
public class MovieRuleViolationExceptionMapper implements ExceptionMapper<MovieRuleViolationException> {

    @Override
    public Response toResponse(MovieRuleViolationException e) {
        return ErrorResponses.validation("Некорректное тело запроса",
                List.of(FieldError.body(e.field(), e.rejectedValue(), e.getMessage())));
    }
}
