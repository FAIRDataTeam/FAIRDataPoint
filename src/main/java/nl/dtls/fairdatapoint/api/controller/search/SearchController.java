package nl.dtls.fairdatapoint.api.controller.search;

import nl.dtls.fairdatapoint.api.dto.search.SearchQueryDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchResultDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<List<SearchResultDTO>> search(@RequestBody @Valid SearchQueryDTO reqDto) throws MetadataRepositoryException {
        return ResponseEntity.ok(searchService.search(reqDto));
    }

}
