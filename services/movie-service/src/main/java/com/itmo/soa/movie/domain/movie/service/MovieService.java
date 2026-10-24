package com.itmo.soa.movie.domain.movie.service;

import com.itmo.soa.movie.domain.movie.service.actions.CreateMovieAction;
import com.itmo.soa.movie.domain.movie.service.actions.DeleteMovieAction;
import com.itmo.soa.movie.domain.movie.service.actions.DeleteMoviesByGenreAction;
import com.itmo.soa.movie.domain.movie.service.actions.PatchMovieAction;
import com.itmo.soa.movie.domain.movie.service.filters.GetMovieByIdFilter;
import com.itmo.soa.movie.domain.movie.service.filters.GetMoviesPageFilter;
import com.itmo.soa.movie.domain.movie.service.filters.SearchDictionaryFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.experimental.Accessors;

@ApplicationScoped
@Getter
@Accessors(fluent = true)
public class MovieService {

    @Inject
    CreateMovieAction createMovie;
    @Inject
    PatchMovieAction patchMovie;
    @Inject
    DeleteMovieAction deleteMovie;
    @Inject
    DeleteMoviesByGenreAction deleteMoviesByGenre;

    @Inject
    GetMovieByIdFilter getMovieById;
    @Inject
    GetMoviesPageFilter getMoviesPage;
    @Inject
    SearchDictionaryFilter searchDictionary;
}
