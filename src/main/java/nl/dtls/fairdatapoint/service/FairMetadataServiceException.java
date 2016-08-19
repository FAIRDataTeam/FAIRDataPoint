/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-25
 * @version 0.1
 */
public class FairMetadataServiceException extends Exception {
    
    private int errorCode;

    /**
     * Creates a new instance of <code>LDPServerResponseBodyException</code>
     * without detail message.
     */
    public FairMetadataServiceException() {
    }

    /**
     * Constructs an instance of <code>LDPServerResponseBodyException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public FairMetadataServiceException(String msg) {
        super(msg);
    }
    
    public FairMetadataServiceException(String msg, int errorCode) {        
        super(msg);
        this.setErrorCode(errorCode);
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    private void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
