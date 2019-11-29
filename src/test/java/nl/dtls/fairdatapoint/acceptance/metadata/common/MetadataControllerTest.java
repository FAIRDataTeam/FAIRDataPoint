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
package nl.dtls.fairdatapoint.acceptance.metadata.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.filter.CORSFilter;
import nl.dtls.fairdatapoint.api.filter.JwtTokenFilter;
import nl.dtls.fairdatapoint.api.filter.LoggingFilter;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.utils.MetadataFixtureLoader;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@EnableWebMvc
@EnableWebSecurity
@DirtiesContext
public abstract class MetadataControllerTest extends BaseIntegrationTest {

    @InjectMocks
    protected final LoggingFilter loggingFilter = new LoggingFilter();

    @InjectMocks
    protected final CORSFilter corsFilter = new CORSFilter();

    @Autowired
    @Qualifier("requestMappingHandlerAdapter")
    protected RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    protected RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MetadataFixtureLoader metadataFixtureLoader;

    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(jwtTokenFilter)
                .alwaysDo(print())
                .build();
    }

    @BeforeEach
    public void setupExampleMetadata() throws MetadataServiceException {
        metadataFixtureLoader.storeExampleMetadata();
    }

}
