package com.itmo.soa.contract.error;

/**
 * Одна ошибка валидации. location: path, query или body.
 * field: имя параметра (filter[1], size), путь в JSON (coordinates.x) или null для ошибки всего тела.
 */
public record FieldError(String location, String field, Object rejectedValue, String message) {

    public static final String PATH = "path";
    public static final String QUERY = "query";
    public static final String BODY = "body";

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
