package com.itmo.soa.movie.domain.movie.service.filters;

import com.itmo.soa.query.EnumSearch;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SearchDictionaryFilter {

    public <E extends Enum<E>> List<E> execute(Class<E> dictionary, String search) {
        return EnumSearch.search(dictionary, search);
    }
}
