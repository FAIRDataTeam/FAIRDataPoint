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
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
public class ResourceDefinitionService {

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionValidator resourceDefinitionValidator;

    @Autowired
    private ResourceDefinitionMapper resourceDefinitionMapper;

    public List<ResourceDefinition> getAll() {
        return resourceDefinitionRepository.findAll();
    }

    public ResourceDefinition getByUuid(String uuid) {
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUuid(uuid);
        if (oRd.isEmpty()) {
            throw new ResourceNotFoundException(
                    format("Resource with provided uuid ('%s') is not defined", uuid)
            );
        }
        return oRd.get();
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
    public ResourceDefinition create(ResourceDefinitionChangeDTO reqDto) {
        String uuid = UUID.randomUUID().toString();
        ResourceDefinition rd = resourceDefinitionMapper.fromChangeDTO(reqDto, uuid);

        resourceDefinitionValidator.validate(rd);
        resourceDefinitionRepository.save(rd);
        return rd;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResourceDefinition update(String uuid, ResourceDefinitionChangeDTO reqDto) {
        ResourceDefinition rd = getByUuid(uuid);
        ResourceDefinition updatedRd = resourceDefinitionMapper.fromChangeDTO(reqDto, rd.getUuid());
        updatedRd.setId(rd.getId());

        resourceDefinitionValidator.validate(updatedRd);
        resourceDefinitionRepository.save(updatedRd);
        return updatedRd;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteByUuid(String uuid) {
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUuid(uuid);
        if (oRd.isEmpty()) {
            return false;
        }
        ResourceDefinition rd = oRd.get();
        resourceDefinitionRepository.delete(rd);
        return true;
    }

}
