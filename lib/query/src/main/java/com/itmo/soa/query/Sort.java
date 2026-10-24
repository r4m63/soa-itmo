package com.itmo.soa.query;

public record Sort(QueryField field, Direction direction) {

    public static String format(String field, Direction direction) {
        return field + ";" + direction.token();
    }
}
