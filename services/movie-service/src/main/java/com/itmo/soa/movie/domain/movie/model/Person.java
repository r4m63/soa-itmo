package com.itmo.soa.movie.domain.movie.model;

public record Person(String name, String passportID, Color hairColor, Country nationality) {

    private static final int MIN_PASSPORT_ID_LENGTH = 5;

    public Person {
        if (name == null || name.isBlank()) {
            throw new MovieRuleViolationException("screenwriter.name", name, "Имя не может быть пустым");
        }
        if (passportID != null && passportID.length() < MIN_PASSPORT_ID_LENGTH) {
            throw new MovieRuleViolationException("screenwriter.passportID", passportID,
                    "Длина должна быть не меньше " + MIN_PASSPORT_ID_LENGTH + " символов");
        }
        if (nationality == null) {
            throw new MovieRuleViolationException("screenwriter.nationality", null, "Поле обязательно");
        }
    }
}
