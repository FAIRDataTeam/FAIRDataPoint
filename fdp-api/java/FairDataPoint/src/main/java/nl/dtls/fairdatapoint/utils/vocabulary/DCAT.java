/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.utils.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 *
 * @author citroen
 */
public class DCAT {
    public static final String uri = "<http://www.w3.org/ns/dcat#>";
    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }
    public static final Property downloadURL = ResourceFactory.createProperty(uri + "downloadURL" );
    public static final Property mediaType = ResourceFactory.createProperty(uri + "mediaType" );
}
