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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.metadata;

import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class MetadataController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MetadataController.class);

    protected static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    @Autowired
    @Qualifier("repositoryMetadataService")
    protected MetadataService repositoryMetadataService;

    @Autowired
    @Qualifier("catalogMetadataService")
    protected MetadataService catalogMetadataService;

    @Autowired
    @Qualifier("genericMetadataService")
    protected MetadataService genericMetadataService;

    protected String getRequestURL(HttpServletRequest request) {

        String url = request.getRequestURL().toString();
        LOGGER.info("Original requesed url {}", url);

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        List<String> rdfExt = RDFWriterRegistry.getInstance()
                .getKeys()
                .stream()
                .map(RDFFormat::getDefaultFileExtension)
                .collect(Collectors.toList());

        for (String ext : rdfExt) {
            String extension = "." + ext;
            if (url.contains(extension)) {
                LOGGER.info("Found RDF extension in url {}", ext);
                url = url.replace(extension, "");
                break;
            }
        }
        try {
            URL requestedURL = new URL(url);
            String host = request.getHeader("x-forwarded-host");
            String proto = request.getHeader("x-forwarded-proto");
            String port = request.getHeader("x-forwarded-port");

            if (host != null && !host.isEmpty()) {
                url = url.replace(requestedURL.getHost(), host);
            }

            if (proto != null && !proto.isEmpty()) {
                url = url.replace(requestedURL.getProtocol(), proto);
            }

            if (port != null && requestedURL.getPort() != -1) {
                String val = ":" + requestedURL.getPort();
                LOGGER.info("x-forwarded-port {}", port);
                switch (port) {
                    case "443":
                        url = url.replace(val, "");
                        break;
                    case "80":
                        url = url.replace(val, "");
                        break;
                    default:
                        break;
                }
            }
        } catch (MalformedURLException ex) {
            LOGGER.error("Error creating url  {}", ex.getMessage());
            return null;
        }
        LOGGER.info("Modified requesed url {}", url);
        return url;
    }

    protected IRI getRequestURLasIRI(HttpServletRequest request) {
        return VALUEFACTORY.createIRI(getRequestURL(request));
    }

    protected IRI generateNewIRI(HttpServletRequest request) {
        String requestedURL = getRequestURL(request);
        UUID uid = UUID.randomUUID();
        return VALUEFACTORY.createIRI(requestedURL + "/" + uid.toString());
    }

    protected MetadataService getMetadataServiceByUrlPrefix(String urlPrefix) {
        switch (urlPrefix) {
            case "catalog":
                return catalogMetadataService;
            case "":
                return repositoryMetadataService;
            default:
                return genericMetadataService;
        }
    }
}
