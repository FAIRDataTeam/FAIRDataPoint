package nl.dtls.fairdatapoint.api.converter;

import org.openrdf.rio.RDFFormat;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import nl.dtl.fairmetadata.model.Metadata;
import org.apache.logging.log4j.LogManager;

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

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
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
