package com.itmo.soa.movie.domain.movie.model;

/** Название фильма: не null, не пустое. */
public record MovieName(String value) {

    public MovieName {
        if (value == null || value.isBlank()) {
            throw new MovieRuleViolationException("name", value, "Название не может быть пустым");
        }
    }
}
