package com.itmo.soa.query;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
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

    public boolean requiresValue() {
        return this != IS_NULL && this != NOT_NULL;
    }

    public static Optional<Operator> fromToken(String token) {
        return Arrays.stream(values()).filter(op -> op.token.equals(token)).findFirst();
    }
}
