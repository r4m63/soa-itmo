package com.itmo.soa.movie.infrastructure.storage.movie.entity;

import com.wdbt.query.QueryAttribute;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CoordinatesEmbeddable {

    @QueryAttribute
    @Column(name = "coordinates_x", nullable = false)
    private int x;

    @QueryAttribute
    @Column(name = "coordinates_y", nullable = false)
    private long y;
}
