package com.itmo.soa.movie.api.http.json;

import com.itmo.soa.contract.error.FieldError;
import com.itmo.soa.movie.domain.movie.model.ValidationException;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Читает поля JSON-объекта с проверкой типов. Ошибки копит в общем списке с путём поля (screenwriter.passportID).
 * Отсутствующее поле и JSON null читаются как null; отличить их можно через {@link #has(String)}.
 */
final class JsonFields {

    static final String INVALID_BODY = "Некорректное тело запроса";

    private final JsonObject json;
    private final String prefix;
    private final List<FieldError> errors;

    JsonFields(JsonObject json, String prefix, List<FieldError> errors) {
        this.json = json;
        this.prefix = prefix;
        this.errors = errors;
    }

    static JsonObject parseObject(InputStream body) {
        try (JsonReader reader = Json.createReader(body)) {
            return reader.readObject();
        } catch (JsonException | IllegalStateException e) {
            throw new ValidationException(INVALID_BODY,
                    List.of(FieldError.body(null, null, "Невалидный JSON: ожидается объект")));
        }
    }

    static void throwIfAny(List<FieldError> errors) {
        if (!errors.isEmpty()) {
            throw new ValidationException(INVALID_BODY, errors);
        }
    }

    /** Поля вне allowed — ошибка; для полей из special — своё сообщение (например, про id). */
    void rejectUnknown(Set<String> allowed, Map<String, String> special) {
        for (String key : json.keySet()) {
            if (!allowed.contains(key)) {
                String message = special.getOrDefault(key, "Неизвестное поле " + path(key));
                errors.add(FieldError.body(path(key), rejected(json.get(key)), message));
            }
        }
    }

    boolean has(String key) {
        return json.containsKey(key);
    }

    String string(String key) {
        JsonValue value = value(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonString string) {
            return string.getString();
        }
        return typeError(key, value, "Ожидается строка");
    }

    Integer integer(String key) {
        JsonValue value = value(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonNumber number && number.isIntegral()) {
            try {
                return number.intValueExact();
            } catch (ArithmeticException ignored) {
                // ниже — общее сообщение
            }
        }
        return typeError(key, value, "Ожидается целое число от " + Integer.MIN_VALUE + " до " + Integer.MAX_VALUE);
    }

    Long longNumber(String key) {
        JsonValue value = value(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonNumber number && number.isIntegral()) {
            try {
                return number.longValueExact();
            } catch (ArithmeticException ignored) {
                // ниже — общее сообщение
            }
        }
        return typeError(key, value, "Ожидается целое число от " + Long.MIN_VALUE + " до " + Long.MAX_VALUE);
    }

    <E extends Enum<E>> E enumeration(String key, Class<E> type) {
        JsonValue value = value(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonString string) {
            for (E constant : type.getEnumConstants()) {
                if (constant.name().equals(string.getString())) {
                    return constant;
                }
            }
        }
        return typeError(key, value, "Допустимые значения: " + Arrays.stream(type.getEnumConstants())
                .map(Enum::name).collect(Collectors.joining(", ")));
    }

    /** Вложенный объект или null, если поля нет, оно null или имеет неверный тип. */
    JsonFields object(String key) {
        JsonValue value = value(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonObject object) {
            return new JsonFields(object, path(key), errors);
        }
        return typeError(key, value, "Ожидается объект");
    }

    private JsonValue value(String key) {
        JsonValue value = json.get(key);
        return value == null || value.getValueType() == JsonValue.ValueType.NULL ? null : value;
    }

    private <T> T typeError(String key, JsonValue value, String message) {
        errors.add(FieldError.body(path(key), rejected(value), message));
        return null;
    }

    private String path(String key) {
        return prefix.isEmpty() ? key : prefix + "." + key;
    }

    private static Object rejected(JsonValue value) {
        return switch (value.getValueType()) {
            case STRING -> ((JsonString) value).getString();
            case NUMBER -> ((JsonNumber) value).numberValue();
            case TRUE -> true;
            case FALSE -> false;
            case NULL -> null;
            default -> value.toString();
        };
    }
}
