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

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.entity.search.SearchFilterCacheContainer;
import nl.dtls.fairdatapoint.service.label.LabelService;
import nl.dtls.fairdatapoint.service.settings.SettingsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.config.CacheConfig.SEARCH_FILTERS_CACHE;

@Component
@Slf4j
public class SearchFilterCache {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Autowired
    private SettingsCache settingsCache;

    @Autowired
    private LabelService labelService;

    @PostConstruct
    public void clearCache() {
        cache().clear();

        settingsCache
                .getOrDefaults()
                .getSearchFilters()
                .forEach(filter -> cache().put(filter.getPredicate(), null));
    }

    public SearchFilterCacheContainer getFilter(String cacheKey) {
        return cache().get(cacheKey, SearchFilterCacheContainer.class);
    }

    public void setFilter(String cacheKey, SearchFilterCacheContainer result) {
        cache().put(cacheKey, result);
    }

    public void updateLabels(String cacheKey) {
        final SearchFilterCacheContainer container = getFilter(cacheKey);
        try {
            if (container != null) {
                container.getValues().forEach(value -> {
                    if (value.getLabel() == null) {
                        labelService
                                .getLabel(value.getValue(), "en")
                                .ifPresent(label -> value.setLabel(label.getLabel()));
                    }
                });
            }
        }
        catch (Exception exception) {
            log.debug(
                    format("Error occurred while updating labels for search filters: %s",
                            exception.getMessage())
            );
        }
    }

    public void clearFilter(String cacheKey) {
        cache().put(cacheKey, null);
    }

    private Cache cache() {
        return cacheManager.getCache(SEARCH_FILTERS_CACHE);
    }

}
