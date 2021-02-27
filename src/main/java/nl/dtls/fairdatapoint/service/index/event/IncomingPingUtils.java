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
package nl.dtls.fairdatapoint.service.index.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.entity.index.event.IncomingPing;
import nl.dtls.fairdatapoint.entity.index.http.Exchange;
import nl.dtls.fairdatapoint.entity.index.http.ExchangeDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class IncomingPingUtils {

    private static final Integer VERSION = 1;

    @Autowired
    private ObjectMapper objectMapper;

    public Event prepareEvent(PingDTO reqDto, HttpServletRequest request, String remoteAddr) {
        var incomingPing = new IncomingPing();
        var ex = new Exchange(ExchangeDirection.INCOMING, remoteAddr);
        incomingPing.setExchange(ex);

        ex.getRequest().setHeaders(getHeaders(request));
        ex.getRequest().setFromHttpServletRequest(request);
        try {
            ex.getRequest().setBody(objectMapper.writeValueAsString(reqDto));
        } catch (JsonProcessingException e) {
            ex.getRequest().setBody(null);
        }

        return new Event(VERSION, incomingPing);
    }

    private Map<String, List<String>> getHeaders(HttpServletRequest request) {
        Map<String, List<String>> map = new HashMap<>();
        Iterator<String> requestI = request.getHeaderNames().asIterator();
        while (requestI.hasNext()) {
            String headerName = requestI.next();
            List<String> headerValues = new ArrayList<>();
            Iterator<String> headerI = request.getHeaders(headerName).asIterator();
            while (headerI.hasNext()) {
                headerValues.add(headerI.next());
            }
            map.put(headerName, headerValues);
        }
        return map;
    }

}
