package nl.dtls.fairdatapoint.api.converter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.openrdf.model.Model;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
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
        return parser.parse(Lists.newArrayList(model), new URIImpl(null));
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
