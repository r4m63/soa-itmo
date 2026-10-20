package com.itmo.soa.movie.domain.movie.model;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(int id) {
        super("Фильм с id=" + id + " не найден");
    }
}
