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
package nl.dtls.fairdatapoint.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public final class FDP {
    public static final String NAMESPACE = "https://w3id.org/fdp/fdp-o#";
    public static final String PREFIX = "fdp";

    /** <tt>https://w3id.org/fdp/fdp-o#DeprecatedClasses</tt> */
    public static final IRI DEPRECATEDCLASSES;

    /** <tt>https://w3id.org/fdp/fdp-o#FAIRDataPoint</tt> */
    public static final IRI FAIRDATAPOINT;

    /** <tt>https://w3id.org/fdp/fdp-o#Metadata</tt> */
    public static final IRI METADATA;

    /** <tt>https://w3id.org/fdp/fdp-o#MetadataService</tt> */
    public static final IRI METADATASERVICE;

    /** <tt>https://w3id.org/fdp/fdp-o#deprecatedObjectProperties</tt> */
    public static final IRI DEPRECATEDOBJECTPROPERTIES;

    /** <tt>https://w3id.org/fdp/fdp-o#fdpIdentifier</tt> */
    public static final IRI FDPIDENTIFIER;

    /** <tt>https://w3id.org/fdp/fdp-o#metadataCatalog</tt> */
    public static final IRI METADATACATALOG;

    /** <tt>https://w3id.org/fdp/fdp-o#metadataIdentifier</tt> */
    public static final IRI METADATAIDENTIFIER;

    /** <tt>https://w3id.org/fdp/fdp-o#servesMetadata</tt> */
    public static final IRI SERVESMETADATA;

    /** <tt>https://w3id.org/fdp/fdp-o#fdpEndDate</tt> */
    public static final IRI FDPENDDATE;

    /** <tt>https://w3id.org/fdp/fdp-o#fdpSoftwareVersion</tt> */
    public static final IRI FDPSOFTWAREVERSION;

    /** <tt>https://w3id.org/fdp/fdp-o#fdpStartDate</tt> */
    public static final IRI FDPSTARTDATE;

    /** <tt>https://w3id.org/fdp/fdp-o#fdpUILanguage</tt> */
    public static final IRI FDPUILANGUAGE;

    /** <tt>https://w3id.org/fdp/fdp-o#metadataIssued</tt> */
    public static final IRI METADATAISSUED;

    /** <tt>https://w3id.org/fdp/fdp-o#metadataModified</tt> */
    public static final IRI METADATAMODIFIED;

    static {
        final ValueFactory factory = SimpleValueFactory.getInstance();

        DEPRECATEDCLASSES = factory.createIRI("https://w3id.org/fdp/fdp-o#DeprecatedClasses");
        FAIRDATAPOINT = factory.createIRI("https://w3id.org/fdp/fdp-o#FAIRDataPoint");
        METADATA = factory.createIRI("https://w3id.org/fdp/fdp-o#Metadata");
        METADATASERVICE = factory.createIRI("https://w3id.org/fdp/fdp-o#MetadataService");
        DEPRECATEDOBJECTPROPERTIES = factory.createIRI("https://w3id.org/fdp/fdp-o#deprecatedObjectProperties");
        FDPIDENTIFIER = factory.createIRI("https://w3id.org/fdp/fdp-o#fdpIdentifier");
        METADATACATALOG = factory.createIRI("https://w3id.org/fdp/fdp-o#metadataCatalog");
        METADATAIDENTIFIER = factory.createIRI("https://w3id.org/fdp/fdp-o#metadataIdentifier");
        SERVESMETADATA = factory.createIRI("https://w3id.org/fdp/fdp-o#servesMetadata");
        FDPENDDATE = factory.createIRI("https://w3id.org/fdp/fdp-o#fdpEndDate");
        FDPSOFTWAREVERSION = factory.createIRI("https://w3id.org/fdp/fdp-o#fdpSoftwareVersion");
        FDPSTARTDATE = factory.createIRI("https://w3id.org/fdp/fdp-o#fdpStartDate");
        FDPUILANGUAGE = factory.createIRI("https://w3id.org/fdp/fdp-o#fdpUILanguage");
        METADATAISSUED = factory.createIRI("https://w3id.org/fdp/fdp-o#metadataIssued");
        METADATAMODIFIED = factory.createIRI("https://w3id.org/fdp/fdp-o#metadataModified");

    }

    /** Utility class; private constructor to prevent instance being created. */
    private FDP() {
    }
}
