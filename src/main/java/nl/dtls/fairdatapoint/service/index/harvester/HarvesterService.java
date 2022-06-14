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
package nl.dtls.fairdatapoint.service.index.harvester;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.LDP;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getChildren;
import static nl.dtls.fairdatapoint.util.HttpUtil.getRdfContentType;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.read;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.readFile;
import static nl.dtls.fairdatapoint.util.RdfUtil.getObjectsBy;
import static nl.dtls.fairdatapoint.util.RdfUtil.getSubjectsBy;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
@Service
public class HarvesterService {

    private static final String DEFAULT_NAVIGATION_SHACL = "defaultNavigationShacl.ttl";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GenericMetadataRepository genericMetadataRepository;

    @Async
    public void harvest(String clientUrl) throws MetadataRepositoryException {
        log.info(format("Start harvesting '%s'", clientUrl));

        // 1. Get navigation relationships
        final List<IRI> navigationRelationships = getNavigationRelationships(clientUrl);

        // 2. Harvest data
        final Map<String, Model> result =
                visitNode(clientUrl, navigationRelationships, new HashMap<>());

        // 3. Store data
        for (Map.Entry<String, Model> item : result.entrySet()) {
            genericMetadataRepository.save(new ArrayList<>(item.getValue()), i(item.getKey()));
        }

        log.info(format("Harvesting for '%s' completed", clientUrl));
    }

    private List<IRI> getNavigationRelationships(String uri) {
        final Model model = readFile(DEFAULT_NAVIGATION_SHACL, "http://fairdatapoint.org");
        return getObjectsBy(model, null, "http://www.w3.org/ns/shacl#path")
                .stream()
                .map(object -> i(object.stringValue()))
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String, Model> visitNode(
            String uri, List<IRI> relationships, Map<String, Model> nodes
    ) {
        try {
            final Model model = makeRequest(uri);
            Map<String, Model> actNodes = nodes;
            actNodes.put(uri, model);

            final List<Resource> containers = getSubjectsBy(model, RDF.TYPE, LDP.DIRECT_CONTAINER);
            if (containers.size() > 0) {
                // Get children through LDP links
                for (Value container : containers) {
                    for (Value child : getObjectsBy(
                            model, i(container.stringValue()), LDP.CONTAINS
                    )) {
                        if (!actNodes.containsKey(child.stringValue())) {
                            actNodes = visitNode(child.stringValue(), relationships, actNodes);
                        }
                    }
                }
            }
            else {
                // Get children through default navigation SHACL
                for (IRI relationship : relationships) {
                    final List<IRI> children = getChildren(model, relationship);
                    for (IRI child : children) {
                        if (!actNodes.containsKey(child.stringValue())) {
                            actNodes = visitNode(child.stringValue(), relationships, actNodes);
                        }
                    }
                }
            }
            return actNodes;
        }
        catch (HttpClientErrorException exception) {
            return nodes;
        }
    }

    private Model makeRequest(String uri) {
        log.info(format("Making request to '%s'", uri));
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.parseMediaType(RDFFormat.TURTLE.getDefaultMIMEType())));
        final HttpEntity<Void> entity = new HttpEntity<>(null, headers);
        try {
            final ResponseEntity<String> response =
                    restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.info(format("Request to '%s' failed (%s)", uri, response.getStatusCode()));
                throw new HttpClientErrorException(response.getStatusCode());
            }
            final RDFFormat rdfContentType =
                    getRdfContentType(response.getHeaders().getContentType().getType());
            log.info(format("Request to '%s' successfully received", uri));
            final Model result = read(response.getBody(), uri, rdfContentType);
            log.info(format("Request to '%s' successfully parsed", uri));
            return result;
        }
        catch (RestClientException exception) {
            log.info(format("Request to '%s' failed", uri));
            throw new HttpClientErrorException(
                    HttpStatus.BAD_GATEWAY,
                    ofNullable(exception.getMessage())
                            .orElse("HTTP request failed to proceed")
            );
        }
    }
}
