/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.api.controller.search;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fairdatateam.fairdatapoint.api.dto.search.SearchResultDTO;
import org.fairdatateam.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import org.fairdatateam.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import org.fairdatateam.fairdatapoint.database.rdf.repository.MetadataRepositoryException;
import org.fairdatateam.fairdatapoint.entity.exception.ResourceNotFoundException;
import org.fairdatateam.fairdatapoint.entity.search.SearchSavedQuery;
import org.fairdatateam.fairdatapoint.service.search.SearchService;
import org.fairdatateam.fairdatapoint.service.search.query.SearchSavedQueryMapper;
import org.fairdatateam.fairdatapoint.service.search.query.SearchSavedQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Tag(name = "Search")
@RestController
@RequestMapping("/search/query/saved")
public class SearchSavedQueryController {

    private static final String NOT_FOUND_MSG = "Saved query '%s' doesn't exist";

    private final SearchSavedQueryMapper savedQueryMapper;

    private final SearchSavedQueryService savedQueryService;

    private final SearchService searchService;

    /**
     * Constructor (autowired)
     */
    public SearchSavedQueryController(
            SearchSavedQueryMapper savedQueryMapper,
            SearchSavedQueryService savedQueryService,
            SearchService searchService
    ) {
        this.savedQueryMapper = savedQueryMapper;
        this.savedQueryService = savedQueryService;
        this.searchService = searchService;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "List all existing saved queries")
    public ResponseEntity<List<SearchSavedQueryDTO>> getAll() {
        return new ResponseEntity<>(savedQueryService.getAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Retrieve an existing saved query definition")
    public ResponseEntity<SearchSavedQueryDTO> getSingle(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final Optional<SearchSavedQueryDTO> oDto = savedQueryService.getSingle(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PostMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Evaluate an existing saved query")
    public ResponseEntity<List<SearchResultDTO>> search(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException, MetadataRepositoryException {
        final SearchSavedQueryDTO savedQueryDTO = savedQueryService.getSingle(uuid).orElseThrow(
                () -> new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid)));
        return ResponseEntity.ok(searchService.search(savedQueryDTO.getVariables()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Save the query that is defined in the request body")
    public ResponseEntity<SearchSavedQueryDTO> create(
            @RequestBody @Valid SearchSavedQueryChangeDTO body
    ) {
        final SearchSavedQuery savedQuery = savedQueryService.create(savedQueryMapper.fromChangeDTO(body));
        return new ResponseEntity<>(savedQueryMapper.toDTO(savedQuery), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Change an existing saved query")
    public ResponseEntity<SearchSavedQueryDTO> update(
            @PathVariable final String uuid,
            @RequestBody @Valid SearchSavedQueryChangeDTO reqDto
    ) {
        final Optional<SearchSavedQueryDTO> oDto = savedQueryService.update(uuid, reqDto);
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
    @Operation(description = "Delete an existing saved query")
    public ResponseEntity<Void> delete(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final boolean result = savedQueryService.delete(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }
}
