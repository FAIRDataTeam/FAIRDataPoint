package nl.dtls.fairdatapoint.api.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.dtls.fairdatapoint.entity.search.SearchResultRelation;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchResultDTO {

    private String uri;

    private String title;

    private String description;

    private List<SearchResultRelation> relations;

}