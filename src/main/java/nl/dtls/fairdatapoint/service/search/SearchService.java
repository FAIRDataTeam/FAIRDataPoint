package nl.dtls.fairdatapoint.service.search;

import nl.dtls.fairdatapoint.api.dto.search.SearchQueryDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchResultDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import nl.dtls.fairdatapoint.entity.search.SearchResult;
import nl.dtls.fairdatapoint.service.metadata.state.MetadataStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

@Service
public class SearchService {

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Autowired
    private MetadataStateService metadataStateService;

    public List<SearchResultDTO> search(SearchQueryDTO reqDto) throws MetadataRepositoryException {
        return metadataRepository.findByLiteral(l(reqDto.getQ()))
                .stream()
                .collect(Collectors.groupingBy(SearchResult::getUri, Collectors.mapping(i -> i, toList())))
                .entrySet()
                .stream()
                .filter(entry -> !metadataStateService.get(i(entry.getKey())).getState().equals(MetadataState.DRAFT))
                .map(entry -> new SearchResultDTO(
                        entry.getKey(),
                        entry.getValue().get(0).getTitle(),
                        entry.getValue().get(0).getDescription(),
                        entry.getValue().stream()
                                .map(SearchResult::getRelation).collect(Collectors.toList())
                ))
                .collect(toList());
    }

}
