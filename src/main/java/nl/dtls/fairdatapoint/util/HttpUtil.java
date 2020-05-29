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

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
public class HttpUtil {

    public static String getRequestURL(HttpServletRequest request, String persistentUrl) {
        String urlS = request.getRequestURL().toString();
        log.info("Original requesed url {}", urlS);
        try {
            urlS = removeLastSlash(urlS.replace("/expanded", ""));
            persistentUrl = removeLastSlash(persistentUrl);

            URL url = new URL(urlS);
            String modifiedUrl = persistentUrl + url.getPath();
            log.info("Modified requesed url {}", modifiedUrl);

            return modifiedUrl;

        } catch (MalformedURLException e) {
            throw new ValidationException("URL was not in the right format");
        }
    }

    public static IRI generateNewIRI(HttpServletRequest request, String persistentUrl) {
        String requestedURL = getRequestURL(request, persistentUrl);
        UUID uid = UUID.randomUUID();
        return i(requestedURL + "/" + uid.toString());
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

    public static String removeLastSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }

    public static String removeProtocol(String url) {
        if (url.contains("http://")) {
            return url.substring(7);
        } else if (url.contains("https://")) {
            return url.substring(8);
        } else {
            return url;
        }
    }

}
