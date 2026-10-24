package com.itmo.soa.query;

import static com.itmo.soa.query.Operator.*;

import java.util.EnumSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum FieldType {
    INTEGER(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    LONG(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    DATE(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    STRING(EnumSet.of(EQ, NE, GT, GTE, LT, LTE, CONTAINS, STARTS_WITH, ENDS_WITH)),
    ENUM(EnumSet.of(EQ, NE, GT, GTE, LT, LTE)),
    OBJECT(EnumSet.noneOf(Operator.class));

    private final Set<Operator> operators;
}
