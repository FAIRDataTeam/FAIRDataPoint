/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.utils.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class LDP {
    public static final String uri = "<http://www.w3.org/ns/ldp#>";
    
    
    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }
    public static final Property contains = ResourceFactory.createProperty(uri + "contains" );
    public static final Resource Container = ResourceFactory.createResource(uri + "Container" );
}
