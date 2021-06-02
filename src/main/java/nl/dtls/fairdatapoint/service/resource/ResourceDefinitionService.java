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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class ResourceDefinitionService {

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

    public ResourceDefinitionDTO toDTO(ResourceDefinition rd) {
        return resourceDefinitionMapper.toDTO(rd, getTargetClassUris(rd));
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

    public ResourceDefinition getByUrlPrefix(String urlPrefix) {
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUrlPrefix(urlPrefix);
        if (oRd.isEmpty()) {
            throw new ResourceNotFoundException(
                    format("Resource with provided uri prefix ('%s') is not defined", urlPrefix)
            );
        }
        return oRd.get();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDefinitionDTO create(ResourceDefinitionChangeDTO reqDto) throws BindException {
        String uuid = UUID.randomUUID().toString();
        ResourceDefinition rd = resourceDefinitionMapper.fromChangeDTO(reqDto, uuid);

        // TODO: check if shapes exist

        resourceDefinitionValidator.validate(rd);
        resourceDefinitionRepository.save(rd);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();

        membershipService.addToMembership(rd);
        openApiService.updateGenericPaths(rd);
        return toDTO(rd);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<ResourceDefinitionDTO> update(String uuid, ResourceDefinitionChangeDTO reqDto) throws BindException {
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUuid(uuid);
        if (oRd.isEmpty()) {
            return Optional.empty();
        }
        ResourceDefinition rd = oRd.get();
        ResourceDefinition updatedRd = resourceDefinitionMapper.fromChangeDTO(reqDto, rd.getUuid());
        updatedRd.setId(rd.getId());

        // TODO: check if shapes exist

        resourceDefinitionValidator.validate(updatedRd);
        resourceDefinitionRepository.save(updatedRd);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
        openApiService.updateGenericPaths(updatedRd);
        return Optional.of(updatedRd).map(this::toDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteByUuid(String uuid) {
        // 1. Get resource definition
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUuid(uuid);
        if (oRd.isEmpty()) {
            return false;
        }
        ResourceDefinition rd = oRd.get();

        // 2. Delete from parent resource definition
        ResourceDefinition rdParent = resourceDefinitionCache.getParentByUuid(rd.getUuid());
        if (rdParent != null) {
            rdParent = resourceDefinitionRepository.findByUuid(rdParent.getUuid()).get();
            rdParent.setChildren(
                    rdParent.getChildren()
                            .stream()
                            .filter(x -> !x.getResourceDefinitionUuid().equals(rd.getUuid()))
                            .collect(Collectors.toList())
            );
            resourceDefinitionRepository.save(rdParent);
        }

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

    public List<String> getTargetClassUris(ResourceDefinition rd) {
        List<String> result = targetClassesCache.getByUuid(rd.getUuid());
        if (result == null) {
            targetClassesCache.computeCache();
            return targetClassesCache.getByUuid(rd.getUuid());
        }
        return result;
    }
}
