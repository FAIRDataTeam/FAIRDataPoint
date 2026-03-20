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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDetailDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryInfoDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryUpdateDTO;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryPermit;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.service.index.entry.IndexEntryService;
import nl.dtls.fairdatapoint.service.index.event.EventService;
import nl.dtls.fairdatapoint.service.index.harvester.HarvesterService;
import nl.dtls.fairdatapoint.service.index.webhook.WebhookService;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Index")
@RestController
@RequestMapping("/index/entries")
public class IndexEntryController {

    @Autowired
    private IndexEntryService service;

    @Autowired
    private HarvesterService harvesterService;

    @Autowired
    private EventService eventService;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private IndexEntryService indexEntryService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<IndexEntryDTO> getEntriesPage(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String state,
            @RequestParam(required = false, defaultValue = "accepted") String permit
    ) {
        return service.getEntriesPageDTOs(pageable, state, permit);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<IndexEntryDetailDTO> getEntry(@PathVariable final String uuid) {
        return service.getEntryDetailDTO(uuid);
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<IndexEntryDetailDTO> updateEntry(
            @PathVariable final String uuid,
            @RequestBody IndexEntryUpdateDTO reqDto,
            HttpServletRequest request
    ) throws MetadataRepositoryException {
        final Optional<IndexEntryDetailDTO> resDto = service.updateEntry(uuid, reqDto);
        if (resDto.isPresent()) {
            final String clientUrl = resDto.get().getClientUrl();
            if (resDto.get().getPermit().equals(IndexEntryPermit.ACCEPTED)) {
                final Event event = eventService.acceptAdminTrigger(request, new PingDTO(clientUrl));
                webhookService.triggerWebhooks(event);
                eventService.triggerMetadataRetrieval(event);
                indexEntryService.harvest(clientUrl);
            }
            else {
                harvesterService.deleteHarvestedData(clientUrl);
            }
        }
        return resDto;
    }

    @GetMapping(path = "/{uuid}/data", produces = "!application/json")
    public Model getEntryData(@PathVariable final String uuid) throws MetadataRepositoryException {
        return service.getEntryHarvestedData(uuid);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable final String uuid) throws MetadataRepositoryException {
        service.deleteEntry(uuid);
    }

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IndexEntryDTO> getEntriesAll(
            @RequestParam(required = false, defaultValue = "accepted") String permit
    ) {
        return service.getAllEntriesAsDTOs(permit);
    }

    @GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public IndexEntryInfoDTO getEntriesInfo(
            @RequestParam(required = false, defaultValue = "accepted") String permit
    ) {
        return service.getEntriesInfo(permit);
    }
}
