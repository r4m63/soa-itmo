package com.itmo.soa.movie.infrastructure.storage.movie.entity;

import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.model.Country;
import com.wdbt.query.QueryAttribute;
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

    @QueryAttribute(nullable = true)
    @Column(name = "screenwriter_name")
    private String name;

    @QueryAttribute(nullable = true)
    @Column(name = "screenwriter_passport_id")
    private String passportID;

    @QueryAttribute(nullable = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "screenwriter_hair_color")
    private Color hairColor;

    @QueryAttribute(nullable = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "screenwriter_nationality")
    private Country nationality;
}
