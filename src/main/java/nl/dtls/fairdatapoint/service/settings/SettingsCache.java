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

import nl.dtls.fairdatapoint.database.mongo.repository.SettingsRepository;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static nl.dtls.fairdatapoint.config.CacheConfig.SETTINGS_CACHE;

@Service
public class SettingsCache {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Autowired
    private SettingsRepository settingsRepository;

    private static final String SETTINGS_KEY = "settings";

    @PostConstruct
    public void updateCachedSettings() {
        updateCachedSettings(settingsRepository.findFirstBy().orElse(Settings.getDefault()));
    }

    public void updateCachedSettings(Settings settings) {
        // Get cache
        Cache cache = cache();

        // Clear cache
        cache.clear();

        // Add to cache
        cache.put(SETTINGS_KEY, settings);
    }

    public Settings getOrDefaults() {
        return cache().get(SETTINGS_KEY, Settings.class);
    }

    private Cache cache() {
        return cacheManager.getCache(SETTINGS_CACHE);
    }
}
