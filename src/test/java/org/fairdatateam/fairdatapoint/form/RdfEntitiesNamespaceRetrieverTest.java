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
package org.fairdatateam.fairdatapoint.form;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * A tiny JDK {@link HttpServer} bound to loopback stands in for an internal service (an
 * admin UI, a database, a cloud metadata endpoint) that a caller-supplied rdfType must not
 * be able to reach. Asserting that this server is never contacted checks the real outcome
 * — no request reaches the network — rather than mocking the private {@code retrieve(URL,
 * IRI)} call and asserting it is never invoked, which would stop catching a regression the
 * moment that internal call shape changed, even if the method it feeds kept making the
 * request.
 */
class RdfEntitiesNamespaceRetrieverTest {

    private final RdfEntitiesNamespaceRetriever retriever = new RdfEntitiesNamespaceRetriever();

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void doesNotFetchFromALoopbackAddress() throws IOException {
        // GIVEN: a server listening only on loopback, reachable from this process but not
        // from an external caller — exactly the kind of internal target this endpoint must
        // not act as a proxy towards.
        final AtomicInteger requestsReceived = new AtomicInteger(0);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requestsReceived.incrementAndGet();
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.start();
        final int port = server.getAddress().getPort();

        // WHEN: a caller asks for a type whose namespace "lives" on that loopback address.
        final Map<String, String> result = retriever.retrieve("http://127.0.0.1:" + port + "/ns#Thing");

        // THEN: the server is never contacted, and the caller gets nothing back — not an
        // error that would tell them the address was reachable but empty.
        assertThat(requestsReceived.get(), is(equalTo(0)));
        assertThat(result, is(nullValue()));
    }

    @Test
    void rejectsANonHttpScheme() {
        // A file: IRI must not even reach the loader, regardless of what parsing its
        // target as RDF would or would not produce.
        final Map<String, String> result = retriever.retrieve("file:///etc/hostname#Thing");

        assertThat(result, is(nullValue()));
    }

    @Test
    void rejectsAnUnparseableUrl() {
        // getNamespaceUrl() truncates at the last "/", which throws StringIndexOutOfBounds
        // for an rdfType with neither a "#" nor a "/" at all, and new URL(...) throws
        // MalformedURLException for a scheme the JVM has no handler for. Neither should
        // reach the caller as an unhandled exception.
        assertThat(retriever.retrieve("urn:no-slash-or-fragment"), is(nullValue()));
        assertThat(retriever.retrieve("foo-bogus-scheme://host/path#Thing"), is(nullValue()));
    }

}
