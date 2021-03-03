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

import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsDTO;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsPingDTO;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsRetrievalDTO;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsUpdateDTO;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettings;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettingsPing;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettingsRetrieval;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IndexSettingsMapper {

    private IndexSettingsPingDTO toPingDTO(IndexSettingsPing indexSettingsPing) {
        return new IndexSettingsPingDTO(
                indexSettingsPing.getValidDuration().toString(),
                indexSettingsPing.getRateLimitDuration().toString(),
                indexSettingsPing.getRateLimitHits(),
                indexSettingsPing.getDenyList()
        );
    }

    private IndexSettingsRetrievalDTO toRetrievalDTO(IndexSettingsRetrieval indexSettingsRetrieval) {
        return new IndexSettingsRetrievalDTO(
                indexSettingsRetrieval.getRateLimitWait().toString(),
                indexSettingsRetrieval.getTimeout().toString()
        );
    }

    public IndexSettingsDTO toDTO(IndexSettings indexSettings) {
        return new IndexSettingsDTO(
                toRetrievalDTO(indexSettings.getRetrieval()),
                toPingDTO(indexSettings.getPing()),
                indexSettings.equals(IndexSettings.getDefault())
        );
    }

    public IndexSettingsUpdateDTO toUpdateDTO(IndexSettings indexSettings) {
        return new IndexSettingsUpdateDTO(
                toRetrievalDTO(indexSettings.getRetrieval()),
                toPingDTO(indexSettings.getPing())
        );
    }

    private IndexSettingsPing fromDTO(IndexSettingsPingDTO dto, IndexSettingsPing ping) {
        return
                ping
                        .toBuilder()
                        .validDuration(Duration.parse(dto.getValidDuration()))
                        .rateLimitDuration(Duration.parse(dto.getRateLimitDuration()))
                        .rateLimitHits(dto.getRateLimitHits())
                        .denyList(dto.getDenyList())
                        .build();
    }

    private IndexSettingsRetrieval fromDTO(IndexSettingsRetrievalDTO dto, IndexSettingsRetrieval retrieval) {
        return
                retrieval
                        .toBuilder()
                        .rateLimitWait(Duration.parse(dto.getRateLimitWait()))
                        .timeout(Duration.parse(dto.getTimeout()))
                        .build();
    }

    public IndexSettings fromUpdateDTO(IndexSettingsUpdateDTO dto, IndexSettings indexSettings) {
        return
                indexSettings
                        .toBuilder()
                        .ping(fromDTO(dto.getPing(), indexSettings.getPing()))
                        .retrieval(fromDTO(dto.getRetrieval(), indexSettings.getRetrieval()))
                        .build();
    }
}
