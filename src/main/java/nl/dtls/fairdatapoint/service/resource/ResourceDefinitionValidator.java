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

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChildDTO;
import nl.dtls.fairdatapoint.database.db.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nl.dtls.fairdatapoint.util.ValidationUtil.uniquenessValidationFailed;

@Service
@RequiredArgsConstructor
public class ResourceDefinitionValidator {

    private static final String ERR_DEP_CYCLE = "Detect dependency cycle through child";

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private final ResourceDefinitionCache resourceDefinitionCache;

    public void validate(UUID uuid, ResourceDefinitionChangeDTO reqDto) throws BindException {
        // Check uniqueness
        final Optional<ResourceDefinition> resourceDefinitionByName =
                resourceDefinitionRepository.findByName(reqDto.getName());
        if (resourceDefinitionByName.isPresent()
                && !resourceDefinitionByName.get().getUuid().equals(uuid)) {
            uniquenessValidationFailed("name", reqDto);
        }
        final Optional<ResourceDefinition> resourceDefinitionByPrefix =
                resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix());
        if (resourceDefinitionByPrefix.isPresent()
                && !resourceDefinitionByPrefix.get().getUuid().equals(uuid)) {
            uniquenessValidationFailed("urlPrefix", reqDto);
        }

        // Check urlPrefix validity
        if (!isValidPrefixUrl(reqDto.getUrlPrefix())) {
            throw new ValidationException("URL prefix is not valid");
        }

        // Check existence of connected entities
        for (ResourceDefinitionChildDTO child : reqDto.getChildren()) {
            if (resourceDefinitionCache.getByUuid(child.getResourceDefinitionUuid()) == null) {
                throw new ValidationException("Child doesn't exist");
            }
        }

        // Check existence of dependency cycles
        validateDependencyCyclesChildDTO(uuid, reqDto.getChildren());
    }

    private void validateDependencyCyclesChildDTO(
            UUID uuid, List<ResourceDefinitionChildDTO> children
    ) {
        for (ResourceDefinitionChildDTO child : children) {
            final UUID childUuid = child.getResourceDefinitionUuid();
            if (childUuid.equals(uuid)) {
                throw new ValidationException(ERR_DEP_CYCLE);
            }

            final ResourceDefinition rdChild = resourceDefinitionCache.getByUuid(childUuid);
            if (rdChild.getChildren().isEmpty()) {
                return;
            }
            validateDependencyCyclesChild(uuid, rdChild.getChildren());
        }
    }

    private void validateDependencyCyclesChild(
            UUID uuid, List<ResourceDefinitionChild> children
    ) {
        for (ResourceDefinitionChild child : children) {
            final UUID childUuid = child.getTarget().getUuid();
            if (childUuid.equals(uuid)) {
                throw new ValidationException(ERR_DEP_CYCLE);
            }

            final ResourceDefinition rdChild = resourceDefinitionCache.getByUuid(childUuid);
            if (rdChild.getChildren().isEmpty()) {
                return;
            }
            validateDependencyCyclesChild(uuid, rdChild.getChildren());
        }
    }

    private boolean isValidPrefixUrl(String urlPrefix) {
        return urlPrefix.matches("[a-zA-Z0-9-_]*");
    }

}
