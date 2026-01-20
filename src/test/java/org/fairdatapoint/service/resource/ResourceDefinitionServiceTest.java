package org.fairdatapoint.service.resource;

import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@AutoConfigureTestEntityManager
@Transactional  // required for TestEntityManager outside of @DataJpaTest
public class ResourceDefinitionServiceTest extends BaseIntegrationTest {

    UUID uuid;

    final ResourceDefinitionService resourceDefinitionService;

    final TestEntityManager testEntityManager;

    /**
     * Constructor
     * @param resourceDefinitionService The service to test
     */
    @Autowired
    public ResourceDefinitionServiceTest(
            ResourceDefinitionService resourceDefinitionService,
            TestEntityManager testEntityManager
    ) {
        this.resourceDefinitionService = resourceDefinitionService;
        this.testEntityManager = testEntityManager;
    }

    @BeforeEach
    public void setUp() {
        ResourceDefinition resourceDefinition = new ResourceDefinition();
        resourceDefinition.setName("test");
        resourceDefinition.setUrlPrefix("test");
        resourceDefinition.setChildren(List.of());
        resourceDefinition.setParents(List.of());
        resourceDefinition.setExternalLinks(List.of());
        resourceDefinition.setMetadataSchemaUsages(List.of());
        resourceDefinition = testEntityManager.persist(resourceDefinition);
        uuid = resourceDefinition.getUuid();
    }

    @Test
    public void testUpdate() {
        ResourceDefinition resourceDefinition = testEntityManager.find(ResourceDefinition.class, uuid);
    }
}
