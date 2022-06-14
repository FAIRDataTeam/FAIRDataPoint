/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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

    private static final String PREFIX_DCAT = "PREFIX dcat: <http://www.w3.org/ns/dcat#>";
    private static final String ORDER_TITLE = "ASC(?title)";

    public SearchSavedQuery savedQueryPublic01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name("All datasets")
                .description("Quickly query all datasets (DCAT)")
                .type(SearchSavedQueryType.PUBLIC)
                .userUuid(KnownUUIDs.USER_ALBERT_UUID)
                .variables(SearchQueryVariablesDTO.builder()
                        .prefixes(PREFIX_DCAT)
                        .graphPattern("?entity rdf:type dcat:Dataset .")
                        .ordering(ORDER_TITLE)
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
                        .prefixes(PREFIX_DCAT)
                        .graphPattern("?entity rdf:type dcat:Distribution .")
                        .ordering(ORDER_TITLE)
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
                        .ordering(ORDER_TITLE)
                        .build()
                )
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
