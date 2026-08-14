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
package org.fairdatateam.fairdatapoint.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class HttpClientConfig {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Bean
    public RestClient restClient(RestClient.Builder builder, ClientHttpRequestFactory clientHttpRequestFactory) {
        // The builder argument is required for autoconfiguration of MockRestServiceServer in tests
        // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
        // Even though spring autoconfig already picks up the custom HttpClientSettings bean by default,
        // we use a custom request factory to apply the settings explicitly.
        // https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient.customization
        return builder.requestFactory(clientHttpRequestFactory).build();
    }

    @Bean
    public HttpClientSettings httpClientSettings() {
        return HttpClientSettings.defaults()
                .withConnectTimeout(TIMEOUT)
                .withReadTimeout(TIMEOUT);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientSettings httpClientSettings) {
        // Apply the http client settings explicitly (even though spring autoconfig already takes care of this)
        return ClientHttpRequestFactoryBuilder.detect().build(httpClientSettings);
    }

}
