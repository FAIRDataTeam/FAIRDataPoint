/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatapoint.service.resource;

import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestEntityManager
@Transactional  // required for TestEntityManager outside of @DataJpaTest
public class ResourceDefinitionServiceTest extends BaseIntegrationTest {

    final HashMap<String, UUID> uuids = new HashMap<>();

    final ResourceDefinitionService resourceDefinitionService;

    final ResourceDefinitionMapper resourceDefinitionMapper;

    final TestEntityManager testEntityManager;

    /**
     * Constructor
     *
     * @param resourceDefinitionService The service to test
     */
    @Autowired
    public ResourceDefinitionServiceTest(
            ResourceDefinitionService resourceDefinitionService,
            ResourceDefinitionMapper resourceDefinitionMapper,
            TestEntityManager testEntityManager
    ) {
        this.resourceDefinitionService = resourceDefinitionService;
        this.resourceDefinitionMapper = resourceDefinitionMapper;
        this.testEntityManager = testEntityManager;
    }

    /**
     * Provides arguments for @ParameterizedTest
     * @return UUID of resource
     */
    static Stream<UUID> uuidProvider() {
        return Stream.of(
                KnownUUIDs.RD_FDP_UUID,
                KnownUUIDs.RD_CATALOG_UUID,
                KnownUUIDs.RD_DATASET_UUID,
                KnownUUIDs.RD_DISTRIBUTION_UUID
        );
    }

    /**
     * Reproduces #830, under the assumption that at least one of the default resources has children
     * (in this case fdp, catalog, and dataset) and at least one of the resources has external links (distribution).
     */
    @ParameterizedTest
    @MethodSource("uuidProvider")
    @WithMockUser(roles="ADMIN")
    public void testUpdateDoesNotDuplicateRelatedItems(UUID uuid) throws BindException {
        // create DTO from existing resource (without making any actual changes)
        ResourceDefinition resourceDefinition = testEntityManager.find(ResourceDefinition.class, uuid);
        ResourceDefinitionChangeDTO changeDTO = resourceDefinitionMapper.toChangeDTO(resourceDefinition);
        // count related items, for reference
        final int expectedChildCount = resourceDefinition.getChildren().size();
        final int expectedParentCount = resourceDefinition.getParents().size();
        final int expectedExternalLinkCount =  resourceDefinition.getExternalLinks().size();
        final int expectedMetadataSchemaUsageCount = resourceDefinition.getMetadataSchemaUsages().size();
        assertTrue(expectedMetadataSchemaUsageCount > 0);
        // call update method
        resourceDefinitionService.update(uuid, changeDTO);
        // check for duplicates (or other inconsistencies in the number of related items)
        testEntityManager.refresh(resourceDefinition);
        assertEquals(expectedChildCount, resourceDefinition.getChildren().size());
        assertEquals(expectedParentCount, resourceDefinition.getParents().size());
        assertEquals(expectedExternalLinkCount, resourceDefinition.getExternalLinks().size());
        assertEquals(expectedMetadataSchemaUsageCount, resourceDefinition.getMetadataSchemaUsages().size());
    }
}
