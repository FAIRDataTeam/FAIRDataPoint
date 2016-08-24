package nl.dtls.fairdatapoint.aoipmh;

import java.util.List;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
*/
public class ListItemIdentifiersResult {
    private final boolean hasMore;
    private final List<ItemIdentifier> results;
    private int totalResults = -1;

    public ListItemIdentifiersResult(boolean hasMoreResults, List<ItemIdentifier> results) {
        this.hasMore = hasMoreResults;
        this.results = results;
    }

    public ListItemIdentifiersResult(boolean hasMoreResults, List<ItemIdentifier> results, int totalResults) {
        this.hasMore = hasMoreResults;
        this.results = results;
        this.totalResults = totalResults;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public List<ItemIdentifier> getResults() {
        return results;
    }

    public boolean hasTotalResults() {
        return this.totalResults > 0;
    }

    public int getTotal() {
        return this.totalResults;
    }
}

