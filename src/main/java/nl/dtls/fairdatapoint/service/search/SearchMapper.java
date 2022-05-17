package nl.dtls.fairdatapoint.service.search;

import nl.dtls.fairdatapoint.api.dto.search.*;
import nl.dtls.fairdatapoint.entity.search.SearchFilterValue;
import nl.dtls.fairdatapoint.entity.search.SearchResult;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilter;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilterItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class SearchMapper {

    public SearchQueryTemplateDTO toQueryTemplateDTO(String template) {
        return new SearchQueryTemplateDTO(template);
    }

    public SearchResultDTO toResultDTO(String uri, List<SearchResult> resultList) {
        return new SearchResultDTO(
                uri,
                resultList
                        .stream()
                        .map(SearchResult::getType)
                        .distinct()
                        .toList(),
                resultList.get(0).getTitle(),
                resultList.get(0).getDescription(),
                resultList
                        .stream()
                        .map(SearchResult::getRelation)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList()
        );
    }

    public SearchFilterDTO toFilterDTO(SettingsSearchFilter filter) {
        return SearchFilterDTO
                .builder()
                .type(filter.getType())
                .queryFromRecords(filter.isQueryFromRecords())
                .predicate(filter.getPredicate())
                .label(filter.getLabel())
                .values(filter
                        .getPresetValues()
                        .stream()
                        .map(this::toFilterItemDTO)
                        .toList()
                )
                .build();
    }

    public SearchFilterItemDTO toFilterItemDTO(SettingsSearchFilterItem item) {
        return SearchFilterItemDTO
                .builder()
                .value(item.getValue())
                .label(item.getLabel())
                .preset(true)
                .build();
    }

    public SearchFilterItemDTO toFilterItemDTO(SearchFilterValue value) {
        return SearchFilterItemDTO
                .builder()
                .value(value.getValue())
                .label(value.getLabel())
                .preset(false)
                .build();
    }

    public Map<String, String> toSubstitutions(SearchQueryVariablesDTO reqDto) {
        return Map.of(
                "prefixes", reqDto.getPrefixes(),
                "graphPattern", reqDto.getGraphPattern(),
                "ordering", reqDto.getOrdering()
        );
    }
}
