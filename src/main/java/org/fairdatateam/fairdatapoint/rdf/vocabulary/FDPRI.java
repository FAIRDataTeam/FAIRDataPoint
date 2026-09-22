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
package org.fairdatateam.fairdatapoint.rdf.vocabulary;

import org.eclipse.rdf4j.model.IRI;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;

/**
 * Vocabulary of the FAIR Data Point reference implementation (FDP-RI-O).
 *
 * <p>Terms that describe how <em>this</em> implementation stores its own configuration in the
 * triple store: record state, metadata schema versions, resource definitions, settings and the
 * RDF-migration log. Anything implementation-agnostic belongs in FDP-O ({@link FDP}) instead.
 * The ontology itself is shipped as {@code fdp-ri-o.ttl} next to this class on the classpath.
 */
public final class FDPRI {

    public static final String NAMESPACE = "https://w3id.org/fdp/fdp-ri-o#";

    public static final String PREFIX = "fdp-ri";

    /**
     * IRI prefix shared by the system graph and all of its sub-graphs. Search excludes every graph
     * whose IRI starts with it from its results.
     */
    public static final String SYSTEM_GRAPH_PREFIX = "https://w3id.org/fdp/fdp-ri-o/system";

    /**
     * Named graph in the main repository that holds the implementation's own configuration.
     * Search and the SPARQL endpoint exclude it from their results.
     */
    public static final IRI SYSTEM_GRAPH = i(SYSTEM_GRAPH_PREFIX);

    /** Sub-graph holding the state (draft or published) of every record. */
    public static final IRI STATE_GRAPH = systemGraph("state");

    /** Class of the states a record can be in. */
    public static final IRI RECORD_STATE = i(NAMESPACE + "RecordState");

    /** State of a record that is only visible to authenticated users. */
    public static final IRI DRAFT = i(NAMESPACE + "Draft");

    /** State of a record that is visible to everybody. */
    public static final IRI PUBLISHED = i(NAMESPACE + "Published");

    /** Relates a record to its {@link #RECORD_STATE}. */
    public static final IRI HAS_STATE = i(NAMESPACE + "hasState");

    private FDPRI() {
    }

    /**
     * Sub-graph of the system graph for one area of the configuration, e.g. {@code state}.
     *
     * @param area name of the area, used as the last segment of the graph IRI
     * @return the IRI of the sub-graph
     */
    public static IRI systemGraph(String area) {
        return i(SYSTEM_GRAPH_PREFIX + "/" + area);
    }

}
