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

import org.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChildDTO;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChildListViewDTO;
import org.fairdatapoint.database.db.repository.ResourceDefinitionRepository;
import org.fairdatapoint.entity.exception.ValidationException;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ResourceDefinitionValidatorTest {

    @Mock
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Mock
    private ResourceDefinitionCache resourceDefinitionCache;

    @InjectMocks
    private ResourceDefinitionValidator resourceDefinitionValidator;

    private ResourceDefinition newResourceDefinition(UUID uuid, String name, String urlPrefix) {
        return ResourceDefinition.builder()
                .uuid(uuid)
                .name(name)
                .urlPrefix(urlPrefix)
                .children(new ArrayList<>())
                .parents(new ArrayList<>())
                .externalLinks(List.of())
                .metadataSchemaUsages(List.of())
                .build();
    }

    private ResourceDefinitionChild newChild(
            UUID uuid, ResourceDefinition source, ResourceDefinition target,
            String relationUri
    ) {
        return ResourceDefinitionChild.builder()
                .uuid(uuid)
                .source(source)
                .target(target)
                .relationUri(relationUri)
                .title("")
                .tagsUri("")
                .metadata(List.of())
                .orderPriority(1)
                .build();
    }

    @Test
    public void nameUniqueness() throws BindException {
        // GIVEN: Prepare reqDto
        ResourceDefinition rdCatalog = newResourceDefinition(KnownUUIDs.RD_CATALOG_UUID, "Catalog", "catalog");
        ResourceDefinitionChangeDTO reqDto =
                ResourceDefinitionChangeDTO.builder()
                        .name("Catalog")
                        .urlPrefix("catalog2")
                        .children(List.of())
                        .externalLinks(List.of())
                        .metadataSchemaUuids(List.of())
                        .build();

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.of(rdCatalog));

        // WHEN:
        resourceDefinitionValidator.validate(KnownUUIDs.RD_CATALOG_UUID, reqDto);

        // THEN:
        // Nothing to check
    }

    @Test
    public void nameUniquenessBreach() {
        // GIVEN: Prepare reqDto
        ResourceDefinition rdCatalog = newResourceDefinition(KnownUUIDs.RD_CATALOG_UUID, "Catalog", "catalog");
        ResourceDefinitionChangeDTO reqDto =
                ResourceDefinitionChangeDTO.builder()
                        .name("Catalog")
                        .urlPrefix("catalog2")
                        .children(List.of())
                        .externalLinks(List.of())
                        .metadataSchemaUuids(List.of())
                        .build();

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.of(rdCatalog));

        // WHEN:
        BindException exception = assertThrows(
                BindException.class,
                () -> resourceDefinitionValidator.validate(KnownUUIDs.NULL_UUID, reqDto)
        );

        // THEN:
        assertThat(exception.getBindingResult().getFieldError("name"), is(notNullValue()));
    }

    @Test
    public void urlPrefixUniqueness() throws BindException {
        // GIVEN: Prepare reqDto
        ResourceDefinition rdCatalog = newResourceDefinition(KnownUUIDs.RD_CATALOG_UUID, "Catalog", "catalog");
        ResourceDefinitionChangeDTO reqDto =
                ResourceDefinitionChangeDTO.builder()
                        .name("Catalog 2")
                        .urlPrefix("catalog")
                        .children(List.of())
                        .externalLinks(List.of())
                        .metadataSchemaUuids(List.of())
                        .build();

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.of(rdCatalog));

        // WHEN:
        resourceDefinitionValidator.validate(KnownUUIDs.RD_CATALOG_UUID, reqDto);

        // THEN:
        // Nothing to check
    }

    @Test
    public void urlPrefixUniquenessBreach() {
        // GIVEN: Prepare reqDto
        ResourceDefinition rdCatalog = newResourceDefinition(KnownUUIDs.RD_CATALOG_UUID, "Catalog", "catalog");
        ResourceDefinitionChangeDTO reqDto =
                ResourceDefinitionChangeDTO.builder()
                        .name("Catalog 2")
                        .urlPrefix("catalog")
                        .children(List.of())
                        .externalLinks(List.of())
                        .metadataSchemaUuids(List.of())
                        .build();

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.of(rdCatalog));

        // WHEN:
        BindException exception = assertThrows(
                BindException.class,
                () -> resourceDefinitionValidator.validate(KnownUUIDs.NULL_UUID, reqDto)
        );

        // THEN:
        assertThat(exception.getBindingResult().getFieldError("urlPrefix"), is(notNullValue()));
    }

    @Test
    public void nonExistingChild() {
        // GIVEN: Prepare reqDto
        ResourceDefinitionChangeDTO reqDto =
                ResourceDefinitionChangeDTO.builder()
                        .name("Catalog 2")
                        .urlPrefix("catalog2")
                        .children(List.of())
                        .externalLinks(List.of())
                        .metadataSchemaUuids(List.of())
                        .build();
        ResourceDefinitionChildDTO child = ResourceDefinitionChildDTO.builder()
                .resourceDefinitionUuid(KnownUUIDs.NULL_UUID)
                .listView(
                        ResourceDefinitionChildListViewDTO.builder()
                                .title("")
                                .metadata(List.of())
                                .tagsUri("")
                                .build()
                )
                .relationUri("http://www.w3.org/ns/dcat#record")
                .build();
        reqDto.setChildren(List.of(child));

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionCache.getByUuid(null))
                .thenReturn(null);

        // WHEN:
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> resourceDefinitionValidator.validate(KnownUUIDs.USER_NIKOLA_UUID, reqDto)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo("Child doesn't exist")));
    }

    @Test
    public void existingDependencyCycle() {
        // GIVEN: Prepare reqDto and resource definitions
        ResourceDefinition rdFdp = newResourceDefinition(KnownUUIDs.RD_FDP_UUID, "FDP", "");
        ResourceDefinition rdCatalog = newResourceDefinition(KnownUUIDs.RD_CATALOG_UUID, "Catalog", "catalog");
        ResourceDefinition rdDataset = newResourceDefinition(KnownUUIDs.RD_DATASET_UUID, "Dataset", "dataset");
        ResourceDefinition rdDistribution = newResourceDefinition(KnownUUIDs.RD_DISTRIBUTION_UUID, "Distribution", "distribution");
        ResourceDefinitionChild childFdpCatalog = newChild(KnownUUIDs.RD_CHILD_FDP_CATALOG_UUID, rdFdp, rdCatalog, "https://www.w3.org/ns/dcat#catalog");
        ResourceDefinitionChild childCatalogDataset = newChild(KnownUUIDs.RD_CHILD_CATALOG_DATASET_UUID, rdCatalog, rdDataset, "https://www.w3.org/ns/dcat#record");
        ResourceDefinitionChild childDatasetDistribution = newChild(KnownUUIDs.RD_CHILD_DATASET_DISTRIBUTION_UUID, rdDataset, rdDistribution, "https://www.w3.org/ns/dcat#distribution");
        rdFdp.getChildren().add(childFdpCatalog);
        rdCatalog.getChildren().add(childCatalogDataset);
        rdDataset.getChildren().add(childDatasetDistribution);
        rdCatalog.getParents().add(childFdpCatalog);
        rdDataset.getParents().add(childCatalogDataset);
        rdDistribution.getParents().add(childDatasetDistribution);

        // DTOs to validate
        ResourceDefinitionChangeDTO reqDto =
                ResourceDefinitionChangeDTO.builder()
                        .name("Dataset")
                        .urlPrefix("dataset")
                        .children(List.of())
                        .externalLinks(List.of())
                        .metadataSchemaUuids(List.of())
                        .build();
        ResourceDefinitionChildDTO validChildDTO = ResourceDefinitionChildDTO.builder()
                .resourceDefinitionUuid(rdDistribution.getUuid())
                .listView(
                        ResourceDefinitionChildListViewDTO.builder()
                                .title("")
                                .metadata(List.of())
                                .tagsUri("")
                                .build()
                )
                .relationUri("http://www.w3.org/ns/dcat#distribution")
                .build();
        ResourceDefinitionChildDTO invalidChildDTO = ResourceDefinitionChildDTO.builder()
                .resourceDefinitionUuid(rdFdp.getUuid())
                .listView(
                        ResourceDefinitionChildListViewDTO.builder()
                                .title("")
                                .metadata(List.of())
                                .tagsUri("")
                                .build()
                )
                .relationUri("http://www.w3.org/ns/dcat#record")
                .build();
        // add valid child without children before invalid child, to reproduce #831
        reqDto.setChildren(List.of(validChildDTO, invalidChildDTO));

        // AND: Mock database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionCache.getByUuid(rdFdp.getUuid()))
                .thenReturn(rdFdp);
        when(resourceDefinitionCache.getByUuid(rdCatalog.getUuid()))
                .thenReturn(rdCatalog);
        when(resourceDefinitionCache.getByUuid(rdDataset.getUuid()))
                .thenReturn(rdDataset);
        when(resourceDefinitionCache.getByUuid(rdDistribution.getUuid()))
                .thenReturn(rdDistribution);

        // WHEN:
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> resourceDefinitionValidator.validate(KnownUUIDs.RD_DATASET_UUID, reqDto)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo("Detect dependency cycle through child")));
    }

}
