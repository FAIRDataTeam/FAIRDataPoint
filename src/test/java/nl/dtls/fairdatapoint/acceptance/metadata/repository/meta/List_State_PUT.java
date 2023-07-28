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
package nl.dtls.fairdatapoint.acceptance.metadata.repository.meta;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateChangeDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.metadata.Common.createMetadataStateAlreadyPublished;
import static nl.dtls.fairdatapoint.acceptance.metadata.Common.createMetadataStateChangeToDraft;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /meta/state")
public class List_State_PUT extends WebIntegrationTest {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private GenericMetadataRepository repository;

    private URI url() {
        return URI.create("/meta/state");
    }

    private MetaStateChangeDTO reqDto() {
        return new MetaStateChangeDTO(MetadataState.PUBLISHED);
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() throws MetadataRepositoryException {
        // GIVEN:
        RequestEntity<MetaStateChangeDTO> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .header(HttpHeaders.ACCEPT, "application/json")
                .body(reqDto());
        ParameterizedTypeReference<MetaStateChangeDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // AND: Prepare database (make it a draft)
        repository.moveToDrafts(i(persistentUrl));

        // WHEN:
        ResponseEntity<MetaStateChangeDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody(), is(equalTo(reqDto())));
    }

    @Test
    @DisplayName("HTTP 400: Metadata is already published")
    public void res400_already_published() throws MetadataRepositoryException {
        repository.moveToMain(i(persistentUrl));
        createMetadataStateAlreadyPublished(client, url());
    }

    @Test
    @DisplayName("HTTP 400: You can not change state to DRAFT")
    public void res400_change_to_draft() throws MetadataRepositoryException {
        repository.moveToDrafts(i(persistentUrl));
        createMetadataStateChangeToDraft(client, url());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestPut(client, url(), reqDto());
    }

}
