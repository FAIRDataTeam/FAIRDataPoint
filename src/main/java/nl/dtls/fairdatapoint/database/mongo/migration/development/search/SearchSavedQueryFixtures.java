package nl.dtls.fairdatapoint.database.mongo.migration.development.search;

import nl.dtls.fairdatapoint.api.dto.search.SearchQueryVariablesDTO;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQuery;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQueryType;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SearchSavedQueryFixtures {

    public SearchSavedQuery savedQueryPublic01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name("All datasets")
                .description("Quickly query all datasets (DCAT)")
                .type(SearchSavedQueryType.PUBLIC)
                .userUuid(KnownUUIDs.USER_ALBERT_UUID)
                .variables(SearchQueryVariablesDTO.builder()
                        .prefixes("PREFIX dcat: <http://www.w3.org/ns/dcat#>")
                        .graphPattern("?entity rdf:type dcat:Dataset .")
                        .ordering("ASC(?title)")
                        .build()
                )
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchSavedQuery savedQueryInternal01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name("All distributions")
                .description("Quickly query all distributions (DCAT)")
                .type(SearchSavedQueryType.INTERNAL)
                .userUuid(KnownUUIDs.USER_ADMIN_UUID)
                .variables(SearchQueryVariablesDTO.builder()
                        .prefixes("PREFIX dcat: <http://www.w3.org/ns/dcat#>")
                        .graphPattern("?entity rdf:type dcat:Distribution .")
                        .ordering("ASC(?title)")
                        .build()
                )
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchSavedQuery savedQueryPrivate01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name("Things with data")
                .description("This is private query of Nikola Tesla.")
                .type(SearchSavedQueryType.PRIVATE)
                .userUuid(KnownUUIDs.USER_NIKOLA_UUID)
                .variables(SearchQueryVariablesDTO.builder()
                        .prefixes("")
                        .graphPattern("""
                                ?entity ?relationPredicate ?relationObject .
                                FILTER isLiteral(?relationObject)
                                FILTER CONTAINS(LCASE(str(?relationObject)), LCASE("data"))""")
                        .ordering("ASC(?title)")
                        .build()
                )
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
