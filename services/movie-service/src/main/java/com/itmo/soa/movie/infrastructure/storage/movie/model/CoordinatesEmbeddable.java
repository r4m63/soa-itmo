package com.itmo.soa.movie.infrastructure.storage.movie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CoordinatesEmbeddable {

    @Column(name = "coordinates_x", nullable = false)
    private int x;

    @Column(name = "coordinates_y", nullable = false)
    private long y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }
}
