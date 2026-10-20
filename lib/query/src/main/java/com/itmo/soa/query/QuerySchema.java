package com.itmo.soa.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Описание коллекции: какие поля можно фильтровать и сортировать, сортировка и размер страницы по умолчанию.
 */
public final class QuerySchema {

    private final Map<String, QueryField> fields;
    private final List<Sort> defaultSort;
    private final Sort tieBreaker;
    private final int defaultPageSize;
    private final int maxPageSize;

    private QuerySchema(Builder b) {
        this.fields = Map.copyOf(b.fields);
        this.defaultSort = List.copyOf(b.defaultSort);
        this.tieBreaker = b.tieBreaker;
        this.defaultPageSize = b.defaultPageSize;
        this.maxPageSize = b.maxPageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<QueryField> field(String name) {
        return Optional.ofNullable(fields.get(name));
    }

    public List<Sort> defaultSort() {
        return defaultSort;
    }

    /** Добавляется в конец любой сортировки, чтобы порядок страниц был стабильным. */
    public Optional<Sort> tieBreaker() {
        return Optional.ofNullable(tieBreaker);
    }

    public int defaultPageSize() {
        return defaultPageSize;
    }

    public int maxPageSize() {
        return maxPageSize;
    }

    public static final class Builder {

        private final Map<String, QueryField> fields = new LinkedHashMap<>();
        private final List<Sort> defaultSort = new ArrayList<>();
        private Sort tieBreaker;
        private int defaultPageSize = 20;
        private int maxPageSize = 100;

        private Builder() {
        }

        public Builder field(QueryField field) {
            if (fields.putIfAbsent(field.name(), field) != null) {
                throw new IllegalArgumentException("Поле " + field.name() + " уже описано");
            }
            return this;
        }

        public Builder defaultSort(String field, Direction direction) {
            defaultSort.add(new Sort(sortable(field), direction));
            return this;
        }

        public Builder tieBreaker(String field, Direction direction) {
            tieBreaker = new Sort(sortable(field), direction);
            return this;
        }

        public Builder pageSize(int defaultSize, int maxSize) {
            if (defaultSize < 1 || defaultSize > maxSize) {
                throw new IllegalArgumentException("Размер страницы по умолчанию должен быть от 1 до " + maxSize);
            }
            defaultPageSize = defaultSize;
            maxPageSize = maxSize;
            return this;
        }

        public QuerySchema build() {
            return new QuerySchema(this);
        }

        private QueryField sortable(String name) {
            QueryField field = fields.get(name);
            if (field == null || !field.sortable()) {
                throw new IllegalArgumentException("Поле " + name + " не описано или не сортируется");
            }
            return field;
        }
    }
}
