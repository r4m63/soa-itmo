package com.itmo.soa.movie.api.http.json;

import com.itmo.soa.contract.movie.MovieInput;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class MovieInputReader implements MessageBodyReader<MovieInput> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MovieInput.class;
    }

    @Override
    public MovieInput readFrom(Class<MovieInput> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        return MovieJson.input(JsonFields.parseObject(entityStream));
    }
}
