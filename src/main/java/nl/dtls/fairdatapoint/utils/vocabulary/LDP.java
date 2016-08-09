/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.utils.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class LDP {
    public static final String uri = "<http://www.w3.org/ns/ldp#>";
    public static final String BASE_URI = "http://www.w3.org/ns/ldp#";
    
    
    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }
    public static final Property contains = ResourceFactory.createProperty(uri + "contains" );
    public static final Resource Container = ResourceFactory.createResource(uri + "Container" );    
    public static final URI CONTAINER = new URIImpl(BASE_URI + "Container" );
    public static final URI CONTAINS = new URIImpl(BASE_URI + "contains" );
}
