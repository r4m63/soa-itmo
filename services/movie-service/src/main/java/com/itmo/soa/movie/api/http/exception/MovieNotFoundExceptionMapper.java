package com.itmo.soa.movie.api.http.exception;

import com.itmo.soa.movie.domain.movie.model.MovieNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MovieNotFoundExceptionMapper implements ExceptionMapper<MovieNotFoundException> {

    @Override
    public Response toResponse(MovieNotFoundException e) {
        return ErrorResponses.error(Response.Status.NOT_FOUND, e.getMessage());
    }
}
