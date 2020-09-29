package nl.dtls.fairdatapoint.entity.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchResult {

    private String uri;

    private String title;

    private String description;

    private SearchResultRelation relation;
}
