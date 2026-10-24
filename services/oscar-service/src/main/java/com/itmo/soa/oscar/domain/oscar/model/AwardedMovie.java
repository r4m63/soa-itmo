package com.itmo.soa.oscar.domain.oscar.model;

public record AwardedMovie(int id, int oscarsCount) {

    public AwardedMovie {
        if (oscarsCount < 1) {
            throw new IllegalStateException("У фильма " + id + " oscarsCount = " + oscarsCount + ", ожидается не меньше 1");
        }
    }

    public OscarsChange rewarded() {
        return new OscarsChange(id, oscarsCount, Math.addExact(oscarsCount, 1));
    }
}
