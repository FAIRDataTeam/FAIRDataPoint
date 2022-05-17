package nl.dtls.fairdatapoint.service.search;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.entity.search.SearchFilterCacheContainer;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.service.label.LabelService;
import nl.dtls.fairdatapoint.service.settings.SettingsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.config.CacheConfig.SEARCH_FILTERS_CACHE;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

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

        Settings settings = settingsCache.getOrDefaults();
        settings.getSearchFilters().forEach(filter -> cache().put(filter.getPredicate(), null));
    }

    public SearchFilterCacheContainer getFilter(String cacheKey) {
        return cache().get(cacheKey, SearchFilterCacheContainer.class);
    }

    public void setFilter(String cacheKey, SearchFilterCacheContainer result) {
        cache().put(cacheKey, result);
    }

    public void updateLabels(String cacheKey) {
        SearchFilterCacheContainer container = getFilter(cacheKey);
        try {
            if (container != null) {
                container.getValues().forEach(value -> {
                    if (value.getLabel() == null) {
                        labelService.getLabel(value.getValue(), "en").ifPresent(label -> value.setLabel(label.getLabel()));
                    }
                });
            }
        } catch (Exception ex) {
            log.debug(format("Error occurred while updating labels for search filters: %s", ex.getMessage()));
        }
    }

    public void clearFilter(String cacheKey) {
        cache().put(cacheKey, null);
    }

    private Cache cache() {
        return cacheManager.getCache(SEARCH_FILTERS_CACHE);
    }

}
