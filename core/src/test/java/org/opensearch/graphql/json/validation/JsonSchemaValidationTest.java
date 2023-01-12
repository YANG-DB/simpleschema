package org.opensearch.graphql.json.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.ValidationMessage;
import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonSchemaValidationTest extends BaseJsonSchemaValidatorTest {
    static GraphQLSchema schema;

    @BeforeAll
    static void setup() {
    }

    private JsonSchema initSchema(String name) throws IOException {
        JsonNode schemaNode = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource(name).toString());
        // With automatic version detection
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setTypeLoose(false);
        JsonSchema schema = getJsonSchemaFromJsonNodeAutomaticVersion(schemaNode, config);
        schema.initializeValidators(); // by default all schemas are loaded lazily. You can load them eagerly via
        return schema;
    }

    @Test
    public void validationTest() throws IOException {
        JsonSchema schema = initSchema("json/schema/simpleNestedSchema.json");

        JsonNode node = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource("json/validation/sampleValidData.json").toString());
        Set<ValidationMessage> errors = schema.validate(node);
        assertEquals(errors.size(), 0);
    }


    @Test
    public void sampleNonValidAuthorRefInBooksDataTest() throws IOException {
        JsonSchema schema = initSchema("json/schema/simpleNestedSchema.json");

        JsonNode node = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource("./json/validation/sampleNonValidAuthorRefInBooksData.json").toString());
        Set<ValidationMessage> errors = schema.validate(node);
        assertEquals(errors.size(), 1);
    }
    @Test
    public void sampleNonValidBooksNotAsArrayInAuthorDataTest() throws IOException {
        JsonSchema schema = initSchema("json/schema/simpleNestedSchema.json");

        JsonNode node = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource("json/validation/sampleNonValidBooksNotAsArrayInAuthorData.json").toString());
        Set<ValidationMessage> errors = schema.validate(node);
        assertEquals(errors.size(), 2);
    }
    @Test
    public void sampleNonValidNoNationalityAuthorDataTest() throws IOException {
        JsonSchema schema = initSchema("json/schema/simpleNestedSchema.json");

        JsonNode node = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource("json/validation/sampleNonValidNoNationalityAuthorData.json").toString());
        Set<ValidationMessage> errors = schema.validate(node);
        assertEquals(errors.size(), 1);
    }
    @Test
    public void sampleNonValidWrongDateFormatAuthorDataTest() throws IOException {
        JsonSchema schema = initSchema("json/schema/simpleNestedSchema.json");

        JsonNode node = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource("json/validation/sampleNonValidWrongDateFormatAuthorData.json").toString());
        Set<ValidationMessage> errors = schema.validate(node);
        assertEquals(errors.size(), 2);
    }
    @Test
    public void sampleNonValidWrongStructureAuthorDataTest() throws IOException {
        JsonSchema schema = initSchema("json/schema/simpleNestedSchema.json");

        JsonNode node = getJsonNodeFromUrl(Thread.currentThread().getContextClassLoader().getResource("json/validation/sampleNonValidWrongStructureAuthorData.json").toString());
        Set<ValidationMessage> errors = schema.validate(node);
        assertEquals(errors.size(), 2);
    }
}