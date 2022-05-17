package nl.dtls.fairdatapoint.database.mongo.migration.development.settings;

import nl.dtls.fairdatapoint.database.mongo.migration.development.settings.data.SettingsFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsMigration {

    @Autowired
    private SettingsRepository settingsRepository;

    public void runMigration() {
        settingsRepository.deleteAll();
        settingsRepository.save(SettingsFixtures.settings());
    }
}
