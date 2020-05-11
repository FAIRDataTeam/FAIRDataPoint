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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceDefinitionValidator {

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    public void validate(ResourceDefinition reqDto) {
        // Check uniqueness
        List<ResourceDefinition> rds = resourceDefinitionRepository.findAll();
        for (ResourceDefinition rd : rds) {
            if (!rd.getUuid().equals(reqDto.getUuid()) && rd.getName().equals(reqDto.getName())) {
                throw new ValidationException("Name should be unique");
            }
            if (!rd.getUuid().equals(reqDto.getUuid()) && rd.getUrlPrefix().equals(reqDto.getUrlPrefix())) {
                throw new ValidationException("Url Prefix should be unique");
            }
        }

        // Check existence of connected entities
        if (reqDto.getChild() != null) {
            if (resourceDefinitionRepository.findByUuid(reqDto.getChild().getResourceDefinitionUuid()).isEmpty()) {
                throw new ValidationException("Child doesn't exist");
            }
        }
        if (reqDto.getParent() != null) {
            if (resourceDefinitionRepository.findByUuid(reqDto.getParent().getResourceDefinitionUuid()).isEmpty()) {
                throw new ValidationException("Parent doesn't exist");
            }
        }

        // Check existence of dependency cycles
        if (reqDto.getParent() != null) {
            String parentUuid = reqDto.getParent().getResourceDefinitionUuid();
            while (true) {
                if (reqDto.getUuid().equals(parentUuid)) {
                    throw new ValidationException("Detect dependency cycle through parent");
                }

                ResourceDefinition rd = getResourceDefinition(rds, parentUuid);
                if (rd.getParent() == null) {
                    break;
                }
                parentUuid = rd.getParent().getResourceDefinitionUuid();
            }
        }
        if (reqDto.getChild() != null) {
            String childUuid = reqDto.getChild().getResourceDefinitionUuid();
            while (true) {
                if (reqDto.getUuid().equals(childUuid)) {
                    throw new ValidationException("Detect dependency cycle through child");
                }

                ResourceDefinition rd = getResourceDefinition(rds, childUuid);
                if (rd.getChild() == null) {
                    break;
                }
                childUuid = rd.getChild().getResourceDefinitionUuid();
            }
        }

        // Check if parent already has some other child
        if (reqDto.getParent() != null) {
            ResourceDefinition parentRd = getResourceDefinition(rds, reqDto.getParent().getResourceDefinitionUuid());
            if (parentRd.getChild() != null && !parentRd.getChild().getResourceDefinitionUuid().equals(reqDto.getUuid())) {
                throw new ValidationException("Parent already has some other child");
            }
        }

    }

    private ResourceDefinition getResourceDefinition(List<ResourceDefinition> resourceDefinitions, String uuid) {
        return resourceDefinitions.stream().filter(rd -> rd.getUuid().equals(uuid)).findAny().get();
    }

}
