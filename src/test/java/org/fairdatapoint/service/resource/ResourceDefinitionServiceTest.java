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
