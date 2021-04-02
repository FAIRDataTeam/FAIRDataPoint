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
package nl.dtls.fairdatapoint.service.ping;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
@ConditionalOnProperty(name = "ping.enabled", havingValue = "true", matchIfMissing = true)
public class PingService {

    @Value("${instance.clientUrl}")
    private String clientUrl;

    @Value("#{'${ping.endpoint:https://home.fairdatapoint.org}'.split('[, ]')}")
    private List<String> endpoints;

    @Autowired
    private RestTemplate client;

    @Scheduled(initialDelayString = "${ping.initDelay:#{10*1000}}", fixedRateString = "${ping.interval:#{7*24*60*60*1000}}")
    public void ping() {
        var request = Map.of("clientUrl", clientUrl);
        for (String endpoint : endpoints) {
            pingEndpoint(endpoint.trim(), request);
        }
    }

    @Async
    void pingEndpoint(String endpoint, Map<String, String> request) {
        try {
            log.info("Pinging {}", endpoint);
            client.postForEntity(endpoint, request, String.class);
        } catch (Exception e) {
            log.warn("Failed to ping {}: {}", endpoint, e.getMessage());
        }
    }
}
