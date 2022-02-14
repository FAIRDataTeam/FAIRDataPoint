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
package nl.dtls.fairdatapoint.api.controller.schema;

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaRemoteDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaService;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Tag(name = "Metadata Model")
@RestController
@RequestMapping("/metadata-schemas")
public class MetadataSchemaController {

    private static final String NOT_FOUND_MSG = "Metadata schema '%s' doesn't exist";

    @Autowired
    private MetadataSchemaService metadataSchemaService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaDTO>> getSchemas() {
        List<MetadataSchemaDTO> dto = metadataSchemaService.getSchemas();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaDTO>> getPublishedSchemas() {
        List<MetadataSchemaDTO> dto = metadataSchemaService.getPublishedSchemas();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaRemoteDTO>> getImportableSchemas(@RequestParam(name = "from") String fdpUrl) {
        List<MetadataSchemaRemoteDTO> dto = metadataSchemaService.getRemoteSchemas(fdpUrl);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaDTO>> importSchemas(@RequestBody @Valid List<MetadataSchemaRemoteDTO> reqDtos) {
        List<MetadataSchemaDTO> dto = metadataSchemaService.importSchemas(reqDtos);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDTO> createSchema(@RequestBody @Valid MetadataSchemaChangeDTO reqDto) {
        MetadataSchemaDTO dto = metadataSchemaService.createSchema(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDTO> getSchema(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<MetadataSchemaDTO> oDto = metadataSchemaService.getSchemaByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @GetMapping(path = "/{uuid}", produces = {
            "text/turtle",
            "application/x-turtle",
            "text/n3",
            "text/rdf+n3",
            "application/ld+json",
            "application/rdf+xml",
            "application/xml",
            "text/xml",
    })
    public ResponseEntity<Model> getSchemaContent(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<Model> oDto = metadataSchemaService.getSchemaContentByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDTO> putSchema(@PathVariable final String uuid,
                                                      @RequestBody @Valid MetadataSchemaChangeDTO reqDto) throws ResourceNotFoundException {
        Optional<MetadataSchemaDTO> oDto = metadataSchemaService.updateSchema(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSchema(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        boolean result = metadataSchemaService.deleteSchema(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

}
