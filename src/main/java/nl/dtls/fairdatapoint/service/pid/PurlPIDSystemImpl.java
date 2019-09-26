/**
 * The MIT License
 * Copyright © 2017 DTL
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.pid;

import com.google.common.base.Preconditions;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.model.Metadata;
import nl.dtls.fairdatapoint.service.metadata.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Implementation of purl.org PID system
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2018-06-06
 * @version 0.1
 */
public class PurlPIDSystemImpl implements PIDSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurlPIDSystemImpl.class);
    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    @Autowired
    @Qualifier("purlBaseUrl")
    private IRI purlBaseUrl;

    @Autowired
    private MetadataService fairMetaDataService;

    /**
     * Create a new purl.org PID uri for a given metadata
     *
     * @param <T>
     * @param metadata Subtype of Metadata object
     * @throws NullPointerException exception if the metadata or the metadata URI is null
     * @throws IllegalStateException exception if the base purl.org is empty
     * @throws IllegalStateException exception if the fdpUri is empty or null
     * @return purl.org uri as IRI
     */
    @Override
    public <T extends Metadata> IRI getURI(T metadata) throws IllegalStateException {
        Preconditions.checkNotNull(metadata, "Metadata must not be null.");
        Preconditions.checkNotNull(metadata.getUri(), "Metadata URI must not be null.");
        Preconditions.checkNotNull(purlBaseUrl, "Purl base url can't be null.");

        IRI fdpUri = null;
        try {
            if (metadata instanceof FDPMetadata) {
                fdpUri = metadata.getUri();
            } else {
                Preconditions.checkNotNull(metadata.getParentURI(),
                        "Metadata parent URI must not be null");
                fdpUri = fairMetaDataService.getFDPIri(metadata.getParentURI());
            }
        } catch (MetadataServiceException ex) {
            LOGGER.error("Error getting fdp uri");
        }

        Preconditions.checkNotNull(fdpUri, "FDP base url can't be null.");

        LOGGER.info("Creating an new purl.org PID");
        String purlIRI = metadata.getUri().toString().replace(fdpUri.toString(),
                purlBaseUrl.toString());

        return VALUEFACTORY.createIRI(purlIRI);

    }

    /**
     * Returns identifier of a given purl PID iri.
     *
     * @param iri Purl PID IRI
     * @throws NullPointerException exception if the purl pid URI is null
     * @throws IllegalStateException exception if the purl URI doesn't contain "purl.org" string
     * @return ID as String
     */
    @Override
    public String getId(IRI iri) {
        Preconditions.checkNotNull(iri, "Purl pid uri must not be null.");
        Preconditions.checkState(iri.toString().contains("purl.org"),
                "Not an valid default pid uri.");
        String id = iri.getLocalName();
        return id;
    }

}
