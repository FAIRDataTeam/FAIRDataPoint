/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.exception;

/**
 * 
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-09-13
 * @version 0.1
 */
public class MetadataControllerException extends Exception {

    /**
     * Creates a new instance of <code>MetadataControllerException</code>
     * without detail message.
     */
    public MetadataControllerException() {
    }

    /**
     * Constructs an instance of <code>MetadataControllerException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MetadataControllerException(String msg) {
        super(msg);
    }
}
