package com.itmo.soa.common.query;

import java.util.Arrays;
import java.util.Optional;

public enum Operator {
    EQ("eq"),
    NE("ne"),
    GT("gt"),
    GTE("gte"),
    LT("lt"),
    LTE("lte"),
    CONTAINS("contains"),
    STARTS_WITH("startsWith"),
    ENDS_WITH("endsWith"),
    IS_NULL("isNull"),
    NOT_NULL("notNull");

    private final String token;

    Operator(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }

    public boolean requiresValue() {
        return this != IS_NULL && this != NOT_NULL;
    }

    public static Optional<Operator> fromToken(String token) {
        return Arrays.stream(values()).filter(op -> op.token.equals(token)).findFirst();
    }
}
