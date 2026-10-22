package com.itmo.soa.contract.common.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Одна ошибка валидации. location: path, query или body.
 * field: имя параметра (filter[1], size), путь в JSON (coordinates.x) или null для ошибки всего тела.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldError {

    public static final String PATH = "path";
    public static final String QUERY = "query";
    public static final String BODY = "body";

    private String location;
    private String field;
    private Object rejectedValue;
    private String message;

    public static FieldError path(String field, Object rejectedValue, String message) {
        return new FieldError(PATH, field, rejectedValue, message);
    }

    public static FieldError query(String field, Object rejectedValue, String message) {
        return new FieldError(QUERY, field, rejectedValue, message);
    }

    public static FieldError body(String field, Object rejectedValue, String message) {
        return new FieldError(BODY, field, rejectedValue, message);
    }
}
