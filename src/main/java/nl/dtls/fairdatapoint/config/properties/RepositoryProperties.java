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
    // TODO: use polymorphism for types of repository

    public static final int TYPE_IN_MEMORY = 1;

    public static final int TYPE_NATIVE = 2;

    public static final int TYPE_ALLEGRO = 3;

    public static final int TYPE_GRAPHDB = 4;

    public static final int TYPE_BLAZEGRAPH = 5;

    private int type;
    private RepositoryNativeProperties nativeRepo;
    private RepositoryBasicProperties agraph;
    private RepositoryBasicProperties graphDb;
    private RepositoryBasicProperties blazegraph;

    public void setNative(RepositoryNativeProperties repositoryNativeProperties) {
        this.nativeRepo = repositoryNativeProperties;
    }

    public String getStringType() {
        return switch (type) {
            case TYPE_IN_MEMORY -> "InMemory";
            case TYPE_NATIVE -> "Native";
            case TYPE_ALLEGRO -> "AllegroGraph";
            case TYPE_GRAPHDB -> "GraphDB";
            case TYPE_BLAZEGRAPH -> "Blazegraph";
            default -> "Invalid";
        };
    }

    public String getDir() {
        if (type == TYPE_NATIVE) {
            return nativeRepo.getDir();
        }
        return null;
    }

    public String getUrl() {
        return switch (type) {
            case TYPE_ALLEGRO -> agraph.getUrl();
            case TYPE_GRAPHDB -> graphDb.getUrl();
            case TYPE_BLAZEGRAPH -> blazegraph.getUrl();
            default -> null;
        };
    }

    public String getRepository() {
        return switch (type) {
            case TYPE_ALLEGRO -> agraph.getRepository();
            case TYPE_GRAPHDB -> graphDb.getRepository();
            case TYPE_BLAZEGRAPH -> blazegraph.getRepository();
            default -> null;
        };
    }

    public String getUsername() {
        return switch (type) {
            case TYPE_ALLEGRO -> agraph.getUsername();
            case TYPE_GRAPHDB -> graphDb.getUsername();
            default -> null;
        };
    }

    public String getPassword() {
        return switch (type) {
            case TYPE_ALLEGRO -> agraph.getPassword();
            case TYPE_GRAPHDB -> graphDb.getPassword();
            default -> null;
        };
    }
}
