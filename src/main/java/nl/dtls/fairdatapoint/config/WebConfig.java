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
package nl.dtls.fairdatapoint.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dtls.fairdatapoint.api.converter.ErrorConverter;
import nl.dtls.fairdatapoint.api.converter.RdfConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springdoc.core.Constants.CLASSPATH_RESOURCE_LOCATION;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private List<ErrorConverter> errorConverters;

    @Autowired
    private List<RdfConverter> rdfConverters;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.addAll(errorConverters);
        converters.addAll(rdfConverters);
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        for (ErrorConverter converter : errorConverters) {
            converter.configureContentNegotiation(configurer);
        }
        for (RdfConverter converter : rdfConverters) {
            converter.configureContentNegotiation(configurer);
        }
        configurer.favorParameter(true);
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // Swagger
        registry.setOrder(Integer.MIN_VALUE + 1).addResourceHandler("/webjars/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATION+"/webjars/")
                .resourceChain(true)
                .addResolver(new WebJarsResourceResolver());
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }
}
