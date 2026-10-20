package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.contract.error.FieldError;

import java.util.ArrayList;
import java.util.List;

/**
 * Собирает нарушения ограничений тела запроса, чтобы вернуть их все сразу.
 */
final class Violations {

    static final String REQUIRED = "Поле обязательно";

    private final List<FieldError> errors = new ArrayList<>();

    void add(String field, Object rejectedValue, String message) {
        errors.add(FieldError.body(field, rejectedValue, message));
    }

    void throwIfAny() {
        if (!errors.isEmpty()) {
            throw new ValidationException("Некорректное тело запроса", errors);
        }
    }
}
