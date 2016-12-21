/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.converter;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import nl.dtl.fairmetadata.io.DistributionMetadataParser;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.io.MetadataParserException;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.utils.MetadataParserUtils;
import nl.dtl.fairmetadata.utils.MetadataUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Distribution metadata message converter
 * 
 * @author Rajaram Kaliyaperumal, Kees Burger
 * @since 2016-09-19
 * @version 0.1
 */
public class DistributionMetadataConverter extends 
        AbstractMetadataMessageConverter <DistributionMetadata> {

    public DistributionMetadataConverter(RDFFormat format) {
        super(format);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return DistributionMetadata.class.isAssignableFrom(clazz);
    }

    @Override
    protected DistributionMetadata readInternal(Class<? extends 
            DistributionMetadata> type, HttpInputMessage inputMessage) 
            throws IOException, 
            HttpMessageNotReadableException {
        DistributionMetadataParser parser = MetadataParserUtils.
                getDistributionParser();
        try {
            String body = CharStreams.toString(new InputStreamReader(
                    inputMessage.getBody(), Charsets.UTF_8 ));
            return parser.parse(body, null, format);
        } catch (MetadataParserException ex) {
           throw new HttpMessageNotReadableException("", ex);
        }
    }

    @Override
    protected void writeInternal(DistributionMetadata metadata, 
            HttpOutputMessage outputMessage) throws IOException, 
            HttpMessageNotWritableException {
        
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
