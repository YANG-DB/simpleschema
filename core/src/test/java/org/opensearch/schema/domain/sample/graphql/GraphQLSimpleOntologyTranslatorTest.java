package org.opensearch.schema.domain.sample.graphql;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.graphql.GraphQLEngineFactory;
import org.opensearch.graphql.GraphQLToOntologyTransformer;
import org.opensearch.schema.index.schema.BaseTypeElement;
import org.opensearch.schema.index.schema.Entity;
import org.opensearch.schema.index.schema.IndexProvider;
import org.opensearch.schema.ontology.Accessor;
import org.opensearch.schema.ontology.DirectiveType;
import org.opensearch.schema.ontology.DirectiveType.Argument;
import org.opensearch.schema.ontology.Ontology;
import org.opensearch.schema.ontology.Property;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.opensearch.schema.ontology.DirectiveType.Argument.*;
import static org.opensearch.schema.ontology.PrimitiveType.Types.*;
import static org.opensearch.schema.ontology.Property.equal;


/**
 * This test is verifying that the (example) simple SDL is correctly transformed into ontology & index-provider components
 */
public class GraphQLSimpleOntologyTranslatorTest {
    public static Ontology ontology;
    public static Accessor ontologyAccessor;

    @AfterAll
    public static void tearDown() throws Exception {
        GraphQLEngineFactory.reset();
    }
    @BeforeAll
    /**
     * load sample graphQL SDL files, transform them into the ontology & index-provider components
     */
    public static void setUp() throws Exception {
        InputStream filterSchemaInput = new FileInputStream("../schema/filter.graphql");
        InputStream aggregationSchemaInput = new FileInputStream("../schema/aggregation.graphql");
        InputStream utilsSchemaInput = new FileInputStream("../schema/utils.graphql");

        InputStream simpleSchemaInput = new FileInputStream("../schema/sample/simple.graphql");
        GraphQLToOntologyTransformer transformer = new GraphQLToOntologyTransformer();

        ontology = transformer.transform("simple", utilsSchemaInput, filterSchemaInput, aggregationSchemaInput, simpleSchemaInput);
        Assertions.assertNotNull(ontology);
        ontologyAccessor = new Accessor(ontology);
    }

    /**
     * test creation of an index provider using the predicate conditions for top level entity will be created an index
     */
    @Test
    public void testIndexProviderBuilder() {
        IndexProvider provider = IndexProvider.Builder.generate(ontology);
        List<String> names = provider.getEntities().stream().map(Entity::getType).map(BaseTypeElement.Type::getName)
                .collect(Collectors.toList());

        Assertions.assertTrue(names.contains("Author"));
        Assertions.assertTrue(names.contains("Book"));
    }

    /**
     * test properties are correctly translated (sample properties are selected for comparison)
     */
//    @Ignore("Fix overriding properties by different schema types with similar names")
    @Test
    public void testSimplePropertiesTranslation() {
        Assertions.assertTrue(equal(ontologyAccessor.property$("ISBN"),
                new Property.MandatoryProperty(new Property("ISBN", "ISBN", ID.asType()))));
        Assertions.assertTrue(equal(ontologyAccessor.property$("title"),
                new Property.MandatoryProperty(new Property("title", "title", STRING.asType()))));
        Assertions.assertTrue(equal(ontologyAccessor.property$("name"),
                new Property.MandatoryProperty(new Property("name", "name", STRING.asType()))));
        Assertions.assertTrue(equal(ontologyAccessor.property$("published"),
                new Property.MandatoryProperty(new Property("published", "published", DATETIME.asType()))));
        Assertions.assertTrue(equal(ontologyAccessor.property$("born"),
                new Property.MandatoryProperty(new Property("born", "born", DATETIME.asType()))));
        Assertions.assertTrue(equal(ontologyAccessor.property$("died"),
                new Property(new Property("died", "died", DATETIME.asType()))));
        Assertions.assertTrue(equal(ontologyAccessor.property$("nationality"),
                new Property("nationality", "nationality", STRING.asType())));
    }


    /**
     * test the schema is correctly translated into ontology structure
     */
    @Test
    public void testBookEntityTranslation() {
        Assertions.assertTrue(ontologyAccessor.entity("Book").isPresent());
        Assertions.assertEquals(ontologyAccessor.entity$("Book").geteType(), "Book");
        Assertions.assertTrue(ontologyAccessor.entity$("Book").getProperties().contains("ISBN"));
        Assertions.assertTrue(ontologyAccessor.entity$("Book").getProperties().contains("title"));
        Assertions.assertTrue(ontologyAccessor.entity$("Book").getProperties().contains("description"));
        Assertions.assertTrue(ontologyAccessor.entity$("Book").getProperties().contains("author"));
        Assertions.assertTrue(ontologyAccessor.entity$("Book").getProperties().contains("published"));
    }

    @Test
    public void testAuthorTranslation() {
        Assertions.assertTrue(ontologyAccessor.entity("Author").isPresent());
        Assertions.assertFalse(ontologyAccessor.entity$("Author").getDirectives().isEmpty());
        Assertions.assertEquals(new DirectiveType("model", DirectiveType.DirectiveClasses.DATATYPE), ontologyAccessor.entity$("Author").getDirectives().get(0));

        Assertions.assertEquals(ontologyAccessor.entity$("Author").geteType(), "Author");
        Assertions.assertTrue(ontologyAccessor.entity$("Author").getProperties().contains("name"));
        Assertions.assertTrue(ontologyAccessor.entity$("Author").getProperties().contains("nationality"));
        Assertions.assertTrue(ontologyAccessor.entity$("Author").getProperties().contains("age"));
        Assertions.assertTrue(ontologyAccessor.entity$("Author").getProperties().contains("born"));
        Assertions.assertTrue(ontologyAccessor.entity$("Author").getProperties().contains("died"));
        Assertions.assertTrue(ontologyAccessor.entity$("Author").getProperties().contains("books"));
    }

    @Test
    public void testAuthorToBooksRelationTranslation() {
        Assertions.assertTrue(ontologyAccessor.$relation("has_Book").isPresent());
        Assertions.assertFalse(ontologyAccessor.relation$("has_Book").getDirectives().isEmpty());
        Assertions.assertEquals(new DirectiveType("relation", DirectiveType.DirectiveClasses.DATATYPE, Collections.singletonList(of("mappingType","embedded"))),
                ontologyAccessor.relation$("has_Book").getDirectives().get(0));
    }

    @Test
    public void testBooksToAuthorRelationTranslation() {
        Assertions.assertTrue(ontologyAccessor.$relation("has_Author").isPresent());
        Assertions.assertTrue(ontologyAccessor.relation$("has_Author").getDirectives().isEmpty());
    }


}
