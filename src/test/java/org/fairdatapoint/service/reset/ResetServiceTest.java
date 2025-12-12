package org.fairdatapoint.service.reset;

import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.api.dto.reset.ResetDTO;
import org.fairdatapoint.database.db.repository.*;
import org.fairdatapoint.database.db.repository.base.BaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResetServiceTest extends BaseIntegrationTest {
    @Autowired
    private ResetService resetService;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipPermissionRepository membershipPermissionRepository;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResourceDefinitionChildRepository resourceDefinitionChildRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    public void testResetToFactoryDefaultsAll() throws Exception {
        // given: some of the repositories have been modified (emptied in this case)
        final List<BaseRepository<?>> repositories = List.of(
                apiKeyRepository,
                membershipPermissionRepository,
                membershipRepository,
                metadataSchemaRepository,
                resourceDefinitionChildRepository,
                resourceDefinitionRepository,
                userAccountRepository
        );
        repositories.forEach(BaseRepository::deleteAll);
        repositories.forEach(repository -> assertEquals(0, repository.count()));
        // test: default repository content is restored
        ResetDTO resetAll = new ResetDTO(true, true, true, true);
        resetService.resetToFactoryDefaults(resetAll);
        repositories.forEach(repository -> assertTrue(repository.count() > 0));
    }
}
