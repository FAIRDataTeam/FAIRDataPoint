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

import nl.dtls.fairdatapoint.api.dto.settings.*;
import nl.dtls.fairdatapoint.config.properties.InstanceProperties;
import nl.dtls.fairdatapoint.config.properties.PingProperties;
import nl.dtls.fairdatapoint.config.properties.RepositoryProperties;
import nl.dtls.fairdatapoint.database.mongo.repository.SettingsRepository;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.entity.settings.SettingsPing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SettingsMapper {

    @Autowired
    private InstanceProperties instanceProperties;

    @Autowired
    private PingProperties pingProperties;

    @Autowired
    private RepositoryProperties repositoryProperties;

    public SettingsDTO toDTO(Settings settings) {
        return new SettingsDTO(
                instanceProperties.getClientUrl(),
                instanceProperties.getPersistentUrl(),
                settings.getMetadataMetrics(),
                toDTO(settings.getPing()),
                getRepositoryDTO()
        );
    }

    public SettingsPingDTO toDTO(SettingsPing settingsPing) {
        return new SettingsPingDTO(
                settingsPing.isEnabled(),
                settingsPing.getEndpoints(),
                pingProperties.getInterval().toString()
        );
    }

    public SettingsRepositoryDTO getRepositoryDTO() {
        return new SettingsRepositoryDTO(
                repositoryProperties.getStringType(),
                repositoryProperties.getDir(),
                repositoryProperties.getUrl(),
                repositoryProperties.getRepository(),
                repositoryProperties.getUsername(),
                repositoryProperties.getPassword() != null ? "<SECRET>" : null
        );
    }

    public Settings fromUpdateDTO(SettingsUpdateDTO dto, Settings settings) {
        return settings.toBuilder()
                .metadataMetrics(dto.getMetadataMetrics())
                .ping(fromUpdateDTO(dto.getPing(), settings.getPing()))
                .build();
    }

    public SettingsPing fromUpdateDTO(SettingsPingUpdateDTO dto, SettingsPing settingsPing) {
        return settingsPing.toBuilder()
                .enabled(dto.isEnabled())
                .endpoints(dto.getEndpoints())
                .build();
    }

    public SettingsUpdateDTO toUpdateDTO(Settings settings) {
        return new SettingsUpdateDTO(
                settings.getMetadataMetrics(),
                toUpdateDTO(settings.getPing())
        );
    }

    public SettingsPingUpdateDTO toUpdateDTO(SettingsPing settingsPing) {
        return new SettingsPingUpdateDTO(
                settingsPing.isEnabled(),
                settingsPing.getEndpoints()
        );
    }
}
