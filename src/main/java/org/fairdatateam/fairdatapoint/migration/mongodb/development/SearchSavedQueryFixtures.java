/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.migration.mongodb.development;

import org.fairdatateam.fairdatapoint.search.SearchSavedQuery;
import org.fairdatateam.fairdatapoint.search.SearchSavedQueryType;
import org.fairdatateam.fairdatapoint.search.SearchSavedQueryVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SearchSavedQueryFixtures {

    private static final String PREFIX_DCAT = "PREFIX dcat: <http://www.w3.org/ns/dcat#>";

    private static final String ORDER_TITLE = "ASC(?title)";

    @Autowired
    private UserFixtures userFixtures;

    public SearchSavedQuery savedQueryPublic01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID())
                .name("All datasets")
                .description("Quickly query all datasets (DCAT)")
                .type(SearchSavedQueryType.PUBLIC)
                .user(userFixtures.albert())
                .variables(SearchSavedQueryVariables.builder()
                        .prefixes(PREFIX_DCAT)
                        .graphPattern("?entity rdf:type dcat:Dataset .")
                        .ordering(ORDER_TITLE)
                        .build()
                )
                .build();
    }

    public SearchSavedQuery savedQueryInternal01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID())
                .name("All distributions")
                .description("Quickly query all distributions (DCAT)")
                .type(SearchSavedQueryType.INTERNAL)
                .user(userFixtures.admin())
                .variables(SearchSavedQueryVariables.builder()
                        .prefixes(PREFIX_DCAT)
                        .graphPattern("?entity rdf:type dcat:Distribution .")
                        .ordering(ORDER_TITLE)
                        .build()
                )
                .build();
    }

    public SearchSavedQuery savedQueryPrivate01() {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID())
                .name("Things with data")
                .description("This is private query of Nikola Tesla.")
                .type(SearchSavedQueryType.PRIVATE)
                .user(userFixtures.nikola())
                .variables(SearchSavedQueryVariables.builder()
                        .prefixes("")
                        .graphPattern("""
                                ?entity ?relationPredicate ?relationObject .
                                FILTER isLiteral(?relationObject)
                                FILTER CONTAINS(LCASE(str(?relationObject)), LCASE("data"))""")
                        .ordering(ORDER_TITLE)
                        .build()
                )
                .build();
    }
}
