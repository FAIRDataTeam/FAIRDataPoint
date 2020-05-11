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
package nl.dtls.fairdatapoint.api.controller.resource;

import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/resource-definitions")
public class ResourceDefinitionController {

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ResourceDefinition>> getResourceDefinitions() {
        List<ResourceDefinition> dto = resourceDefinitionService.getAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ResourceDefinition> createResourceDefinitions(@RequestBody @Valid ResourceDefinitionChangeDTO reqDto) {
        ResourceDefinition dto = resourceDefinitionService.create(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<ResourceDefinition> getResourceDefinition(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        ResourceDefinition dto = resourceDefinitionService.getByUuid(uuid);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ResponseEntity<ResourceDefinition> putResourceDefinitions(@PathVariable final String uuid,
                                                                     @RequestBody @Valid ResourceDefinitionChangeDTO reqDto)
            throws ResourceNotFoundException {
        ResourceDefinition dto = resourceDefinitionService.update(uuid, reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteResourceDefinitions(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        boolean result = resourceDefinitionService.deleteByUuid(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException(format("Resource Definition '%s' doesn't exist", uuid));
        }
    }
}
