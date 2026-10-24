package com.itmo.soa.movie.domain.movie.model;

public record OscarsCount(int value) {

    public OscarsCount {
        if (value <= 0) {
            throw new MovieRuleViolationException("oscarsCount", value, "Значение должно быть больше 0");
        }
    }
}
