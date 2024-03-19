/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.service.resource;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChildDTO;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionDTO;
import nl.dtls.fairdatapoint.database.db.repository.*;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.resource.MetadataSchemaUsage;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionLink;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.service.membership.MembershipService;
import nl.dtls.fairdatapoint.service.openapi.OpenApiService;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.*;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ResourceDefinitionService {

    @Qualifier("persistentUrl")
    private final String persistentUrl;

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private final ResourceDefinitionChildRepository childRepository;

    private final ResourceDefinitionChildMetadataRepository childMetadataRepository;

    private final ResourceDefinitionLinkRepository linkRepository;

    private final MetadataSchemaUsageRepository usageRepository;

    private final ResourceDefinitionValidator resourceDefinitionValidator;

    private final ResourceDefinitionMapper mapper;

    private final ResourceDefinitionCache resourceDefinitionCache;

    private final ResourceDefinitionTargetClassesCache targetClassesCache;

    private final MetadataSchemaService metadataSchemaService;

    private final MembershipService membershipService;

    private final OpenApiService openApiService;

    private final EntityManager entityManager;

    public ResourceDefinitionDTO toDTO(ResourceDefinition definition) {
        return mapper.toDTO(definition, getTargetClassUris(definition));
    }

    public List<ResourceDefinitionDTO> getAll() {
        return resourceDefinitionRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<ResourceDefinition> getByUuid(UUID uuid) {
        return resourceDefinitionRepository.findByUuid(uuid);
    }

    public ResourceDefinition getByUuidOrThrow(UUID uuid) {
        return resourceDefinitionRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(format("Resource Definition ('%s') not found", uuid)));
    }

    public Optional<ResourceDefinitionDTO> getDTOByUuid(UUID uuid) {
        return getByUuid(uuid).map(this::toDTO);
    }

    public ResourceDefinition getByUrl(String url) {
        final String[] parts = url.replace(persistentUrl, "").split("/");
        // Repository
        String parentPrefix = "";
        if (parts.length > 1 && parts[0].isEmpty()) {
            // Other prefix (first empty caused by leading /)
            parentPrefix = parts[1];
        }
        else if (parts.length > 0) {
            // Other prefix
            parentPrefix = parts[0];
        }
        return getByUrlPrefix(parentPrefix);
    }

    public ResourceDefinition getByUrlPrefix(String urlPrefix) {
        final Optional<ResourceDefinition> definition = resourceDefinitionRepository.findByUrlPrefix(urlPrefix);
        if (definition.isEmpty()) {
            throw new ResourceNotFoundException(
                    format("Resource with provided uri prefix ('%s') is not defined", urlPrefix)
            );
        }
        return definition.get();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDefinitionDTO create(ResourceDefinitionChangeDTO reqDto) throws BindException {
        resourceDefinitionValidator.validate(null, reqDto);

        final ResourceDefinition definition = resourceDefinitionRepository.saveAndFlush(mapper.fromChangeDTO(reqDto));
        entityManager.refresh(definition);
        createDependents(definition, reqDto);

        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();

        membershipService.addToMembership(definition);
        openApiService.updateGenericPaths(definition);
        return toDTO(definition);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<ResourceDefinitionDTO> update(UUID uuid, ResourceDefinitionChangeDTO reqDto)
            throws BindException {
        final Optional<ResourceDefinition> optionalDefinition = resourceDefinitionRepository.findByUuid(uuid);
        if (optionalDefinition.isEmpty()) {
            return Optional.empty();
        }
        resourceDefinitionValidator.validate(uuid, reqDto);
        final ResourceDefinition definition = optionalDefinition.get();
        deleteDependents(definition);

        final ResourceDefinition updatedDefinition = resourceDefinitionRepository.saveAndFlush(
                mapper.fromChangeDTO(reqDto, definition)
        );
        entityManager.refresh(updatedDefinition);
        createDependents(updatedDefinition, reqDto);

        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
        openApiService.updateGenericPaths(updatedDefinition);
        return Optional.of(updatedDefinition).map(this::toDTO);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteByUuid(UUID uuid) {
        // 1. Get resource definition
        final Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUuid(uuid);
        if (oRd.isEmpty()) {
            return false;
        }
        final ResourceDefinition rd = oRd.get();

        // 2. Delete from parent resource definitions
        rd.getParents().forEach(this::deleteChild);

        // 3. Delete resource definition (incl. children and links)
        deleteDependents(rd);
        resourceDefinitionRepository.delete(rd);

        // 4. Delete entity from membership
        membershipService.removeFromMembership(rd);
        entityManager.flush();

        // 5. Recompute cache
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();

        // 6. Delete from OpenAPI docs
        openApiService.removeGenericPaths(rd);
        return true;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    protected void createDependents(ResourceDefinition definition, ResourceDefinitionChangeDTO reqDto) {
        // Get metadata schemas
        final List<MetadataSchema> schemas = metadataSchemaService.getAll(reqDto.getMetadataSchemaUuids());
        if (reqDto.getMetadataSchemaUuids().size() > schemas.size()) {
            throw new ResourceNotFoundException(
                    format("Some of the metadata schemas do not exist: %s", reqDto.getMetadataSchemaUuids())
            );
        }
        // Create usages
        final List<MetadataSchemaUsage> usages = new ArrayList<>();
        for (int index = 0; index < schemas.size(); index++) {
            usages.add(mapper.toUsage(schemas.get(index), definition, index));
        }
        usageRepository.saveAllAndFlush(usages);

        // Create links
        final List<ResourceDefinitionLink> links = new ArrayList<>();
        for (int index = 0; index < reqDto.getExternalLinks().size(); index++) {
            links.add(mapper.toLink(reqDto.getExternalLinks().get(index), definition, index));
        }
        linkRepository.saveAllAndFlush(links);

        // Create children + metadata
        for (int index = 0; index < reqDto.getChildren().size(); index++) {
            final ResourceDefinitionChildDTO dto = reqDto.getChildren().get(index);
            final ResourceDefinition target = getByUuidOrThrow(dto.getResourceDefinitionUuid());
            final ResourceDefinitionChild child = childRepository.saveAndFlush(
                    mapper.toChild(dto, definition, target, index)
            );

            child.setMetadata(new ArrayList<>());
            for (int index2 = 0; index2 < dto.getListView().getMetadata().size(); index2++) {
                child.getMetadata()
                        .add(mapper.toChildMetadata(dto.getListView().getMetadata().get(index2), child, index2));
            }
            childMetadataRepository.saveAllAndFlush(child.getMetadata());
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    protected void deleteDependents(ResourceDefinition resourceDefinition) {
        resourceDefinition.getChildren().forEach(this::deleteChild);
        linkRepository.deleteAll(resourceDefinition.getExternalLinks());
        usageRepository.deleteAll(resourceDefinition.getMetadataSchemaUsages());
        entityManager.flush();
        entityManager.refresh(resourceDefinition);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    protected void deleteChild(ResourceDefinitionChild child) {
        childMetadataRepository.deleteAll(child.getMetadata());
        childRepository.delete(child);
    }

    public List<String> getTargetClassUris(ResourceDefinition resourceDefinition) {
        final Set<String> result = targetClassesCache
                .getByUuid(resourceDefinition.getUuid().toString());
        if (result == null) {
            targetClassesCache.computeCache();
            return targetClassesCache.getByUuid(resourceDefinition.getUuid().toString()).stream().toList();
        }
        return result.stream().toList();
    }
}
