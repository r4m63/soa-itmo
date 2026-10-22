package com.itmo.soa.common.query;

import java.util.List;
import java.util.function.Function;

public record Page<T>(List<T> items, int page, int size, long totalElements, int totalPages) {

    public static <T> Page<T> of(List<T> items, PageRequest request, long totalElements) {
        int totalPages = (int) ((totalElements + request.size() - 1) / request.size());
        return new Page<>(List.copyOf(items), request.page(), request.size(), totalElements, totalPages);
    }

    public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
        return new Page<>(items.stream().<R>map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
