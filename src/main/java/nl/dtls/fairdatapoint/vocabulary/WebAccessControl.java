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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * WebAccessControl vocabulary. See {@link
 * <a href="https://www.w3.org/wiki/WebAccessControl">WebAccessControl Vocabulary</a>}.
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @version 0.1
 * @since 2017-02-22
 */
public class WebAccessControl {

    private static final ValueFactory f = SimpleValueFactory.getInstance();
    public static final String PREFIX = "acl";
    public static final String NAMESPACE = "http://www.w3.org/ns/auth/acl#";
    public static final IRI AUTHORIZATION = f.createIRI(NAMESPACE + "Authorization");
    public static final IRI ACCESS_APPEND = f.createIRI(NAMESPACE + "Append");
    public static final IRI ACCESS_WRITE = f.createIRI(NAMESPACE + "Write");
    public static final IRI ACCESS_READ = f.createIRI(NAMESPACE + "Read");
    public static final IRI ACCESS_MODE = f.createIRI(NAMESPACE + "mode");
    public static final IRI ACCESS_AGENT = f.createIRI(NAMESPACE + "agent");
    public static final IRI ACCESS_TO = f.createIRI(NAMESPACE + "accessTo");

}
