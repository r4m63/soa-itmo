package com.itmo.soa.movie.domain.movie.model;

public record Coordinates(int x, long y) {

    /** Проверяет координаты из запроса; при нарушениях возвращает null и пишет их в violations. */
    static Coordinates from(com.itmo.soa.contract.movie.Coordinates dto, Violations violations) {
        if (dto == null) {
            violations.add("coordinates", null, Violations.REQUIRED);
            return null;
        }
        if (dto.x() == null) {
            violations.add("coordinates.x", null, Violations.REQUIRED);
        }
        if (dto.y() == null) {
            violations.add("coordinates.y", null, Violations.REQUIRED);
        }
        return dto.x() == null || dto.y() == null ? null : new Coordinates(dto.x(), dto.y());
    }
}
