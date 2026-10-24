package com.itmo.soa.movie.infrastructure.storage.movie.model;

import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.model.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
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
}
