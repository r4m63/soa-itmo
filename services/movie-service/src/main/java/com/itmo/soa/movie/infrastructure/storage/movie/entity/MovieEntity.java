package com.itmo.soa.movie.infrastructure.storage.movie.entity;

import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import com.wdbt.query.FieldType;
import com.wdbt.query.QueryAttribute;
import com.wdbt.query.QueryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
@QueryEntity(defaultSort = "creationDate;asc", tieBreaker = "id", defaultPageSize = 20, maxPageSize = 100)
@Getter
@Setter
@NoArgsConstructor
public class MovieEntity {

    @QueryAttribute
    @Id
    @SequenceGenerator(name = "movies_id", sequenceName = "movies_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movies_id")
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private Integer id;

    @QueryAttribute
    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private CoordinatesEmbeddable coordinates;

    @QueryAttribute
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate;

    @QueryAttribute
    @Column(name = "oscars_count", nullable = false)
    private int oscarsCount;

    @QueryAttribute(nullable = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private MovieGenre genre;

    @QueryAttribute
    @Enumerated(EnumType.STRING)
    @Column(name = "mpaa_rating", nullable = false)
    private MpaaRating mpaaRating;

    @QueryAttribute(type = FieldType.OBJECT, nullable = true, sortable = false,
            presencePath = "screenwriter.name")
    @Embedded
    private ScreenwriterEmbeddable screenwriter;
}
