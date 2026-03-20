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

import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.membership.MembershipService;
import nl.dtls.fairdatapoint.service.openapi.OpenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class ResourceDefinitionService {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionValidator resourceDefinitionValidator;

    @Autowired
    private ResourceDefinitionMapper resourceDefinitionMapper;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private ResourceDefinitionTargetClassesCache targetClassesCache;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private OpenApiService openApiService;

    public ResourceDefinitionDTO toDTO(ResourceDefinition definition) {
        return resourceDefinitionMapper.toDTO(definition, getTargetClassUris(definition));
    }

    public List<ResourceDefinitionDTO> getAll() {
        return resourceDefinitionRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<ResourceDefinition> getByUuid(String uuid) {
        return resourceDefinitionRepository.findByUuid(uuid);
    }

    public Optional<ResourceDefinitionDTO> getDTOByUuid(String uuid) {
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

    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDefinitionDTO create(ResourceDefinitionChangeDTO reqDto) throws BindException {
        final String uuid = UUID.randomUUID().toString();
        final ResourceDefinition definition = resourceDefinitionMapper.fromChangeDTO(reqDto, uuid);

        // TODO: check if schemas exist

        resourceDefinitionValidator.validate(definition);
        resourceDefinitionRepository.save(definition);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();

        membershipService.addToMembership(definition);
        openApiService.updateGenericPaths(definition);
        return toDTO(definition);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<ResourceDefinitionDTO> update(String uuid, ResourceDefinitionChangeDTO reqDto)
            throws BindException {
        final Optional<ResourceDefinition> optionalDefinition = resourceDefinitionRepository.findByUuid(uuid);
        if (optionalDefinition.isEmpty()) {
            return Optional.empty();
        }
        final ResourceDefinition definition = optionalDefinition.get();
        final ResourceDefinition updatedDefinition =
                resourceDefinitionMapper.fromChangeDTO(reqDto, definition.getUuid());
        updatedDefinition.setId(definition.getId());

        // TODO: check if schemas exist

        resourceDefinitionValidator.validate(updatedDefinition);
        resourceDefinitionRepository.save(updatedDefinition);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
        openApiService.updateGenericPaths(updatedDefinition);
        return Optional.of(updatedDefinition).map(this::toDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteByUuid(String uuid) {
        // 1. Get resource definition
        final Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUuid(uuid);
        if (oRd.isEmpty()) {
            return false;
        }
        final ResourceDefinition rd = oRd.get();

        // 2. Delete from parent resource definitions
        final Set<ResourceDefinition> rdParents = resourceDefinitionCache.getParentsByUuid(rd.getUuid());
        rdParents.forEach(definition -> {
            final ResourceDefinition rdParent = resourceDefinitionRepository.findByUuid(definition.getUuid()).get();
            rdParent.setChildren(
                    rdParent.getChildren()
                            .stream()
                            .filter(child -> !child.getResourceDefinitionUuid().equals(rd.getUuid()))
                            .collect(Collectors.toList())
            );
            resourceDefinitionRepository.save(rdParent);
        });

        // 3. Delete resource definition
        resourceDefinitionRepository.delete(rd);

        // 4. Delete entity from membership
        membershipService.removeFromMembership(rd);

        // 5. Recompute cache
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();

        // 6. Delete from OpenAPI docs
        openApiService.removeGenericPaths(rd);
        return true;
    }

    public List<String> getTargetClassUris(ResourceDefinition resourceDefinition) {
        final List<String> result = targetClassesCache.getByUuid(resourceDefinition.getUuid());
        if (result == null) {
            targetClassesCache.computeCache();
            return targetClassesCache.getByUuid(resourceDefinition.getUuid());
        }
        return result;
    }
}
