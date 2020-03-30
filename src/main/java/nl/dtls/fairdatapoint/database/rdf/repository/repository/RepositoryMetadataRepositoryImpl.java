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
package nl.dtls.fairdatapoint.database.rdf.repository.repository;

import com.google.common.base.Preconditions;
import nl.dtls.fairdatapoint.database.rdf.repository.common.GenericMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RepositoryMetadataRepositoryImpl extends GenericMetadataRepository implements RepositoryMetadataRepository {

    private static final String GET_FDP_IRI = "getFdpIri.sparql";

    public IRI getRepositoryIri(IRI iri) throws MetadataRepositoryException {
        Preconditions.checkNotNull(iri, "URI must not be null.");
        LOGGER.info("Get repository uri for the given uri {}", iri.toString());
        return runSparqlQuery(GET_FDP_IRI, RepositoryMetadataRepositoryImpl.class, Map.of("iri", iri))
                .stream()
                .map(s -> VALUEFACTORY.createIRI(s.getValue("fdp").stringValue()))
                .findFirst()
                .orElse(null);
    }

}
