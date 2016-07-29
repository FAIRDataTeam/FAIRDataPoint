package nl.dtls.fairdatapoint.aoipmh;

import java.util.List;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class ListSetsResult {
    private final boolean hasMore;
    private final List<Set> results;
    private int total = -1;

    public ListSetsResult(boolean hasMoreResults, List<Set> results) {
        this.hasMore = hasMoreResults;
        this.results = results;
    }

    public ListSetsResult(boolean hasMoreResults, List<Set> results, int total) {
        this.hasMore = hasMoreResults;
        this.results = results;
        this.total = total;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public List<Set> getResults() {
        return results;
    }

    public boolean hasTotalResults() {
        return this.total > 0;
    }

    public int getTotalResults() {
        return this.total;
    }
}
