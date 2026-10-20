package com.itmo.soa.oscar.infrastructure.integration.movie;

/**
 * Movie Service не ответил: нет соединения, таймаут или 5xx.
 */
public class MovieServiceUnavailableException extends RuntimeException {

    public MovieServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
