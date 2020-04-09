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
package nl.dtls.fairdatapoint.util;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;

@Slf4j
public class HttpUtil {

    public static String getRequestURL(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        log.info("Original requesed url {}", url);

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
                log.info("Found RDF extension in url {}", ext);
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
                log.info("x-forwarded-port {}", port);
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
            throw new IllegalStateException(format("Error creating url %s", ex.getMessage()));
        }
        log.info("Modified requesed url {}", url);
        return url;
    }

    public static IRI generateNewIRI(HttpServletRequest request) {
        String requestedURL = getRequestURL(request);
        UUID uid = UUID.randomUUID();
        return i(requestedURL + "/" + uid.toString());
    }

    public static RDFFormat getRdfContentType(String name) {
        if (name == null) {
            return RDFFormat.TURTLE;
        }
        switch (name) {
            case "text/plain":
                return RDFFormat.TURTLE;
            case "text/turtle":
                return RDFFormat.TURTLE;
            case "application/ld+json":
                return RDFFormat.JSONLD;
            case "application/rdf+xml":
                return RDFFormat.RDFXML;
            case "text/n3":
                return RDFFormat.N3;
            default:
                return RDFFormat.TURTLE;
        }
    }

}
