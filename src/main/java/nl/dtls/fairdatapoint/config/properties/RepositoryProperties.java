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
package nl.dtls.fairdatapoint.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "repository")
public class RepositoryProperties {
    private int type;
    private RepositoryNativeProperties nativeRepo = null;
    private RepositoryBasicProperties agraph = null;
    private RepositoryBasicProperties graphDb = null;
    private RepositoryBasicProperties blazegraph = null;

    public void setNative(RepositoryNativeProperties nativeRepo) {
        this.nativeRepo = nativeRepo;
    }

    public String getStringType() {
        return switch (type) {
            case 1 -> "InMemory";
            case 2 -> "Native";
            case 3 -> "AllegroGraph";
            case 4 -> "GraphDB";
            case 5 -> "Blazegraph";
            default -> "Invalid";
        };
    }

    public String getDir() {
        if (type == 2) {
            return nativeRepo.getDir();
        }
        return null;
    }

    public String getUrl() {
        return switch (type) {
            case 3 -> agraph.getUrl();
            case 4 -> graphDb.getUrl();
            case 5 -> blazegraph.getUrl();
            default -> null;
        };
    }

    public String getRepository() {
        return switch (type) {
            case 3 -> agraph.getRepository();
            case 4 -> graphDb.getRepository();
            case 5 -> blazegraph.getRepository();
            default -> null;
        };
    }

    public String getUsername() {
        return switch (type) {
            case 3 -> agraph.getUsername();
            case 4 -> graphDb.getUsername();
            default -> null;
        };
    }

    public String getPassword() {
        return switch (type) {
            case 3 -> agraph.getPassword();
            case 4 -> graphDb.getPassword();
            default -> null;
        };
    }
}
