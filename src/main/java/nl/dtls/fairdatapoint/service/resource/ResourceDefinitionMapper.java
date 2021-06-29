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
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceDefinitionMapper {

    public ResourceDefinition fromChangeDTO(ResourceDefinitionChangeDTO dto, String uuid) {
        return new ResourceDefinition(
                uuid,
                dto.getName(),
                dto.getUrlPrefix(),
                dto.getShapeUuids(),
                dto.getChildren(),
                dto.getExternalLinks());
    }

    public ResourceDefinitionChangeDTO toChangeDTO(ResourceDefinition rd) {
        return new ResourceDefinitionChangeDTO(
                rd.getName(),
                rd.getUrlPrefix(),
                rd.getShapeUuids(),
                rd.getChildren(),
                rd.getExternalLinks());
    }

    public ResourceDefinitionDTO toDTO(ResourceDefinition rd, List<String> targetClassUris) {
        return new ResourceDefinitionDTO(
                rd.getUuid(),
                rd.getName(),
                rd.getUrlPrefix(),
                rd.getShapeUuids(),
                targetClassUris,
                rd.getChildren(),
                rd.getExternalLinks()
        );
    }
}
