package org.opensearch.graphql.json.translation;

import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.opensearch.graphql.json.TranslationStrategy;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * translate the GQL entities into a names list of types
 */
public class ObjectTypeTranslation implements TranslationStrategy {

    public void translate(GraphQLSchema graphQLSchema, TranslationContext context) {
        context.addObjectTypes(Stream.concat(graphQLSchema.getAllTypesAsList().stream()
                                .filter(p -> GraphQLInterfaceType.class.isAssignableFrom(p.getClass()))
                                .map(GraphQLNamedSchemaElement::getName),
                        graphQLSchema.getAllTypesAsList().stream()
                                .filter(p -> GraphQLObjectType.class.isAssignableFrom(p.getClass()))
                                .map(GraphQLNamedSchemaElement::getName)
                )
                .filter(p -> !p.startsWith("__"))
                .filter(p -> !context.getLanguageTypes().contains(p)).collect(Collectors.toList()));
    }

}
