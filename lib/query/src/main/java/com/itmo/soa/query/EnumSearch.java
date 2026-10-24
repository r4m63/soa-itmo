package com.itmo.soa.query;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumSearch {

    public static <E extends Enum<E>> List<E> search(Class<E> type, String query) {
        String needle = query == null ? "" : query.toLowerCase(Locale.ROOT);
        return Arrays.stream(type.getEnumConstants())
                .filter(e -> e.name().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
