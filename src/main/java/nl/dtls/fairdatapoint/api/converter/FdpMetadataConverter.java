/**
 * The MIT License
 * Copyright © 2016 DTL
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
package nl.dtls.fairdatapoint.api.converter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.google.common.collect.Lists;

import nl.dtl.fairmetadata.io.FDPMetadataParser;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtl.fairmetadata.utils.MetadataParserUtils;
import nl.dtl.fairmetadata.utils.MetadataUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

public class FdpMetadataConverter extends AbstractMetadataMessageConverter
        <FDPMetadata> {
    
    public FdpMetadataConverter(RDFFormat format) {
        super(format);
    }
    
    @Override
    protected boolean supports(Class<?> clazz) {
        return FDPMetadata.class.isAssignableFrom(clazz);
    }

    @Override
    protected FDPMetadata readInternal(Class<? extends FDPMetadata> clazz,
            HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        Model model;
        try {
            model = Rio.parse(inputMessage.getBody(), "", format);
        } catch (RDFParseException e) {
            throw new HttpMessageNotReadableException("", e);
        }
        
        FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        return parser.parse(Lists.newArrayList(model), f.createIRI(null));
    }
    
    
    
    @Override
    protected void writeInternal(FDPMetadata metadata, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        String result;
        try {
            result = MetadataUtils.getString(metadata, format);
        } catch (MetadataException e) {
            throw new HttpMessageNotWritableException("", e);
        }
        
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(),
                StandardCharsets.UTF_8);
        writer.write(result);
        writer.close();
    }
}
