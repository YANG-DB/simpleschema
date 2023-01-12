package org.opensearch.graphql.ontology.translation;

import graphql.schema.*;
import org.opensearch.graphql.ontology.TranslationStrategy;

import java.util.HashSet;

/**
 * translate the GQL entity's & relation's properties into a flat list of ontology properties
 */
public class PropertiesTranslation implements TranslationStrategy {

    public void translate(GraphQLSchema graphQLSchema, TranslationContext context) {
        context.getBuilder().withProperties(new HashSet<>(context.getProperties()));
    }
}
