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
package org.fairdatateam.fairdatapoint.migration.mongodb.development;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.fairdatateam.fairdatapoint.Profiles;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionCache;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionTargetClassesCache;
import org.fairdatateam.fairdatapoint.search.SearchFilterCache;
import org.fairdatateam.fairdatapoint.settings.SettingsCache;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(Profiles.NON_PRODUCTION)
@RequiredArgsConstructor
public class DevelopmentMigrationRunner {

    private final UserMigration userMigration;

    private final MembershipMigration membershipMigration;

    private final AclMigration aclMigration;

    private final ResourceDefinitionMigration resourceDefinitionMigration;

    private final MetadataSchemaMigration metadataSchemaMigration;

    private final ApiKeyMigration apiKeyMigration;

    private final MetadataMigration metadataMigration;

    private final IndexEntryMigration indexEntryMigration;

    private final EventMigration eventMigration;

    private final SettingsMigration settingsMigration;

    private final SettingsCache settingsCache;

    private final ResourceDefinitionTargetClassesCache resourceDefinitionTargetClassesCache;

    private final ResourceDefinitionCache resourceDefinitionCache;

    private final SearchFilterCache searchFilterCache;

    @PostConstruct
    public void run() {
        settingsMigration.runMigration();
        settingsCache.updateCachedSettings();
        userMigration.runMigration();
        membershipMigration.runMigration();
        aclMigration.runMigration();
        resourceDefinitionMigration.runMigration();
        metadataSchemaMigration.runMigration();
        apiKeyMigration.runMigration();
        metadataMigration.runMigration();
        indexEntryMigration.runMigration();
        eventMigration.runMigration();
        resourceDefinitionTargetClassesCache.computeCache();
        resourceDefinitionCache.computeCache();
        searchFilterCache.clearCache();
    }

}
