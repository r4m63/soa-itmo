package com.itmo.soa.movie.domain.movie.model;

/**
 * Нарушено ограничение предметной области. field — поле модели (name, screenwriter.passportID).
 * Через API не возникает: тот же вход раньше отклоняют аннотации DTO контракта.
 */
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
