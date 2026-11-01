package com.itmo.soa.movie.infrastructure.storage.movie;

import com.itmo.soa.movie.infrastructure.storage.movie.entity.MovieEntity;
import com.wdbt.query.Direction;
import com.wdbt.query.FieldType;
import com.wdbt.query.Query;
import com.wdbt.query.QueryRegistry;
import com.wdbt.query.QuerySchema;
import com.wdbt.query.QueryService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieQuerySchemaTest {

    private final QueryService queries = QueryService.forEntities(MovieEntity.class);
    private final QuerySchema schema = QueryRegistry.of(MovieEntity.class).schema(MovieEntity.class);

    @Test
    void exposesAllMovieFieldsFromAnnotations() {
        List<String> fields = List.of(
                "id", "name", "coordinates.x", "coordinates.y", "creationDate", "oscarsCount",
                "genre", "mpaaRating", "screenwriter", "screenwriter.name", "screenwriter.passportID",
                "screenwriter.hairColor", "screenwriter.nationality");

        assertEquals(13, fields.size());
        fields.forEach(field -> assertTrue(schema.field(field).isPresent(), field));
        assertEquals(FieldType.INTEGER, schema.field("coordinates.x").orElseThrow().type());
        assertEquals(FieldType.LONG, schema.field("coordinates.y").orElseThrow().type());
        assertEquals(FieldType.OBJECT, schema.field("screenwriter").orElseThrow().type());
        assertFalse(schema.field("screenwriter").orElseThrow().sortable());
    }

    @Test
    void preservesNullableRulesAndPaginationDefaults() {
        assertTrue(schema.field("genre").orElseThrow().nullable());
        assertTrue(schema.field("screenwriter").orElseThrow().nullable());
        assertTrue(schema.field("screenwriter.name").orElseThrow().nullable());
        assertTrue(schema.field("screenwriter.passportID").orElseThrow().nullable());
        assertTrue(schema.field("screenwriter.hairColor").orElseThrow().nullable());
        assertTrue(schema.field("screenwriter.nationality").orElseThrow().nullable());
        assertFalse(schema.field("name").orElseThrow().nullable());

        Query query = queries.parse(MovieEntity.class, List.of(), List.of(), null, null);
        assertEquals(0, query.page().page());
        assertEquals(20, query.page().size());
        assertEquals(100, schema.maxPageSize());
        assertEquals("creationDate", schema.defaultSort().getFirst().field().name());
        assertEquals(Direction.ASC, schema.defaultSort().getFirst().direction());
        assertEquals("id", schema.tieBreaker().orElseThrow().field().name());
        assertEquals(Direction.ASC, schema.tieBreaker().orElseThrow().direction());
    }

}
