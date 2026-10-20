package com.itmo.soa.movie.api.http.exception;

import com.itmo.soa.contract.error.FieldError;
import com.itmo.soa.query.QueryValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class QueryValidationExceptionMapper implements ExceptionMapper<QueryValidationException> {

    @Override
    public Response toResponse(QueryValidationException e) {
        return ErrorResponses.validation(e.getMessage(), e.errors().stream()
                .map(error -> FieldError.query(error.parameter(), error.rejectedValue(), error.message()))
                .toList());
    }
}
