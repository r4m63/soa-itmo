package com.itmo.soa.movie.infrastructure.storage.movie.model;

import com.itmo.soa.contract.movie.Color;
import com.itmo.soa.contract.movie.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Сценарист хранится колонками фильма. Все колонки null — сценариста нет.
 */
@Embeddable
public class ScreenwriterEmbeddable {

    @Column(name = "screenwriter_name")
    private String name;

    @Column(name = "screenwriter_passport_id")
    private String passportID;

    @Enumerated(EnumType.STRING)
    @Column(name = "screenwriter_hair_color")
    private Color hairColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "screenwriter_nationality")
    private Country nationality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }
}
