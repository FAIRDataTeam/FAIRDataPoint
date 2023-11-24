package nl.dtls.fairdatapoint.config;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.database.db.migration.DatabaseSeeder;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(@Value("${spring.profiles.active}") String activeProfile) {
        return flywayOld -> {
            Flyway flyway = Flyway.configure()
                    .configuration(flywayOld.getConfiguration())
                    //.callbacks(new DatabaseSeeder(activeProfile))
                    .load();

            if (activeProfile.equals(Profiles.DEVELOPMENT)) {
                log.info("Development mode - cleaning up database");
                flyway.clean();
            }

            if (activeProfile.equals(Profiles.TESTING)) {
                log.info("Testing mode - not migrating database");
                return;
            }
            flyway.migrate();
        };
    }
}
