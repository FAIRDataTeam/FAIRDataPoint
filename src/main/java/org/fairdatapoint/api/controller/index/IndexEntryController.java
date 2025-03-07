/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.api.controller.index;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import org.fairdatapoint.api.dto.index.entry.IndexEntryDetailDTO;
import org.fairdatapoint.api.dto.index.entry.IndexEntryInfoDTO;
import org.fairdatapoint.api.dto.index.entry.IndexEntryUpdateDTO;
import org.fairdatapoint.api.dto.index.ping.PingDTO;
import org.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import org.fairdatapoint.entity.index.entry.IndexEntryPermit;
import org.fairdatapoint.entity.index.event.IndexEvent;
import org.fairdatapoint.service.index.entry.IndexEntryService;
import org.fairdatapoint.service.index.event.EventService;
import org.fairdatapoint.service.index.harvester.HarvesterService;
import org.fairdatapoint.service.index.webhook.WebhookService;
import org.eclipse.rdf4j.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Index")
@RestController
@RequestMapping("/index/entries")
@RequiredArgsConstructor
public class IndexEntryController {
    private static final List<String> sortFields = List.of(
            "clientUrl", "createdAt", "lastRetrievalAt", "permit", "status", "updatedAt"
    );

    private final IndexEntryService service;

    private final HarvesterService harvesterService;

    private final EventService eventService;

    private final WebhookService webhookService;

    private final IndexEntryService indexEntryService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<IndexEntryDTO> getEntriesPage(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String state,
            @RequestParam(required = false, defaultValue = "accepted") String permit
    ) {
        // validate sort query parameters
        // todo: implement validator for Pageable if used more often
        pageable.getSort().stream().forEach(order -> {
            if (! sortFields.contains(order.getProperty())) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Invalid sort parameter: " + order.getProperty());
            }
        });
        return service.getEntriesPageDTOs(pageable, state, permit);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<IndexEntryDetailDTO> getEntry(@PathVariable final UUID uuid) {
        return service.getEntryDetailDTO(uuid);
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<IndexEntryDetailDTO> updateEntry(
            @PathVariable final UUID uuid,
            @RequestBody IndexEntryUpdateDTO reqDto,
            HttpServletRequest request
    ) throws MetadataRepositoryException {
        final Optional<IndexEntryDetailDTO> resDto = service.updateEntry(uuid, reqDto);
        if (resDto.isPresent()) {
            final String clientUrl = resDto.get().getClientUrl();
            if (resDto.get().getPermit().equals(IndexEntryPermit.ACCEPTED)) {
                final IndexEvent event = eventService.acceptAdminTrigger(request, new PingDTO(clientUrl));
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
    public Model getEntryData(@PathVariable final UUID uuid) throws MetadataRepositoryException {
        return service.getEntryHarvestedData(uuid);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable final UUID uuid) throws MetadataRepositoryException {
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
