/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.index.event;

import jakarta.servlet.http.HttpServletRequest;
import org.fairdatateam.fairdatapoint.index.event.dto.PingDTO;
import org.fairdatateam.fairdatapoint.index.http.Exchange;
import org.fairdatateam.fairdatapoint.index.http.ExchangeDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;

@Service
public class IncomingPingUtils {

    private static final Integer VERSION = 1;

    @Autowired
    private JsonMapper jsonMapper;

    public Event prepareEvent(PingDTO reqDto, HttpServletRequest request, String remoteAddr) {
        final IncomingPing incomingPing = new IncomingPing();
        final Exchange ex = new Exchange(ExchangeDirection.INCOMING, remoteAddr);
        incomingPing.setExchange(ex);

        ex.getRequest().setHeaders(getHeaders(request));
        ex.getRequest().setFromHttpServletRequest(request);
        try {
            ex.getRequest().setBody(jsonMapper.writeValueAsString(reqDto));
        }
        catch (JacksonException exception) {
            ex.getRequest().setBody(null);
        }

        return new Event(VERSION, incomingPing);
    }

    private Map<String, List<String>> getHeaders(HttpServletRequest request) {
        final Map<String, List<String>> map = new HashMap<>();
        final Iterator<String> requestI = request.getHeaderNames().asIterator();
        while (requestI.hasNext()) {
            final String headerName = requestI.next();
            final List<String> headerValues = new ArrayList<>();
            final Iterator<String> headerI = request.getHeaders(headerName).asIterator();
            while (headerI.hasNext()) {
                headerValues.add(headerI.next());
            }
            map.put(headerName, headerValues);
        }
        return map;
    }

}
