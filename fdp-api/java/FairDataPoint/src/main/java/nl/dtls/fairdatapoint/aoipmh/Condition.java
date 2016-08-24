package nl.dtls.fairdatapoint.aoipmh;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
*/
public interface Condition {
    public Filter getFilter(FilterResolver fr);
}