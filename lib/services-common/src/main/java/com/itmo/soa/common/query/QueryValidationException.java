package com.itmo.soa.common.query;

import java.util.List;

public class QueryValidationException extends RuntimeException {

    private final List<QueryError> errors;

    public QueryValidationException(List<QueryError> errors) {
        super("Некорректные параметры запроса");
        this.errors = List.copyOf(errors);
    }

    public List<QueryError> errors() {
        return errors;
    }
}
