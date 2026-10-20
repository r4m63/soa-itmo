package com.itmo.soa.movie.api.http.json;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * JSON-B по умолчанию пропускает null-поля, а в ответах спецификации они есть: "genre": null.
 */
@Provider
public class JsonbConfigProvider implements ContextResolver<Jsonb> {

    private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(true));

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}
