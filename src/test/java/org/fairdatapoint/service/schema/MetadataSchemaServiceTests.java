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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles(Profiles.TESTING)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
public class MetadataSchemaServiceTests {
    @Autowired
    private MetadataSchemaService service;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void changeExistingSchema() throws Exception {
        // reproduces #660

        // GIVEN: existing schema version (without existing draft) and changes to be made
        UUID uuid = Common.SCHEMA_SIMPLE_UUID;
        MetadataSchemaChangeDTO changeDTO = MetadataSchemaChangeDTO.builder()
                .name("test")
                .description("")
                .definition("")
                .abstractSchema(false)
                .extendsSchemaUuids(new ArrayList<>())
                .build();

        // WHEN: changes are applied to existing schema
        Optional<MetadataSchemaDraftDTO> updatedDraft = service.updateSchemaDraft(uuid, changeDTO);

        // THEN: draft schema is created with updated properties
        assertEquals(updatedDraft.map(MetadataSchemaDraftDTO::getName).orElse(""), changeDTO.getName());
    }
}
