package com.itmo.soa.query;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Поиск по значениям enum для справочников: contains без учёта регистра, без запроса — все значения.
 */
public final class EnumSearch {

    private EnumSearch() {
    }

    public static <E extends Enum<E>> List<E> search(Class<E> type, String query) {
        String needle = query == null ? "" : query.toLowerCase(Locale.ROOT);
        return Arrays.stream(type.getEnumConstants())
                .filter(e -> e.name().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
