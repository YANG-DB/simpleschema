package org.opensearch.graphql.json;


import com.networknt.schema.JsonSchema;
import org.opensearch.graphql.GraphQLSchemaUtils;
import org.opensearch.graphql.json.translation.EntitiesCreationTranslation;
import org.opensearch.graphql.json.translation.InterfaceTypeTranslation;
import org.opensearch.graphql.json.translation.ObjectTypeTranslation;
import org.opensearch.schema.OntologyTransformerIfc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * This component is the transformation element which takes a GQL SDL files and translates them into a Json Schema
 */
public class GraphQLToJsonSchemaTransformer implements OntologyTransformerIfc<String, JsonSchema>, GraphQLSchemaUtils {

    private List<TranslationStrategy> chain;

    public GraphQLToJsonSchemaTransformer() {
        chain = List.of(
                new ObjectTypeTranslation(),
                new InterfaceTypeTranslation(),
                new EntitiesCreationTranslation()
        );
    }

    /**
     * API that will transform a GraphQL schema into a json schema
     *
     * @param source
     * @return
     */
    public JsonSchema transform(String ontologyName, String source) throws RuntimeException {
        try {
            return transform(ontologyName, new FileInputStream(source));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * API that will transform a GraphQL schema into a json schema
     *
     * @param streams
     * @return
     */
    public JsonSchema transform(String ontologyName, InputStream... streams) {
         //todo implement
        return null;
    }

    @Override
    public String translate(JsonSchema source) {
        return null;
    }

}
