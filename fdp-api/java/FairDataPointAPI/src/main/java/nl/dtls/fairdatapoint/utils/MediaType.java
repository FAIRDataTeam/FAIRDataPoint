/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

/**
 * MediaType extention for RDF files 
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-20
 * @version 0.1
 */
public class MediaType extends javax.ws.rs.core.MediaType {
    /**
     * Mime type for RDF turtle 
     */
    public final static String TEXT_TURTLE = "text/turtle";
    /**
     * Mime type for JSON LD
     */
    public final static String APPLICATION_JSONLD = "application/ld+json";
    
}
