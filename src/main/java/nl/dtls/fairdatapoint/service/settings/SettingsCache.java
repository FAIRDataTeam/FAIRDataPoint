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

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.database.db.repository.SettingsRepository;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static nl.dtls.fairdatapoint.config.CacheConfig.SETTINGS_CACHE;

@Service
@RequiredArgsConstructor
public class SettingsCache {

    private static final String SETTINGS_KEY = "settings";

    private final ConcurrentMapCacheManager cacheManager;

    private final SettingsRepository settingsRepository;

    @PostConstruct
    public void updateCachedSettings() {
        // TODO PG - use ResetService (null)?
        updateCachedSettings(
                settingsRepository
                        .findByUuid(KnownUUIDs.SETTINGS_UUID)
                        .orElse(null)
        );
    }

    public void updateCachedSettings(Settings settings) {
        // Get cache
        final Cache cache = cache();

        // Clear cache
        cache.clear();

        // Add to cache
        cache.put(SETTINGS_KEY, settings);
    }

    public Settings getOrDefaults() {
        Settings yolo = Optional.ofNullable(cache().get(SETTINGS_KEY, Settings.class)).orElse(Settings.defaultSettings());
        return yolo;
    }

    private Cache cache() {
        return cacheManager.getCache(SETTINGS_CACHE);
    }
}
