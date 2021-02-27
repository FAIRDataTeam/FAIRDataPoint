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

import io.swagger.annotations.ApiOperation;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.service.UtilityService;
import nl.dtls.fairdatapoint.service.index.event.EventService;
import nl.dtls.fairdatapoint.service.index.harvester.HarvesterService;
import nl.dtls.fairdatapoint.service.index.settings.IndexSettingsService;
import nl.dtls.fairdatapoint.service.index.webhook.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class PingController {
    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private HarvesterService harvesterService;

    @Autowired
    private IndexSettingsService indexSettingsService;
    
    @Autowired
    private UtilityService utilityService;

    @ApiOperation(
            value = "Ping payload with FAIR Data Point info",
            notes = "Inform about running FAIR Data Point. It is expected to send pings regularly (at least weekly). " +
                    "There is a rate limit set both per single IP within a period of time and per URL in message."
    )
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> receivePing(@RequestBody @Valid PingDTO reqDto, HttpServletRequest request) throws MetadataRepositoryException {
        logger.info("Received ping from {}", utilityService.getRemoteAddr(request));
        if (indexSettingsService.isPingDenied(reqDto)) {
            logger.info("Received ping is denied");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        final Event event = eventService.acceptIncomingPing(reqDto, request);
        logger.info("Triggering metadata retrieval for {}", event.getRelatedTo().getClientUrl());
        eventService.triggerMetadataRetrieval(event);
        harvesterService.harvest(reqDto.getClientUrl());
        webhookService.triggerWebhooks(event);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
