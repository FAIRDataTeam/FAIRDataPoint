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
package nl.dtls.fairdatapoint.service.index.webhook;

import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.entity.index.http.Exchange;
import nl.dtls.fairdatapoint.entity.index.http.ExchangeDirection;
import nl.dtls.fairdatapoint.entity.index.http.ExchangeState;
import nl.dtls.fairdatapoint.entity.index.webhook.Webhook;
import nl.dtls.fairdatapoint.entity.index.webhook.WebhookEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

public class WebhookUtils {

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private static boolean webhookMatches(Webhook webhook, WebhookEvent webhookEvent, Event triggerEvent) {
        boolean matchEvent = webhook.isAllEvents() || webhook.getEvents().contains(webhookEvent);
        boolean matchEntry =
                webhook.isAllEntries() || triggerEvent.getRelatedTo() == null || webhook.getEntries().contains(triggerEvent.getRelatedTo().getClientUrl());
        return matchEvent && matchEntry && webhook.isEnabled();
    }

    public static Stream<Webhook> filterMatching(List<Webhook> webhooks, WebhookEvent webhookEvent,
                                                 Event triggerEvent) {
        return webhooks.parallelStream().filter(webhook -> WebhookUtils.webhookMatches(webhook, webhookEvent,
                triggerEvent));
    }

    public static String computeHashSignature(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(value.getBytes(StandardCharsets.UTF_8));
        return String.format("sha1=%040x", new BigInteger(1, digest.digest()));
    }

    public static void postWebhook(Event event, Duration timeout, String payload, String signature) {
        var ex = new Exchange(ExchangeDirection.OUTGOING);
        event.getWebhookTrigger().setExchange(ex);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(event.getWebhookTrigger().getWebhook().getPayloadUrl()))
                    .timeout(timeout)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                    .header("X-Signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            ex.getRequest().setFromHttpRequest(request);
            ex.setState(ExchangeState.Requested);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ex.getResponse().setFromHttpResponse(response);
            ex.setState(ExchangeState.Retrieved);
        } catch (InterruptedException e) {
            ex.setState(ExchangeState.Timeout);
            ex.setError("Timeout");
        } catch (IllegalArgumentException e) {
            ex.setState(ExchangeState.Failed);
            ex.setError("Invalid URI: " + e.getMessage());
        } catch (IOException e) {
            ex.setState(ExchangeState.Failed);
            ex.setError("IO error: " + e.getMessage());
        }
    }
}
