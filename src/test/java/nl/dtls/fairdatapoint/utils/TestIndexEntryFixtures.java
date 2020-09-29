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
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestIndexEntryFixtures {

    private static IndexEntry newIndexEntry(String uuid, String clientUrl, Instant timestamp) {
        IndexEntry indexEntry = new IndexEntry();
        indexEntry.setUuid(uuid);
        indexEntry.setClientUrl(clientUrl);
        indexEntry.setModificationTime(timestamp);
        indexEntry.setRegistrationTime(timestamp);
        indexEntry.setState(IndexEntryState.Invalid);
        return indexEntry;
    }

    public static IndexEntry entryExample() {
        return newIndexEntry("7663c0c2-2b9d-4787-968d-d284ff3fc5bd", "http://example.com", Instant.now());
    }

    public static List<IndexEntry> entriesFew() {
        Instant ref = Instant.now();
        return Arrays.asList(
                newIndexEntry("09200532-18b4-4721-86dd-fbfa13ec78c3", "http://example.com", ref),
                newIndexEntry("b6cfa934-dc67-4b88-b8f9-c63448c8272c", "http://test.com", ref.minusSeconds(1)),
                newIndexEntry("da9ddfb8-6fdb-41b1-889e-387c8cbafc39", "http://localhost", ref.minusSeconds(2))
        );
    }

    public static List<IndexEntry> entriesN(long n) {
        ArrayList<IndexEntry> entries = new ArrayList<>();
        Instant ref = Instant.now();
        for (int i = 0; i < n; i++) {
            Instant entryTime = ref.minusSeconds(i);
            entries.add(newIndexEntry(UUID.randomUUID().toString(), "http://example" + i + ".com",
                    entryTime));
        }
        return entries;
    }

}
