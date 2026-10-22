package com.itmo.soa.oscar.domain.oscar.model;

/**
 * Всё, что Oscar Service нужно знать о фильме.
 */
public record AwardedMovie(int id, int oscarsCount) {

    /** У фильма всегда хотя бы один Оскар — это же правило держит Movie Service. */
    public AwardedMovie {
        if (oscarsCount < 1) {
            throw new IllegalStateException("У фильма " + id + " oscarsCount = " + oscarsCount + ", ожидается не меньше 1");
        }
    }

    /** Награда +1. */
    public OscarsChange rewarded() {
        return new OscarsChange(id, oscarsCount, Math.addExact(oscarsCount, 1));
    }
}
