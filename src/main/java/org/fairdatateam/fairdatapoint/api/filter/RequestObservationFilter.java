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

package org.fairdatateam.fairdatapoint.api.filter;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class RequestObservationFilter implements ObservationFilter {

    /**
     * Replaces the actuator metrics http.server.requests default `uri` path pattern values with full paths.
     * @param context Micrometer Observation.Context
     * @return modified context
     */
    @Override
    public Observation.Context map(Observation.Context context) {
        if (context instanceof ServerRequestObservationContext serverContext) {
            log.debug("Using customized micrometer observation context");
            final Optional<String> requestURI = Optional.ofNullable(serverContext.getCarrier().getRequestURI());
            // Replace the default URI path pattern (e.g. `/catalog/{id}`) with the full URI path (e.g. `/catalog/123`).
            // This is high-cardinality data, but we add it as low-cardinality to make sure it shows up in the metrics.
            // Beware, this could lead to memory issues in case of excessive number of URIs.
            // See micrometer docs for more info.
            context.addLowCardinalityKeyValue(KeyValue.of("uri", requestURI.orElse("")));
        }
        return context;
    }
}
