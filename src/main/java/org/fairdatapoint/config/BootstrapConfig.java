package org.fairdatapoint.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

@Configuration
public class BootstrapConfig {
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        // load all json resources from the fixtures dir
        try {
            final Resource[] resources = resourceResolver.getResources("classpath:fixtures/*.json");
            // sort resources to guarantee lexicographic order
            Arrays.sort(
                    resources,
                    Comparator.comparing(
                            Resource::getFilename,
                            Comparator.nullsLast(String::compareTo)
                    )
            );
            factory.setResources(resources);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        return factory;
    }
}
