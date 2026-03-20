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
package nl.dtls.fairdatapoint.acceptance.schema;

import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDraftDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class Common {

    public static void compare(MetadataSchemaChangeDTO entity, MetadataSchemaDTO dto) {
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getLatest().getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchemaChangeDTO entity, MetadataSchemaDraftDTO dto) {
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchema entity, MetadataSchemaDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getLatest().getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchemaDraft entity, MetadataSchemaDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDraft().getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchema entity, MetadataSchemaVersionDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchemaDraft entity, MetadataSchemaDraftDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDefinition(), is(equalTo(entity.getDefinition())));
    }
}
