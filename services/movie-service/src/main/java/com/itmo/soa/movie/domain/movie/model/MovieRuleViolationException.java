package com.itmo.soa.movie.domain.movie.model;

public class MovieRuleViolationException extends RuntimeException {

    private final String field;
    private final Object rejectedValue;

    public MovieRuleViolationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String field() {
        return field;
    }

    public Object rejectedValue() {
        return rejectedValue;
    }
}
