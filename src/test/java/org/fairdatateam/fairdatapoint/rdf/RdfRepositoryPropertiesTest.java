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
package org.fairdatateam.fairdatapoint.rdf;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class RdfRepositoryPropertiesTest {

    @Test
    public void virtuosoDispatchesToItsOwnBasicProperties() {
        final RdfRepositoryBasicProperties virtuoso = new RdfRepositoryBasicProperties();
        virtuoso.setUrl("http://localhost:8890");
        virtuoso.setUsername("dba");
        virtuoso.setPassword("secret");

        final RdfRepositoryProperties properties = new RdfRepositoryProperties();
        properties.setType(RdfRepositoryProperties.TYPE_VIRTUOSO);
        properties.setVirtuoso(virtuoso);

        assertThat(properties.getStringType(), is(equalTo("Virtuoso")));
        assertThat(properties.getUrl(), is(equalTo("http://localhost:8890")));
        assertThat(properties.getUsername(), is(equalTo("dba")));
        assertThat(properties.getPassword(), is(equalTo("secret")));
        // Virtuoso has no per-repository namespace concept (a single database per install),
        // matching AllegroGraph and GraphDB's own dispatch, which also return null here.
        assertThat(properties.getRepository(), is(nullValue()));
    }

}
