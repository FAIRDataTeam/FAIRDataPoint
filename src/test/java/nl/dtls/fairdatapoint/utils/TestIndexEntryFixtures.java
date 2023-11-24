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
package nl.dtls.fairdatapoint.utils;

import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryPermit;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestIndexEntryFixtures {

    private static IndexEntry newIndexEntry(UUID uuid, String clientUrl, Instant timestamp) {
        IndexEntry indexEntry = new IndexEntry();
        indexEntry.setUuid(uuid);
        indexEntry.setClientUrl(clientUrl);
        indexEntry.setUpdatedAt(timestamp);
        indexEntry.setCreatedAt(timestamp);
        indexEntry.setState(IndexEntryState.INVALID);
        indexEntry.setPermit(IndexEntryPermit.ACCEPTED);
        return indexEntry;
    }

    private static IndexEntry newIndexEntry(UUID uuid, String clientUrl, Instant timestamp,
                                            IndexEntryPermit permit) {
        IndexEntry indexEntry = newIndexEntry(uuid, clientUrl, timestamp);
        indexEntry.setPermit(permit);
        return indexEntry;
    }

    public static IndexEntry entryExample() {
        return newIndexEntry(UUID.fromString("7663c0c2-2b9d-4787-968d-d284ff3fc5bd"), "http://example.com", Instant.now());
    }

    public static List<IndexEntry> entriesDefault() {
        final Instant ref = Instant.now();
        final IndexEntry entryActive1 = newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c0"), "http://example.com/active1", ref.minus(Duration.ofMinutes(2)));
        entryActive1.setState(IndexEntryState.VALID);
        entryActive1.setLastRetrievalAt(ref.minus(Duration.ofMinutes(1)));
        final IndexEntry entryInactive1 = newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c1"), "http://example.com/inactive1", ref.minus(Duration.ofDays(20)));
        entryInactive1.setState(IndexEntryState.VALID);
        entryInactive1.setLastRetrievalAt(ref.minus(Duration.ofDays(20)));
        final IndexEntry entryInactive2 = newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c2"), "http://example.com/inactive2", ref.minus(Duration.ofDays(666)));
        entryInactive2.setState(IndexEntryState.VALID);
        entryInactive2.setLastRetrievalAt(ref.minus(Duration.ofDays(666)));
        final IndexEntry entryUnknown1 = newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c3"), "http://example.com/unknown1", ref);
        entryUnknown1.setState(IndexEntryState.UNKNOWN);
        final IndexEntry entryInvalid1 = newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c4"), "http://example.com/invalid1", ref);
        entryInvalid1.setState(IndexEntryState.INVALID);
        final IndexEntry entryUnreachable1 = newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c5"), "http://example.com/unreachable1", ref);
        entryUnreachable1.setState(IndexEntryState.UNREACHABLE);
        return Arrays.asList(
                entryActive1,
                entryInactive1,
                entryInactive2,
                entryUnknown1,
                entryInvalid1,
                entryUnreachable1
        );
    }

    public static List<IndexEntry> entriesFew() {
        final Instant ref = Instant.now();
        return Arrays.asList(
                newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c3"), "http://example.com", ref),
                newIndexEntry(UUID.fromString("b6cfa934-dc67-4b88-b8f9-c63448c8272c"), "http://test.com", ref.minusSeconds(1)),
                newIndexEntry(UUID.fromString("da9ddfb8-6fdb-41b1-889e-387c8cbafc39"), "http://localhost", ref.minusSeconds(2))
        );
    }

    public static List<IndexEntry> entriesPermits() {
        Instant ref = Instant.now();
        return Arrays.asList(
                newIndexEntry(UUID.fromString("09200532-18b4-4721-86dd-fbfa13ec78c3"), "http://example.com/accepted", ref, IndexEntryPermit.ACCEPTED),
                newIndexEntry(UUID.fromString("b6cfa934-dc67-4b88-b8f9-c63448c8272c"), "http://example.com/rejected", ref, IndexEntryPermit.REJECTED),
                newIndexEntry(UUID.fromString("da9ddfb8-6fdb-41b1-889e-387c8cbafc39"), "http://example.com/pending", ref, IndexEntryPermit.PENDING)
        );
    }

    public static List<IndexEntry> entriesN(long n) {
        ArrayList<IndexEntry> entries = new ArrayList<>();
        Instant ref = Instant.now();
        for (int i = 0; i < n; i++) {
            Instant entryTime = ref.minusSeconds(i);
            entries.add(newIndexEntry(UUID.randomUUID(), "http://example" + i + ".com",
                    entryTime));
        }
        return entries;
    }
}
