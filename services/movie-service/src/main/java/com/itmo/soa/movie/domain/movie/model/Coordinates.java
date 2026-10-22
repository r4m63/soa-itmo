package com.itmo.soa.movie.domain.movie.model;

public record Coordinates(int x, long y) {

    static Coordinates from(com.itmo.soa.contract.request.movie.CoordinatesDto dto) {
        return new Coordinates(dto.getX(), dto.getY());
    }
}
