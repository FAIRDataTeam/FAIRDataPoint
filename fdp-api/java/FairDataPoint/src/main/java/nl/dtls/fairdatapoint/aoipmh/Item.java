/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh;

import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.writables.About;
import nl.dtls.fairdatapoint.aoipmh.writables.Metadata;

/**
 *
 * @author Shamanou van Leeuwen
 */
public interface Item extends ItemIdentifier {

    public List<About> getAbout();

    public Metadata getMetadata();
}
