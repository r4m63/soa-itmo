package com.itmo.soa.movie.api.http.exception;

import com.itmo.soa.movie.domain.movie.model.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException e) {
        return ErrorResponses.validation(e.getMessage(), e.errors());
    }
}
