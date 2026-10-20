package com.itmo.soa.oscar.domain.oscar.model;

/**
 * Всё, что Oscar Service нужно знать о фильме.
 */
public record AwardedMovie(int id, int oscarsCount) {

    /** Награда +1. */
    public OscarsChange rewarded() {
        return new OscarsChange(id, oscarsCount, Math.addExact(oscarsCount, 1));
    }
}
