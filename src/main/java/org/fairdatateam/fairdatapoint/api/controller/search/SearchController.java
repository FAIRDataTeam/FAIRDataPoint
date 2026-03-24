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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fairdatateam.fairdatapoint.api.dto.search.*;
import org.fairdatateam.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import org.fairdatateam.fairdatapoint.service.search.SearchService;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Search")
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<SearchResultDTO>> search(
            @RequestBody @Valid SearchQueryDTO reqDto
    ) throws MetadataRepositoryException {
        return ResponseEntity.ok(searchService.search(reqDto));
    }

    @GetMapping(
            path = "/query",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SearchQueryTemplateDTO> getSearchQueryTemplate() {
        return ResponseEntity.ok(searchService.getSearchQueryTemplate());
    }

    @PostMapping(
            path = "/query",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<SearchResultDTO>> searchWithQuery(
            @RequestBody @Valid SearchQueryVariablesDTO reqDto
    ) throws MetadataRepositoryException, MalformedQueryException {
        return ResponseEntity.ok(searchService.search(reqDto));
    }

    @GetMapping(
            path = "/filters",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<SearchFilterDTO>> getSearchFilters() {
        return ResponseEntity.ok(searchService.getSearchFilters());
    }

    @DeleteMapping(
            path = "/filters",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SearchFilterDTO>> resetSearchFiltersCache() {
        return ResponseEntity.ok(searchService.resetSearchFilters());
    }

}
