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

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

public final class FDP {

    public static final String NAMESPACE = "https://w3id.org/fdp/fdp-o#";

    public static final IRI FAIRDATAPOINT = i(NAMESPACE + "FAIRDataPoint");
    public static final IRI FDPSOFTWAREVERSION = i(NAMESPACE + "fdpSoftwareVersion");
    public static final IRI METADATACATALOG = i(NAMESPACE + "metadataCatalog");
    public static final IRI METADATAIDENTIFIER = i(NAMESPACE + "metadataIdentifier");
    public static final IRI METADATAISSUED = i(NAMESPACE + "metadataIssued");
    public static final IRI METADATAMODIFIED = i(NAMESPACE + "metadataModified");
    public static final IRI METADATASERVICE = i(NAMESPACE + "MetadataService");

}
