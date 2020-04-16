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

import nl.dtls.fairdatapoint.service.pid.DefaultPIDSystemImpl;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.pid.PurlPIDSystemImpl;
import nl.dtls.fairmetadata4j.model.Agent;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static nl.dtls.fairdatapoint.util.HttpUtil.removeLastSlash;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.l;

@Configuration
public class MetadataConfig {

    @Bean(name = "publisher")
    public Agent publisher(@Value("${metadataProperties.publisherURI:}") String publisherURI,
                           @Value("${metadataProperties.publisherName:}") String publishername) {

        Agent publisher = null;
        if (!publisherURI.isEmpty() && !publishername.isEmpty()) {
            publisher = new Agent();
            publisher.setUri(i(publisherURI));
            publisher.setName(l(publishername));
        }
        return publisher;
    }

    @Bean(name = "language")
    public IRI language(@Value("${metadataProperties.language:}") String languageURI) {

        IRI language = null;
        if (!languageURI.isEmpty()) {
            language = i(languageURI);
        }
        return language;
    }

    @Bean(name = "license")
    public IRI license(@Value("${metadataProperties.license:}") String licenseURI) {

        IRI license = null;
        if (!licenseURI.isEmpty()) {
            license = i(licenseURI);
        }
        return license;
    }

    @Bean
    public PIDSystem pidSystem(@Value("${pidSystem.type:1}") int pidSystemtype,
                               @Value("${instance.url}") String instanceUrl,
                               @Qualifier("purlBaseUrl") IRI purlBaseUrl) {
        if (pidSystemtype == 2) {
            return new PurlPIDSystemImpl(instanceUrl, purlBaseUrl);
        } else {
            return new DefaultPIDSystemImpl();
        }
    }

    @Bean
    public IRI purlBaseUrl(@Value("${pidSystem.purl.baseUrl:}") String url) {
        return i(removeLastSlash(url));
    }

}
