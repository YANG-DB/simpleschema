package org.opensearch.schema.domain.sample.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.NoOpClient;
import org.opensearch.common.bytes.BytesReference;
import org.opensearch.common.xcontent.ToXContent;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.graphql.GraphQLEngineFactory;
import org.opensearch.schema.domain.sample.graphql.GraphQLSimpleEmbeddedOntologyTranslatorTest;
import org.opensearch.schema.domain.sample.graphql.GraphQLSimpleForeignOntologyTranslatorTest;
import org.opensearch.schema.index.schema.IndexProvider;
import org.opensearch.schema.index.template.PutIndexTemplateRequestBuilder;
import org.opensearch.schema.index.transform.IndexEntitiesMappingBuilder;
import org.opensearch.schema.index.transform.IndexRelationsMappingBuilder;
import org.opensearch.schema.ontology.Accessor;
import org.opensearch.schema.ontology.DirectiveEnumTypes;
import org.opensearch.schema.ontology.Ontology;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This test is verifying that the process of generating a mapping template from the index provider using the ontology is working as expected
 */
public class MappingSimpleForeignIndexProviderTest {
    static Ontology ontology;
    static IndexProvider indexProvider;

    @AfterAll
    public static void tearDown() throws Exception {
        GraphQLEngineFactory.reset();
    }

    @BeforeAll
    /**
     * load process (including all it's dependencies) graphQL SDL files, transform them into the ontology & index-provider components
     */
    public static void setUp() throws Exception {
        GraphQLSimpleForeignOntologyTranslatorTest.setUp();
        ontology = GraphQLSimpleForeignOntologyTranslatorTest.ontology;
        indexProvider = IndexProvider.Builder.generate(ontology
                , e -> e.getDirectives().stream()
                        .anyMatch(d -> DirectiveEnumTypes.MODEL.isSame(d.getName()))
                , r -> true);
    }

    @Test
    /**
     * verify the process entity index-provider contains basic structure and properties
     */
    public void generateAuthorEntityNestedBooksMappingTest() {
        IndexEntitiesMappingBuilder builder = new IndexEntitiesMappingBuilder(indexProvider);
        HashMap<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        builder.map(new Accessor(ontology), new NoOpClient("test"), requests);

        Assert.assertNotNull(requests.get("author"));
        Assert.assertEquals(1, requests.get("author").getMappings().size());
        Assert.assertNotNull(requests.get("author").getMappings().get("Author"));

        Map author = (Map) requests.get("author").getMappings().get("Author");
        Assert.assertNotNull(author.get("properties"));

        Map authorPropertiesMap = (Map) author.get("properties");
        Assert.assertTrue(authorPropertiesMap.containsKey("books"));
    }

    @Test
    /**
     * verify the process entity index-provider contains basic structure and properties
     */
    public void generateAuthorEntityMappingTest() throws IOException {
        IndexEntitiesMappingBuilder builder = new IndexEntitiesMappingBuilder(indexProvider);
        HashMap<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        builder.map(new Accessor(ontology), new NoOpClient("test"), requests);

        Assert.assertNotNull(requests.get("author"));
        Assert.assertEquals(1, requests.get("author").getMappings().size());
        Assert.assertNotNull(requests.get("author").getMappings().get("Author"));

        Map author = (Map) requests.get("author").getMappings().get("Author");
        Assert.assertNotNull(author.get("properties"));

        Map authorPropertiesMap = (Map) author.get("properties");
        //books are a foreign index - only store FK reference (nested object with ISBN field in this case)
        Assert.assertTrue(authorPropertiesMap.containsKey("books"));
        Assert.assertTrue(authorPropertiesMap.get("books") instanceof Map);
        Assert.assertTrue(((Map)authorPropertiesMap.get("books")).containsKey("properties"));
        Assert.assertTrue(((Map)authorPropertiesMap.get("books")).get("properties") instanceof Map);
        Assert.assertTrue(((Map)((Map)authorPropertiesMap.get("books")).get("properties")).containsKey("ISBN"));

        Assert.assertTrue(authorPropertiesMap.containsKey("nationality"));
        Assert.assertTrue(authorPropertiesMap.containsKey("name"));
        Assert.assertTrue(authorPropertiesMap.containsKey("born"));
        Assert.assertTrue(authorPropertiesMap.containsKey("died"));
        Assert.assertTrue(authorPropertiesMap.containsKey("age"));


        //test template generation structure
        XContentBuilder xContent = requests.get("author").request().toXContent(XContentBuilder.builder(XContentType.JSON.xContent()), ToXContent.EMPTY_PARAMS);
        xContent.prettyPrint();
        xContent.flush();
        Assert.assertNotNull(BytesReference.bytes(xContent).utf8ToString());
    }

    @Test
    /**
     * verify the process entity index-provider contains basic structure and properties
     */
    public void generateBookEntityMappingTest() throws IOException {
        IndexEntitiesMappingBuilder builder = new IndexEntitiesMappingBuilder(indexProvider);
        HashMap<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        builder.map(new Accessor(ontology), new NoOpClient("test"), requests);

        Assert.assertNotNull(requests.get("book"));
        Assert.assertEquals(1, requests.get("book").getMappings().size());
        Assert.assertNotNull(requests.get("book").getMappings().get("Book"));

        Map book = (Map) requests.get("book").getMappings().get("Book");
        Assert.assertNotNull(book.get("properties"));

        Map bookPropertiesMap = (Map) book.get("properties");
        //books are a foreign index - only store FK reference (nested object with ISBN field in this case)
        Assert.assertTrue(bookPropertiesMap.containsKey("author"));
        Assert.assertTrue(bookPropertiesMap.get("author") instanceof Map);
        Assert.assertTrue(((Map)bookPropertiesMap.get("author")).containsKey("properties"));
        Assert.assertTrue(((Map)bookPropertiesMap.get("author")).get("properties") instanceof Map);
        Assert.assertTrue(((Map)((Map)bookPropertiesMap.get("author")).get("properties")).containsKey("id"));

        Assert.assertTrue(bookPropertiesMap.containsKey("ISBN"));
        Assert.assertTrue(bookPropertiesMap.containsKey("title"));
        Assert.assertTrue(bookPropertiesMap.containsKey("published"));
        Assert.assertTrue(bookPropertiesMap.containsKey("genre"));
        Assert.assertTrue(bookPropertiesMap.containsKey("description"));


        //test template generation structure
        XContentBuilder xContent = requests.get("book").request().toXContent(XContentBuilder.builder(XContentType.JSON.xContent()), ToXContent.EMPTY_PARAMS);
        xContent.prettyPrint();
        xContent.flush();
        Assert.assertNotNull(BytesReference.bytes(xContent).utf8ToString());
    }


    @Test
    /**
     * verify the process relations index-provider contains basic structure and properties
     */
    public void generateAuthorBooksRelationsMappingTest() {
        IndexRelationsMappingBuilder builder = new IndexRelationsMappingBuilder(indexProvider);
        HashMap<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        builder.map(new Accessor(ontology), new NoOpClient("test"), requests);

        // since the reference is maintained in the entity nested structure (Author has book's FK's) there is no physical relationship index of its own
        Assert.assertTrue(requests.isEmpty());
    }

}
