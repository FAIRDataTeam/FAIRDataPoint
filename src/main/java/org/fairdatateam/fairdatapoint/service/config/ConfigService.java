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
package nl.dtls.fairdatapoint.service.config;

import nl.dtls.fairdatapoint.api.dto.config.BootstrapConfigDTO;
import nl.dtls.fairdatapoint.config.properties.InstanceProperties;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private InstanceProperties instanceProperties;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Autowired
    private SettingsService settingsService;

    public BootstrapConfigDTO getBootstrapConfig() {
        final Settings settings = settingsService.getOrDefaults();
        String appTitle = settings.getAppTitle();
        String appSubtitle = settings.getAppSubtitle();
        if (appTitle == null || appTitle.isBlank()) {
            appTitle = instanceProperties.getTitle();
        }
        if (appSubtitle == null || appSubtitle.isBlank()) {
            appSubtitle = instanceProperties.getSubtitle();
        }
        return BootstrapConfigDTO.builder()
                .persistentUrl(persistentUrl)
                .resourceDefinitions(resourceDefinitionService.getAll())
                .index(instanceProperties.isIndex())
                .appTitle(appTitle)
                .appSubtitle(appSubtitle)
                .build();
    }

}
