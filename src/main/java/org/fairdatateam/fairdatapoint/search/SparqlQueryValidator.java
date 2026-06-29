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
package org.fairdatateam.fairdatapoint.search;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.fairdatateam.fairdatapoint.entity.exception.ValidationException;

/**
 * Custom validator for SPARQL query strings. Throws a <code>ValidationException</code> if validation fails.
 */
@Slf4j
public class SparqlQueryValidator {

    public static final String MESSAGE_INVALID_QUERY = "Invalid SPARQL query";

    /**
     * Validate the SPARQL query string. This also guards against SPARQL update commands.
     */
    public static void validate(String query) {
        // create temporary in-memory repository because we need a repository connection
        // but do not want to run the risk of accidental access to the main repository
        final Repository temporaryRepository = new SailRepository(new MemoryStore());
        try (RepositoryConnection connection = temporaryRepository.getConnection()) {
            // use RDF4J's built-in query validation (note there is also a prepareUpdate() method)
            connection.prepareQuery(query);
        }
        catch (MalformedQueryException | RepositoryException exception) {
            log.warn(exception.getMessage());
            throw new ValidationException(MESSAGE_INVALID_QUERY);
        }
        finally {
            temporaryRepository.shutDown();
        }
    }

}
