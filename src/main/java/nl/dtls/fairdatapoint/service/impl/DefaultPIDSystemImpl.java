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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import com.google.common.base.Preconditions;
import java.util.UUID;
import javax.annotation.Nonnull;
import nl.dtl.fairmetadata4j.model.Metadata;
import nl.dtls.fairdatapoint.service.PIDSystem;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of default PID system
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2018-06-05
 * @version 0.1
 */
public class DefaultPIDSystemImpl implements PIDSystem {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultPIDSystemImpl.class);

    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    /**
     * Create a new PID uri for a given metadata
     *
     * @param <T>
     * @param metadata Subtype of Metadata object
     * @return PID uri as IRI
     */
    @Override
    public <T extends Metadata> IRI getURI(@Nonnull T metadata) {
        
        Preconditions.checkNotNull(metadata, "Metadata must not be null.");
        Preconditions.checkNotNull(metadata.getUri(), "Metadata URI must not be null.");
        LOGGER.info("Creating an new default PID");
        UUID uid = UUID.randomUUID();
        String id = ("pid-").concat(uid.toString());
        String iri = String.join("#", metadata.getUri().stringValue(), id);
        IRI pidIRI = VALUEFACTORY.createIRI(iri);
        return pidIRI;
    }

    /**
     * Returns identifier of a given default PID iri.
     *
     * @param iri PID IRI
     * @return ID as String
     */
    @Override
    public String getId(@Nonnull IRI iri) {

        Preconditions.checkNotNull(iri, "Default pid uri must not be null.");
        Preconditions.checkState(iri.toString().contains("#"), "Not an valid default pid uri.");
        String id = null;
        String uri = iri.toString();
        id = uri.substring(uri.lastIndexOf('#') + 1, uri.length());
        return id;
    }

}
