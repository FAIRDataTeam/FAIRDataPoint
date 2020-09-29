package nl.dtls.fairdatapoint.entity.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchResultRelation {

    private String predicate;

    private String object;

}
