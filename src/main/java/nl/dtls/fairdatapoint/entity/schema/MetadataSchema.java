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

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class MetadataSchema {

    @Id
    private ObjectId id;

    @Indexed
    private String uuid;

    @Indexed
    private String versionUuid;

    @Indexed
    private String versionString;

    @NotNull
    @Transient
    private SemVer version;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String definition;

    @NotNull
    private Set<String> targetClasses = new HashSet<>();

    @NotNull
    private List<String> extendSchemas = new ArrayList<>();

    @NotNull
    private MetadataSchemaType type;

    private String origin;

    private String importedFrom;

    private boolean latest;

    private boolean published;

    private boolean abstractSchema;

    private String suggestedResourceName;

    private String suggestedUrlPrefix;

    private Instant createdAt;

    private String previousVersionUuid;

    @PersistenceConstructor
    public MetadataSchema(
            ObjectId id, String uuid, String versionUuid, String versionString,
            String name, String description, String definition, Set<String> targetClasses,
            List<String> extendSchemas, MetadataSchemaType type, String origin,
            String importedFrom, boolean latest, boolean published, boolean abstractSchema,
            String suggestedResourceName, String suggestedUrlPrefix, Instant createdAt,
            String previousVersionUuid
    ) {
        this.id = id;
        this.uuid = uuid;
        this.versionUuid = versionUuid;
        this.versionString = versionString;
        this.name = name;
        this.description = description;
        this.definition = definition;
        this.targetClasses = targetClasses;
        this.extendSchemas = extendSchemas;
        this.type = type;
        this.origin = origin;
        this.importedFrom = importedFrom;
        this.latest = latest;
        this.published = published;
        this.abstractSchema = abstractSchema;
        this.suggestedResourceName = suggestedResourceName;
        this.suggestedUrlPrefix = suggestedUrlPrefix;
        this.createdAt = createdAt;
        this.previousVersionUuid = previousVersionUuid;
    }

    public void setVersionString(String versionString) {
        this.versionString = versionString;
        this.version = new SemVer(versionString);
    }

    public void setVersion(SemVer version) {
        this.version = version;
        this.versionString = version.toString();
    }

    public SemVer getVersion() {
        if (this.version == null) {
            this.version = new SemVer(this.versionString);
        }
        return version;
    }
}
