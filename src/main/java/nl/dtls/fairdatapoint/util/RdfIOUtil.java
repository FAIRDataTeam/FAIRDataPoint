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
package nl.dtls.fairdatapoint.util;

import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.LDP;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataSetter.setRdfTypes;
import static nl.dtls.fairdatapoint.util.RdfUtil.getSubjectBy;
import static nl.dtls.fairdatapoint.util.ResourceReader.getResource;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.s;

public class RdfIOUtil {

    private static final String MSG_NO_RDF_TYPE = "Validation failed (no rdf:type was provided)";

    private static final String MSG_EXC_IO = "Unable to read RDF (IO exception)";

    private static final String MSG_EXC_PARSE = "Unable to read RDF (parse exception)";

    private static final String MSG_EXC_HANDLER = "Unable to read RDF (handler exception)";

    public static Model changeBaseUri(Model oldModel, String newBaseUri, List<String> rdfTypes) {
        // - get baseUri
        final Resource oldBaseUri = rdfTypes
                .stream()
                .map(rdfType -> getSubjectBy(oldModel, RDF.TYPE, i(rdfType)))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ValidationException(MSG_NO_RDF_TYPE));
        // - sanitize statements
        final List<Statement> sanitizedStatements =
                new ArrayList<>(oldModel)
                        .stream()
                        .map(stmt -> changeSubject(stmt, oldBaseUri.stringValue(), newBaseUri))
                        .toList();
        final Model model = new LinkedHashModel();
        model.addAll(sanitizedStatements);
        setRdfTypes(model, i(newBaseUri), rdfTypes.stream().map(ValueFactoryHelper::i).toList());
        return model;
    }

    private static Statement changeSubject(Statement statement, String oldUri, String newUri) {
        if (statement.getSubject().stringValue().equals(oldUri)) {
            return s(i(newUri), statement.getPredicate(), statement.getObject());
        }
        return statement;
    }

    public static Model readFile(String name, String baseUri) {
        return readFile(name, baseUri, RDFFormat.TURTLE);
    }

    public static Model readFile(String name, String baseUri, RDFFormat format) {
        try (InputStream inputStream = getResource(name).getInputStream()) {
            return Rio.parse(inputStream, baseUri, format);
        }
        catch (IOException exception) {
            throw new ValidationException(MSG_EXC_IO);
        }
        catch (RDFParseException exception) {
            throw new ValidationException(MSG_EXC_PARSE);
        }
        catch (RDFHandlerException exception) {
            throw new ValidationException(MSG_EXC_HANDLER);
        }
    }

    public static Model read(String content, String baseUri) {
        return read(content, baseUri, RDFFormat.TURTLE);
    }

    public static Model read(String content, String baseUri, RDFFormat format) {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            return Rio.parse(inputStream, baseUri, format);
        }
        catch (IOException exception) {
            throw new ValidationException(MSG_EXC_IO);
        }
        catch (RDFParseException exception) {
            throw new ValidationException(MSG_EXC_PARSE);
        }
        catch (RDFHandlerException exception) {
            throw new ValidationException(MSG_EXC_HANDLER);
        }
    }

    public static String write(Model model) {
        return write(model, RDFFormat.TURTLE);
    }

    public static String write(Model model, RDFFormat format) {
        model.setNamespace(DCTERMS.NS);
        model.setNamespace(DCAT.NS);
        model.setNamespace(FOAF.NS);
        model.setNamespace(XMLSchema.NS);
        model.setNamespace(LDP.NS);

        try (StringWriter out = new StringWriter()) {
            Rio.write(model, out, format, getWriterConfig());
            return out.toString();
        }
        catch (IOException exception) {
            throw new ValidationException(MSG_EXC_IO);
        }
    }

    public static WriterConfig getWriterConfig() {
        final WriterConfig config = new WriterConfig();
        config.set(BasicWriterSettings.INLINE_BLANK_NODES, true);
        return config;
    }

}
