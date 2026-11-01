package com.wdbt.query.parser;

import com.wdbt.query.model.Direction;
import com.wdbt.query.model.Operator;
import com.wdbt.query.model.Query;
import com.wdbt.query.schema.FieldType;
import com.wdbt.query.schema.QueryField;
import com.wdbt.query.schema.QuerySchema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QueryParserTest {

    private static final QuerySchema SCHEMA = QuerySchema.builder()
            .field(QueryField.integer("id"))
            .field(new QueryField("name", FieldType.STRING, null, false, true, "name", null,
                    Set.of(Operator.CONTAINS, Operator.STARTS_WITH)))
            .field(QueryField.enumeration("rating", SampleRating.class))
            .field(QueryField.object("screenwriter", "screenwriter.name"))
            .field(QueryField.string("screenwriter.name").asNullable())
            .defaultSort("name", Direction.ASC)
            .tieBreaker("id", Direction.ASC)
            .pageSize(10, 50)
            .build();

    private final QueryParser queries = new QueryParser(SCHEMA);

    @Test
    void parsesFiltersUsingProgrammaticSchema() {
        Query query = queries.parse(
                List.of("screenwriter.name;contains;John", "rating;eq;R"), List.of(), null, null);

        assertEquals(10, query.page().size());
        assertEquals("John", query.filters().get(0).value());
        assertEquals(SampleRating.R, query.filters().get(1).value());
        assertEquals("screenwriter.name", query.schema().field("screenwriter.name").orElseThrow().path());
        assertTrue(query.schema().field("screenwriter").orElseThrow().operators().contains(Operator.IS_NULL));
    }

    @Test
    void appliesPaginationAndDefaultSortFromSchema() {
        Query query = queries.parse(List.of(), List.of(), null, null);

        assertEquals(10, query.page().size());
        assertEquals(List.of("name", "id"), query.effectiveSort().stream().map(sort -> sort.field().name()).toList());
        assertThrows(QueryValidationException.class,
                () -> queries.parse(List.of(), List.of(), null, "51"));
    }

    @Test
    void honorsCustomOperators() {
        QueryField name = SCHEMA.field("name").orElseThrow();

        assertTrue(name.operators().contains(Operator.CONTAINS));
        assertFalse(name.operators().contains(Operator.GT));
        assertThrows(QueryValidationException.class,
                () -> queries.parse(List.of("name;gt;A"), List.of(), null, null));
    }

    @Test
    void rejectsUnknownFields() {
        assertThrows(QueryValidationException.class,
                () -> queries.parse(List.of("unknown;eq;value"), List.of(), null, null));
    }

    enum SampleRating {
        G, PG, R
    }
}
