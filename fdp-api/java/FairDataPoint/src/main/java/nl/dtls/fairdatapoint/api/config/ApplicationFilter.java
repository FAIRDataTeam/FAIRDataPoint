/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.1
 */
@Component
public class ApplicationFilter implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, 
            FilterChain fc) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) sr1;
    response.setHeader(HttpHeaders.SERVER, "FAIR data point (JAVA)");
    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    response.setHeader(HttpHeaders.ALLOW, (RequestMethod.GET.name()));
    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, 
            (HttpHeaders.ACCEPT));
    fc.doFilter(sr, sr1);
    }

    @Override
    public void destroy() {}
    
}
