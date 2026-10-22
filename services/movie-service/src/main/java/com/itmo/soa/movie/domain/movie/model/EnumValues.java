package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.common.exception.InvalidDataException;
import com.itmo.soa.contract.common.error.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Значения справочников приходят в контракте строками; здесь они становятся enum сервиса.
 */
public final class EnumValues {

    private EnumValues() {
    }

    /** null — значения нет. Неизвестное имя — ошибка в errors и null. */
    public static <E extends Enum<E>> E parse(Class<E> type, String value, String location, String field,
                                              List<FieldError> errors) {
        if (value == null) {
            return null;
        }
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equals(value)) {
                return constant;
            }
        }
        errors.add(new FieldError(location, field, value, "Допустимые значения: " + Arrays.stream(type.getEnumConstants())
                .map(Enum::name).collect(Collectors.joining(", "))));
        return null;
    }

    public static void throwIfAny(String message, List<FieldError> errors) {
        if (!errors.isEmpty()) {
            throw new InvalidDataException(message, errors);
        }
    }
}
