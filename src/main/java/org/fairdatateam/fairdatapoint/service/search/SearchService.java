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
package org.fairdatateam.fairdatapoint.service.search;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.fairdatateam.fairdatapoint.api.dto.search.*;
import org.fairdatateam.fairdatapoint.database.rdf.repository.MetadataRepositoryException;
import org.fairdatateam.fairdatapoint.database.rdf.repository.GenericMetadataRepository;
import org.fairdatateam.fairdatapoint.entity.exception.ResourceNotFoundException;
import org.fairdatateam.fairdatapoint.entity.metadata.MetadataState;
import org.fairdatateam.fairdatapoint.entity.search.SearchFilterCacheContainer;
import org.fairdatateam.fairdatapoint.entity.search.SearchFilterType;
import org.fairdatateam.fairdatapoint.entity.search.SearchFilterValue;
import org.fairdatateam.fairdatapoint.entity.search.SearchResult;
import org.fairdatateam.fairdatapoint.entity.settings.SettingsSearchFilter;
import org.fairdatateam.fairdatapoint.service.metadata.state.MetadataStateService;
import org.fairdatateam.fairdatapoint.service.settings.SettingsService;
import org.apache.commons.lang.text.StrSubstitutor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.l;

@Service
public class SearchService {

    private static final String FIELD_VALUE = "value";
    private static final String FIELD_LABEL = "label";

    private static final String FIND_ENTITY_BY_LITERAL = "/sparql/findEntityByLiteral.sparql";
    private static final String FIND_OBJECT_FOR_PREDICATE = "/sparql/findObjectsForPredicate.sparql";
    private static final String QUERY_TEMPLATE_NAME = "/sparql/queryTemplate.sparql";

    private static final String QUERY_TEMPLATE = loadSparqlQueryTemplate();

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    private SearchMapper searchMapper;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private SearchFilterCache searchFilterCache;

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    public List<SearchResultDTO> search(
            SearchSavedQueryDTO searchSavedQueryDTO
    ) throws MetadataRepositoryException {
        return search(searchSavedQueryDTO.getVariables());
    }

    public List<SearchResultDTO> search(SearchQueryDTO reqDto) throws MetadataRepositoryException {
        final List<SearchResult> results = findByLiteral(l(reqDto.getQuery()));
        return processSearchResults(results);
    }

    public List<SearchResultDTO> search(
            SearchQueryVariablesDTO reqDto
    ) throws MetadataRepositoryException, MalformedQueryException {
        // Compose query
        final String query = composeQuery(reqDto);
        // Verify query
        final SPARQLParser parser = new SPARQLParser();
        parser.parseQuery(query, persistentUrl);
        // Get and process results for query
        final List<SearchResult> results = findBySparqlQuery(query);
        return processSearchResults(results);
    }

    public SearchQueryTemplateDTO getSearchQueryTemplate() {
        return searchMapper.toQueryTemplateDTO(QUERY_TEMPLATE);
    }

    public List<SearchFilterDTO> getSearchFilters() {
        return settingsService
                .getOrDefaults()
                .getSearchFilters()
                .parallelStream()
                .map(this::enrichItems)
                .toList();
    }

    public List<SearchFilterDTO> resetSearchFilters() {
        searchFilterCache.clearCache();
        return getSearchFilters();
    }

    private SearchFilterDTO enrichItems(SettingsSearchFilter filter) {
        final SearchFilterDTO result = searchMapper.toFilterDTO(filter);
        final Set<String> values =
                result
                        .getValues()
                        .stream()
                        .map(SearchFilterItemDTO::getValue)
                        .collect(Collectors.toSet());
        if (filter.isQueryFromRecords()) {
            final List<SearchFilterItemDTO> xvalues = new ArrayList<>();
            xvalues.addAll(result.getValues());
            xvalues.addAll(
                queryFilterItems(filter.getPredicate())
                    .stream()
                    .filter(item -> !values.contains(item.getValue()))
                    .map(searchMapper::toFilterItemDTO)
                    .toList()
            );
            if (filter.getType().equals(SearchFilterType.IRI)) {
                searchFilterCache.updateLabels(filter.getPredicate());
            }
            result.setValues(xvalues);
        }
        return result;
    }

