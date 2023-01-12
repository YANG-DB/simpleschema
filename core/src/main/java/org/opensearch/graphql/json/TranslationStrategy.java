package org.opensearch.graphql.json;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import graphql.schema.GraphQLSchema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.opensearch.graphql.GraphQLSchemaUtils.QUERY;

/**
 * Contains:
 * <br>
 * An interface for the translation of a specific section in the schema
 * <br>
 * A context for graphQL to Json Schema translation session
 */
public interface TranslationStrategy {
    /**
     * translates the specific section of the GraphQLSchema into the same relevant section in the Json Schema
     * @param graphQLSchema
     * @param context
     */
    void translate(GraphQLSchema graphQLSchema, TranslationContext context);

    /**
     * A context for graphQL to Ontology translation session
     */
    class TranslationContext {
        private JsonSchemaFactory builder;
        private Set<String> objectTypes;

        private Set<String> languageTypes = new HashSet<>();

        public TranslationContext(String schemaName) {
            objectTypes = new HashSet<>();
            languageTypes.addAll(Arrays.asList(QUERY));
            builder = JsonSchemaFactory.getInstance();

        }

        public void addObjectTypes(List<String> types) {
            objectTypes.addAll(types);
        }

        public JsonSchemaFactory getBuilder() {
            return builder;
        }

        public Set<String> getObjectTypes() {
            return objectTypes;
        }

        public Set<String> getLanguageTypes() {
            return languageTypes;
        }

        public JsonSchemaFactory build() {
            return builder;
        }
    }
}
