/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.common.util;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Guards against turning caller-supplied input into a request the server makes on the
 * caller's behalf (SSRF). A URL is accepted only when its scheme is http or https and
 * every address its host resolves to is a public, routable address: loopback, link-local,
 * private (RFC 1918) and IPv6 unique-local (fc00::/7) addresses are all rejected, since
 * each of those is a target the caller could not otherwise reach directly, but the server
 * can.
 *
 * This checks the URL as given; it does not re-check a redirect target the connection may
 * follow afterwards. Closing that gap needs the caller to validate every hop, not just the
 * first one.
 */
public final class SafeUrlValidator {

    private SafeUrlValidator() {
    }

    public static boolean isSafeHttpUrl(URL url) {
        final String protocol = url.getProtocol();
        if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
            return false;
        }
        try {
            final InetAddress[] addresses = InetAddress.getAllByName(url.getHost());
            if (addresses.length == 0) {
                return false;
            }
            for (final InetAddress address : addresses) {
                if (isDisallowed(address)) {
                    return false;
                }
            }
            return true;
        }
        catch (UnknownHostException exception) {
            return false;
        }
    }

    private static boolean isDisallowed(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return true;
        }
        final byte[] bytes = address.getAddress();
        // IPv6 unique-local addresses (fc00::/7) are not covered by isSiteLocalAddress(),
        // which only recognises the deprecated site-local range (fec0::/10).
        return bytes.length == 16 && (bytes[0] & 0xFE) == 0xFC;
    }

}
