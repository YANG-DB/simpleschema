package org.opensearch.schema.domain.sample.mapping;

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
import org.opensearch.schema.domain.sample.graphql.GraphQLSimpleForeignJoinIndexOntologyTranslatorTest;
import org.opensearch.schema.domain.sample.graphql.GraphQLSimpleForeignOntologyTranslatorTest;
import org.opensearch.schema.index.schema.IndexProvider;
import org.opensearch.schema.index.template.PutIndexTemplateRequestBuilder;
import org.opensearch.schema.index.transform.IndexEntitiesMappingBuilder;
import org.opensearch.schema.index.transform.IndexRelationsMappingBuilder;
import org.opensearch.schema.ontology.Accessor;
import org.opensearch.schema.ontology.DirectiveEnumTypes;
import org.opensearch.schema.ontology.Ontology;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This test is verifying that the process of generating a mapping template from the index provider using the ontology is working as expected
 */
public class MappingSimpleForeignJoinIndexProviderTest {
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
        GraphQLSimpleForeignJoinIndexOntologyTranslatorTest.setUp();
        ontology = GraphQLSimpleForeignJoinIndexOntologyTranslatorTest.ontology;
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
        //books is defined as foreign relation to author therefor it will not consider as a property
        // it will have a dedicated relation index named has_books
        Assert.assertFalse(authorPropertiesMap.containsKey("books"));
    }

    @Test
    /**
     * verify the process entity index-provider contains basic structure and properties
     */
    public void generateAuthorEntityMappingTest() throws IOException, JSONException {
        IndexEntitiesMappingBuilder builder = new IndexEntitiesMappingBuilder(indexProvider);
        HashMap<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        builder.map(new Accessor(ontology), new NoOpClient("test"), requests);

        Assert.assertNotNull(requests.get("author"));
        Assert.assertEquals(1, requests.get("author").getMappings().size());
        Assert.assertNotNull(requests.get("author").getMappings().get("Author"));

        Map author = (Map) requests.get("author").getMappings().get("Author");
        Assert.assertNotNull(author.get("properties"));

        Map authorPropertiesMap = (Map) author.get("properties");
        //books are a foreign index
        Assert.assertFalse(authorPropertiesMap.containsKey("books"));
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
     * verify the process relations index-provider contains basic structure and properties
     */
    public void generateAuthorBooksRelationsMappingTest() {
        IndexRelationsMappingBuilder builder = new IndexRelationsMappingBuilder(indexProvider);
        HashMap<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        builder.map(new Accessor(ontology), new NoOpClient("test"), requests);

        Assert.assertNotNull(requests.get("has_author"));
        Assert.assertEquals(1, requests.get("has_author").getMappings().size());
        Assert.assertTrue(requests.get("has_author").getMappings().containsKey("has_Author"));
        Assert.assertTrue(requests.get("has_author").getMappings().get("has_Author") instanceof Map);
        Assert.assertTrue(((Map)requests.get("has_author").getMappings().get("has_Author")).containsKey("properties"));
        Assert.assertTrue(((Map)requests.get("has_author").getMappings().get("has_Author")).get("properties") instanceof Map);
        Assert.assertTrue(((Map)((Map)requests.get("has_author").getMappings().get("has_Author")).get("properties")).containsKey("entityA"));
        Assert.assertTrue(((Map)((Map)requests.get("has_author").getMappings().get("has_Author")).get("properties")).containsKey("entityB"));
        Assert.assertTrue(((Map)((Map)requests.get("has_author").getMappings().get("has_Author")).get("properties")).containsKey("direction"));


        Assert.assertNotNull(requests.get("has_book"));
        Assert.assertEquals(1, requests.get("has_book").getMappings().size());
        Assert.assertTrue(requests.get("has_book").getMappings().containsKey("has_Book"));
        Assert.assertTrue(requests.get("has_book").getMappings().get("has_Book") instanceof Map);
        Assert.assertTrue(((Map)requests.get("has_book").getMappings().get("has_Book")).containsKey("properties"));
        Assert.assertTrue(((Map)requests.get("has_book").getMappings().get("has_Book")).get("properties") instanceof Map);
        Assert.assertTrue(((Map)((Map)requests.get("has_book").getMappings().get("has_Book")).get("properties")).containsKey("entityA"));
        Assert.assertTrue(((Map)((Map)requests.get("has_book").getMappings().get("has_Book")).get("properties")).containsKey("entityB"));
        Assert.assertTrue(((Map)((Map)requests.get("has_book").getMappings().get("has_Book")).get("properties")).containsKey("direction"));

        //todo - this is how it should be - next fix => we expect here the relationship table be symmetric for both author->book & book->author
 /*
        Assert.assertNotNull(requests.get("written"));
        Assert.assertEquals(1, requests.get("written").getMappings().size());
        Assert.assertTrue(requests.get("written").getMappings().containsKey("written"));
        Assert.assertTrue(requests.get("written").getMappings().get("written") instanceof Map);
        Assert.assertTrue(((Map)requests.get("written").getMappings().get("written")).containsKey("properties"));
        Assert.assertTrue(((Map)requests.get("written").getMappings().get("written")).get("properties") instanceof Map);
        Assert.assertTrue(((Map)((Map)requests.get("written").getMappings().get("written")).get("properties")).containsKey("entityA"));
        Assert.assertTrue(((Map)((Map)requests.get("written").getMappings().get("written")).get("properties")).containsKey("entityB"));
        Assert.assertTrue(((Map)((Map)requests.get("written").getMappings().get("written")).get("properties")).containsKey("direction"));
*/
    }

}
