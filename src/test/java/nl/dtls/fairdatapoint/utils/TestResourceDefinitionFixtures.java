package nl.dtls.fairdatapoint.utils;

import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import org.springframework.stereotype.Service;

@Service
public class TestResourceDefinitionFixtures extends ResourceDefinitionFixtures {

    public ResourceDefinition catalogDefinition() {
        return super.catalogDefinition(repositoryDefinition());
    }

    public ResourceDefinition datasetDefinition() {
        return super.datasetDefinition(catalogDefinition());
    }

    public ResourceDefinition distributionDefinition() {
        return super.distributionDefinition(datasetDefinition());
    }

}
