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
import nl.dtls.fairdatapoint.api.dto.schema.*;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.exception.UnauthorizedException;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
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

    private static final String NOT_FOUND_MSG =
            "Metadata schema '%s' doesn't exist";
    private static final String NOT_FOUND_VERSION_MSG =
            "Metadata Schema '%s' doesn't exist with version '%s'";

    @Autowired
    private MetadataSchemaService metadataSchemaService;

    @Autowired
    private CurrentUserService currentUserService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaDTO>> getSchemas(
            @RequestParam(name = "drafts", required = false, defaultValue = "false")
            boolean includeDrafts,
            @RequestParam(name = "abstract", required = false, defaultValue = "true")
            boolean includeAbstract
    ) throws UnauthorizedException {
        if (includeDrafts && currentUserService.isAdmin()) {
            return new ResponseEntity<>(
                    metadataSchemaService.getSchemasWithDrafts(includeAbstract),
                    HttpStatus.OK
            );
        }
        else if (includeDrafts) {
            throw new UnauthorizedException("Unauthorized to see drafts of metadata schemas");
        }
        return new ResponseEntity<>(
                metadataSchemaService.getSchemasWithoutDrafts(includeAbstract),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDTO> getSchema(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        final Optional<MetadataSchemaDTO> oDto = metadataSchemaService.getSchemaByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
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
        "text/xml"
    })
    public ResponseEntity<Model> getSchemaContent(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        final Optional<Model> oDto = metadataSchemaService.getSchemaContentByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSchemaFull(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final boolean result = metadataSchemaService.deleteSchemaFull(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDraftDTO> createSchemaDraft(
            @RequestBody @Valid MetadataSchemaChangeDTO reqDto
    ) {
        final MetadataSchemaDraftDTO dto = metadataSchemaService.createSchemaDraft(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{uuid}/draft", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDraftDTO> getSchemaDraft(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final Optional<MetadataSchemaDraftDTO> oDto = metadataSchemaService.getSchemaDraft(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{uuid}/draft", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaDraftDTO> updateSchemaDraft(
            @PathVariable final String uuid,
            @RequestBody @Valid MetadataSchemaChangeDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<MetadataSchemaDraftDTO> oDto =
                metadataSchemaService.updateSchemaDraft(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{uuid}/draft")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSchemaDraft(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        final boolean result = metadataSchemaService.deleteSchemaDraft(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/{uuid}/versions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MetadataSchemaDTO> releaseSchemaVersion(
            @PathVariable final String uuid,
            @RequestBody @Valid MetadataSchemaReleaseDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<MetadataSchemaDTO> oDto = metadataSchemaService.releaseDraft(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/{uuid}/versions/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaVersionDTO> getSchemaVersion(
            @PathVariable final String uuid,
            @PathVariable final String version
    ) throws ResourceNotFoundException {
        final Optional<MetadataSchemaVersionDTO> oDto =
                metadataSchemaService.getVersion(uuid, version);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_VERSION_MSG, uuid, version));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{uuid}/versions/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetadataSchemaVersionDTO> updateSchemaVersion(
            @PathVariable final String uuid,
            @PathVariable final String version,
            @RequestBody @Valid MetadataSchemaUpdateDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<MetadataSchemaVersionDTO> oDto =
                metadataSchemaService.updateVersion(uuid, version, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_VERSION_MSG, uuid, version));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{uuid}/versions/{version}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteSchemaVersion(
            @PathVariable final String uuid,
            @PathVariable final String version
    ) throws ResourceNotFoundException {
        final boolean result = metadataSchemaService.deleteVersion(uuid, version);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_VERSION_MSG, uuid, version));
        }
    }

    @GetMapping(path = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaVersionDTO>> getPublishedSchemas() {
        final List<MetadataSchemaVersionDTO> dto = metadataSchemaService.getPublishedSchemas();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaRemoteDTO>> getImportableSchemas(
            @RequestParam(name = "from") String fdpUrl
    ) {
        final List<MetadataSchemaRemoteDTO> dto = metadataSchemaService.getRemoteSchemas(fdpUrl);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            path = "/import",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<MetadataSchemaVersionDTO>> importSchemas(
            @RequestBody @Valid List<@Valid MetadataSchemaVersionDTO> reqDtos
    ) {
        final List<MetadataSchemaVersionDTO> dto = metadataSchemaService.importSchemas(reqDtos);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetadataSchemaRemoteDTO>> checkForUpdates() {
        final List<MetadataSchemaRemoteDTO> dtos = metadataSchemaService.checkForUpdates();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            path = "/preview",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {
                "text/turtle",
                "application/x-turtle",
                "text/n3",
                "text/rdf+n3",
                "application/ld+json",
                "application/rdf+xml",
                "application/xml",
                "text/xml"
            }
    )
    public ResponseEntity<Model> getShaclPreview(@RequestBody @Valid MetadataSchemaPreviewRequestDTO reqDto) {
        final Model model = metadataSchemaService.getShaclFromSchemas(reqDto);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }
}
