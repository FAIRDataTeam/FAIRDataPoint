/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.utils;

import org.fairdatapoint.entity.index.entry.IndexEntry;
import org.fairdatapoint.entity.index.entry.IndexEntryPermit;
import org.fairdatapoint.entity.index.entry.IndexEntryState;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestIndexEntryFixtures {

    private static IndexEntry newIndexEntry(String clientUrl, Instant timestamp) {
        IndexEntry indexEntry = new IndexEntry();
        indexEntry.setUuid(null);
        indexEntry.setClientUrl(clientUrl);
        indexEntry.setUpdatedAt(timestamp);
        indexEntry.setCreatedAt(timestamp);
        indexEntry.setLastRetrievalAt(timestamp);
        indexEntry.setState(IndexEntryState.INVALID);
        indexEntry.setPermit(IndexEntryPermit.ACCEPTED);
        return indexEntry;
    }

    private static IndexEntry newIndexEntry(String clientUrl, Instant timestamp,
                                            IndexEntryPermit permit) {
        IndexEntry indexEntry = newIndexEntry(clientUrl, timestamp);
        indexEntry.setPermit(permit);
        return indexEntry;
    }

    public static IndexEntry entryExample() {
        return newIndexEntry("http://example.com", Instant.now());
    }

    public static List<IndexEntry> entriesDefault() {
        final Instant ref = Instant.now();
        final IndexEntry entryActive1 = newIndexEntry("http://example.com/active1", ref.minus(Duration.ofMinutes(2)));
        entryActive1.setState(IndexEntryState.VALID);
        entryActive1.setLastRetrievalAt(ref.minus(Duration.ofMinutes(1)));
        final IndexEntry entryInactive1 = newIndexEntry("http://example.com/inactive1", ref.minus(Duration.ofDays(20)));
        entryInactive1.setState(IndexEntryState.VALID);
        entryInactive1.setLastRetrievalAt(ref.minus(Duration.ofDays(20)));
        final IndexEntry entryInactive2 = newIndexEntry("http://example.com/inactive2", ref.minus(Duration.ofDays(666)));
        entryInactive2.setState(IndexEntryState.VALID);
        entryInactive2.setLastRetrievalAt(ref.minus(Duration.ofDays(666)));
        final IndexEntry entryUnknown1 = newIndexEntry("http://example.com/unknown1", ref);
        entryUnknown1.setState(IndexEntryState.UNKNOWN);
        final IndexEntry entryInvalid1 = newIndexEntry("http://example.com/invalid1", ref);
        entryInvalid1.setState(IndexEntryState.INVALID);
        final IndexEntry entryUnreachable1 = newIndexEntry("http://example.com/unreachable1", ref);
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
                newIndexEntry("http://example.com", ref),
                newIndexEntry("http://test.com", ref.minusSeconds(1)),
                newIndexEntry("http://localhost", ref.minusSeconds(2))
        );
    }

    public static List<IndexEntry> entriesPermits() {
        Instant ref = Instant.now();
        return Arrays.asList(
                newIndexEntry("http://example.com/accepted", ref, IndexEntryPermit.ACCEPTED),
                newIndexEntry("http://example.com/rejected", ref, IndexEntryPermit.REJECTED),
                newIndexEntry("http://example.com/pending", ref, IndexEntryPermit.PENDING)
        );
    }

    public static List<IndexEntry> entriesN(long n) {
        ArrayList<IndexEntry> entries = new ArrayList<>();
        Instant ref = Instant.now();
        for (int i = 0; i < n; i++) {
            Instant entryTime = ref.minusSeconds(i);
            entries.add(newIndexEntry("http://example" + i + ".com", entryTime));
        }
        return entries;
    }
}
