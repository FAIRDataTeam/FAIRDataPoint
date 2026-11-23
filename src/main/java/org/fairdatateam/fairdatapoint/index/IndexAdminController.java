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
package org.fairdatateam.fairdatapoint.index;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryService;
import org.fairdatateam.fairdatapoint.index.event.dto.PingDTO;
import org.fairdatateam.fairdatapoint.index.event.Event;
import org.fairdatateam.fairdatapoint.index.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Index")
@RestController
@RequestMapping("/index/admin")
@RequiredArgsConstructor
public class IndexAdminController {

    private final EventService eventService;

    private final IndexEntryService indexEntryService;

    /**
     * Triggers basic metadata retrieval, followed by harvesting, for the FDP specified in the request body.
     */
    @Operation(hidden = true)
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerSingleMetadataRetrievalAndHarvesting(
            @RequestBody @Valid PingDTO pingDTO,
            HttpServletRequest request
    ) throws RdfRepositoryException {
        final String clientUrl = pingDTO.getClientUrl();
        final Event event = eventService.createAdminTriggerEvent(request.getRemoteAddr(), clientUrl);
        eventService.triggerMetadataRetrieval(event);
        indexEntryService.harvest(clientUrl);
    }

    /**
     * Triggers basic metadata retrieval for all registered FDPs. Does not trigger harvesting.
     */
    @Operation(hidden = true)
    @PostMapping("/trigger-all")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerAllMetadataRetrievals(HttpServletRequest request) {
        final Event event = eventService.createAdminTriggerAllEvent(request.getRemoteAddr());
        eventService.triggerMetadataRetrieval(event);
    }
}
