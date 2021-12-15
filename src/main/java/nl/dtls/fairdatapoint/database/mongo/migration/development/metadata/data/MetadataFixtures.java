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
package nl.dtls.fairdatapoint.database.mongo.migration.development.metadata.data;

import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MetadataFixtures {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    public Metadata fdpMetadata() {
        return
                new Metadata(
                        null,
                        persistentUrl,
                        MetadataState.PUBLISHED
                );
    }

    public Metadata catalog1() {
        return
                new Metadata(
                        null,
                        persistentUrl + "/catalog/catalog-1",
                        MetadataState.PUBLISHED
                );
    }

    public Metadata catalog2() {
        return
                new Metadata(
                        null,
                        persistentUrl + "/catalog/catalog-2",
                        MetadataState.DRAFT
                );
    }

    public Metadata dataset1() {
        return
                new Metadata(
                        null,
                        persistentUrl + "/dataset/dataset-1",
                        MetadataState.PUBLISHED
                );
    }

    public Metadata dataset2() {
        return
                new Metadata(
                        null,
                        persistentUrl + "/dataset/dataset-2",
                        MetadataState.DRAFT
                );
    }

    public Metadata distribution1() {
        return
                new Metadata(
                        null,
                        persistentUrl + "/distribution/distribution-1",
                        MetadataState.PUBLISHED
                );
    }

    public Metadata distribution2() {
        return
                new Metadata(
                        null,
                        persistentUrl + "/distribution/distribution-2",
                        MetadataState.DRAFT
                );
    }

}
