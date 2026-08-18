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
package org.fairdatateam.fairdatapoint.service.rdf;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.fairdatateam.fairdatapoint.rdf.RdfValidationException;
import org.fairdatateam.fairdatapoint.rdf.StandaloneShaclValidator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class StandaloneShaclValidatorTest {

    private static final String BASE_URI = "";

    private static final String SHACL_SCHEMA = """
            @prefix dcat: <http://www.w3.org/ns/dcat#> .
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            
            [] a sh:NodeShape ;
              sh:targetClass dcat:Resource ;
              sh:property [
                sh:path dcat:version ;
                sh:nodeKind sh:Literal ;
                sh:minCount 1 ;
                sh:maxCount 1 ;
              ] .
            """;

    private static final String DATA_VALID = """
            @prefix dcat: <http://www.w3.org/ns/dcat#> .
            
            <http://fdp.example.org> a dcat:Resource ;
              dcat:version "1.0" .
            """;

    private static final String DATA_INVALID = """
            @prefix dcat: <http://www.w3.org/ns/dcat#> .
            
            <http://fdp.example.org> a dcat:Resource .
            """;

    private final Model shaclSchema;


    private final StandaloneShaclValidator standaloneShaclValidator;

    /**
     * Constructor (not a spring test, so no autowiring)
     */
    public StandaloneShaclValidatorTest() throws IOException {
        this.standaloneShaclValidator = new StandaloneShaclValidator();
        this.shaclSchema = Rio.parse(new StringReader(SHACL_SCHEMA), BASE_URI, RDFFormat.TURTLE);
    }

    @Test
    public void validDataPassesValidation() throws IOException {
        // given rdf data matching the shacl shape
        final Model validData = Rio.parse(new StringReader(DATA_VALID), BASE_URI, RDFFormat.TURTLE);

        // validation succeeds
        standaloneShaclValidator.validate(shaclSchema, validData, BASE_URI);
    }

    @Test
    public void invalidDataCausesValidationException() throws IOException {
        // given rdf data that does not match the shacl shape
        final Model invalidData = Rio.parse(new StringReader(DATA_INVALID), BASE_URI, RDFFormat.TURTLE);

        // a validation exception is raised
        RdfValidationException exception = assertThrows(RdfValidationException.class,
                () -> standaloneShaclValidator.validate(shaclSchema, invalidData, BASE_URI));

        // the exception contains a validation report indicating the cause of failure
        final Model validationReportModel = exception.getModel();
        assertTrue(validationReportModel.contains(null, SHACL.MIN_COUNT, null));
    }
}
