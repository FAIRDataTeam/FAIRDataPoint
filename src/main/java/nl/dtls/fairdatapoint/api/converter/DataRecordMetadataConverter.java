/**
 * The MIT License
 * Copyright © 2017 DTL
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
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
package nl.dtls.fairdatapoint.api.converter;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import nl.dtl.fairmetadata4j.io.DataRecordMetadataParser;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.io.MetadataParserException;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.utils.MetadataParserUtils;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author rajaram
 */
public class DataRecordMetadataConverter extends
        AbstractMetadataMessageConverter<DataRecordMetadata> {

    public DataRecordMetadataConverter(RDFFormat format) {
        super(format);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DataRecordMetadata.class.isAssignableFrom(clazz);
    }

    @Override
    protected DataRecordMetadata readInternal(Class<? extends DataRecordMetadata> type,
                                              HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {

        DataRecordMetadataParser parser = MetadataParserUtils.getDataRecordParser();
        try {
            String body = CharStreams.toString(new InputStreamReader(
                    inputMessage.getBody(), Charsets.UTF_8));

            return parser.parse(body, null, format);
        } catch (MetadataParserException ex) {
            throw new HttpMessageNotReadableException("", ex);
        }
    }

    @Override
    protected void writeInternal(DataRecordMetadata metadata, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        String result;
        try {
            result = MetadataUtils.getString(metadata, format);
        } catch (MetadataException e) {
            throw new HttpMessageNotWritableException("", e);
        }

        OutputStreamWriter writer = new OutputStreamWriter(
                outputMessage.getBody(), StandardCharsets.UTF_8);
        writer.write(result);
        writer.close();

    }
}
