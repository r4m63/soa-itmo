package com.wdbt.query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QueryService {

    private final QueryRegistry registry;
    private final Map<Class<?>, QueryParser> parsers = new ConcurrentHashMap<>();

    public QueryService(QueryRegistry registry) {
        this.registry = registry;
    }

    public static QueryService forEntities(Class<?>... entities) {
        return new QueryService(QueryRegistry.of(entities));
    }

    public Query parse(Class<?> entityType, List<String> filters, List<String> sorts, String page, String size) {
        QueryParser parser = parsers.computeIfAbsent(entityType, type -> new QueryParser(registry.schema(type)));
        return parser.parse(filters, sorts, page, size);
    }
}
