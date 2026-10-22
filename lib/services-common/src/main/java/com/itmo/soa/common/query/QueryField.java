package com.itmo.soa.common.query;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Поле, доступное для filter/sort.
 *
 * @param name         имя в API: {@code screenwriter.name}
 * @param path         путь к атрибуту сущности, по умолчанию совпадает с name
 * @param presencePath для OBJECT: обязательный атрибут вложенного объекта, по которому проверяется null
 */
public record QueryField(
        String name,
        FieldType type,
        Class<? extends Enum<?>> enumType,
        boolean nullable,
        boolean sortable,
        String path,
        String presencePath
) {

    public static QueryField integer(String name) {
        return new QueryField(name, FieldType.INTEGER, null, false, true, name, null);
    }

    public static QueryField longNumber(String name) {
        return new QueryField(name, FieldType.LONG, null, false, true, name, null);
    }

    public static QueryField date(String name) {
        return new QueryField(name, FieldType.DATE, null, false, true, name, null);
    }

    public static QueryField string(String name) {
        return new QueryField(name, FieldType.STRING, null, false, true, name, null);
    }

    public static QueryField enumeration(String name, Class<? extends Enum<?>> enumType) {
        return new QueryField(name, FieldType.ENUM, enumType, false, true, name, null);
    }

    /** Nullable-объект: доступны только isNull/notNull, сортировки нет. */
    public static QueryField object(String name, String presencePath) {
        return new QueryField(name, FieldType.OBJECT, null, true, false, name, presencePath);
    }

    public QueryField asNullable() {
        return new QueryField(name, type, enumType, true, sortable, path, presencePath);
    }

    public Set<Operator> operators() {
        EnumSet<Operator> result = type.operators().isEmpty()
                ? EnumSet.noneOf(Operator.class)
                : EnumSet.copyOf(type.operators());
        if (nullable) {
            result.add(Operator.IS_NULL);
            result.add(Operator.NOT_NULL);
        }
        return result;
    }

    /** Значения enum в порядке объявления. */
    public Enum<?>[] enumConstants() {
        return enumType == null ? new Enum<?>[0] : enumType.getEnumConstants();
    }

    /**
     * Разбирает строковое значение фильтра.
     *
     * @throws IllegalArgumentException с сообщением для клиента
     */
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
