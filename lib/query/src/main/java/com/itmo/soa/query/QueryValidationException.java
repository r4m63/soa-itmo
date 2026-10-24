package com.itmo.soa.query;

import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class QueryValidationException extends RuntimeException {

    private final List<QueryError> errors;

    public QueryValidationException(List<QueryError> errors) {
        super("Некорректные параметры запроса");
        this.errors = List.copyOf(errors);
    }
}
