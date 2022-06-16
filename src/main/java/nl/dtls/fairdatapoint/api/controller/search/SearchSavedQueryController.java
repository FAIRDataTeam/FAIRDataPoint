package nl.dtls.fairdatapoint.api.controller.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
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

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SearchSavedQueryDTO>> getAll() {
        return new ResponseEntity<>(searchSavedQueryService.getAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSavedQueryDTO> getSingle(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        Optional<SearchSavedQueryDTO> oDto = searchSavedQueryService.getSingle(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSavedQueryDTO> create(
            @RequestBody @Valid SearchSavedQueryChangeDTO reqDto
    ) {
        SearchSavedQueryDTO dto = searchSavedQueryService.create(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSavedQueryDTO> update(
            @PathVariable final String uuid,
            @RequestBody @Valid SearchSavedQueryChangeDTO reqDto
    ) {
        Optional<SearchSavedQueryDTO> oDto = searchSavedQueryService.update(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        boolean result = searchSavedQueryService.delete(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }
}
