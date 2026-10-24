package com.itmo.soa.query;

import static com.itmo.soa.query.Operator.*;

import java.util.EnumSet;
import java.util.Set;

public enum FieldType {
    INTEGER(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    LONG(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    DATE(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    STRING(EnumSet.of(EQ, NE, GT, GTE, LT, LTE, CONTAINS, STARTS_WITH, ENDS_WITH)),
    ENUM(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    OBJECT(EnumSet.noneOf(Operator.class));

    private final Set<Operator> operators;

    FieldType(Set<Operator> operators) {
        this.operators = operators;
    }

    Set<Operator> operators() {
        return operators;
    }
}
