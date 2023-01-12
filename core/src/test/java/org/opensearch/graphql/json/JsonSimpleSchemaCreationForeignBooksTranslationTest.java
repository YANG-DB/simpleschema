package org.opensearch.graphql.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.EchoingWiringFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.graphql.GraphQLEngineFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

class JsonSimpleSchemaCreationForeignBooksTranslationTest {
    static GraphQLSchema schema;
    static JsonSchema jsonSchema;

    @AfterAll
    public static void tearDown() throws Exception {
        GraphQLEngineFactory.reset();
    }

    @BeforeAll
    static void setup() throws FileNotFoundException {
        InputStream utilsSchemaInput = new FileInputStream("../schema/utils.graphql");
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("graphql/sample/simpleGQLForeignBooks.graphql");
        schema = GraphQLEngineFactory.generateSchema(new EchoingWiringFactory(), Arrays.asList(utilsSchemaInput, stream));
        GraphQLToJsonSchemaTransformer transformer = new GraphQLToJsonSchemaTransformer();
        jsonSchema = transformer.transform("simpleSchema", utilsSchemaInput, stream);
    }

    @Test
    void translateMetadataTest() {
        Assertions.assertEquals("https://opensearch.org/schemas/author", jsonSchema.getSchemaNode().get("$id"));
        Assertions.assertEquals("https://json-schema.org/draft/2020-12/schema", jsonSchema.getSchemaNode().get("$schema"));
        Assertions.assertEquals("false", jsonSchema.getSchemaNode().get("additionalProperties"));
        Assertions.assertEquals("SimpleSample", jsonSchema.getSchemaNode().get("title"));
        Assertions.assertEquals("object", jsonSchema.getSchemaNode().get("type"));
        Assertions.assertEquals("An Author's book simple example", jsonSchema.getSchemaNode().get("description"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("required"));
        Assertions.assertInstanceOf(ArrayNode.class,jsonSchema.getSchemaNode().get("required"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("$defs"));
    }

    @Test
    void translateAuthorTest() {
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("required"));
        Assertions.assertInstanceOf(ArrayNode.class,jsonSchema.getSchemaNode().get("required"));
        //todo check required content

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("properties"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("id"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("name"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("born"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("age"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("died"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("nationality"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("properties").get("books"));
        //todo check content

    }

    @Test
    void translateBookTest() {

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("$defs"));
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("$defs").get("book"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("$id"));
        Assertions.assertEquals("/schemas/book",jsonSchema.getSchemaNode().get("$defs").get("book").get("$id"));
        Assertions.assertEquals("object",jsonSchema.getSchemaNode().get("$defs").get("book").get("type"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("additionalProperties"));
        Assertions.assertEquals("false",jsonSchema.getSchemaNode().get("$defs").get("book").get("additionalProperties"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("required"));
        Assertions.assertInstanceOf(ArrayNode.class,jsonSchema.getSchemaNode().get("$defs").get("book").get("required"));
        //todo check required content

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("$defs").get("book").get("properties"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("ISBN"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("title"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("description"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("publish"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("genre"));
        //todo check content
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("type"));
        Assertions.assertEquals("object",jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("type"));
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("description"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("additionalProperties"));
        Assertions.assertEquals("false",jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("additionalProperties"));

        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("properties"));
        Assertions.assertInstanceOf(ObjectNode.class,jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("properties"));
        Assertions.assertNotNull(jsonSchema.getSchemaNode().get("$defs").get("book").get("properties").get("author").get("properties").get("id"));
        //todo check content


    }
}