package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.response.FieldError;
import com.itmo.soa.movie.domain.movie.model.MovieRuleViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class MovieRuleViolationExceptionMapper implements ExceptionMapper<MovieRuleViolationException> {

    @Override
    public Response toResponse(MovieRuleViolationException e) {
        return ErrorResponses.validation("Некорректное тело запроса",
                List.of(FieldError.body(e.field(), e.rejectedValue(), e.getMessage())));
    }
}
