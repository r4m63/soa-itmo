package com.itmo.soa.contract.request.movie;

import jakarta.ws.rs.PathParam;
import lombok.Getter;

/**
 * Параметр пути {genre}. Строка: список жанров принадлежит Movie Service, допустимые значения проверяет он.
 */
@Getter
public class MovieGenrePathParams {

    @PathParam("genre")
    private String genre;
}
