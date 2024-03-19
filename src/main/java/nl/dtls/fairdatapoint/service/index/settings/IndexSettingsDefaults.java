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
package nl.dtls.fairdatapoint.service.index.settings;

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.config.properties.InstanceProperties;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettings;
import nl.dtls.fairdatapoint.entity.index.settings.SettingsIndexPing;
import nl.dtls.fairdatapoint.entity.index.settings.SettingsIndexRetrieval;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexSettingsDefaults {

    private final InstanceProperties instanceProperties;

    public IndexSettings getDefaults() {
        final IndexSettings indexSettings = new IndexSettings();
        indexSettings.setUuid(KnownUUIDs.SETTINGS_UUID);
        indexSettings.setPing(SettingsIndexPing.getDefault());
        indexSettings.setRetrieval(SettingsIndexRetrieval.getDefault());
        indexSettings.setAutoPermit(instanceProperties.isIndexAutoPermit());
        return indexSettings;
    }
}
