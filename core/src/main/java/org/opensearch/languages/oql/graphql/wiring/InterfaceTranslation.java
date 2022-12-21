package org.opensearch.languages.oql.graphql.wiring;

import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.opensearch.schema.SchemaError;
import org.opensearch.schema.ontology.EntityType;

import java.util.List;
import java.util.Optional;

import static org.opensearch.languages.oql.graphql.wiring.TranslationUtils.*;

/**
 * this translator is responsible of translating interfaces to the appropriate ontology concrete entities
 */
public class InterfaceTranslation implements QueryTranslationStrategy{
    @Override
    public Optional<Object> translate(QueryTranslatorContext context, GraphQLType fieldType)  {
        fieldType = extractConcreteFieldType(fieldType);
        if (fieldType instanceof GraphQLInterfaceType) {
            //select the first implementing of interface (no matter which one since all share same common fields)
            List<GraphQLObjectType> implementations = context.getSchema().getImplementations((GraphQLInterfaceType) fieldType);
            //populate vertex or relation
            Optional<EntityType> realType = populateGraphObject(context, ((GraphQLInterfaceType) fieldType).getName());
            try {
                addWhereClause(context, realType);
            } catch (Throwable e) {
                throw new SchemaError.SchemaErrorException("During GraphQL to Ontology QL translation, failed on InterfaceTranslation::addWhereClause",e);
            }
//            return fakeObjectValue(accessor, builder, implementations.get(0));
            return Optional.of(new Object());
        }
        return Optional.empty();
    }
}
