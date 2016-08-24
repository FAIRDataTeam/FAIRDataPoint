/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.client;

import com.lyncode.xoai.serviceprovider.exceptions.OAIRequestException;
import java.io.InputStream;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public interface OAIClient {

    public InputStream execute(Parameters prmtrs) throws OAIRequestException;
}
