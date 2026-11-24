/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.index.settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.index.settings.dto.IndexSettingsDTO;
import org.fairdatateam.fairdatapoint.index.settings.dto.IndexSettingsUpdateDTO;
import org.fairdatateam.fairdatapoint.common.config.InstanceProperties;
import org.fairdatateam.fairdatapoint.index.RequiredEnabledIndexFeature;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndexSettingsService {

    private final IndexSettingsRepository repository;

    private final IndexSettingsMapper mapper;

    private final InstanceProperties instanceProperties;

    /**
     * Returns true, i.e. denies the ping, if the {@code clientUrl} is invalid or if its host part matches any of the
     * hosts from the {@code IndexSettingsPing.denyList}.
     * @see <a href="https://www.rfc-editor.org/info/rfc3986/#section-3.2.2">RFC 3986 host</a>
     */
    @RequiredEnabledIndexFeature
    public boolean isPingDenied(String clientUrl) {
        log.info("Checking if clientUrl matches deny list: {}", clientUrl);
        try {
            // validate the URI and deny if clientUrl is not a valid
            final URI clientURI = new URI(clientUrl);
            // deny if the URI's host part matches any of the denied hosts from the settings
            final List<String> deniedHosts = getOrDefaults().getPing().getDenyList();
            return deniedHosts.stream().anyMatch(deniedHost -> clientURI.getHost().equalsIgnoreCase(deniedHost));
        } catch (URISyntaxException exception) {
            log.warn("Not a valid URI: {} ({})", clientUrl, exception);
            return true;
        }
    }

    @RequiredEnabledIndexFeature
    public IndexSettings getOrDefaults() {
        log.debug("Getting index settings");
        final Optional<IndexSettings> settings = repository.findFirstBy();
        if (settings.isPresent()) {
            log.debug("Index settings found");
            return settings.orElseThrow();
        }
        return getDefaults();
    }

    @RequiredEnabledIndexFeature
    public IndexSettingsDTO getCurrentSettings() {
        return mapper.toDTO(getOrDefaults(), getDefaults());
    }

    @RequiredEnabledIndexFeature
    public IndexSettings getDefaults() {
        log.debug("Using default index settings");
        final IndexSettings settings = new IndexSettings();
        settings.setPing(IndexSettingsPing.getDefault());
        settings.setRetrieval(IndexSettingsRetrieval.getDefault());
        settings.setAutoPermit(instanceProperties.isIndexAutoPermit());
        return settings;
    }

    @RequiredEnabledIndexFeature
    public IndexSettingsDTO updateSettings(IndexSettingsUpdateDTO dto) {
        return mapper.toDTO(
                repository.save(mapper.fromUpdateDTO(dto, getOrDefaults())),
                getDefaults()
        );
    }

    @RequiredEnabledIndexFeature
    public IndexSettingsDTO resetSettings() {
        return updateSettings(mapper.toUpdateDTO(getDefaults()));
    }
}
