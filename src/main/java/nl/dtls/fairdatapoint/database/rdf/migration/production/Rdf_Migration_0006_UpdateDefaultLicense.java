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
package nl.dtls.fairdatapoint.database.rdf.migration.production;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.rdf.migration.entity.RdfMigrationAnnotation;
import nl.dtls.rdf.migration.runner.RdfProductionMigration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RdfMigrationAnnotation(
        number = 6,
        name = "Update default metadata license",
        description = "Use the persistent url for the rdflicense vocabulary"
)
@Slf4j
@Service
public class Rdf_Migration_0006_UpdateDefaultLicense implements RdfProductionMigration {
    @Autowired
    private Repository repository;

    @Override
    public void runMigration() {
        final IRI newValue = Values.iri("http://purl.org/NET/rdflicense/cc-by-nc-nd3.0");

        try (RepositoryConnection conn = repository.getConnection()) {
            final RepositoryResult<Statement> queryResult = conn.getStatements(null, DCTERMS.LICENSE,
                    Values.iri("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                log.debug("Adding: {} {} {}", st.getSubject(), DCTERMS.LICENSE, newValue);
                conn.add(st.getSubject(), DCTERMS.LICENSE, newValue);
                log.debug("Removing: {} {} {}", st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
        } catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
