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
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class Common {

    public static final UUID SCHEMA_SIMPLE_UUID = UUID.fromString("e8b34158-3858-45c7-8e3e-d1e671dd9929");
    public static final UUID SCHEMA_SIMPLE_V1_UUID = UUID.fromString("53619e58-2bb0-4baf-afd8-00c5d01ff8a8");

    public static final UUID SCHEMA_INTERNAL_UUID = UUID.fromString("fe98adbb-6a2c-4c7a-b2b2-a72db5140c61");
    public static final UUID SCHEMA_INTERNAL_V1_UUID = UUID.fromString("f0a4b358-69a3-44e6-9436-c68a56a9f2f2");

    public static final UUID SCHEMA_DRAFT_UUID = UUID.fromString("bfa79edf-00b7-4a04-b5a6-a5144f1a77b7");
    public static final UUID SCHEMA_DRAFT_V1_UUID = UUID.fromString("cb9f6cd7-97af-45d0-b23d-d0aab23607d8");

    public static final UUID SCHEMA_MULTI_UUID = UUID.fromString("978e5c1c-268d-4822-b60b-07d3eccc6896");
    public static final UUID SCHEMA_MULTI_V1_UUID = UUID.fromString("d7acec53-5ac9-4502-9bfa-92d1e9f79a24");
    public static final UUID SCHEMA_MULTI_V2_UUID = UUID.fromString("67896adc-b431-431d-8296-f0b80d8de412");
    public static final UUID SCHEMA_MULTI_V3_UUID = UUID.fromString("c62d4a97-baac-40b8-b6ea-e43b06ec78bd");

    public static final UUID SCHEMA_MULTI_DRAFT_UUID = UUID.fromString("123e48d2-9995-4b44-8b2c-9c81bdbf2dd2");
    public static final UUID SCHEMA_MULTI_DRAFT_V1_UUID = UUID.fromString("fb24f92b-187f-4d53-b744-73024b537f30");
    public static final UUID SCHEMA_MULTI_DRAFT_V2_UUID = UUID.fromString("6011adfa-f8da-478d-86ea-84bb644b458b");
    public static final UUID SCHEMA_MULTI_DRAFT_DRAFT_UUID = UUID.fromString("6b84ec86-2096-48db-bfc7-23506b8c080c");

    public static final UUID SCHEMA_MULTI_EXTS_UUID = UUID.fromString("7c8b8699-ca9f-4d14-86e2-2299b27c5711");
    public static final UUID SCHEMA_MULTI_EXTS_V1_UUID = UUID.fromString("4e44fb19-b9e0-46e9-957a-e7aa3adac7bf");
    public static final UUID SCHEMA_MULTI_EXTS_V2_UUID = UUID.fromString("abcf3a21-6f9a-45dc-a71a-4dde4440c81a");
    public static final UUID SCHEMA_MULTI_EXTS_DRAFT_UUID = UUID.fromString("a6d609ff-905f-4edd-bdb1-2dce000c9a45");

    public static void compare(MetadataSchemaChangeDTO entity, MetadataSchemaDTO dto) {
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getLatest().getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchemaChangeDTO entity, MetadataSchemaDraftDTO dto) {
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchemaVersion entity, MetadataSchemaDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getSchema().getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
    }

    public static void compare(MetadataSchemaVersion entity, MetadataSchemaVersionDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getSchema().getUuid())));
        assertThat(dto.getVersionUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchemaVersion entity, MetadataSchemaDraftDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getName(), is(equalTo(entity.getName())));
        assertThat(dto.getDefinition(), is(equalTo(entity.getDefinition())));
    }

    public static void compare(MetadataSchema metadataSchema, MetadataSchemaDTO body) {
        // TODO
    }
}
