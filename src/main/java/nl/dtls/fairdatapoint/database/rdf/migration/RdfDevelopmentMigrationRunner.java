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
package nl.dtls.fairdatapoint.database.rdf.migration;

import jakarta.annotation.PostConstruct;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.AclMigration;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.RdfMetadataMigration;
import nl.dtls.fairdatapoint.database.rdf.repository.RepositoryMode;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(Profiles.NON_PRODUCTION)
public class RdfDevelopmentMigrationRunner {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private RdfMetadataMigration rdfMetadataMigration;

    @Autowired
    private AclMigration aclMigration;

    @Autowired
    @Qualifier("genericMetadataRepository")
    private GenericMetadataRepository metadataRepository;

    @PostConstruct
    public void run() {
        rdfMetadataMigration.runMigration();
        if (activeProfile.equals(Profiles.DEVELOPMENT)) {
            aclMigration.runMigration();
        }
    }

    public void clean() {
        try {
            metadataRepository.removeAll(RepositoryMode.MAIN);
            metadataRepository.removeAll(RepositoryMode.DRAFTS);
        }
        catch (MetadataRepositoryException exc) {
            throw new RuntimeException(exc);
        }
    }
}
