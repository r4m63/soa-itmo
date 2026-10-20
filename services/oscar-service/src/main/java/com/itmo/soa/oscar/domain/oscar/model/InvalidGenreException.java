package com.itmo.soa.oscar.domain.oscar.model;

import com.itmo.soa.contract.error.FieldError;

import java.util.List;

public class InvalidGenreException extends RuntimeException {

    private final List<FieldError> errors;

    public InvalidGenreException(List<FieldError> errors) {
        super("Некорректные параметры запроса");
        this.errors = List.copyOf(errors);
    }

    public List<FieldError> errors() {
        return errors;
    }
}
