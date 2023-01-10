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

import nl.dtls.fairdatapoint.entity.forms.RdfEntityCacheContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import static nl.dtls.fairdatapoint.config.CacheConfig.FORMS_AUTOCOMPLETE_CACHE;

@Component
public class FormsAutocompleteCache {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    public void clear() {
        cache().clear();
    }

    public void evict(String rdfType) {
        cache().evict(rdfType);
    }

    public void set(RdfEntityCacheContainer container) {
        cache().put(container.getRdfType(), container);
    }

    public RdfEntityCacheContainer get(String rdfType) {
        return cache().get(rdfType, RdfEntityCacheContainer.class);
    }

    private Cache cache() {
        return cacheManager.getCache(FORMS_AUTOCOMPLETE_CACHE);
    }
}
