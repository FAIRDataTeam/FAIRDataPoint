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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.shacl.ShaclValidator;
import org.eclipse.rdf4j.sail.shacl.results.ValidationReport;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Slf4j
@Service
public class StandaloneShaclValidator {

    /**
     * Converts an RDF4J Model instance to a String because ShaclValidator.Builder.withShapes() does not accept Model.
     */
    private String modelToString(Model model) {
        final StringWriter writer = new StringWriter();
        Rio.write(model, writer, RDFFormat.TURTLE);
        return writer.toString();
    }

    /**
     * Uses a standalone ShaclValidator to validate <code>data</code> against <code>shacl</code>.
     * Raises <code>RdfValidationException</code> if validation fails.
     */
    public void validate(Model shacl, Model data, String baseUri) {
        // standalone ShaclValidator
        final ShaclValidator.ValidatorWithShapes validator = ShaclValidator.builder()
                .setRdfsSubClassReasoning(true)
                .withShapes(modelToString(shacl), baseUri, RDFFormat.TURTLE)
                .build();

        // TODO: ValidationReport is deprecated due to planned move to other package
        // https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/sail/shacl/results/ValidationReport.html
        final ValidationReport report = validator.validate(modelToString(data), baseUri, RDFFormat.TURTLE);

        if (!report.conforms()) {
            log.info("RDF validation failed: {}", report);
            throw new RdfValidationException(report.asModel());
        }
    }

}
