package nl.dtls.fairdatapoint.aoipmh;

import java.util.List;

public class ListItemsResults {
    private final boolean hasMore;
    private final List<Item> results;
    private int totalResults = -1;

    public ListItemsResults(boolean hasMoreResults, List<Item> results) {
        this.hasMore = hasMoreResults;
        this.results = results;
    }

    public ListItemsResults(boolean hasMoreResults, List<Item> results, int total) {
        this.hasMore = hasMoreResults;
        this.results = results;
        this.totalResults = total;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public List<Item> getResults() {
        return results;
    }

    public boolean hasTotalResults() {
        return this.totalResults > 0;
    }

    public int getTotal() {
        return this.totalResults;
    }
}
