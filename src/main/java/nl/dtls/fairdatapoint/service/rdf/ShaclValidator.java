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
package nl.dtls.fairdatapoint.service.rdf;

import nl.dtls.fairdatapoint.entity.exception.RdfValidationException;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSailValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Service
public class ShaclValidator {

    public void validate(Model shacl, Model data, String baseUri) {
        // 1. Prepare repository
        final ShaclSail shaclSail = new ShaclSail(new MemoryStore());
        shaclSail.setRdfsSubClassReasoning(true);
        final SailRepository sailRepository = new SailRepository(shaclSail);
        sailRepository.init();

        try (SailRepositoryConnection connection = sailRepository.getConnection()) {
            // 2. Save SHACL
            connection.begin();
            connection.add(shacl, RDF4J.SHACL_SHAPE_GRAPH);
            connection.commit();

            // 3. Validate data
            connection.begin();
            connection.add(new ArrayList<>(data), i(baseUri));
            connection.commit();

        }
        catch (RepositoryException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof ShaclSailValidationException) {
                final Model validationReportModel =
                        ((ShaclSailValidationException) cause).validationReportAsModel();
                throw new RdfValidationException(validationReportModel);
            }
            throw new ValidationException("Validation failed (unsupported exception)");
        }
        finally {
            sailRepository.shutDown();
        }
    }

}
