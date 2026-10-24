package com.itmo.soa.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Query(List<Filter> filters, List<Sort> sorts, PageRequest page, QuerySchema schema) {

    public List<List<Filter>> filterGroups() {
        Map<String, List<Filter>> groups = new LinkedHashMap<>();
        for (Filter filter : filters) {
            groups.computeIfAbsent(groupKey(filter), k -> new ArrayList<>()).add(filter);
        }
        return groups.values().stream().map(List::copyOf).toList();
    }

    public List<Sort> effectiveSort() {
        List<Sort> result = new ArrayList<>(sorts.isEmpty() ? schema.defaultSort() : sorts);
        schema.tieBreaker()
                .filter(tie -> result.stream().noneMatch(s -> s.field().name().equals(tie.field().name())))
                .ifPresent(result::add);
        return List.copyOf(result);
    }

    private static String groupKey(Filter filter) {
        String field = filter.field().name();
        return switch (filter.operator()) {
            case NE -> field + "#ne#" + filter.index();
            case IS_NULL -> field + "#" + Operator.EQ.token();
            default -> field + "#" + filter.operator().token();
        };
    }
}
