package nl.dtls.fairdatapoint;

import nl.dtls.fairdatapoint.config.MetadataTestConfig;
import nl.dtls.fairdatapoint.config.RepositoryTestConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableWebMvc
@ActiveProfiles(Profiles.TESTING)
@ContextConfiguration(classes = {RepositoryTestConfig.class, MetadataTestConfig.class}, loader = SpringBootContextLoader.class)
@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.*")
public abstract class BaseIntegrationTest {
}
