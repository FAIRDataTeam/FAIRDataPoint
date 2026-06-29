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
package org.fairdatateam.fairdatapoint.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.fairdatateam.fairdatapoint.api.converter.ErrorConverter;
import org.fairdatateam.fairdatapoint.rdf.RdfConverter;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final List<ErrorConverter> errorConverters;

    private final List<RdfConverter> rdfConverters;

    /**
     * Constructor (autowired)
     */
    public WebMvcConfig(List<ErrorConverter> errorConverters, List<RdfConverter> rdfConverters) {
        this.errorConverters = errorConverters;
        this.rdfConverters = rdfConverters;
    }

    /**
     * Configures message converters. See
     * <a href="https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/message-converters.html">
     * example in Spring docs</a>.
     */
    @Override
    public void configureMessageConverters(HttpMessageConverters.@NonNull ServerBuilder builder) {
        builder.withStringConverter(new StringHttpMessageConverter())
                .withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper()))
                .addCustomConverter(new ByteArrayHttpMessageConverter());
        errorConverters.forEach(builder::addCustomConverter);
        rdfConverters.forEach(builder::addCustomConverter);
    }

    @Override
    public void configureContentNegotiation(@NonNull ContentNegotiationConfigurer configurer) {
        for (ErrorConverter converter : errorConverters) {
            converter.configureContentNegotiation(configurer);
        }
        for (RdfConverter converter : rdfConverters) {
            converter.configureContentNegotiation(configurer);
        }
        configurer.favorParameter(true);
    }

    @Bean
    @Primary
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                // https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind/2.9.8/com/fasterxml/jackson/databind/ObjectMapper.html#findAndRegisterModules--
                // https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md#objectmapper-serialization-inclusion-configuration
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                // https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md#objectmapper-visibility-configuration
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

    /**
     * Global Cross-Origin Resource Sharing (CORS) configuration based on
     * <a href="https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html#mvc-cors-global">
     * Web MVC CORS example</a>
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders(
                        HttpHeaders.ORIGIN,
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.ACCEPT,
                        HttpHeaders.CONTENT_TYPE
                )
                // If the response contains these headers, the browser will expose them in JavaScript
                .exposedHeaders(HttpHeaders.LOCATION, HttpHeaders.LINK);
    }
}
