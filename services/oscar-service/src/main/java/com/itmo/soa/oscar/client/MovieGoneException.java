package com.itmo.soa.oscar.client;

public class MovieGoneException extends RuntimeException {

    public MovieGoneException(int movieId, Throwable cause) {
        super("Фильм с id=" + movieId + " больше не существует в Movie Service", cause);
    }
}
