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
package nl.dtls.fairdatapoint.config;

import nl.dtls.fairdatapoint.api.converter.ErrorConverter;
import nl.dtls.fairdatapoint.api.converter.RdfConverter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

    @Bean
    public ErrorConverter turtleError() {
        return new ErrorConverter(RDFFormat.TURTLE);
    }

    @Bean
    public ErrorConverter jsonldError() {
        return new ErrorConverter(RDFFormat.JSONLD);
    }

    @Bean
    public ErrorConverter n3Error() {
        return new ErrorConverter(RDFFormat.N3);
    }

    @Bean
    public ErrorConverter rdfxmlError() {
        return new ErrorConverter(RDFFormat.RDFXML);
    }

    @Bean
    public RdfConverter turtleModel() {
        return new RdfConverter(RDFFormat.TURTLE);
    }

    @Bean
    public RdfConverter jsonldModel() {
        return new RdfConverter(RDFFormat.JSONLD);
    }

    @Bean
    public RdfConverter n3Model() {
        return new RdfConverter(RDFFormat.N3);
    }

    @Bean
    public RdfConverter rdfxmlModel() {
        return new RdfConverter(RDFFormat.RDFXML);
    }

}
