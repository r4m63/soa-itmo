package com.itmo.soa.query;

import java.util.EnumSet;
import java.util.Set;

import static com.itmo.soa.query.Operator.*;

public enum FieldType {
    INTEGER(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    LONG(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    DATE(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    STRING(EnumSet.of(EQ, NE, GT, GTE, LT, LTE, CONTAINS, STARTS_WITH, ENDS_WITH)),
    ENUM(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    /** Вложенный объект целиком: фильтруется только на null. */
    OBJECT(EnumSet.noneOf(Operator.class));

    private final Set<Operator> operators;

    FieldType(Set<Operator> operators) {
        this.operators = operators;
    }

    Set<Operator> operators() {
        return operators;
    }
}
