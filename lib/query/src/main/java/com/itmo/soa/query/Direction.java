package com.itmo.soa.query;

import java.util.Arrays;
import java.util.Optional;

public enum Direction {
    ASC("asc"),
    DESC("desc");

    private final String token;

    Direction(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }

    public static Optional<Direction> fromToken(String token) {
        return Arrays.stream(values()).filter(d -> d.token.equals(token)).findFirst();
    }
}
