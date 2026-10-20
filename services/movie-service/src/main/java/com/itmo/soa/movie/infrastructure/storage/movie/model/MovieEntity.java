package com.itmo.soa.movie.infrastructure.storage.movie.model;

import com.itmo.soa.contract.movie.MovieGenre;
import com.itmo.soa.contract.movie.MpaaRating;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Имена атрибутов совпадают с полями API (screenwriter.passportID и т.д.) — по ним строятся filter и sort.
 */
@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private CoordinatesEmbeddable coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate;

    @Column(name = "oscars_count", nullable = false)
    private int oscarsCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private MovieGenre genre;

    @Enumerated(EnumType.STRING)
    @Column(name = "mpaa_rating", nullable = false)
    private MpaaRating mpaaRating;

    @Embedded
    private ScreenwriterEmbeddable screenwriter;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordinatesEmbeddable getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesEmbeddable coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public int getOscarsCount() {
        return oscarsCount;
    }

    public void setOscarsCount(int oscarsCount) {
        this.oscarsCount = oscarsCount;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(MpaaRating mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public ScreenwriterEmbeddable getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(ScreenwriterEmbeddable screenwriter) {
        this.screenwriter = screenwriter;
    }
}
