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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.api.validator.ValidIri;
import nl.dtls.fairdatapoint.entity.base.BaseEntity;

import java.util.List;


@Entity(name = "ResourceDefinitionChild")
@Table(name = "resource_definition_child")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ResourceDefinitionChild extends BaseEntity {

    @NotBlank
    @ValidIri
    @Column(name = "relation_uri", nullable = false)
    private String relationUri;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "tags_uri")
    private String tagsUri;

    @NotNull
    @Column(name = "order_priority", nullable = false)
    private Integer orderPriority;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "source_resource_definition_id", nullable = false)
    private ResourceDefinition source;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "target_resource_definition_id", nullable = false)
    private ResourceDefinition target;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    private List<ResourceDefinitionChildMetadata> metadata;
}
