package com.itmo.soa.movie.domain.movie.service.filters;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class SearchDictionaryFilter {

    public <E extends Enum<E>> List<E> execute(Class<E> dictionary, String search) {
        String needle = search == null ? "" : search.toLowerCase(Locale.ROOT);
        return Arrays.stream(dictionary.getEnumConstants())
                .filter(value -> value.name().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
