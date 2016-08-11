/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

/**
 *
 * @author rajaram
 */
public class MetadataExeception extends Exception {

    /**
     * Creates a new instance of <code>CatalogMetadataExeception</code> without
     * detail message.
     */
    public MetadataExeception() {
    }

    /**
     * Constructs an instance of <code>CatalogMetadataExeception</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public MetadataExeception(String msg) {
        super(msg);
    }
}
