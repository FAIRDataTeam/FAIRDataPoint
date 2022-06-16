package nl.dtls.fairdatapoint.entity.search;


import lombok.*;
import nl.dtls.fairdatapoint.api.dto.search.SearchQueryVariablesDTO;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class SearchSavedQuery {

    @Id
    private ObjectId id;

    private String uuid;

    private String name;

    private String description;

    private String userUuid;

    private SearchSavedQueryType type;

    private Instant createdAt;

    private Instant updatedAt;

    private SearchQueryVariablesDTO variables;
}
