package org.opensearch.schema.index.transform;


import com.google.inject.Inject;
import javaslang.Tuple2;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.Client;
import org.opensearch.schema.SchemaError;
import org.opensearch.schema.index.schema.IndexProvider;
import org.opensearch.schema.index.template.PutIndexTemplateRequestBuilder;
import org.opensearch.schema.ontology.Accessor;
import org.opensearch.schema.ontology.Ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Index Mapping factory is responsible for dispatching mapping generation work
 * to the dedicated entity/relation mapping builders
 */
public class IndexMappingFactory implements OntologyIndexGenerator {

    private IndexProvider indexProvider;
    private Client client;
    private Ontology ontology;

    private IndexRelationsMappingBuilder relationsMappingBuilder;
    private IndexEntitiesMappingBuilder entitiesMappingBuilder;
    private IndexProjectionMappingBuilder projectionMappingBuilder;

    @Inject
    public IndexMappingFactory(Client client, Ontology ontology, IndexProvider indexProvider) {
        this.client = client;
        this.indexProvider = indexProvider;
        this.ontology = ontology;
    }

    private void initBuilders() {
        relationsMappingBuilder = new IndexRelationsMappingBuilder(indexProvider);
        entitiesMappingBuilder = new IndexEntitiesMappingBuilder(indexProvider);
        projectionMappingBuilder = new IndexProjectionMappingBuilder(relationsMappingBuilder, indexProvider);
    }

    /**
     * generate mapping according to ontology
     *
     * @return
     */
    private List<Tuple2<String, Boolean>> generateMappings() {
        initBuilders();

        List<Tuple2<String, AcknowledgedResponse>> responses = new ArrayList<>();
        try {
            //build all template requests
            Map<String, PutIndexTemplateRequestBuilder> requests = buildRequests();
            //execute all template request
            responses.addAll(requests.values().stream()
                    .map(r -> new Tuple2<>(r.request().name(), r.execute().actionGet()))
                    .collect(Collectors.toList()));
            return responses.stream().map(r -> new Tuple2<>(r._1, r._2.isAcknowledged()))
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            throw new SchemaError.SchemaErrorException("Error Generating Mapping for O/S ", t);
        }
    }

    public Map<String, PutIndexTemplateRequestBuilder> buildRequests() {
        //generate the index template requests
        Map<String, PutIndexTemplateRequestBuilder> requests = new HashMap<>();
        Accessor ontology = new Accessor(this.ontology);
        //map the entities index
        entitiesMappingBuilder.map(ontology, client, requests);
        //map the relationships index
        relationsMappingBuilder.map(ontology, client, requests);
        //map the special projection index
        projectionMappingBuilder.map(projectionMappingBuilder.generateProjectionOntology(this.ontology), client, requests);
        return requests;
    }
}
