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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
     * Reproduces #830
     * @throws BindException
     */
    @Test
    @WithMockUser(roles="ADMIN")
    public void testUpdateDoesNotDuplicateChildren() throws BindException {
        // create DTO from existing resource (without making any actual changes)
        final UUID uuid = KnownUUIDs.RD_CATALOG_UUID;
        ResourceDefinition resourceDefinition = testEntityManager.find(ResourceDefinition.class, uuid);
        ResourceDefinitionChangeDTO changeDTO = resourceDefinitionMapper.toChangeDTO(resourceDefinition);
        // count related items, for reference
        final int expectedChildCount = resourceDefinition.getChildren().size();
        final int expectedParentCount = resourceDefinition.getParents().size();
        final int expectedExternalLinkCount =  resourceDefinition.getExternalLinks().size();
        final int expectedMetadataSchemaUsageCount = resourceDefinition.getMetadataSchemaUsages().size();
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
