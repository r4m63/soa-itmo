package com.wdbt.query.model;

import com.wdbt.query.schema.QueryField;

public record Sort(QueryField field, Direction direction) {

    public static String format(String field, Direction direction) {
        return field + ";" + direction.token();
    }
}
