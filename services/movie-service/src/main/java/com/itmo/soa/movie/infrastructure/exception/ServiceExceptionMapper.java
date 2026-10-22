package com.itmo.soa.movie.infrastructure.exception;

import com.itmo.soa.contract.error.exception.ServiceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Все бизнес-ошибки: HTTP-статус из реестра ErrorType, тело — Error или ValidationError (если есть errors).
 */
@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

    @Override
    public Response toResponse(ServiceException e) {
        Response.Status status = Response.Status.fromStatusCode(e.type().httpStatus());
        return e.errors().isEmpty()
                ? ErrorResponses.error(status, e.getMessage())
                : ErrorResponses.validation(e.getMessage(), e.errors());
    }
}
