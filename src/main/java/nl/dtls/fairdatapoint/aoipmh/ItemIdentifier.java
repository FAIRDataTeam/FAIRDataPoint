/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Shamanou van Leeuwen
 */
public interface ItemIdentifier {

    public String getIdentifier();

    public Date getDatestamp();

    public List<Set> getSets();

    public boolean isDeleted();
}

