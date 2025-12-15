/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.service.schema;

import org.fairdatapoint.Profiles;
import org.fairdatapoint.acceptance.schema.Common;
import org.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import org.fairdatapoint.api.dto.schema.MetadataSchemaDraftDTO;
import org.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import org.fairdatapoint.entity.schema.MetadataSchemaVersion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles(Profiles.TESTING)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class MetadataSchemaServiceTests {
    @Autowired
    private MetadataSchemaService service;

    @Autowired
    private MetadataSchemaVersionRepository versionRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testChangeExistingSchema() throws Exception {
        // reproduces #660

        // GIVEN: existing schema version (without existing draft) and changes to be made
        UUID schemaUuid = Common.SCHEMA_SIMPLE_UUID;
        MetadataSchemaChangeDTO changeDTO = MetadataSchemaChangeDTO.builder()
                .abstractSchema(false)
                .definition("")
                .description("")
                .extendsSchemaUuids(List.of())
                .name("test")
                .suggestedResourceName(null)
                .suggestedUrlPrefix(null)
                .build();

        // WHEN: changes are applied to existing schema
        MetadataSchemaDraftDTO updatedDraft = service.updateSchemaDraft(schemaUuid, changeDTO).orElseThrow();

        // THEN: draft schema is created with updated properties
        MetadataSchemaVersion draftVersion = versionRepository.getDraftBySchemaUuid(schemaUuid).orElseThrow();
        assertEquals(updatedDraft.getName(), changeDTO.getName());
        assertEquals(updatedDraft.getName(), draftVersion.getName());
    }
}
