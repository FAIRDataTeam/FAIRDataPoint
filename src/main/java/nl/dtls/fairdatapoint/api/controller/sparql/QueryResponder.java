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
// Adapted from: https://github.com/eclipse/rdf4j/tree/main/spring-components/spring-boot-sparql-web
package nl.dtls.fairdatapoint.api.controller.sparql;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.common.annotation.Experimental;
import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.impl.SimpleDataset;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriter;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterFactory;
import org.eclipse.rdf4j.query.resultio.BooleanQueryResultWriterRegistry;
import org.eclipse.rdf4j.query.resultio.QueryResultFormat;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Experimental
@RestController
@RequiredArgsConstructor
public class QueryResponder {

    @Autowired
    private final Repository mainRepository;

    @RequestMapping(value = "/sparql", method = RequestMethod.POST, consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public void sparqlPostURLencoded(
            @RequestParam(value = "default-graph-uri", required = false) String defaultGraphUri,
            @RequestParam(value = "named-graph-uri", required = false) String namedGraphUri,
            @RequestParam(value = "query") String query, @RequestHeader(ACCEPT) String acceptHeader,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        doSparql(request, query, acceptHeader, defaultGraphUri, namedGraphUri, response);
    }

    @RequestMapping(value = "/sparql", method = RequestMethod.GET)
    public void sparqlGet(@RequestParam(value = "default-graph-uri", required = false) String defaultGraphUri,
                          @RequestParam(value = "named-graph-uri", required = false) String namedGraphUri,
                          @RequestParam(value = "query") String query, @RequestHeader(ACCEPT) String acceptHeader,
                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        doSparql(request, query, acceptHeader, defaultGraphUri, namedGraphUri, response);
    }

    private void doSparql(HttpServletRequest request, String query, String acceptHeader, String defaultGraphUri,
                          String namedGraphUri, HttpServletResponse response) throws IOException {

        try (RepositoryConnection connection = mainRepository.getConnection()) {
            final Query preparedQuery = connection.prepareQuery(QueryLanguage.SPARQL, query);
            setQueryDataSet(preparedQuery, defaultGraphUri, namedGraphUri, connection);
            for (QueryTypes qts : QueryTypes.values()) {
                if (qts.accepts(preparedQuery, acceptHeader)) {
                    qts.evaluate(preparedQuery, acceptHeader, response, defaultGraphUri, namedGraphUri);
                }
            }
        }
        catch (MalformedQueryException | MismatchingAcceptHeaderException mqe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (IllegalArgumentException exc) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad IRI for default or namedGraphIri");
        }
    }

    /**
     * @see <a href="https://www.w3.org/TR/sparql11-protocol/#dataset">protocol dataset</a>
     * @param query             the query
     * @param defaultGraphUri
     * @param namedGraphUri
     * @param connection
     */
    private void setQueryDataSet(Query query, String defaultGraphUri, String namedGraphUri,
                                 RepositoryConnection connection) {
        if (defaultGraphUri != null || namedGraphUri != null) {
            final SimpleDataset dataset = new SimpleDataset();

            if (defaultGraphUri != null) {
                final IRI defaultIri = connection.getValueFactory().createIRI(defaultGraphUri);
                dataset.addDefaultGraph(defaultIri);
            }

            if (namedGraphUri != null) {
                final IRI namedIri = connection.getValueFactory().createIRI(namedGraphUri);
                dataset.addNamedGraph(namedIri);
            }
            query.setDataset(dataset);
        }
    }

    private enum QueryTypes {
        CONSTRUCT_OR_DESCRIBE(query -> query instanceof GraphQuery, RDFFormat.TURTLE, RDFFormat.NTRIPLES,
                RDFFormat.JSONLD, RDFFormat.RDFXML) {
            @Override
            protected void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri,
                                    String namedGraphUri)
                    throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException {
                final GraphQuery gq = (GraphQuery) q;
                final RDFFormat format = (RDFFormat) bestFormat(acceptHeader);
                response.setContentType(format.getDefaultMIMEType());
                gq.evaluate(Rio.createWriter(format, response.getOutputStream()));
            }
        },
        SELECT(query -> query instanceof TupleQuery, TupleQueryResultFormat.JSON, TupleQueryResultFormat.SPARQL,
                TupleQueryResultFormat.CSV, TupleQueryResultFormat.TSV) {
            @Override
            protected void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri,
                                    String namedGraphUri)
                    throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException {
                final TupleQuery tq = (TupleQuery) q;
                final QueryResultFormat format = (QueryResultFormat) bestFormat(acceptHeader);
                response.setContentType(format.getDefaultMIMEType());
                tq.evaluate(QueryResultIO.createTupleWriter(format, response.getOutputStream()));
            }
        },

        ASK(query -> query instanceof BooleanQuery, BooleanQueryResultFormat.TEXT, BooleanQueryResultFormat.JSON,
                BooleanQueryResultFormat.SPARQL) {
            @Override
            protected void evaluate(Query q, String acceptHeader, HttpServletResponse response, String defaultGraphUri,
                                    String namedGraphUri)
                    throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException {
                final BooleanQuery bq = (BooleanQuery) q;
                final QueryResultFormat format = (QueryResultFormat) bestFormat(acceptHeader);
                response.setContentType(format.getDefaultMIMEType());
                final Optional<BooleanQueryResultWriterFactory> optional = BooleanQueryResultWriterRegistry
                        .getInstance()
                        .get(format);
                if (optional.isPresent()) {
                    final BooleanQueryResultWriter writer = optional.get().getWriter(response.getOutputStream());
                    writer.handleBoolean(bq.evaluate());
                }

            }
        };

        private final FileFormat[] formats;
        private final Predicate<Query> typeChecker;

        QueryTypes(Predicate<Query> typeChecker, FileFormat... formats) {
            this.typeChecker = typeChecker;
            this.formats = formats;
        }

        /**
         * Test if the query is of a type that can be answered. And that the accept headers allow for the response to be
         * send.
         *
         * @param preparedQuery
         * @param acceptHeader
         * @return true if the query is of the right type and acceptHeaders are acceptable.
         * @throws MismatchingAcceptHeaderException
         */
        protected boolean accepts(Query preparedQuery, String acceptHeader) throws MismatchingAcceptHeaderException {
            if (accepts(preparedQuery)) {
                if (acceptHeader == null || acceptHeader.isEmpty()) {
                    return true;
                }
                else {
                    for (FileFormat format : formats) {
                        for (String mimeType : format.getMIMETypes()) {
                            if (acceptHeader.contains(mimeType)) {
                                return true;
                            }
                        }
                    }
                }
                throw new MismatchingAcceptHeaderException();
            }
            return false;
        }

        protected boolean accepts(Query query) {
            return typeChecker.test(query);
        }

        protected abstract void evaluate(Query query, String acceptHeader, HttpServletResponse response,
                                         String defaultGraphUri, String namedGraphUri)
                throws QueryEvaluationException, RDFHandlerException, UnsupportedRDFormatException, IOException;

        protected FileFormat bestFormat(String acceptHeader) {
            if (acceptHeader == null || acceptHeader.isEmpty()) {
                return formats[0];
            }
            else {
                for (FileFormat format : formats) {
                    for (String mimeType : format.getMIMETypes()) {
                        if (acceptHeader.contains(mimeType)) {
                            return format;
                        }
                    }
                }
            }
            return formats[0];
        }
    }

    private static final class MismatchingAcceptHeaderException extends Exception {
        private static final long serialVersionUID = 1L;

    }
}
