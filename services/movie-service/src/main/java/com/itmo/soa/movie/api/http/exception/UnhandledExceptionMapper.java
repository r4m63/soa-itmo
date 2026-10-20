package com.itmo.soa.movie.api.http.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UnhandledExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        LOG.log(Level.SEVERE, "Необработанная ошибка", e);
        return ErrorResponses.error(Response.Status.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }
}
