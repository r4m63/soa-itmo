package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.movie.infrastructure.storage.movie.model.Color;
import com.itmo.soa.movie.infrastructure.storage.movie.model.Country;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MovieGenre;
import com.itmo.soa.movie.infrastructure.storage.movie.model.MpaaRating;
import com.itmo.soa.query.Direction;
import com.itmo.soa.query.QueryField;
import com.itmo.soa.query.QuerySchema;

/**
 * Поля filter и sort для GET /movies — как в таблицах спецификации.
 */
public final class MovieQuerySchema {

    public static final QuerySchema MOVIES = QuerySchema.builder()
            .field(QueryField.integer("id"))
            .field(QueryField.string("name"))
            .field(QueryField.integer("coordinates.x"))
            .field(QueryField.longNumber("coordinates.y"))
            .field(QueryField.date("creationDate"))
            .field(QueryField.integer("oscarsCount"))
            .field(QueryField.enumeration("genre", MovieGenre.class).asNullable())
            .field(QueryField.enumeration("mpaaRating", MpaaRating.class))
            .field(QueryField.object("screenwriter", "screenwriter.name"))
            .field(QueryField.string("screenwriter.name").asNullable())
            .field(QueryField.string("screenwriter.passportID").asNullable())
            .field(QueryField.enumeration("screenwriter.hairColor", Color.class).asNullable())
            .field(QueryField.enumeration("screenwriter.nationality", Country.class).asNullable())
            .defaultSort("creationDate", Direction.ASC)
            .tieBreaker("id", Direction.ASC)
            .pageSize(20, 100)
            .build();

    private MovieQuerySchema() {
    }
}
