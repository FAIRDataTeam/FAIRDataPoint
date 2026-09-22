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
package org.fairdatateam.fairdatapoint.index.event;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntry;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryPermit;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryRepository;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryState;
import org.fairdatateam.fairdatapoint.index.http.Exchange;
import org.fairdatateam.fairdatapoint.index.http.ExchangeDirection;
import org.fairdatateam.fairdatapoint.index.http.ExchangeState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// NOTE: none of the test methods here are transactional, and open-in-view is disabled, so an
// event has to bring the entry it is about along with it; that is what the asynchronous triggers
// and the resume of unfinished events rely on as well.
public class EventRepositoryTest extends BaseIntegrationTest {

    private static final String REMOTE_ADDR = "198.51.100.17";

    private static final String OTHER_REMOTE_ADDR = "198.51.100.18";

    private static final String CLIENT_URL = "https://pinging.example.com";

    private static final Integer VERSION = 1;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    @BeforeEach
    public void setup() {
        // Not deleteAll(): the self-referencing triggered_by foreign key on index_event has no
        // ON DELETE action, so deleting row by row can fail when a parent is removed before its
        // child.
        eventRepository.deleteAllInBatch();
        indexEntryRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAllInBatch();
        indexEntryRepository.deleteAll();
    }

    private IndexEntry newEntry() {
        final IndexEntry entry = new IndexEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setClientUrl(CLIENT_URL);
        entry.setState(IndexEntryState.Valid);
        entry.setPermit(IndexEntryPermit.ACCEPTED);
        entry.setRegistrationTime(Instant.now());
        entry.setModificationTime(entry.getRegistrationTime());
        return entry;
    }

    private Event newIncomingPing(String remoteAddr) {
        final IncomingPing incomingPing = new IncomingPing();
        final Exchange exchange = new Exchange(ExchangeDirection.INCOMING, remoteAddr);
        exchange.setState(ExchangeState.Retrieved);
        exchange.getRequest().setMethod("POST");
        exchange.getRequest().setUrl("/");
        exchange.getRequest().setHeaders(Map.of(
                "Accept", List.of("application/json"),
                "X-Forwarded-For", List.of(remoteAddr, OTHER_REMOTE_ADDR)
        ));
        exchange.getRequest().setBody("{\"clientUrl\":\"" + CLIENT_URL + "\"}");
        incomingPing.setExchange(exchange);
        incomingPing.setNewEntry(true);
        return new Event(VERSION, incomingPing);
    }

    private Event newAdminTrigger(String remoteAddr) {
        return new Event(VERSION, new AdminTrigger(remoteAddr, "a-token", CLIENT_URL));
    }

    @Test
    @DisplayName("An event payload survives a round trip through its JSON column")
    public void payloadRoundTrips() {
        // GIVEN: an incoming ping recording a whole HTTP exchange
        final Event event = newIncomingPing(REMOTE_ADDR);
        event.finish();
        eventRepository.save(event);

        // WHEN: the event is read back
        final Event reloaded = eventRepository.findById(event.getUuid()).orElseThrow();

        // THEN: the branch of the payload that was filled came back, nested headers included
        assertThat(reloaded.getType(), is(equalTo(EventType.IncomingPing)));
        assertThat(reloaded.getIncomingPing().getNewEntry(), is(equalTo(true)));
        final Exchange exchange = reloaded.getIncomingPing().getExchange();
        assertThat(exchange.getDirection(), is(equalTo(ExchangeDirection.INCOMING)));
        assertThat(exchange.getState(), is(equalTo(ExchangeState.Retrieved)));
        assertThat(exchange.getRemoteAddr(), is(equalTo(REMOTE_ADDR)));
        assertThat(exchange.getRequest().getHeaders().get("X-Forwarded-For"),
                contains(REMOTE_ADDR, OTHER_REMOTE_ADDR));
        assertThat(exchange.getRequest().getMethod(), is(equalTo("POST")));

        // THEN: the branches that were not filled stay empty
        assertThat(reloaded.getAdminTrigger(), is(equalTo(null)));
        assertThat(reloaded.getMetadataRetrieval(), is(equalTo(null)));

        // THEN: the timestamps are stored, the bookkeeping one filled by the mapping
        assertThat(reloaded.getCreated(), is(notNullValue()));
        assertThat(reloaded.getFinished(), is(notNullValue()));
        assertThat(reloaded.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    @DisplayName("Recent incoming pings of a caller are counted by the address copied out of the payload")
    public void incomingPingsAreCountedByRemoteAddress() {
        // GIVEN: two pings from one caller, one from another, and an admin trigger from the
        // first caller's address
        eventRepository.save(newIncomingPing(REMOTE_ADDR));
        eventRepository.save(newIncomingPing(REMOTE_ADDR));
        eventRepository.save(newIncomingPing(OTHER_REMOTE_ADDR));
        eventRepository.save(newAdminTrigger(REMOTE_ADDR));

        // WHEN: the rate limit asks how many pings that caller sent in the last hour
        final long recent = eventRepository.countByTypeAndRemoteAddrAndCreatedAfter(
                EventType.IncomingPing, REMOTE_ADDR, Instant.now().minusSeconds(3600));

        // THEN: only that caller's pings are counted, the admin trigger left out
        assertThat(recent, is(equalTo(2L)));

        // THEN: and nothing at all from after the events were created
        assertThat(eventRepository.countByTypeAndRemoteAddrAndCreatedAfter(
                EventType.IncomingPing, REMOTE_ADDR, Instant.now().plusSeconds(3600)), is(equalTo(0L)));
    }

    @Test
    @DisplayName("Deleting an entry takes the events about it with it")
    public void deletingAnEntryCascadesToItsEvents() {
        // GIVEN: an entry with an event about it
        final IndexEntry entry = indexEntryRepository.save(newEntry());
        final Event event = newIncomingPing(REMOTE_ADDR);
        event.setRelatedTo(entry);
        eventRepository.save(event);

        // AND: the event brings its entry along, outside any transaction
        final Event reloaded = eventRepository.findById(event.getUuid()).orElseThrow();
        assertThat(reloaded.getRelatedTo().getClientUrl(), is(equalTo(CLIENT_URL)));

        // WHEN: the entry is deleted
        indexEntryRepository.delete(entry);

        // THEN: the database removed the event as well, without the application asking
        assertThat(eventRepository.findById(event.getUuid()).isPresent(), is(equalTo(false)));
    }
}
