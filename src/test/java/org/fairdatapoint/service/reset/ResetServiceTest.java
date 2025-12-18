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
package org.fairdatapoint.service.reset;

import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.api.dto.reset.ResetDTO;
import org.fairdatapoint.database.db.repository.*;
import org.fairdatapoint.database.db.repository.base.BaseRepository;
import org.fairdatapoint.entity.base.BaseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResetServiceTest extends BaseIntegrationTest {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipPermissionRepository membershipPermissionRepository;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResetService resetService;

    @Autowired
    private ResourceDefinitionChildRepository resourceDefinitionChildRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testResetToFactoryDefaultsAll() throws Exception {
        // given: repositories have been populated *before* reference time
        final List<BaseRepository<?>> repositories = List.of(
                apiKeyRepository,
                membershipPermissionRepository,
                membershipRepository,
                metadataSchemaRepository,
                resourceDefinitionChildRepository,
                resourceDefinitionRepository,
                userAccountRepository
        );
        Instant referenceTime = Instant.now();
        checkEntityCreationTimes(repositories, referenceTime, true);
        // when: everything is reset
        ResetDTO resetAll = new ResetDTO(true, true, true, true);
        resetService.resetToFactoryDefaults(resetAll);
        // then: repositories have been repopulated *after* reference time (actually "not before")
        checkEntityCreationTimes(repositories, referenceTime, false);
    }

    private void checkEntityCreationTimes(
            List<BaseRepository<?>> repositories,
            Instant referenceTime,
            boolean before
    ) {
        repositories.forEach(repository -> {
            // repository should be non-empty
            assertTrue(repository.count() > 0);
            // all entities should have creation time before (or not before) reference time
            for (Object entity : repository.findAll()) {
                if (entity instanceof BaseEntity) {
                    assertEquals(before, ((BaseEntity) entity).getCreatedAt().isBefore(referenceTime));
                }
                else {
                    throw new RuntimeException("Unexpected entity type: " + entity.getClass());
                }
            }
        });
    }
}
