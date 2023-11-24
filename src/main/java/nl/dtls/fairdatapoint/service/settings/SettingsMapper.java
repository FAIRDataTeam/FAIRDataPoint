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
package nl.dtls.fairdatapoint.service.settings;

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.search.SearchFilterDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchFilterItemDTO;
import nl.dtls.fairdatapoint.api.dto.settings.*;
import nl.dtls.fairdatapoint.config.properties.InstanceProperties;
import nl.dtls.fairdatapoint.config.properties.PingProperties;
import nl.dtls.fairdatapoint.config.properties.RepositoryConnectionProperties;
import nl.dtls.fairdatapoint.config.properties.RepositoryProperties;
import nl.dtls.fairdatapoint.entity.settings.*;
import nl.dtls.fairdatapoint.service.search.SearchMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SettingsMapper {

    private final InstanceProperties instanceProperties;

    private final PingProperties pingProperties;

    private final RepositoryProperties repositoryProperties;

    private final SearchMapper searchMapper;

    public SettingsDTO toDTO(Settings settings) {
        System.out.println(settings.getMetrics());
        return SettingsDTO.builder()
                .clientUrl(instanceProperties.getClientUrl())
                .persistentUrl(instanceProperties.getPersistentUrl())
                .appTitle(settings.getAppTitle())
                .appSubtitle(settings.getAppSubtitle())
                .appTitleFromConfig(instanceProperties.getTitle())
                .appSubtitleFromConfig(instanceProperties.getSubtitle())
                .metadataMetrics(settings.getMetrics().stream().map(this::toMetricDTO).toList())
                .ping(toPingDTO(settings))
                .mainRepository(toRepositoryDTO(repositoryProperties.getMain()))
                .draftsRepository(toRepositoryDTO(repositoryProperties.getDrafts()))
                .search(toSearchDTO(settings))
                .forms(toFormsDTO(settings))
                .build();
    }

    private SettingsFormsDTO toFormsDTO(Settings settings) {
        return SettingsFormsDTO.builder()
                .autocomplete(
                        SettingsFormsAutocompleteDTO.builder()
                                .searchNamespace(settings.getAutocompleteSearchNamespace())
                                .sources(settings.getAutocompleteSources().stream().map(this::toSourceDTO).toList())
                                .build()
                )
                .build();
    }

    private SettingsAutocompleteSourceDTO toSourceDTO(SettingsAutocompleteSource source) {
        return SettingsAutocompleteSourceDTO.builder()
                .rdfType(source.getRdfType())
                .sparqlEndpoint(source.getSparqlEndpoint())
                .sparqlQuery(source.getSparqlQuery())
                .build();
    }

    private SettingsSearchDTO toSearchDTO(Settings settings) {
        return SettingsSearchDTO.builder()
                .filters(settings.getSearchFilters().stream().map(this::toSearchFilterDTO).toList())
                .build();
    }

    private SearchFilterDTO toSearchFilterDTO(SettingsSearchFilter settingsSearchFilter) {
        return SearchFilterDTO.builder()
                .type(settingsSearchFilter.getType())
                .label(settingsSearchFilter.getLabel())
                .predicate(settingsSearchFilter.getPredicate())
                .queryFromRecords(settingsSearchFilter.getQueryRecords())
                .values(settingsSearchFilter.getItems().stream().map(this::toSearchFilterItemDTO).toList())
                .build();
    }

    private SearchFilterItemDTO toSearchFilterItemDTO(SettingsSearchFilterItem settingsSearchFilterItem) {
        return SearchFilterItemDTO.builder()
                .label(settingsSearchFilterItem.getLabel())
                .value(settingsSearchFilterItem.getValue())
                .preset(true)
                .build();
    }

    private SettingsRepositoryDTO toRepositoryDTO(RepositoryConnectionProperties repo) {
        return SettingsRepositoryDTO.builder()
                .type(repo.getStringType())
                .repository(repo.getRepository())
                .dir(repo.getDir())
                .url(repo.getUrl())
                .username(repo.getUsername())
                .password(redact(repo.getPassword()))
                .build();
    }

    private SettingsPingDTO toPingDTO(Settings settings) {
        return SettingsPingDTO.builder()
                .enabled(settings.getPingEnabled())
                .endpoints(settings.getPingEndpoints())
                .endpointsFromConfig(pingProperties.getEndpoints())
                .interval(pingProperties.getInterval().toString())
                .build();
    }

    public SettingsMetricDTO toMetricDTO(SettingsMetric metric) {
        return SettingsMetricDTO.builder()
                .metricUri(metric.getMetricUri())
                .resourceUri(metric.getResourceUri())
                .build();
    }


    private String redact(String secret) {
        return secret != null ? "<SECRET>" : null;
    }

    public Settings fromUpdateDTO(SettingsUpdateDTO dto, Settings settings) {
        return settings.toBuilder()
                .appTitle(dto.getAppTitle())
                .appSubtitle(dto.getAppSubtitle())
                .pingEnabled(dto.getPing().isEnabled())
                .pingEndpoints(dto.getPing().getEndpoints())
                .autocompleteSearchNamespace(dto.getForms().getAutocomplete().getSearchNamespace())
                .updatedAt(Instant.now())
                .build();
    }

    public SettingsMetric fromMetricDTO(SettingsMetricDTO dto, int orderPriority, Settings settings) {
        return SettingsMetric.builder()
                .metricUri(dto.getMetricUri())
                .resourceUri(dto.getResourceUri())
                .orderPriority(orderPriority)
                .settings(settings)
                .build();
    }

    public SettingsAutocompleteSource fromAutocompleteSourceDTO(SettingsAutocompleteSourceDTO dto, int orderPriority, Settings settings) {
        return SettingsAutocompleteSource.builder()
                .rdfType(dto.getRdfType())
                .sparqlEndpoint(dto.getSparqlEndpoint())
                .sparqlQuery(dto.getSparqlQuery())
                .orderPriority(orderPriority)
                .settings(settings)
                .build();
    }

    public SettingsSearchFilter fromSearchFilterDTO(SearchFilterDTO dto, int orderPriority, Settings settings) {
        final List<SettingsSearchFilterItem> items = new ArrayList<>();
        final SettingsSearchFilter filter = SettingsSearchFilter.builder()
                .queryRecords(dto.isQueryFromRecords())
                .label(dto.getLabel())
                .predicate(dto.getPredicate())
                .type(dto.getType())
                .orderPriority(orderPriority)
                .settings(settings)
                .build();
        for (int index = 0; index < dto.getValues().size(); index++) {
            items.add(fromSearchFilterIntemDTO(dto.getValues().get(index), index, filter));
        }
        filter.setItems(items);
        return filter;
    }

    private SettingsSearchFilterItem fromSearchFilterIntemDTO(SearchFilterItemDTO dto, int orderPriority, SettingsSearchFilter filter) {
        return SettingsSearchFilterItem.builder()
                .label(dto.getLabel())
                .value(dto.getValue())
                .orderPriority(orderPriority)
                .filter(filter)
                .build();
    }
}
