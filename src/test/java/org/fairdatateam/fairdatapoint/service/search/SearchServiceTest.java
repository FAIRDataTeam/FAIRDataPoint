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
package org.fairdatateam.fairdatapoint.service.search;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.database.rdf.repository.MetadataRepositoryException;
import org.fairdatateam.fairdatapoint.entity.search.SearchFilterValue;
import org.fairdatateam.fairdatapoint.entity.search.SearchResult;
import org.fairdatateam.fairdatapoint.utils.TestRdfMetadataFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataGetter.getTitle;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

public class SearchServiceTest extends BaseIntegrationTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Test
    public void findByLiteralWorks() throws MetadataRepositoryException {
        // given
        final Model catalog1 = testMetadataFixtures.catalog1();
        final Literal title = getTitle(catalog1);

        // when
        List<SearchResult> result = searchService.findByLiteral(title);

        // then
        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void findByFilterPredicateWorks() throws MetadataRepositoryException {
        // given
        // (value from SettingsFixtures.SEARCH_FILTER_TYPE is private)
        final String predicate = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

        // when
        final List<SearchFilterValue> result = searchService.findByFilterPredicate(i(predicate));

        // then
        assertThat(result.size(), is(greaterThan(0)));
    }

}
