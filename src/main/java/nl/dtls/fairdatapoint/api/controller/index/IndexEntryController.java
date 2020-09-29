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

import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDetailDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryInfoDTO;
import nl.dtls.fairdatapoint.service.index.entry.IndexEntryMapper;
import nl.dtls.fairdatapoint.service.index.entry.IndexEntryService;
import nl.dtls.fairdatapoint.service.index.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/index/entries")
public class IndexEntryController {

    @Autowired
    private IndexEntryService service;

    @Autowired
    private EventService eventService;

    @Autowired
    private IndexEntryMapper mapper;

    @GetMapping("")
    public Page<IndexEntryDTO> getEntriesPage(Pageable pageable,
                                              @RequestParam(required = false, defaultValue = "") String state) {
        return service.getEntriesPage(pageable, state).map(mapper::toDTO);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public Optional<IndexEntryDetailDTO> getEntry(@PathVariable final String uuid) {
        return service.getEntry(uuid).map(entry -> mapper.toDetailDTO(entry,
                eventService.getEvents(entry.getUuid())));
    }

    @GetMapping("/all")
    public List<IndexEntryDTO> getEntriesAll() {
        return StreamSupport.stream(service.getAllEntries().spliterator(), true).map(mapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/info")
    public IndexEntryInfoDTO getEntriesInfo() {
        return service.getEntriesInfo();
    }
}
