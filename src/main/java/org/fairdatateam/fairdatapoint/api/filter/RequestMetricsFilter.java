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
import java.util.Optional;

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
        log.debug("Actuator counting request for {}", request.getRequestURL());
        // From [servlet spec]: requestURI = contextPath + servletPath + pathInfo
        // [servlet spec]: https://jakarta.ee/specifications/servlet/6.1/jakarta-servlet-spec-6.1#request-path-elements
        final Optional<String> query = Optional.ofNullable(request.getQueryString());
        meterRegistry.counter(
                "custom.http.server.requests",
                "method", request.getMethod(),
                "query", query.orElseGet(() -> ""),
                "uri", request.getRequestURI()
        ).increment();
        filterChain.doFilter(request, response);
    }
}
