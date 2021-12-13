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

import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.Optional;

import static nl.dtls.fairdatapoint.util.ValidationUtil.uniquenessValidationFailed;

@Service
public class ResourceDefinitionValidator {

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    public void validate(ResourceDefinition reqDto) throws BindException {
        // Check uniqueness
        Optional<ResourceDefinition> oRdByName = resourceDefinitionRepository.findByName(reqDto.getName());
        if (oRdByName.isPresent() && !oRdByName.get().getUuid().equals(reqDto.getUuid())) {
            uniquenessValidationFailed("name", reqDto);
        }
        Optional<ResourceDefinition> oRdByUrlPrefix =
                resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix());
        if (oRdByUrlPrefix.isPresent() && !oRdByUrlPrefix.get().getUuid().equals(reqDto.getUuid())) {
            uniquenessValidationFailed("urlPrefix", reqDto);
        }

        // Check urlPrefix validity
        if (!isValidPrefixUrl(reqDto.getUrlPrefix())) {
            throw new ValidationException("URL prefix is not valid");
        }

        // Check existence of connected entities
        for (ResourceDefinitionChild child : reqDto.getChildren()) {
            if (resourceDefinitionCache.getByUuid(child.getResourceDefinitionUuid()) == null) {
                throw new ValidationException("Child doesn't exist");
            }
        }

        // Check existence of dependency cycles
        validateDependencyCycles(reqDto, reqDto.getChildren());
    }

    private void validateDependencyCycles(ResourceDefinition reqDto, List<ResourceDefinitionChild> children) {
        for (ResourceDefinitionChild child : children) {
            String childUuid = child.getResourceDefinitionUuid();
            if (reqDto.getUuid().equals(childUuid)) {
                throw new ValidationException("Detect dependency cycle through child");
            }

            ResourceDefinition rdChild = resourceDefinitionCache.getByUuid(childUuid);
            if (rdChild.getChildren().isEmpty()) {
                return;
            }
            validateDependencyCycles(reqDto, rdChild.getChildren());
        }
    }

    private boolean isValidPrefixUrl(String urlPrefix) {
        return urlPrefix.matches("[a-zA-Z0-9-_]*");
    }

}
