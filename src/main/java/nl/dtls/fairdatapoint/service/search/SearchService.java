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
package nl.dtls.fairdatapoint.service.search;

import nl.dtls.fairdatapoint.api.dto.search.SearchQueryDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchResultDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
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
                .filter(entry -> {
                    try {
                        return !metadataStateService.get(i(entry.getKey())).getState().equals(MetadataState.DRAFT);
                    } catch (ResourceNotFoundException e) {
                        return true;
                    }
                })
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
