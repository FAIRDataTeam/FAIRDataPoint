package nl.dtls.fairdatapoint.aoipmh;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public interface ItemIdentifier {

    public String getIdentifier();

    public Date getDatestamp();

    public List<Set> getSets();

    public boolean isDeleted();
}

