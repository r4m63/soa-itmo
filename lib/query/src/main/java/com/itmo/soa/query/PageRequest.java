package com.itmo.soa.query;

public record PageRequest(int page, int size) {

    public long offset() {
        return (long) page * size;
    }
}
