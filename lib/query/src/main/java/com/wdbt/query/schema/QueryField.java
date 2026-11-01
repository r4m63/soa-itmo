package com.wdbt.query.schema;

import com.wdbt.query.model.Operator;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public record QueryField(
        String name,
        FieldType type,
        Class<? extends Enum<?>> enumType,
        boolean nullable,
        boolean sortable,
        String path,
        String presencePath,
        Set<Operator> operators
) {

    public QueryField {
        operators = operators.isEmpty()
                ? Set.of()
                : Collections.unmodifiableSet(EnumSet.copyOf(operators));
    }

    public static QueryField integer(String name) {
        return of(name, FieldType.INTEGER, null);
    }

    public static QueryField longNumber(String name) {
        return of(name, FieldType.LONG, null);
    }

    public static QueryField date(String name) {
        return of(name, FieldType.DATE, null);
    }

    public static QueryField string(String name) {
        return of(name, FieldType.STRING, null);
    }

    public static QueryField enumeration(String name, Class<? extends Enum<?>> enumType) {
        return of(name, FieldType.ENUM, enumType);
    }

    public static QueryField object(String name, String presencePath) {
        return new QueryField(name, FieldType.OBJECT, null, true, false, name, presencePath,
                defaultOperators(FieldType.OBJECT, true));
    }

    private static QueryField of(String name, FieldType type, Class<? extends Enum<?>> enumType) {
        return new QueryField(name, type, enumType, false, true, name, null, defaultOperators(type, false));
    }

    public QueryField asNullable() {
        return new QueryField(name, type, enumType, true, sortable, path, presencePath,
                defaultOperators(type, true));
    }

    private static Set<Operator> defaultOperators(FieldType type, boolean nullable) {
        EnumSet<Operator> result = type.operators().isEmpty()
                ? EnumSet.noneOf(Operator.class)
                : EnumSet.copyOf(type.operators());
        if (nullable) {
            result.add(Operator.IS_NULL);
            result.add(Operator.NOT_NULL);
        }
        return result;
    }

    public Enum<?>[] enumConstants() {
        return enumType == null ? new Enum<?>[0] : enumType.getEnumConstants();
    }

    public Object parseValue(String raw) {
        return switch (type) {
            case INTEGER -> {
                try {
                    yield Integer.valueOf(raw);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Значение для " + name + " должно быть целым числом");
                }
            }
            case LONG -> {
                try {
                    yield Long.valueOf(raw);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Значение для " + name + " должно быть целым числом");
                }
            }
            case DATE -> {
                try {
                    yield LocalDate.parse(raw);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Значение для " + name + " должно быть датой в формате YYYY-MM-DD");
                }
            }
            case STRING -> raw;
            case ENUM -> Arrays.stream(enumConstants())
                    .filter(c -> c.name().equals(raw))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Допустимые значения для " + name + ": " + enumNames()));
            case OBJECT -> throw new IllegalArgumentException("Поле " + name + " не сравнивается со значением");
        };
    }

    private String enumNames() {
        return Arrays.stream(enumConstants()).map(Enum::name).collect(Collectors.joining(", "));
    }
}
