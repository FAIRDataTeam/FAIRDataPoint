package nl.dtls.fairdatapoint.entity.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchFilterCacheContainer {

    private List<SearchFilterValue> values;
}
