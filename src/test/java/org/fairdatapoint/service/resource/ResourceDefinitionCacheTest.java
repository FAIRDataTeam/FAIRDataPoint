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
package org.fairdatapoint.service.resource;

import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.Profiles;
import org.fairdatapoint.database.db.repository.ResourceDefinitionRepository;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.util.KnownUUIDs;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@ActiveProfiles(Profiles.TESTING)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ResourceDefinitionCacheTest extends BaseIntegrationTest {

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    protected Flyway flyway;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ResourceReaderRepositoryPopulator populator;

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
        // re-populate the database using default fixtures
        populator.populate(new Repositories(applicationContext));
    }

    @Test
    public void computeCacheWorks() {
        // GIVEN: Resource definitions
        ResourceDefinition rdRepository = resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_FDP_UUID).get();
        ResourceDefinition rdCatalog = resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_CATALOG_UUID).get();
        ResourceDefinition rdDataset = resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_DATASET_UUID).get();
        ResourceDefinition rdDistribution = resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_DISTRIBUTION_UUID).get();

        // WHEN:
        resourceDefinitionCache.computeCache();

        // THEN: caches by UUID
        assertThat(resourceDefinitionCache.getByUuid(rdRepository.getUuid()).getName(), is(equalTo(rdRepository.getName())));
        assertThat(resourceDefinitionCache.getByUuid(rdCatalog.getUuid()).getName(), is(equalTo(rdCatalog.getName())));
        assertThat(resourceDefinitionCache.getByUuid(rdDataset.getUuid()).getName(), is(equalTo(rdDataset.getName())));
        assertThat(resourceDefinitionCache.getByUuid(rdDistribution.getUuid()).getName(), is(equalTo(rdDistribution.getName())));

        // AND: caches parents
        assertThat(resourceDefinitionCache.getParentsByUuid(rdRepository.getUuid()).isEmpty(), is(true));
        assertThat(resourceDefinitionCache.getParentsByUuid(rdCatalog.getUuid()).size(), is(equalTo(1)));
        assertThat(resourceDefinitionCache.getParentsByUuid(rdCatalog.getUuid()).stream().toList().get(0).getUuid(), is(equalTo(rdRepository.getUuid())));
        assertThat(resourceDefinitionCache.getParentsByUuid(rdDataset.getUuid()).size(), is(equalTo(1)));
        assertThat(resourceDefinitionCache.getParentsByUuid(rdDataset.getUuid()).stream().toList().get(0).getUuid(), is(equalTo(rdCatalog.getUuid())));
        assertThat(resourceDefinitionCache.getParentsByUuid(rdDistribution.getUuid()).size(), is(equalTo(1)));
        assertThat(resourceDefinitionCache.getParentsByUuid(rdDistribution.getUuid()).stream().toList().get(0).getUuid(), is(equalTo(rdDataset.getUuid())));
    }

}
