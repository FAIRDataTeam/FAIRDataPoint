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
package nl.dtls.fairdatapoint.api.controller.shape;

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeChangeDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeRemoteDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.shape.ShapeService;
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
@RequestMapping("/shapes")
public class ShapeController {

    @Autowired
    private ShapeService shapeService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShapeDTO>> getShapes() {
        List<ShapeDTO> dto = shapeService.getShapes();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path = "/public", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShapeDTO>> getPublishedShapes() {
        List<ShapeDTO> dto = shapeService.getPublishedShapes();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShapeRemoteDTO>> getImportableShapes(@RequestParam(name = "from") String fdpUrl) {
        List<ShapeRemoteDTO> dto = shapeService.getRemoteShapes(fdpUrl);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShapeDTO>> importShapes(@RequestBody @Valid List<ShapeRemoteDTO> reqDtos) {
        List<ShapeDTO> dto = shapeService.importShapes(reqDtos);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShapeDTO> createShape(@RequestBody @Valid ShapeChangeDTO reqDto) {
        ShapeDTO dto = shapeService.createShape(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShapeDTO> getShape(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<ShapeDTO> oDto = shapeService.getShapeByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
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
    public ResponseEntity<Model> getShapeContent(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        Optional<Model> oDto = shapeService.getShapeContentByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShapeDTO> putShape(@PathVariable final String uuid,
                                             @RequestBody @Valid ShapeChangeDTO reqDto) throws ResourceNotFoundException {
        Optional<ShapeDTO> oDto = shapeService.updateShape(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteShape(@PathVariable final String uuid)
            throws ResourceNotFoundException {
        boolean result = shapeService.deleteShape(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException(format("Shape '%s' doesn't exist", uuid));
        }
    }

}
