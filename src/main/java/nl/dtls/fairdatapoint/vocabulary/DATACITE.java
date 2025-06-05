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

public final class DATACITE {
    public static final String NAMESPACE = "";
    public static final String PREFIX = "datacite";

    /** <tt>http://purl.org/spar/datacite/AlternateResourceIdentifier</tt> */
    public static final IRI ALTERNATERESOURCEIDENTIFIER;

    /** <tt>http://purl.org/co/List</tt> */
    public static final IRI LIST;

    /** <tt>http://purl.org/spar/datacite/PrimaryResourceIdentifier</tt> */
    public static final IRI PRIMARYRESOURCEIDENTIFIER;

    /** <tt>http://www.essepuntato.it/2010/06/literalreification/Literal</tt> */
    public static final IRI LITERAL;

    /** <tt>http://purl.org/spar/datacite/FunderIdentifier</tt> */
    public static final IRI FUNDERIDENTIFIER;

    /** <tt>http://purl.org/spar/datacite/MetadataScheme</tt> */
    public static final IRI METADATASCHEME;

    /** <tt>http://purl.org/spar/datacite/OrganizationIdentifier</tt> */
    public static final IRI ORGANIZATIONIDENTIFIER;

    /** <tt>http://purl.org/spar/datacite/PersonalIdentifier</tt> */
    public static final IRI PERSONALIDENTIFIER;

    /** <tt>http://www.w3.org/2004/02/skos/core#Concept</tt> */
    public static final IRI CONCEPT;

    /** <tt>http://purl.org/spar/datacite/FunderIdentifierScheme</tt> */
    public static final IRI FUNDERIDENTIFIERSCHEME;

    /** <tt>http://purl.org/spar/datacite/Identifier</tt> */
    public static final IRI IDENTIFIER;

    /** <tt>http://purl.org/spar/datacite/OrganizationIdentifierScheme</tt> */
    public static final IRI ORGANIZATIONIDENTIFIERSCHEME;

    /** <tt>http://purl.org/spar/datacite/AgentIdentifier</tt> */
    public static final IRI AGENTIDENTIFIER;

    /** <tt>http://purl.org/spar/datacite/DescriptionType</tt> */
    public static final IRI DESCRIPTIONTYPE;

    /** <tt>http://purl.org/spar/datacite/AgentIdentifierScheme</tt> */
    public static final IRI AGENTIDENTIFIERSCHEME;

    /** <tt>http://purl.org/spar/datacite/IdentifierScheme</tt> */
    public static final IRI IDENTIFIERSCHEME;

    /** <tt>http://purl.org/spar/datacite/ResourceIdentifier</tt> */
    public static final IRI RESOURCEIDENTIFIER;

    /** <tt>http://purl.org/spar/datacite/PersonalIdentifierScheme</tt> */
    public static final IRI PERSONALIDENTIFIERSCHEME;

    /** <tt>http://purl.org/spar/datacite/ResourceIdentifierScheme</tt> */
    public static final IRI RESOURCEIDENTIFIERSCHEME;

    /** <tt>http://purl.org/spar/datacite/hasCreatorList</tt> */
    public static final IRI HASCREATORLIST;

    /** <tt>http://purl.org/spar/datacite/hasGeneralResourceType</tt> */
    public static final IRI HASGENERALRESOURCETYPE;

    /** <tt>http://purl.org/spar/datacite/usesMetadataScheme</tt> */
    public static final IRI USESMETADATASCHEME;

    /** <tt>http://purl.org/spar/fabio/hasURL</tt> */
    public static final IRI HASURL;

    /** <tt>http://www.w3.org/2002/07/owl#topObjectProperty</tt> */
    public static final IRI TOPOBJECTPROPERTY;

    /** <tt>http://purl.org/spar/datacite/hasDescription</tt> */
    public static final IRI HASDESCRIPTION;

    /** <tt>http://purl.org/spar/datacite/hasDescriptionType</tt> */
    public static final IRI HASDESCRIPTIONTYPE;

    /** <tt>http://purl.org/dc/terms/type</tt> */
    public static final IRI TYPE;

    /** <tt>http://purl.org/spar/datacite/hasIdentifier</tt> */
    public static final IRI HASIDENTIFIER;

    /** <tt>http://purl.org/spar/datacite/usesIdentifierScheme</tt> */
    public static final IRI USESIDENTIFIERSCHEME;

    static {
        final ValueFactory factory = SimpleValueFactory.getInstance();

        ALTERNATERESOURCEIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/AlternateResourceIdentifier");
        LIST = factory.createIRI("http://purl.org/co/List");
        PRIMARYRESOURCEIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/PrimaryResourceIdentifier");
        LITERAL = factory.createIRI("http://www.essepuntato.it/2010/06/literalreification/Literal");
        FUNDERIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/FunderIdentifier");
        METADATASCHEME = factory.createIRI("http://purl.org/spar/datacite/MetadataScheme");
        ORGANIZATIONIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/OrganizationIdentifier");
        PERSONALIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/PersonalIdentifier");
        CONCEPT = factory.createIRI("http://www.w3.org/2004/02/skos/core#Concept");
        FUNDERIDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/FunderIdentifierScheme");
        IDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/Identifier");
        ORGANIZATIONIDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/OrganizationIdentifierScheme");
        AGENTIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/AgentIdentifier");
        DESCRIPTIONTYPE = factory.createIRI("http://purl.org/spar/datacite/DescriptionType");
        AGENTIDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/AgentIdentifierScheme");
        IDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/IdentifierScheme");
        RESOURCEIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/ResourceIdentifier");
        PERSONALIDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/PersonalIdentifierScheme");
        RESOURCEIDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/ResourceIdentifierScheme");
        HASCREATORLIST = factory.createIRI("http://purl.org/spar/datacite/hasCreatorList");
        HASGENERALRESOURCETYPE = factory.createIRI("http://purl.org/spar/datacite/hasGeneralResourceType");
        USESMETADATASCHEME = factory.createIRI("http://purl.org/spar/datacite/usesMetadataScheme");
        HASURL = factory.createIRI("http://purl.org/spar/fabio/hasURL");
        TOPOBJECTPROPERTY = factory.createIRI("http://www.w3.org/2002/07/owl#topObjectProperty");
        HASDESCRIPTION = factory.createIRI("http://purl.org/spar/datacite/hasDescription");
        HASDESCRIPTIONTYPE = factory.createIRI("http://purl.org/spar/datacite/hasDescriptionType");
        TYPE = factory.createIRI("http://purl.org/dc/terms/type");
        HASIDENTIFIER = factory.createIRI("http://purl.org/spar/datacite/hasIdentifier");
        USESIDENTIFIERSCHEME = factory.createIRI("http://purl.org/spar/datacite/usesIdentifierScheme");

    }

    /** Utility class; private constructor to prevent instance being created. */
    private DATACITE() {
    }
}
