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
package nl.dtls.fairdatapoint.acceptance.resource;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /resource-definitions/:resourceDefinitionUuid")
public class Detail_PUT extends WebIntegrationTest {

    @Autowired
    private ResourceDefinitionMapper resourceDefinitionMapper;

    private URI url(String uuid) {
        return URI.create(format("/resource-definitions/%s", uuid));
    }

    private ResourceDefinitionChangeDTO reqDto(ResourceDefinition rd) {
        rd.setName(format("EDITED: %s", rd.getName()));
        rd.setUrlPrefix(format("%s-edited", rd.getUrlPrefix()));
        return resourceDefinitionMapper.toChangeDTO(rd);
    }

    @Autowired
    private ResourceDefinitionFixtures resourceDefinitionFixtures;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        ResourceDefinition resourceDefinition = resourceDefinitionFixtures.catalogDefinition();
        ResourceDefinitionChangeDTO reqDto = reqDto(resourceDefinition);

        // AND: Prepare request
        RequestEntity<ResourceDefinitionChangeDTO> request = RequestEntity
                .put(url(resourceDefinition.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<ResourceDefinition> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<ResourceDefinition> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody().getName(), is(equalTo(reqDto.getName())));
        assertThat(result.getBody().getUrlPrefix(), is(equalTo(reqDto.getUrlPrefix())));
    }

    @Test
    @DisplayName("HTTP 403: ResourceDefinition is not authenticated")
    public void res403_notAuthenticated() {
        ResourceDefinition resourceDefinition = resourceDefinitionFixtures.catalogDefinition();
        createNoUserForbiddenTestPut(client, url(resourceDefinition.getUuid()), reqDto(resourceDefinition));
    }

    @Test
    @DisplayName("HTTP 403: ResourceDefinition is not an admin")
    public void res403_resourceDefinition() {
        ResourceDefinition resourceDefinition = resourceDefinitionFixtures.catalogDefinition();
        createUserForbiddenTestPut(client, url(resourceDefinition.getUuid()), reqDto(resourceDefinition));
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestPut(client, url("nonExisting"),
                reqDto(resourceDefinitionFixtures.catalogDefinition()));
    }

}
