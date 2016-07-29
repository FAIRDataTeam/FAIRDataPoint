package nl.dtls.fairdatapoint.aoipmh;

/**
 *
 * @author Shamanou van Leeuwen
 */
public interface Condition {
    public Filter getFilter(FilterResolver fr);
}