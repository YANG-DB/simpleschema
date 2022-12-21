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

class IndexMappingReferenceInnerEntityUtilsTest {

    static Ontology ontology;
    static Accessor accessor;

    @Test
    void testCreateSimpleProperties() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaReferenceBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        Props test = IndexMappingUtils.createProperties("test", accessor);
        Assert.assertEquals(new Props(List.of("test")), test);
    }

    @Test
    void testCalculateChildReferenceEntityMappingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaReferenceBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        Assertions.assertEquals(MappingIndexType.STATIC, IndexMappingUtils.calculateMappingType(accessor.entity$("Author"), accessor));
        assertEquals(MappingIndexType.STATIC, IndexMappingUtils.calculateMappingType(accessor.entity$("Book"), accessor));
    }

    @Test
    void testCalculateChildReferenceRelationMappingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaReferenceBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        assertEquals(MappingIndexType.STATIC, IndexMappingUtils.calculateMappingType(accessor.relation$("has_Author"), accessor));
        assertEquals(MappingIndexType.STATIC, IndexMappingUtils.calculateMappingType(accessor.relation$("has_Book"), accessor));
    }

    @Test
    void calculateChildReferenceEntityNestingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaReferenceBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        Assertions.assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.entity$("Author"), accessor));
        assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.entity$("Book"), accessor));
    }

    @Test
    void calculateChildReferenceRelationNestingType() throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ontology/sample/simpleSchemaReferenceBooks.json");
        ontology = new ObjectMapper().readValue(stream, Ontology.class);
        accessor = new Accessor(ontology);

        assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.relation$("has_Author"), accessor));
        assertEquals(NestingType.NONE, IndexMappingUtils.calculateNestingType(accessor.relation$("has_Book"), accessor));
    }
}