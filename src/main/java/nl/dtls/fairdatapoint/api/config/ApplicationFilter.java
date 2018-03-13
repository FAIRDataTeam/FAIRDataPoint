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
package nl.dtls.fairdatapoint.api.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to add mandatory headers to all request.
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-11-19
 * @version 0.1
 */
@Component
public class ApplicationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationFilter.class);

    private static final Marker APIREQUEST = MarkerFactory.getMarker("API-REQUEST");

    @Override
    public void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain fc)
            throws IOException, ServletException {
        String allowedMtds = (RequestMethod.GET.name() + ","
                + RequestMethod.POST.name() + "," + RequestMethod.PATCH.name());
        
        response.setHeader(HttpHeaders.SERVER, "FAIR data point (JAVA)");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.CONTENT_TYPE);
        response.setHeader(HttpHeaders.ALLOW, allowedMtds);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMtds);
        
        // Set information for custom request log pattern 
        ThreadContext.put("ipAddress", request.getRemoteAddr());
        ThreadContext.put("responseStatus", String.valueOf(response.getStatus()));
        ThreadContext.put("requestMethod", request.getMethod());
        ThreadContext.put("requestURI", request.getRequestURI());
        ThreadContext.put("requestProtocol", request.getProtocol());
        ThreadContext.put("responseStatus", String.valueOf(response.getStatus()));
        ThreadContext.put("contentSize", response.getHeader(HttpHeaders.CONTENT_LENGTH));
        // Log API request
        LOGGER.info(APIREQUEST, "");
        fc.doFilter(request, response);
        ThreadContext.clearAll();
    }
}
