package com.wdbt.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class QueryRegistryTest {

    private final QueryService queries = QueryService.forEntities(SampleEntity.class);

    @Test
    void buildsSchemaFromEntityAndNestedFieldAnnotations() {
        Query query = queries.parse(SampleEntity.class,
                List.of("screenwriter.name;contains;John", "rating;eq;R"), List.of(), null, null);

        assertEquals(10, query.page().size());
        assertEquals("John", query.filters().get(0).value());
        assertEquals(SampleRating.R, query.filters().get(1).value());
        assertEquals("screenwriter.name", query.schema().field("screenwriter.name").orElseThrow().path());
        assertTrue(query.schema().field("screenwriter").orElseThrow().operators().contains(Operator.IS_NULL));
    }

    @Test
    void appliesPaginationAndDefaultSortFromEntityAnnotation() {
        Query query = queries.parse(SampleEntity.class, List.of(), List.of(), null, null);

        assertEquals(10, query.page().size());
        assertEquals(List.of("name", "id"), query.effectiveSort().stream().map(sort -> sort.field().name()).toList());
        assertThrows(QueryValidationException.class,
                () -> queries.parse(SampleEntity.class, List.of(), List.of(), null, "51"));
    }

    @Test
    void honorsCustomOperators() {
        QuerySchema schema = QueryRegistry.of(SampleEntity.class).schema(SampleEntity.class);
        QueryField name = schema.field("name").orElseThrow();

        assertTrue(name.operators().contains(Operator.CONTAINS));
        assertFalse(name.operators().contains(Operator.GT));
        assertThrows(QueryValidationException.class,
                () -> queries.parse(SampleEntity.class, List.of("name;gt;A"), List.of(), null, null));
    }

    @Test
    void rejectsUnregisteredEntities() {
        assertThrows(IllegalArgumentException.class, () -> QueryRegistry.of(String.class));
        assertThrows(IllegalArgumentException.class, () -> queries.parse(UnregisteredEntity.class, List.of(), List.of(), null, null));
    }

    @QueryEntity(defaultPageSize = 10, maxPageSize = 50, defaultSort = "name;asc", tieBreaker = "id")
    static class SampleEntity {
        @QueryAttribute
        private Integer id;

        @QueryAttribute(operators = {Operator.CONTAINS, Operator.STARTS_WITH})
        private String name;

        @QueryAttribute
        private SampleRating rating;

        @QueryAttribute(type = FieldType.OBJECT, nullable = true, sortable = false, presencePath = "screenwriter.name")
        private SampleScreenwriter screenwriter;
    }

    static class SampleScreenwriter {
        @QueryAttribute(nullable = true)
        private String name;
    }

    enum SampleRating {
        G, PG, R
    }

    static class UnregisteredEntity {
    }
}
