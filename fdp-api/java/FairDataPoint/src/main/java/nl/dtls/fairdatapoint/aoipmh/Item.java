package nl.dtls.fairdatapoint.aoipmh;

import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.writables.About;
import nl.dtls.fairdatapoint.aoipmh.writables.Metadata;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public interface Item extends ItemIdentifier {

    public List<About> getAbout();

    public Metadata getMetadata();
}
