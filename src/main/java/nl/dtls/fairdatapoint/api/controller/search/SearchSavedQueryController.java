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
package nl.dtls.fairdatapoint.api.controller.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.search.SearchResultDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.search.SearchService;
import nl.dtls.fairdatapoint.service.search.query.SearchSavedQueryService;
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

@Tag(name = "Search")
@RestController
@RequestMapping("/search/query/saved")
public class SearchSavedQueryController {

    private static final String NOT_FOUND_MSG = "Saved query '%s' doesn't exist";

    @Autowired
    private SearchSavedQueryService searchSavedQueryService;

    @Autowired
    private SearchService searchService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SearchSavedQueryDTO>> getAll() {
        return new ResponseEntity<>(searchSavedQueryService.getAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSavedQueryDTO> getSingle(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final Optional<SearchSavedQueryDTO> oDto = searchSavedQueryService.getSingle(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PostMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SearchResultDTO>> search(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException, MetadataRepositoryException {
        final Optional<SearchSavedQueryDTO> oDto = searchSavedQueryService.getSingle(uuid);
        if (oDto.isPresent()) {
            return ResponseEntity.ok(searchService.search(oDto.get()));
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSavedQueryDTO> create(
            @RequestBody @Valid SearchSavedQueryChangeDTO reqDto
    ) {
        final SearchSavedQueryDTO dto = searchSavedQueryService.create(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSavedQueryDTO> update(
            @PathVariable final String uuid,
            @RequestBody @Valid SearchSavedQueryChangeDTO reqDto
    ) {
        final Optional<SearchSavedQueryDTO> oDto = searchSavedQueryService.update(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final boolean result = searchSavedQueryService.delete(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }
}