    private List<SearchFilterValue> queryFilterItems(String predicate) {
        // TODO: filter related to DRAFT records
        final SearchFilterCacheContainer cacheContainer =
                searchFilterCache.getFilter(predicate);
        if (cacheContainer != null) {
            return cacheContainer.getValues();
        }
        try {
            final List<SearchFilterValue> result = findByFilterPredicate(i(predicate));
            searchFilterCache.setFilter(predicate, new SearchFilterCacheContainer(result));
            return result;
        }
        catch (MetadataRepositoryException exception) {
            throw new RuntimeException(exception);
        }
    }

    private List<SearchResultDTO> processSearchResults(List<SearchResult> results) {
        final Map<String, SearchResultDTO> resultDtos = results
                .stream()
                .collect(
                        Collectors.groupingBy(
                                SearchResult::getUri,
                                Collectors.mapping(Function.identity(), toList())
                        )
                )
                .entrySet()
                .parallelStream()
                .filter(entry -> isUsableForFilter(i(entry.getKey())))
                .map(entry -> searchMapper.toResultDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(SearchResultDTO::getUri, Function.identity()));
        return results
                .stream()
                .map(SearchResult::getUri)
                .distinct()
                .map(resultDtos::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean isUsableForFilter(IRI iri) {
        try {
            return !metadataStateService
                    .get(iri)
                    .getState()
                    .equals(MetadataState.DRAFT);
        }
        catch (ResourceNotFoundException exception) {
            return true;
        }
    }

    /**
     * Renders a SPARQL query string based on the restricted query defined in <code>QUERY_TEMPLATE</code> and user input
     * @param queryVariables User input to be used as template context
     * @return Full SPARQL query string
     */
    private String composeQuery(SearchQueryVariablesDTO queryVariables) {
        final Map<String, String> templateContext = Map.of(
                "prefixes", queryVariables.getPrefixes(),
                "graphPattern", queryVariables.getGraphPattern(),
                "ordering", queryVariables.getOrdering()
        );
        final StrSubstitutor substitutor = new StrSubstitutor(templateContext, "{{", "}}");
        return substitutor.replace(QUERY_TEMPLATE);
    }

    /**
     * Loads a query template string from file
     * @return SPARQL query template string
     */
    private static String loadSparqlQueryTemplate() {
        try {
            final Optional<URL> fileURL = Optional.ofNullable(SearchService.class.getResource(QUERY_TEMPLATE_NAME));
            return Resources.toString(fileURL.orElseThrow(), Charsets.UTF_8);
        }
        catch (IOException | NoSuchElementException exception) {
            throw new RuntimeException(
                    format("Cannot load SPARQL template for search: %s",
                            exception.getMessage())
            );
        }
    }

    public List<SearchResult> findByLiteral(Literal query) throws MetadataRepositoryException {
        return metadataRepository.runSparqlQueryFromFile(FIND_ENTITY_BY_LITERAL, Map.of("query", query))
                .stream()
                .map(item -> searchMapper.toSearchResult(item, true))
                .toList();
    }

    public List<SearchResult> findBySparqlQuery(String query) throws MetadataRepositoryException {
        return metadataRepository
                .runSparqlQuery(query, null)
                .stream()
                .map(item -> searchMapper.toSearchResult(item, false))
                .toList();
    }

    public List<SearchFilterValue> findByFilterPredicate(IRI predicateUri) throws MetadataRepositoryException {
        final Map<String, String> values = new HashMap<>();
        metadataRepository.runSparqlQueryFromFile(
                FIND_OBJECT_FOR_PREDICATE,
                Map.of("predicate", predicateUri)
        ).forEach(entry -> {
            values.put(
                    entry.getValue(FIELD_VALUE).stringValue(),
                    Optional.ofNullable(entry.getValue(FIELD_LABEL)).map(Value::stringValue).orElse(null)
            );
        });
        return values
                .entrySet()
                .stream()
                .map(entry -> new SearchFilterValue(entry.getKey(), entry.getValue()))
                .toList();
    }
}
