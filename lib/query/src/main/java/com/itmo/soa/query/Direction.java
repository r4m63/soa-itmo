package com.itmo.soa.query;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum Direction {
    ASC("asc"),
    DESC("desc");

    private final String token;

    public static Optional<Direction> fromToken(String token) {
        return Arrays.stream(values()).filter(d -> d.token.equals(token)).findFirst();
    }
}
