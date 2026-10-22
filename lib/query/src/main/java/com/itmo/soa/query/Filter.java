package com.itmo.soa.query;

/**
 * Разобранный фильтр. index — позиция параметра filter в запросе, для сообщений об ошибках.
 */
public record Filter(QueryField field, Operator operator, Object value, int index) {

    /** Собирает значение параметра filter, например для вызова другого сервиса: {@code mpaaRating;eq;R}. */
    public static String format(String field, Operator operator, Object value) {
        if (!operator.requiresValue()) {
            return field + ";" + operator.token();
        }
        String raw = value instanceof Enum<?> e ? e.name() : String.valueOf(value);
        return field + ";" + operator.token() + ";" + raw;
    }
}
