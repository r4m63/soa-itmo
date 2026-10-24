package com.itmo.soa.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Разбирает параметры filter, sort, page и size по схеме.
 * Проверяет все параметры и бросает {@link QueryValidationException} со списком всех ошибок сразу.
 */
public final class QueryParser {

    private final QuerySchema schema;

    public QueryParser(QuerySchema schema) {
        this.schema = schema;
    }

    /**
     * page и size принимаются строками, чтобы на нечисловое значение ответить 400 с понятным текстом.
     */
    public Query parse(List<String> filters, List<String> sorts, String page, String size) {
        List<QueryError> errors = new ArrayList<>();

        List<Filter> parsedFilters = new ArrayList<>();
        List<String> rawFilters = filters == null ? List.of() : filters;
        for (int i = 0; i < rawFilters.size(); i++) {
            parseFilter(rawFilters.get(i), i, errors).ifPresent(parsedFilters::add);
        }

        List<Sort> parsedSorts = new ArrayList<>();
        List<String> rawSorts = sorts == null ? List.of() : sorts;
        for (int i = 0; i < rawSorts.size(); i++) {
            parseSort(rawSorts.get(i), i, errors).ifPresent(parsedSorts::add);
        }

        Integer pageNumber = parsePage(page, errors);
        Integer pageSize = parseSize(size, errors);

        if (!errors.isEmpty()) {
            throw new QueryValidationException(errors);
        }
        return new Query(List.copyOf(parsedFilters), List.copyOf(parsedSorts),
                new PageRequest(pageNumber, pageSize), schema);
    }

    private Optional<Filter> parseFilter(String raw, int index, List<QueryError> errors) {
        String parameter = "filter[" + index + "]";
        String value = raw == null ? "" : raw;

        int first = value.indexOf(';');
        if (first < 0) {
            errors.add(new QueryError(parameter, raw, "Ожидается формат field;operator;value или field;operator"));
            return Optional.empty();
        }
        int second = value.indexOf(';', first + 1);
        String fieldName = value.substring(0, first);
        String operatorToken = second < 0 ? value.substring(first + 1) : value.substring(first + 1, second);
        String rawValue = second < 0 ? null : value.substring(second + 1);

        Optional<QueryField> field = schema.field(fieldName);
        if (field.isEmpty()) {
            errors.add(new QueryError(parameter, raw, "Неизвестное поле " + fieldName));
            return Optional.empty();
        }
        Optional<Operator> operator = Operator.fromToken(operatorToken);
        if (operator.isEmpty()) {
            errors.add(new QueryError(parameter, raw, "Неизвестный оператор " + operatorToken));
            return Optional.empty();
        }
        if (!field.get().operators().contains(operator.get())) {
            errors.add(new QueryError(parameter, raw, "Оператор " + operatorToken + " недопустим для " + fieldName
                    + ", допустимы: " + tokens(field.get())));
            return Optional.empty();
        }

        if (!operator.get().requiresValue()) {
            if (rawValue != null) {
                errors.add(new QueryError(parameter, raw,
                        "Оператор " + operatorToken + " используется без значения: " + fieldName + ";" + operatorToken));
                return Optional.empty();
            }
            return Optional.of(new Filter(field.get(), operator.get(), null, index));
        }
        if (rawValue == null) {
            errors.add(new QueryError(parameter, raw,
                    "Для оператора " + operatorToken + " нужно значение: " + fieldName + ";" + operatorToken + ";value"));
            return Optional.empty();
        }
        try {
            return Optional.of(new Filter(field.get(), operator.get(), field.get().parseValue(rawValue), index));
        } catch (IllegalArgumentException e) {
            errors.add(new QueryError(parameter, raw, e.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<Sort> parseSort(String raw, int index, List<QueryError> errors) {
        String parameter = "sort[" + index + "]";
        String value = raw == null ? "" : raw;

        int separator = value.indexOf(';');
        if (separator < 0 || value.indexOf(';', separator + 1) >= 0) {
            errors.add(new QueryError(parameter, raw, "Ожидается формат field;asc или field;desc"));
            return Optional.empty();
        }
        String fieldName = value.substring(0, separator);
        String directionToken = value.substring(separator + 1);

        Optional<QueryField> field = schema.field(fieldName);
        if (field.isEmpty()) {
            errors.add(new QueryError(parameter, raw, "Неизвестное поле " + fieldName));
            return Optional.empty();
        }
        if (!field.get().sortable()) {
            errors.add(new QueryError(parameter, raw, "По полю " + fieldName + " нельзя сортировать"));
            return Optional.empty();
        }
        Optional<Direction> direction = Direction.fromToken(directionToken);
        if (direction.isEmpty()) {
            errors.add(new QueryError(parameter, raw, "Направление сортировки должно быть asc или desc"));
            return Optional.empty();
        }
        return Optional.of(new Sort(field.get(), direction.get()));
    }

    private Integer parsePage(String raw, List<QueryError> errors) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            int page = Integer.parseInt(raw.trim());
            if (page >= 0) {
                return page;
            }
        } catch (NumberFormatException ignored) {
        }
        errors.add(new QueryError("page", raw, "Значение должно быть целым числом не меньше 0"));
        return null;
    }

    private Integer parseSize(String raw, List<QueryError> errors) {
        if (raw == null || raw.isBlank()) {
            return schema.defaultPageSize();
        }
        try {
            int size = Integer.parseInt(raw.trim());
            if (size >= 1 && size <= schema.maxPageSize()) {
                return size;
            }
        } catch (NumberFormatException ignored) {
        }
        errors.add(new QueryError("size", raw, "Значение должно быть от 1 до " + schema.maxPageSize()));
        return null;
    }

    private static String tokens(QueryField field) {
        return field.operators().stream().map(Operator::token).collect(Collectors.joining(", "));
    }
}
