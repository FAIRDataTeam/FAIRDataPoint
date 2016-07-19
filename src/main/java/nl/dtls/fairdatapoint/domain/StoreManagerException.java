/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

/**
 *
 * @author rajaram
 */
public class StoreManagerException extends Exception {

    /**
     * Creates a new instance of <code>StoreManagerException</code> without
     * detail message.
     */
    public StoreManagerException() {
    }

    /**
     * Constructs an instance of <code>StoreManagerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public StoreManagerException(String msg) {
        super(msg);
    }
}
