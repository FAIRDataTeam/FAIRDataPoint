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
package org.fairdatateam.fairdatapoint.index.webhook;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// NOTE: none of the test methods here are transactional, and open-in-view is disabled, so both
// element collections have to be loaded by the query itself; reading them after the repository
// call is exactly what the webhook matching does, on another thread.
public class WebhookRepositoryTest extends BaseIntegrationTest {

    private static final String ENTRY_A = "https://a.example.com";

    private static final String ENTRY_B = "https://b.example.com";

    @Autowired
    private WebhookRepository webhookRepository;

    @BeforeEach
    public void setup() {
        webhookRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        // no endpoint creates webhooks, and the other tests trigger webhook events: a webhook
        // left behind here would have them posting to a URL that does not exist
        webhookRepository.deleteAll();
    }

    private Webhook newWebhook() {
        return Webhook
                .builder()
                .uuid(UUID.randomUUID())
                .payloadUrl("https://webhook.example.com/payload")
                .secret("top-secret")
                .allEvents(false)
                .events(Set.of(WebhookEvent.EntryValid, WebhookEvent.IncomingPing))
                .allEntries(false)
                .entries(Set.of(ENTRY_B, ENTRY_A))
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("A webhook round-trips with both of its collections, listed outside a transaction")
    public void webhookRoundTripsWithBothCollections() {
        // GIVEN: a webhook listening to two events for two entries
        final Webhook webhook = webhookRepository.save(newWebhook());

        // WHEN: every webhook is listed, as the matching does before triggering anything
        final List<Webhook> listed = webhookRepository.findAll();

        // THEN: the webhook is reported exactly once, despite the join over two collections
        assertThat(listed, hasSize(1));
        final Webhook reloaded = listed.getFirst();
        assertThat(reloaded.getUuid(), is(equalTo(webhook.getUuid())));

        // THEN: both collections are readable after the query, in a stable order: events in the
        // order the enum declares them, entries in alphabetical order
        assertThat(reloaded.getEvents(),
                contains(WebhookEvent.IncomingPing, WebhookEvent.EntryValid));
        assertThat(reloaded.getEntries(), contains(ENTRY_A, ENTRY_B));

        // THEN: the scalar columns and the audit timestamps came along
        assertThat(reloaded.getPayloadUrl(), is(equalTo("https://webhook.example.com/payload")));
        assertThat(reloaded.getSecret(), is(equalTo("top-secret")));
        assertThat(reloaded.isAllEvents(), is(equalTo(false)));
        assertThat(reloaded.isAllEntries(), is(equalTo(false)));
        assertThat(reloaded.isEnabled(), is(equalTo(true)));
        assertThat(reloaded.getCreatedAt(), is(notNullValue()));
        assertThat(reloaded.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    @DisplayName("Looking a webhook up by identifier loads both of its collections as well")
    public void findByUuidLoadsBothCollections() {
        // GIVEN: a stored webhook
        final Webhook webhook = webhookRepository.save(newWebhook());

        // WHEN: it is looked up by identifier, as the webhook ping endpoint does
        final Webhook reloaded = webhookRepository.findByUuid(webhook.getUuid()).orElseThrow();

        // THEN: both collections are readable outside the query's transaction
        assertThat(reloaded.getEvents(), hasSize(2));
        assertThat(reloaded.getEntries(), contains(ENTRY_A, ENTRY_B));

        // THEN: an unknown identifier is simply not found
        assertThat(webhookRepository.findByUuid(UUID.randomUUID()).isPresent(), is(equalTo(false)));
    }
}
