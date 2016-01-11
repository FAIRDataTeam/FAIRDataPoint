/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-07
 * @version 0.1
 */
public class DataAccessorServiceException extends Exception {

    /**
     * Creates a new instance of <code>DataAccessorServiceException</code>
     * without detail message.
     */
    public DataAccessorServiceException() {
    }

    /**
     * Constructs an instance of <code>DataAccessorServiceException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DataAccessorServiceException(String msg) {
        super(msg);
    }
}
