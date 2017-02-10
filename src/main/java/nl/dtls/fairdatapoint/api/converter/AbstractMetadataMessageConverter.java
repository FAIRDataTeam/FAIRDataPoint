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

import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import nl.dtl.fairmetadata4j.model.Metadata;
import org.apache.logging.log4j.LogManager;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Abstract base class for {@link Metadata} based {@link HttpMessageConverter
 * HttpMessageConverters}.
 * @param <T> {@link Metadata} instance this converter provides conversion for 
 */
public abstract class AbstractMetadataMessageConverter<T extends Metadata> extends AbstractHttpMessageConverter<T> {
    protected RDFFormat format;
    
     private static final org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(AbstractMetadataMessageConverter.class);
    
    public AbstractMetadataMessageConverter(RDFFormat format) {
        super(getMediaTypes(format));
        this.format = format;
    }
    
    /**
     * Visitor method to configure content negotiation for this converter. 
     * @param configurer {@link WebMvcConfigurerAdapter#configureContentNegotiation(ContentNegotiationConfigurer)
     *        WebMvcConfigurerAdapter} configurer instance.
     */
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {           
        configurer.mediaType(format.getDefaultFileExtension(), 
                MediaType.parseMediaType(format.getDefaultMIMEType()));
           
        LOGGER.info("registering {} with {}", format.getDefaultFileExtension(), 
                format.getDefaultMIMEType());          
    }
    
    /**
     * Convenience method for transforming the mimetypes of a {@link RDFFormat}
     * into {@link MediaType} objecs Spring understands.
     * @param format the {@link RDFFormat} this converter supports
     * @return array of {@link MediaType MediaTypes} based on {@link RDFFormat#getMIMETypes()}
     */
    private static MediaType[] getMediaTypes(RDFFormat format) {
        return format.getMIMETypes()
                .stream()
                .map(MediaType::parseMediaType)
                .toArray(MediaType[]::new);
    }
}
