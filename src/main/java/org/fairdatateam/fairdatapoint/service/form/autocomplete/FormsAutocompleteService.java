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
package nl.dtls.fairdatapoint.service.form.autocomplete;

import nl.dtls.fairdatapoint.api.dto.form.FormAutocompleteItemDTO;
import nl.dtls.fairdatapoint.api.dto.form.FormAutocompleteRequestDTO;
import nl.dtls.fairdatapoint.entity.forms.RdfEntityCacheContainer;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.service.form.autocomplete.retrieval.RdfEntitiesNamespaceRetriever;
import nl.dtls.fairdatapoint.service.form.autocomplete.retrieval.RdfEntitiesRetriever;
import nl.dtls.fairdatapoint.service.form.autocomplete.retrieval.RdfEntitiesSparqlRetriever;
import nl.dtls.fairdatapoint.service.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FormsAutocompleteService {

    @Autowired
    private FormsAutocompleteCache cache;

    @Autowired
    private RdfEntitiesNamespaceRetriever namespaceRetriever;

    @Autowired
    private RdfEntitiesSparqlRetriever sparqlRetriever;

    @Autowired
    private SettingsService settingsService;

    public List<FormAutocompleteItemDTO> searchItems(FormAutocompleteRequestDTO reqDto) {
        RdfEntityCacheContainer container = cache.get(reqDto.getRdfType());
        if (container == null) {
            container = retrieveItems(reqDto.getRdfType());
        }
        if (container == null) {
            return Collections.emptyList();
        }
        else {
            cache.set(container);
        }
        return filterItems(reqDto.getQuery().toLowerCase(), container);
    }

    public List<FormAutocompleteItemDTO> filterItems(String query, RdfEntityCacheContainer container) {
        return container
                .getItems()
                .stream()
                .filter(item -> item.getLabel().toLowerCase().contains(query))
                .toList();
    }

    public RdfEntityCacheContainer retrieveItems(String rdfType) {
        final Settings settings = settingsService.getOrDefaults();
        RdfEntityCacheContainer container = null;
        if (settings.getForms().getAutocomplete().getSearchNamespace()) {
            container = retrieveItems(rdfType, namespaceRetriever);
        }
        if (container == null) {
            container = retrieveItems(rdfType, sparqlRetriever);
        }
        return container;
    }

    public RdfEntityCacheContainer retrieveItems(String rdfType, RdfEntitiesRetriever retriever) {
        final Map<String, String> entities = retriever.retrieve(rdfType);
        if (entities == null) {
            return null;
        }
        return RdfEntityCacheContainer
                .builder()
                .sourceType(retriever.getSourceType())
                .rdfType(rdfType)
                .items(
                        entities
                                .entrySet()
                                .stream()
                                .map(pair -> {
                                    return FormAutocompleteItemDTO
                                            .builder()
                                            .uri(pair.getKey())
                                            .label(pair.getValue())
                                            .build();
                                })
                                .toList()
                )
                .build();
    }
}
