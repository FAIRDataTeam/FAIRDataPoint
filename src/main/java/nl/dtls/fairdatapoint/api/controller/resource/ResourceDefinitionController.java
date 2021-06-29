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

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Tag(name = "Metadata Model")
@RestController
@RequestMapping("/resource-definitions")
public class ResourceDefinitionController {

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceDefinitionDTO>> getResourceDefinitions() {
        List<ResourceDefinitionDTO> dto = resourceDefinitionService.getAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDefinitionDTO> createResourceDefinitions(@RequestBody @Valid ResourceDefinitionChangeDTO reqDto) throws BindException {
        ResourceDefinitionDTO dto = resourceDefinitionService.create(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDefinitionDTO> getResourceDefinition(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<ResourceDefinitionDTO> oDto = resourceDefinitionService.getDTOByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Resource Definition '%s' doesn't exist", uuid));
        }
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDefinitionDTO> putResourceDefinitions(@PathVariable final String uuid,
                                                                     @RequestBody @Valid ResourceDefinitionChangeDTO reqDto)
            throws ResourceNotFoundException, BindException {
        Optional<ResourceDefinitionDTO> oDto = resourceDefinitionService.update(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Resource Definition '%s' doesn't exist", uuid));
        }
    }

    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
