/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.index;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.fairdatateam.fairdatapoint.common.persistence.StringMapJsonConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * What the Index knows about the FAIR Data Point behind an entry, as of the last retrieval.
 *
 * <p>Stored in the columns of the entry itself rather than in a table of its own: an entry has
 * exactly one of these, and it is replaced as a whole every time the metadata is retrieved. The
 * column names are given by the entry's {@code @AttributeOverrides}, since an embeddable only
 * knows the names of its own fields. The harvested properties are whatever the remote instance
 * describes itself with, so they stay a map, kept as a JSON object in a single column.</p>
 *
 * <p>Also serialized as-is into the JSON payload of a metadata retrieval event and into the
 * detail representation of an entry, which is why it is a plain bean with public accessors.</p>
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RepositoryMetadata {

    public static final Integer CURRENT_VERSION = 1;

    private Integer metadataVersion = CURRENT_VERSION;

    private String repositoryUri;

    @NotNull
    @Convert(converter = StringMapJsonConverter.class)
    private Map<String, String> metadata = new HashMap<>();

}
