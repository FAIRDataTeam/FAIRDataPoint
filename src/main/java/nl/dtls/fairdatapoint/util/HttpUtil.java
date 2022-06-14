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
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
public class HttpUtil {

    private static final String MSG_BAD_URL_FORMAT = "URL was not in the right format";

    private static final String MSG_NO_METADATA = "No metadata record identifier given";

    private static final String URL_SEP = "/";

    private static final String PROTO_HTTP = "http://";

    private static final String PROTO_HTTPS = "https://";

    private static final String HEADER_TOKEN_PREFIX = "Bearer ";

    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "X-Real-IP",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR",
    };

    public static String getClientIpAddress(HttpServletRequest request, Boolean behindProxy) {
        if (behindProxy) {
            for (String header : IP_HEADER_CANDIDATES) {
                final String ipList = request.getHeader(header);
                if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                    return ipList.split(",")[0];
                }
            }
        }
        return request.getRemoteAddr();
    }

    public static String getRequestURL(HttpServletRequest request, String persistentUrl) {
        String urlS = request.getRequestURL().toString();
        log.info("Original requested url {}", urlS);
        try {
            urlS = removeLastSlash(urlS.replace("/expanded", ""));

            final URL url = new URL(urlS);
            final String modifiedUrl = removeLastSlash(persistentUrl) + url.getPath();
            log.info("Modified requested url {}", modifiedUrl);

            return modifiedUrl;

        }
        catch (MalformedURLException exception) {
            throw new ValidationException(MSG_BAD_URL_FORMAT);
        }
    }

    public static URL getMetadataURL(String persistentUrl, String urlPrefix, String recordId) {
        try {
            if (urlPrefix.isEmpty()) {
                return new URL(persistentUrl);
            }
            if (recordId.isEmpty()) {
                throw new ValidationException(MSG_NO_METADATA);
            }
            return new URL(format("%s/%s/%s", persistentUrl, urlPrefix, recordId));
        }
        catch (MalformedURLException exception) {
            throw new ValidationException(MSG_BAD_URL_FORMAT);
        }
    }

    public static IRI getMetadataIRI(String persistentUrl, String urlPrefix, String recordId) {
        return i(getMetadataURL(persistentUrl, urlPrefix, recordId).toString());
    }

    public static IRI generateNewMetadataIRI(String persistentUrl, String urlPrefix) {
        return getMetadataIRI(persistentUrl, urlPrefix, UUID.randomUUID().toString());
    }

    public static IRI generateNewIRI(HttpServletRequest request, String persistentUrl) {
        final String requestedURL = getRequestURL(request, persistentUrl);
        final UUID uid = UUID.randomUUID();
        return i(requestedURL + URL_SEP + uid.toString());
    }

    public static RDFFormat getRdfContentType(String name) {
        if (name == null) {
            return RDFFormat.TURTLE;
        }
        return switch (name) {
            case "text/plain" -> RDFFormat.TURTLE;
            case "text/turtle" -> RDFFormat.TURTLE;
            case "application/ld+json" -> RDFFormat.JSONLD;
            case "application/rdf+xml" -> RDFFormat.RDFXML;
            case "text/n3" -> RDFFormat.N3;
            default -> RDFFormat.TURTLE;
        };
    }

    public static String getToken(HttpServletRequest req) {
        return ofNullable(req.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith(HEADER_TOKEN_PREFIX))
                .flatMap(header -> of(header.substring(HEADER_TOKEN_PREFIX.length())))
                .orElse(null);
    }

    public static String removeLastSlash(String url) {
        if (url.endsWith(URL_SEP)) {
            return url.substring(0, url.length() - 1);
        }
        else {
            return url;
        }
    }

    public static String removeProtocol(String url) {
        if (url.startsWith(PROTO_HTTP)) {
            return url.substring(PROTO_HTTP.length());
        }
        else if (url.startsWith(PROTO_HTTPS)) {
            return url.substring(PROTO_HTTPS.length());
        }
        else {
            return url;
        }
    }

}
