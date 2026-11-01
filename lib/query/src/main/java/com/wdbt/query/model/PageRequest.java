package com.wdbt.query.model;

public record PageRequest(int page, int size) {

    public long offset() {
        return (long) page * size;
    }
}
