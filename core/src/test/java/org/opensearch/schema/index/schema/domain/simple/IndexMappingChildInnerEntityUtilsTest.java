package org.opensearch.schema.index.schema.domain.simple;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensearch.schema.index.schema.IndexMappingUtils;
import org.opensearch.schema.index.schema.MappingIndexType;
import org.opensearch.schema.index.schema.NestingType;
import org.opensearch.schema.index.schema.Props;
import org.opensearch.schema.ontology.Accessor;
import org.opensearch.schema.ontology.Ontology;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexMappingChildInnerEntityUtilsTest {

    static Ontology ontology;
    static Accessor accessor;

    @Test
    void testCreateSimpleProperties() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaChildBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        Props test = IndexMappingUtils.createProperties("test", accessor);
        Assert.assertEquals(new Props(List.of("test")), test);
    }

    @Test
    void testCalculateChildInnerEntityMappingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaChildBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        Assertions.assertEquals(MappingIndexType.STATIC, IndexMappingUtils.calculateMappingType(accessor.entity$("Author"), accessor));
        assertEquals(MappingIndexType.NESTED, IndexMappingUtils.calculateMappingType(accessor.entity$("Book"), accessor));
    }

    @Test
    void testCalculateChildInnerRelationMappingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaChildBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        assertEquals(MappingIndexType.NONE, IndexMappingUtils.calculateMappingType(accessor.relation$("has_Author"), accessor));
        assertEquals(MappingIndexType.NONE, IndexMappingUtils.calculateMappingType(accessor.relation$("has_Book"), accessor));
    }

    @Test
    void calculateChildInnerEntityNestingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaChildBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        Assertions.assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.entity$("Author"), accessor));
        assertEquals(NestingType.CHILD, IndexMappingUtils.calculateNestingType(accessor.entity$("Book"), accessor));
    }

    @Test
    void calculateChildInnerRelationNestingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaChildBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.relation$("has_Author"), accessor));
        assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.relation$("has_Book"), accessor));
    }
}