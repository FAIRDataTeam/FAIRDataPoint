package nl.dtls.fairdatapoint.service.shape;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeDTO;
import nl.dtls.fairdatapoint.entity.exception.ShapeImportException;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class ShapeRetrievalUtils {

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofMinutes(1))
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final TypeReference<List<ShapeDTO>> responseType = new TypeReference<>() {
    };

    public static List<ShapeDTO> retrievePublishedShapes(String fdpUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fdpUrl.replaceAll("/$", "") + "/shapes/public"))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), responseType);
        } catch (JsonProcessingException e) {
            throw new ShapeImportException(fdpUrl, "Cannot process response: " + e.getMessage());
        } catch (IOException e) {
            throw new ShapeImportException(fdpUrl, "Cannot get response: " + e.getMessage());
        } catch (Exception e) {
            throw new ShapeImportException(fdpUrl, e.getMessage());
        }
    }
}
