package org.fairdatateam.fairdatapoint.api.filter;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestMetricsFilter extends OncePerRequestFilter {

    private MeterRegistry meterRegistry;

    /**
     * Constructor
     * @param meterRegistry Default MeterRegistry (autowired)
     */
    public RequestMetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Increment counter for custom actuator metric based on HTTP request content
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     */
    @Override
    public void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws IOException, ServletException {
        final String uri = request.getRequestURI();
        final String method = request.getMethod();
        String query = request.getQueryString();
        if (query == null) {
            query = "";
        }
        String path = request.getServletPath();
        if (path == null) {
            path = "";
        }
        log.debug("Actuator counting request for {}", uri);
        meterRegistry.counter(
                "custom",
                "method", method,
                "path", path,
                "query", query
        ).increment();
        filterChain.doFilter(request, response);
    }
}
