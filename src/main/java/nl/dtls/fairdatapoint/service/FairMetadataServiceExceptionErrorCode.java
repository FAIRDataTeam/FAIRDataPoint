/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-19
 * @version 0.1
 */
public class FairMetadataServiceExceptionErrorCode {
    
    public final static int RESOURCE_EXIST = HttpServletResponse.SC_CONFLICT;
    
}
