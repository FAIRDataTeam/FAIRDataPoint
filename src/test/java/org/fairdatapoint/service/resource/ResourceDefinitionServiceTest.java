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
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChildDTO;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChildListViewDTO;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChildListViewMetadataDTO;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.fairdatapoint.entity.resource.ResourceDefinitionChildMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestEntityManager
@Transactional  // required for TestEntityManager outside of @DataJpaTest
public class ResourceDefinitionServiceTest extends BaseIntegrationTest {

    final HashMap<String, UUID> uuids = new HashMap<>();

    final ResourceDefinitionService resourceDefinitionService;

    final TestEntityManager testEntityManager;

    /**
     * Constructor
     *
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

    /**
     * Creates ResourceDefinitionChangeDTO from existing ResourceDefinition
     *
     * @param uuid ResourceDefinition identifier
     * @return Change DTO based on the ResourceDefinition
     */
    private ResourceDefinitionChangeDTO createResourceDefinitionChangeDTO(UUID uuid) {
        ResourceDefinition resourceDefinition = testEntityManager.find(ResourceDefinition.class, uuid);
        System.out.println(resourceDefinition.getChildren().size());  // TEMP
        final ResourceDefinitionChangeDTO dto = new ResourceDefinitionChangeDTO();
        dto.setName(resourceDefinition.getName());
        dto.setUrlPrefix(resourceDefinition.getUrlPrefix());
        dto.setMetadataSchemaUuids(resourceDefinition.getMetadataSchemaUsages()
                .stream()
                .map(usage -> usage.getUsedMetadataSchema().getUuid())
                .toList()
        );
        dto.setChildren(resourceDefinition.getChildren()
                .stream()
                .map(child -> new ResourceDefinitionChildDTO(
                        child.getTarget().getUuid(),
                        child.getRelationUri(),
                        new ResourceDefinitionChildListViewDTO(
                                child.getTitle(),
                                child.getTagsUri(),
                                child.getMetadata()
                                        .stream()
                                        .map(
                                                metadata -> new ResourceDefinitionChildListViewMetadataDTO(
                                                        metadata.getTitle(), metadata.getPropertyUri()
                                                )
                                        ).toList()
                        )
                ))
                .toList()
        );
        dto.setExternalLinks(List.of());  // TEMP
// TODO:
//        dto.setExternalLinks(resourceDefinition.getExternalLinks()
//                .stream()
//                .map()
//                .toList()
//        );
        return dto;
    }

    @BeforeEach
    public void setUp() {
        // create child resource
        ResourceDefinition child = new ResourceDefinition();
        child.setName("child resource");
        child.setUrlPrefix("child");
        child.setChildren(List.of());
        child.setParents(List.of());
        child.setExternalLinks(List.of());
        child.setMetadataSchemaUsages(List.of());
        child = testEntityManager.persist(child);
        uuids.put("child", child.getUuid());
        // create parent resource
        ResourceDefinition parent = new ResourceDefinition();
        parent.setName("parent resource");
        parent.setUrlPrefix("parent");
        parent.setExternalLinks(List.of());
        parent.setMetadataSchemaUsages(List.of());
        parent = testEntityManager.persist(parent);
        uuids.put("parent", parent.getUuid());
        // create parent-child relation
        ResourceDefinitionChild relation = new ResourceDefinitionChild();
        relation.setRelationUri("http://example.org/relation");
        relation.setTitle("relation");
        relation.setOrderPriority(1);
        relation.setSource(parent);
        relation.setTarget(child);
        relation = testEntityManager.persist(relation);
        uuids.put("relation", relation.getUuid());
        // manage both sides of bidirectional relations
        parent.setChildren(List.of(relation));
        child.setParents(List.of(relation));
        // create relation metadata
        ResourceDefinitionChildMetadata relationMetadata = new ResourceDefinitionChildMetadata();
        relationMetadata.setTitle("relation metadata");
        relationMetadata.setPropertyUri("http://example.org/property");
        relationMetadata.setOrderPriority(1);
        relationMetadata.setChild(relation);
        relationMetadata = testEntityManager.persist(relationMetadata);
        relation.setMetadata(List.of(relationMetadata));
        // flushed by transaction
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testUpdate() throws BindException {
        final UUID parentUuid = uuids.get("parent");
        ResourceDefinitionChangeDTO changeDTO = createResourceDefinitionChangeDTO(parentUuid);
        System.out.println(changeDTO.getName());
        resourceDefinitionService.update(parentUuid, changeDTO);
        ResourceDefinition updatedResourceDefinition = testEntityManager.find(ResourceDefinition.class, parentUuid);
        assertEquals(1,  updatedResourceDefinition.getChildren().size());
    }
}
