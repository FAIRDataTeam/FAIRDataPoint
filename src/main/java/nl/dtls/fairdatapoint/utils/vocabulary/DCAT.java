/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.utils.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author citroen
 */
public class DCAT {
    public static final String uri = "<http://www.w3.org/ns/dcat#>";
    public static final String BASE_URI = "http://www.w3.org/ns/dcat#";
    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }
    public static final Property downloadURL = ResourceFactory.createProperty(uri + "downloadURL" );
    public static final Property mediaType = ResourceFactory.createProperty(uri + "mediaType" );
    public static final URI THEME_TAXONOMY = new URIImpl(BASE_URI + "themeTaxonomy");
    public static final URI LANDING_PAGE = new URIImpl(BASE_URI + "landingPage");
    public static final URI THEME = new URIImpl(BASE_URI + "theme");
    public static final URI CONTACT_POINT = new URIImpl(
            BASE_URI + "contactPoint");
    public static final URI KEYWORD = new URIImpl(BASE_URI + "keyword");
    public static final URI TYPE_CATALOG = new URIImpl(BASE_URI + "Catalog");
    public static final URI TYPE_DATASET = new URIImpl(BASE_URI + "Dataset");
    public static final URI TYPE_DISTRIBUTION = new URIImpl(
            BASE_URI + "Distribution");
    public static final URI DATASET = new URIImpl(BASE_URI + "dataset");
    public static final URI ACCESS_URL = new URIImpl(BASE_URI + "accessURL");
    public static final URI DISTRIBUTION = new URIImpl(
            BASE_URI + "distribution");
    public static final URI DOWNLOAD_URL = new URIImpl(
            BASE_URI + "downloadURL");
    public static final URI MEDIA_TYPE = new URIImpl(
            BASE_URI + "mediaType");
    public static final URI FORMAT = new URIImpl(
            BASE_URI + "format");
    public static final URI BYTE_SIZE = new URIImpl(
            BASE_URI + "byteSize");
    
}
