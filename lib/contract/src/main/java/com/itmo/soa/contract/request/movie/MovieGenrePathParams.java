package com.itmo.soa.contract.request.movie;

import jakarta.ws.rs.PathParam;
import lombok.Getter;

@Getter
public class MovieGenrePathParams {
    @PathParam("genre")
    private String genre;
}
