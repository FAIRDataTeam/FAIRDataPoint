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

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntity;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "IndexEntry")
@Table(name = "index_entry")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class IndexEntry extends BaseEntity {

    public static final Integer CURRENT_VERSION = 1;

    @NotNull
    @Column(name = "client_url", nullable = false)
    private String clientUrl;

    @Column(name = "repository_uri")
    private String repositoryUri;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "state", columnDefinition = "INDEX_ENTRY_STATE", nullable = false)
    private IndexEntryState state = IndexEntryState.UNKNOWN;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "permit", columnDefinition = "INDEX_ENTRY_PERMIT", nullable = false)
    private IndexEntryPermit permit = IndexEntryPermit.PENDING;
    
    @NotNull
    @Column(name = "metadata_version", nullable = false)
    private Integer metadataVersion = CURRENT_VERSION;

    @Column(name = "last_retrieval_at")
    private Instant lastRetrievalAt;

    @NotNull
    @Type(JsonBinaryType.class)
    @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> metadata = new HashMap<>();

    public Duration getLastRetrievalAgo() {
        if (getLastRetrievalAt() == null) {
            return null;
        }
        return Duration.between(getLastRetrievalAt(), Instant.now());
    }

    public void setCurrentMetadata(RepositoryMetadata repositoryMetadata) {
        setRepositoryUri(repositoryMetadata.getRepositoryUri());
        setMetadataVersion(repositoryMetadata.getMetadataVersion());
        setMetadata(repositoryMetadata.getMetadata());
    }
}
