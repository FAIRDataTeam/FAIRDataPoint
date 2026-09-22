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
package org.fairdatateam.fairdatapoint.search;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * The SPARQL fragments a saved query is built from, stored in the columns of the saved query
 * itself. Every fragment is mandatory but may be empty: a query without prefixes or without an
 * ordering clause is perfectly normal, a missing column is not.
 */
@Embeddable
@NoArgsConstructor
// Package-private for the same reason as in SearchSavedQuery: @NoArgsConstructor stops Lombok
// from synthesizing the all-args constructor @Builder needs, so it is declared here and kept
// out of the public API.
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@Builder(toBuilder = true)
public class SearchSavedQueryVariables {

    @Column(name = "var_prefixes", nullable = false)
    private String prefixes;

    @Column(name = "var_graph_pattern", nullable = false)
    private String graphPattern;

    @Column(name = "var_ordering", nullable = false)
    private String ordering;

}
