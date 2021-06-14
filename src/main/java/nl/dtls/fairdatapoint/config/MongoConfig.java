/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.config;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = {"nl.dtls.fairdatapoint", "nl.dtls.rdf.migration", "org.springframework.security.acls"})
public class MongoConfig {

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private ResourceDefinitionTargetClassesCache targetClassesCache;

    @Bean("mongockRunner")
    public MongockSpring5.MongockApplicationRunner mongockApplicationRunner(
            ApplicationContext springContext,
            MongoTemplate mongoTemplate) {
        return MongockSpring5.builder()
                .setDriver(SpringDataMongoV3Driver.withDefaultLock(mongoTemplate))
                .addChangeLogsScanPackage("nl.dtls.fairdatapoint.database.mongo.migration.production")
                .setSpringContext(springContext)
                .setLegacyMigration(new LegacyMigration("dbchangelog"))
                .addDependency(ResourceDefinitionCache.class, resourceDefinitionCache)
                .addDependency(ResourceDefinitionTargetClassesCache.class, targetClassesCache)
                .buildApplicationRunner();
    }
}