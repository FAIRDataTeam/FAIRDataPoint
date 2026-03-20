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

    private static final String FRAGMENT_SUFFIX = "\n";

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
                "prefixes", reqDto.getPrefixes() + FRAGMENT_SUFFIX,
                "graphPattern", reqDto.getGraphPattern() + FRAGMENT_SUFFIX,
                "ordering", reqDto.getOrdering() + FRAGMENT_SUFFIX
        );
    }
}
