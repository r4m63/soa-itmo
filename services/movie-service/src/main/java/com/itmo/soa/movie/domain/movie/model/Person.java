package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.contract.movie.Color;
import com.itmo.soa.contract.movie.Country;

public record Person(String name, String passportID, Color hairColor, Country nationality) {

    static final int MIN_PASSPORT_ID_LENGTH = 5;

    /** Проверяет сценариста из запроса; null допустим — сценариста нет. */
    static Person from(com.itmo.soa.contract.movie.Person dto, Violations violations) {
        if (dto == null) {
            return null;
        }
        boolean valid = true;
        if (dto.name() == null) {
            violations.add("screenwriter.name", null, Violations.REQUIRED);
            valid = false;
        } else if (dto.name().isBlank()) {
            violations.add("screenwriter.name", dto.name(), "Имя не может быть пустым");
            valid = false;
        }
        if (dto.passportID() != null && dto.passportID().length() < MIN_PASSPORT_ID_LENGTH) {
            violations.add("screenwriter.passportID", dto.passportID(),
                    "Длина должна быть не меньше " + MIN_PASSPORT_ID_LENGTH + " символов");
            valid = false;
        }
        if (dto.nationality() == null) {
            violations.add("screenwriter.nationality", null, Violations.REQUIRED);
            valid = false;
        }
        return valid ? new Person(dto.name(), dto.passportID(), dto.hairColor(), dto.nationality()) : null;
    }
}
