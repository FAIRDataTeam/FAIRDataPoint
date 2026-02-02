package org.fairdatateam.fairdatapoint.config;

import io.micrometer.common.KeyValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;

@Configuration
public class ActuatorConfig {

    /**
     * Replaces the default `uri` path-pattern values with full paths in actuator metrics http.server.requests
     */
    @Bean
    DefaultServerRequestObservationConvention customServerRequestObservationConvention() {
        return new DefaultServerRequestObservationConvention() {
            /**
             * Replace the default URI path pattern (e.g. `/catalog/{id}`) with the full URI path (e.g. `/catalog/123`).
             * This is high-cardinality data, but we add it as low-cardinality to make sure it shows up in the metrics.
             * Beware, this could lead to memory issues in case of excessive number of URIs.
             * See micrometer docs for more info.
             * @param context ServerRequestObservationContext
             * @return KeyValue with full uri
             */
            @Override
            protected KeyValue uri(ServerRequestObservationContext context) {
                // note the resulting tag name is "http.url" instead of "uri"
                return super.httpUrl(context);
            }
        };
    }
}
