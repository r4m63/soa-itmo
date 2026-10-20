package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.contract.error.FieldError;

import java.util.List;

/**
 * Запрос нарушает ограничения: все нарушения сразу, каждое с указанием места.
 */
public class ValidationException extends RuntimeException {

    private final List<FieldError> errors;

    public ValidationException(String message, List<FieldError> errors) {
        super(message);
        this.errors = List.copyOf(errors);
    }

    public List<FieldError> errors() {
        return errors;
    }
}
