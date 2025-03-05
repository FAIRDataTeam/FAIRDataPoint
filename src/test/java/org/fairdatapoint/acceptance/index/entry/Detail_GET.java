package org.fairdatapoint.acceptance.index.entry;

import org.fairdatapoint.WebIntegrationTest;
import org.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import org.fairdatapoint.database.db.repository.IndexEntryRepository;
import org.fairdatapoint.entity.index.entry.IndexEntry;
import org.fairdatapoint.utils.CustomPageImpl;
import org.fairdatapoint.utils.TestIndexEntryFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /index/entries/{uuid}")
public class Detail_GET extends WebIntegrationTest {
    @Autowired
    private IndexEntryRepository indexEntryRepository;

    private final ParameterizedTypeReference<CustomPageImpl<IndexEntryDTO>> responseType =
            new ParameterizedTypeReference<>() {};

    private URI url(UUID uuid) {
        return URI.create("/index/entries/" + uuid);
    }

    @Test
    @DisplayName("HTTP 200: get details")
    public void res200_getDetails() {
        // test for issue #643

        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        IndexEntry entry =  TestIndexEntryFixtures.entryExample();
        indexEntryRepository.save(entry);

        // AND: prepare request
        RequestEntity request = RequestEntity
                .get(url(entry.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }
}
