package nl.dtls.fairdatapoint.aoipmh;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public abstract class FilterResolver {
    public abstract Filter getFilter (Condition condition);
}
