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
package nl.dtls.fairdatapoint.entity.resource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntity;

import java.util.List;

@Entity(name = "ResourceDefinition")
@Table(name = "resource_definition")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class ResourceDefinition extends BaseEntity {

    private static final String CATALOG_PREFIX = "catalog";

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "url_prefix", nullable = false)
    private String urlPrefix;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceDefinitionChild> children;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "target")
    private List<ResourceDefinitionChild> parents;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resourceDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceDefinitionLink> externalLinks;

    @OrderBy("orderPriority")
    @OneToMany(mappedBy = "resourceDefinition", fetch = FetchType.LAZY)
    private List<MetadataSchemaUsage> metadataSchemaUsages;

    public boolean isRoot() {
        return urlPrefix.isEmpty();
    }

    public boolean isCatalog() {
        return urlPrefix.equals(CATALOG_PREFIX);
    }
}
