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
package nl.dtls.fairdatapoint.entity.index.entry;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.Instant;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class IndexEntry {

    @Id
    private ObjectId id;

    private String uuid;

    private String clientUrl;

    private IndexEntryState state = IndexEntryState.Unknown;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant registrationTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant modificationTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastRetrievalTime;

    private RepositoryMetadata currentMetadata;

    public IndexEntry(String uuid, String clientUrl, IndexEntryState state, Instant registrationTime,
                      Instant modificationTime, Instant lastRetrievalTime, RepositoryMetadata currentMetadata) {
        this.uuid = uuid;
        this.clientUrl = clientUrl;
        this.state = state;
        this.registrationTime = registrationTime;
        this.modificationTime = modificationTime;
        this.lastRetrievalTime = lastRetrievalTime;
        this.currentMetadata = currentMetadata;
    }

    public Duration getLastRetrievalAgo() {
        if (lastRetrievalTime == null) {
            return null;
        }
        return Duration.between(lastRetrievalTime, Instant.now());
    }
}
