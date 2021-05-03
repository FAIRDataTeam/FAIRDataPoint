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

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.String.format;

@Component
public class CORSFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(final HttpServletRequest request,
                                 final HttpServletResponse response, final FilterChain fc)
            throws IOException, ServletException {

        String allowedMtds = String.join(",", RequestMethod.GET.name(), RequestMethod.POST.name(),
                RequestMethod.PUT.name(), RequestMethod.PATCH.name(), RequestMethod.DELETE.name());

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                format("%s,%s,%s,%s", HttpHeaders.ORIGIN, HttpHeaders.AUTHORIZATION, HttpHeaders.ACCEPT,
                        HttpHeaders.CONTENT_TYPE));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, format("%s,%s", HttpHeaders.LOCATION,
                HttpHeaders.LINK));
        response.setHeader(HttpHeaders.ALLOW, allowedMtds);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMtds);

        fc.doFilter(request, response);
    }
}
