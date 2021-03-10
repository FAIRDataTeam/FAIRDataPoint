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
package nl.dtls.fairdatapoint.api.filter;

import nl.dtls.fairdatapoint.service.UtilityService;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Autowired
    private UtilityService utilityService;

    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response, final FilterChain fc)
            throws IOException, ServletException {

        ThreadContext.put("ipAddress", utilityService.getRemoteAddr(request));
        ThreadContext.put("responseStatus", String.valueOf(response.getStatus()));
        ThreadContext.put("requestMethod", request.getMethod());
        ThreadContext.put("requestURI", request.getRequestURI());
        ThreadContext.put("requestProtocol", request.getProtocol());
        ThreadContext.put("responseStatus", String.valueOf(response.getStatus()));
        ThreadContext.put("contentSize", response.getHeader(HttpHeaders.CONTENT_LENGTH));
        logger.info(request.getRequestURL());

        fc.doFilter(request, response);
        ThreadContext.clearAll();
    }
}
