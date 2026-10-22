package com.itmo.soa.movie.domain.movie.model;

import com.itmo.soa.contract.common.error.FieldError;
import com.itmo.soa.contract.movie.movie.dto.PersonDto;
import com.itmo.soa.movie.infrastructure.storage.movie.model.Color;
import com.itmo.soa.movie.infrastructure.storage.movie.model.Country;

import java.util.List;

public record Person(String name, String passportID, Color hairColor, Country nationality) {

    /** null — сценариста нет. Неизвестные hairColor / nationality — ошибки в errors. */
    static Person from(PersonDto dto, List<FieldError> errors) {
        if (dto == null) {
            return null;
        }
        return new Person(dto.getName(), dto.getPassportID(),
                EnumValues.parse(Color.class, dto.getHairColor(), FieldError.BODY, "screenwriter.hairColor", errors),
                EnumValues.parse(Country.class, dto.getNationality(), FieldError.BODY, "screenwriter.nationality", errors));
    }
}
