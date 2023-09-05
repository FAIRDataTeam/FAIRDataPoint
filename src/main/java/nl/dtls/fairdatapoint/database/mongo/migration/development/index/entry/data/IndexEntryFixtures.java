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
package nl.dtls.fairdatapoint.database.mongo.migration.development.index.entry.data;

import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryPermit;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;
import nl.dtls.fairdatapoint.entity.index.entry.RepositoryMetadata;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;

@Service
public class IndexEntryFixtures {

    public IndexEntry entryActive() {
        final String clientUri = "https://example.com/my-valid-fairdatapoint1";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        return IndexEntry.builder()
                .uuid("8987abc1-15a4-4752-903c-8f8a5882cca6")
                .clientUrl(clientUri)
                .state(IndexEntryState.Valid)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .lastRetrievalTime(Instant.now())
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryActive2() {
        final String clientUri = "https://app.fairdatapoint.org";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        final Instant date = Instant.parse("2020-05-30T23:38:31.085Z");
        return IndexEntry.builder()
                .uuid("c912331f-4a77-4300-a469-dbaf5fc0b4e2")
                .clientUrl(clientUri)
                .state(IndexEntryState.Valid)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(date)
                .modificationTime(date)
                .lastRetrievalTime(date)
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryInactive() {
        final String clientUri = "https://example.com/my-valid-fairdatapoint2";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        final Instant date = Instant.parse("2020-05-30T23:38:23.085Z");
        return IndexEntry.builder()
                .uuid("b5851ebe-aacf-4de9-bf0a-3686e9256e73")
                .clientUrl(clientUri)
                .state(IndexEntryState.Valid)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(date)
                .modificationTime(date)
                .lastRetrievalTime(date)
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryUnreachable() {
        final String clientUri = "https://example.com/my-unreachable-fairdatapoint";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        return IndexEntry.builder()
                .uuid("dae46b47-87fb-4fdf-995c-8aa3739a27fc")
                .clientUrl(clientUri)
                .state(IndexEntryState.Unreachable)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .lastRetrievalTime(Instant.now())
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryInvalid() {
        final String clientUri = "https://example.com/my-invalid-fairdatapoint";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        return IndexEntry.builder()
                .uuid("b37e8c1f-ac0e-49f8-8e07-35571c4f8235")
                .clientUrl(clientUri)
                .state(IndexEntryState.Invalid)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .lastRetrievalTime(Instant.now())
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryUnknown() {
        final String clientUri = "https://example.com/my-unknown-fairdatapoint";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        return IndexEntry.builder()
                .uuid("4471d7c5-8c5b-4581-a9bc-d175456492c4")
                .clientUrl(clientUri)
                .state(IndexEntryState.Unknown)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .lastRetrievalTime(Instant.now())
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryRejected() {
        final String clientUri = "https://example.com/valid-rejected-fairdatapoint";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        return IndexEntry.builder()
                .uuid("4471d7c5-8c5b-4581-a9bc-d175456492c5")
                .clientUrl(clientUri)
                .state(IndexEntryState.Valid)
                .permit(IndexEntryPermit.REJECTED)
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .lastRetrievalTime(Instant.now())
                .currentMetadata(repositoryData)
                .build();
    }

    public IndexEntry entryPending() {
        final String clientUri = "https://example.com/valid-pending-fairdatapoint";
        final RepositoryMetadata repositoryData = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                clientUri,
                new HashMap<>()
        );
        return IndexEntry.builder()
                .uuid("4471d7c5-8c5b-4581-a9bc-d175456492c6")
                .clientUrl(clientUri)
                .state(IndexEntryState.Valid)
                .permit(IndexEntryPermit.PENDING)
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .lastRetrievalTime(Instant.now())
                .currentMetadata(repositoryData)
                .build();
    }

}
