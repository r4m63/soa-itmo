package com.itmo.soa.query;

public record Filter(QueryField field, Operator operator, Object value, int index) {

    public static String format(String field, Operator operator, Object value) {
        if (!operator.requiresValue()) {
            return field + ";" + operator.token();
        }
        String raw = value instanceof Enum<?> e ? e.name() : String.valueOf(value);
        return field + ";" + operator.token() + ";" + raw;
    }
}
