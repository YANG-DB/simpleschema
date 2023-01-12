package org.opensearch.graphql.json.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLSchema;
import org.opensearch.graphql.json.TranslationStrategy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * translate the GQL interfaces into a list of json abstract schema types
 */
public class InterfaceTypeTranslation implements TranslationStrategy {

    public void translate(GraphQLSchema graphQLSchema, TranslationContext context) {
        List<JsonNode> collect = graphQLSchema.getAllTypesAsList().stream()
                .filter(p -> GraphQLInterfaceType.class.isAssignableFrom(p.getClass()))
                .map(ifc -> createInterface(((GraphQLInterfaceType) ifc), context))
                .collect(Collectors.toList());
        //todo add new object to root
//        context.getBuilder().addEntityTypes(collect);
    }

    private ObjectNode createInterface(GraphQLInterfaceType ifc, TranslationContext context) {
        //todo implement
        return new ObjectNode(null);
    }
}
