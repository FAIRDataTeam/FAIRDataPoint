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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.metadata.metric;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.entity.metadata.Metric;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.service.settings.SettingsService;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
@Service
public class MetricsMetadataService {

    @Autowired
    private SettingsService settingsService;

    public List<Metric> generateMetrics(@Nonnull IRI metadataURI) {
        Settings settings = settingsService.getOrDefaults();
        return settings.getMetadataMetrics().stream()
                .map(entry ->
                        new Metric(
                                i(format("%s/metrics/%s", metadataURI.toString(), DigestUtils.md5Hex(entry.getMetricUri()))),
                                i(entry.getResourceUri()),
                                i(entry.getResourceUri())))
                .collect(Collectors.toList());
    }

}
