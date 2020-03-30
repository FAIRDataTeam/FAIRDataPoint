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
package nl.dtls.fairdatapoint.service.rdf;

import nl.dtls.fairdatapoint.entity.exception.RdfValidationException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSailValidationException;
import org.springframework.stereotype.Service;

import java.io.*;

import static nl.dtls.fairdatapoint.util.ResourceReader.getResource;

@Service
public class RdfFileService {

    public void validate(String shacl, String data) throws MetadataServiceException {
        validate(shacl, data, "");
    }

    public void validate(String shacl, String data, String baseUri) throws MetadataServiceException {
        // 1. Prepare repository
        ShaclSail shaclSail = new ShaclSail(new MemoryStore());
        shaclSail.setRdfsSubClassReasoning(true);
        shaclSail.setUndefinedTargetValidatesAllSubjects(true);
        SailRepository sailRepository = new SailRepository(shaclSail);
        sailRepository.init();

        try (SailRepositoryConnection connection = sailRepository.getConnection()) {
            // 2. Save Shacl
            connection.begin();
            connection.add(new StringReader(shacl), "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
            connection.commit();

            // 3. Validate data
            connection.begin();
            connection.add(new StringReader(data), baseUri, RDFFormat.TURTLE);
            connection.commit();

        } catch (RepositoryException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof ShaclSailValidationException) {
                Model validationReportModel = ((ShaclSailValidationException) cause).validationReportAsModel();
                throw new RdfValidationException(validationReportModel);
            }
            throw new MetadataServiceException("Validation failed (unsupported exception");
        } catch (IOException e) {
            throw new MetadataServiceException("Failed to create a connection to repository for a validation");
        }
    }

    public Model readFile(String name, String baseUri) {
        try (InputStream inputStream = getResource(name).getInputStream()) {
            return Rio.parse(inputStream, baseUri, RDFFormat.TURTLE);
        } catch (IOException e) {
            // handle IO problems (e.g. the file could not be read)
            System.out.println("IOException");
            e.printStackTrace();
        } catch (RDFParseException e) {
            // handle unrecoverable parse error
            System.out.println("RDFParseException");
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            // handle a problem encountered by the RDFHandler
            System.out.println("RDFHandlerException");
            e.printStackTrace();
        }
        return null;
    }

    public Model read(String content, String baseUri) {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            return Rio.parse(inputStream, baseUri, RDFFormat.TURTLE);
        } catch (IOException e) {
            // handle IO problems (e.g. the file could not be read)
            System.out.println("IOException");
            e.printStackTrace();
        } catch (RDFParseException e) {
            // handle unrecoverable parse error
            System.out.println("RDFParseException");
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            // handle a problem encountered by the RDFHandler
            System.out.println("RDFHandlerException");
            e.printStackTrace();
        }
        return null;
    }

    public String write(Model model, RDFFormat format) {
        try (StringWriter out = new StringWriter()) {
            Rio.write(model, out, format, getWriterConfig());
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String write(Model model) {
        return write(model, RDFFormat.TURTLE);
    }

    public static WriterConfig getWriterConfig() {
        WriterConfig config = new WriterConfig();
        config.set(BasicWriterSettings.INLINE_BLANK_NODES, true);
        return config;
    }

}
