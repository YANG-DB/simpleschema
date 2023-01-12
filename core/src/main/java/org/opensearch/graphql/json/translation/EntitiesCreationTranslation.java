package org.opensearch.graphql.json.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.opensearch.graphql.json.TranslationStrategy;

import java.util.List;
import java.util.stream.Collectors;

import static org.opensearch.graphql.GraphQLSchemaUtils.getDirective;

/**
 * translate the GQL entities into json schema entities
 */
public class EntitiesCreationTranslation implements TranslationStrategy {

    public void translate(GraphQLSchema graphQLSchema, TranslationContext context) {
        List<JsonNode> collect = graphQLSchema.getAllTypesAsList().stream()
                .filter(p -> GraphQLObjectType.class.isAssignableFrom(p.getClass()))
                .filter(p -> getDirective((GraphQLObjectType) p, "autoGen").isEmpty())
                .filter(p -> !context.getLanguageTypes().contains(p.getName()))
                .filter(p -> !p.getName().startsWith("__"))
                .map(ifc -> createEntity((GraphQLObjectType) ifc, context))
                .collect(Collectors.toList());
        //todo add new object to root
//        context.getBuilder().addEntityTypes(collect);
    }

    /**
     * generate entity (interface) type
     *
     * @return
     */
    private JsonNode createEntity(GraphQLObjectType object, TranslationContext context) {
        //todo create entity object
        return new ObjectNode(null);
    }
}
