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
package nl.dtls.fairdatapoint.entity.schema;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntityCustomUUID;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity(name = "MetadataSchemaVersion")
@Table(name = "metadata_schema_version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class MetadataSchemaVersion extends BaseEntityCustomUUID {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "metadata_schema_id", nullable = false)
    private MetadataSchema schema;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version_id", nullable = false)
    private MetadataSchemaVersion previousVersion;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "previousVersion")
    private MetadataSchemaVersion nextVersion;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @NotNull
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "definition", nullable = false)
    private String definition;

    @NotNull
    @Type(ListArrayType.class)
    @Column(name = "target_classes", columnDefinition = "text[]", nullable = false)
    private List<String> targetClasses;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", columnDefinition = "METADATA_SCHEMA_TYPE", nullable = false)
    private MetadataSchemaType type;

    @Column(name = "origin")
    private String origin;

    @Column(name = "imported_from")
    private String importedFrom;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "state", columnDefinition = "METADATA_SCHEMA_STATE", nullable = false)
    private MetadataSchemaState state;

    @NotNull
    @Column(name = "published", nullable = false)
    private boolean published;

    @NotNull
    @Column(name = "abstract", nullable = false)
    private boolean abstractSchema;

    @Column(name = "suggested_resource_name")
    private String suggestedResourceName;

    @Column(name = "suggested_url_prefix")
    private String suggestedUrlPrefix;

    @OneToMany(mappedBy = "metadataSchemaVersion", fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetadataSchemaExtension> extensions;

    public boolean isDraft() {
        return getState().equals(MetadataSchemaState.DRAFT);
    }

    public boolean isLatest() {
        return getState().equals(MetadataSchemaState.LATEST);
    }

    public Set<String> getTargetClassesSet() {
        return new HashSet<>(getTargetClasses());
    }

    public void setTargetClassesSet(Set<String> targetClassesSet) {
        setTargetClasses(targetClassesSet.stream().toList());
    }

    public UUID extractSchemaUuid() {
        return getSchema().getUuid();
    }

    public SemVer getSemVer() {
        return new SemVer(getVersion());
    }
}
