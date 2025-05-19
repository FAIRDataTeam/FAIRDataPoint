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
import nl.dtls.fairdatapoint.vocabulary.DCAT3;
import org.fairdatateam.rdf.migration.entity.RdfMigrationAnnotation;
import org.fairdatateam.rdf.migration.runner.RdfProductionMigration;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.s;

@RdfMigrationAnnotation(
        number = 5,
        name = "Fix Metadata Version",
        description = "Use dcat:version instead of dcterms:hasVersion")
@Slf4j
@Service
public class Rdf_Migration_0005_FixMetadataVersion implements RdfProductionMigration {

    private static final String MSG_ADD = "Adding: {} {} {}";
    private static final String MSG_REMOVE = "Removing: {} {} {}";

    @Autowired
    private Repository repository;

    public void runMigration() {
        updateVersionStatements();
    }

    private void updateVersionStatements() {
        // change dcterms:hasVersion to dcat:version property (if object is literal)
        try (RepositoryConnection conn = repository.getConnection()) {
            final RepositoryResult<Statement> queryResult = conn.getStatements(null, DCTERMS.HAS_VERSION, null);
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                if (st.getObject().isLiteral()) {
                    log.debug(MSG_ADD, st.getSubject(), DCAT3.VERSION, st.getObject());
                    conn.add(s(st.getSubject(), DCAT3.VERSION, st.getObject(), st.getSubject()));
                    log.debug(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                    conn.remove(st);
                }
            }
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
