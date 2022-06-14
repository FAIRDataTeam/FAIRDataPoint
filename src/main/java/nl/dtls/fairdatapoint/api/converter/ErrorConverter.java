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
package nl.dtls.fairdatapoint.api.converter;

import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;

import java.io.IOException;

public class ErrorConverter extends AbstractHttpMessageConverter<ErrorDTO> {

    private final RDFFormat format;

    public ErrorConverter(RDFFormat format) {
        super(getMediaTypes(format));
        this.format = format;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return ErrorDTO.class.isAssignableFrom(clazz);
    }

    @Override
    protected ErrorDTO readInternal(
            Class<? extends ErrorDTO> aClass, HttpInputMessage inputMessage
    ) throws HttpMessageNotReadableException {
        return new ErrorDTO();
    }

    @Override
    protected void writeInternal(
            ErrorDTO errorDTO, HttpOutputMessage outputMessage
    ) throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(errorDTO.getMessage().getBytes());
    }

    private static MediaType[] getMediaTypes(RDFFormat format) {
        return format.getMIMETypes()
                .stream()
                .map(MediaType::parseMediaType)
                .toArray(MediaType[]::new);
    }

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType(
                format.getDefaultFileExtension(),
                MediaType.parseMediaType(format.getDefaultMIMEType())
        );
    }

}
