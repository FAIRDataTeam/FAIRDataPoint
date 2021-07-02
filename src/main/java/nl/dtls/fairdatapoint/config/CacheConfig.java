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
package nl.dtls.fairdatapoint.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static nl.dtls.fairdatapoint.config.AclConfig.ACL_CACHE;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CATALOG_THEMES_CACHE = "CATALOG_THEMES_CACHE";

    public static final String RESOURCE_DEFINITION_CACHE = "RESOURCE_DEFINITION_CACHE";

    public static final String RESOURCE_DEFINITION_PARENT_CACHE = "RESOURCE_DEFINITION_PARENT_CACHE";

    public static final String RESOURCE_DEFINITION_TARGET_CLASSES_CACHE = "RESOURCE_DEFINITION_TARGET_CLASSES_CACHE";

    public static final String LABEL_CACHE = "LABEL_CACHE";

    public static final String SETTINGS_CACHE = "SETTINGS_CACHE";

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(List.of(
                ACL_CACHE,
                CATALOG_THEMES_CACHE,
                RESOURCE_DEFINITION_CACHE,
                RESOURCE_DEFINITION_PARENT_CACHE,
                RESOURCE_DEFINITION_TARGET_CLASSES_CACHE,
                SETTINGS_CACHE,
                LABEL_CACHE
        ));
        return cacheManager;
    }

}
