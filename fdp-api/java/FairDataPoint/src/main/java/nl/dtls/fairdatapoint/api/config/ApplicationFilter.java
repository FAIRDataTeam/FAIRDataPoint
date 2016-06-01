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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to add mandatory headers to all request.
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.1
 */
@Component
public class ApplicationFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain fc)
            throws IOException, ServletException {
        response.setHeader(HttpHeaders.SERVER, "FAIR data point (JAVA)");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ALLOW, (RequestMethod.GET.name()));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                (HttpHeaders.ACCEPT));
        ThreadContext.put("ipAddress", request.getRemoteAddr());
        ThreadContext.put("responseStatus", String.valueOf(
                response.getStatus()));
        fc.doFilter(request, response);
        ThreadContext.clearAll();
    }
}
