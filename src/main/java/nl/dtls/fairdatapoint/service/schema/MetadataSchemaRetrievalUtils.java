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
package nl.dtls.fairdatapoint.service.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.entity.exception.MetadataSchemaImportException;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static java.lang.String.format;

@Slf4j
public class MetadataSchemaRetrievalUtils {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofMinutes(1))
            .build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<List<MetadataSchemaVersionDTO>> RESPONSE_TYPE =
            new TypeReference<>() {
            };

    public static List<MetadataSchemaVersionDTO> retrievePublishedMetadataSchemas(String fdpUrl) {
        try {
            log.info(format("Retrieving published metadata schemas from %s", fdpUrl));
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fdpUrl.replaceAll("/$", "") + "/metadata-schemas/public"))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .GET().build();
            final HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return OBJECT_MAPPER.readValue(response.body(), RESPONSE_TYPE);
        }
        catch (JsonProcessingException exception) {
            log.warn(
                    format("Could not parse published metadata schemas from %s: %s",
                            fdpUrl, exception.getMessage())
            );
            throw new MetadataSchemaImportException(
                    fdpUrl, "Cannot process response: " + exception.getMessage()
            );
        }
        catch (IOException exception) {
            log.warn(
                    format("Could not retrieve published metadata schemas from %s: %s",
                            fdpUrl, exception.getMessage())
            );
            throw new MetadataSchemaImportException(
                    fdpUrl, "Cannot get response: " + exception.getMessage()
            );
        }
        catch (Exception exception) {
            log.warn(
                    format("Failed to get published metadata schemas from %s: %s",
                            fdpUrl, exception.getMessage())
            );
            throw new MetadataSchemaImportException(
                    fdpUrl, exception.getMessage()
            );
        }
    }
}
