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
package nl.dtls.fairdatapoint.api.controller.index;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.service.UtilityService;
import nl.dtls.fairdatapoint.service.index.event.EventService;
import nl.dtls.fairdatapoint.service.index.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "Index")
@Log4j2
@RestController
@RequestMapping("/index/admin")
public class IndexAdminController {

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private EventService eventService;

    @Autowired
    private WebhookService webhookService;

    @Operation(hidden = true)
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerMetadataRetrieve(@RequestBody @Valid PingDTO reqDto, HttpServletRequest request) {
        log.info("Received ping from {}", utilityService.getRemoteAddr(request));
        final Event event = eventService.acceptAdminTrigger(request, reqDto);
        webhookService.triggerWebhooks(event);
        eventService.triggerMetadataRetrieval(event);
    }

    @Operation(hidden = true)
    @PostMapping("/trigger-all")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerMetadataRetrieveAll(HttpServletRequest request) {
        log.info("Received ping from {}", utilityService.getRemoteAddr(request));
        final Event event = eventService.acceptAdminTriggerAll(request);
        webhookService.triggerWebhooks(event);
        eventService.triggerMetadataRetrieval(event);
    }

    @Operation(hidden = true)
    @PostMapping("/ping-webhook")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webhookPing(@RequestParam(required = true) UUID webhook, HttpServletRequest request) {
        log.info("Received webhook {} ping trigger from {}", webhook, utilityService.getRemoteAddr(request));
        final Event event = webhookService.handleWebhookPing(request, webhook);
        webhookService.triggerWebhooks(event);
    }
}
