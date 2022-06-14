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
package nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data;

import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.factory.MetadataFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RdfMetadataFixtures {

    private static final String LIPSUM_1 =
            "Duis pellentesque, nunc a fringilla varius, magna dui porta quam, nec ultricies augue turpis sed "
                    + "velit. Donec id consectetur ligula. Suspendisse pharetra egestas massa, vel varius leo "
                    + "viverra at. Donec scelerisque id ipsum id semper. Maecenas facilisis augue vel justo "
                    + "molestie aliquet. Maecenas sed mattis lacus, sed viverra risus. Donec iaculis quis lacus "
                    + "vitae scelerisque. Nullam fermentum lectus nisi, id vulputate nisi congue nec. Morbi "
                    + "fermentum justo at justo bibendum, at tempus ipsum tempor. Donec facilisis nibh sed lectus "
                    + "blandit venenatis. Cras ullamcorper, justo vitae feugiat commodo, orci metus suscipit purus,"
                    + " quis sagittis turpis ante eget ex. Pellentesque malesuada a metus eu pulvinar. Morbi rutrum"
                    + " euismod eros at varius. Duis finibus dapibus ex, a hendrerit mauris efficitur at.";

    private static final String LIPSUM_2 =
            "Nam eget lorem rhoncus, porta odio at, pretium tortor. Morbi dapibus urna magna, at mollis neque "
                    + "sagittis et. Praesent fringilla, justo malesuada gravida cursus, nibh augue semper enim, et "
                    + "efficitur augue justo id odio. Donec id malesuada leo, vel molestie sem. Sed vitae libero a "
                    + "tortor vestibulum ullamcorper vitae ac turpis. Proin posuere nisl sit amet mollis auctor. In"
                    + " vehicula fringilla lorem, a tristique ligula. Vivamus fringilla leo molestie pellentesque "
                    + "vehicula. Nam aliquet condimentum varius. In hac habitasse platea dictumst. Maecenas "
                    + "elementum neque ac ex ultricies auctor. Vestibulum aliquet porttitor enim eu pellentesque. "
                    + "Aenean dapibus tellus ipsum.";

    private static final String LIPSUM_3 =
            "Sed hendrerit accumsan velit, ut eleifend lorem rhoncus a. Curabitur auctor euismod risus lobortis "
                    + "viverra. Donec finibus ultricies venenatis. Suspendisse non pulvinar augue, vel dictum erat."
                    + " Praesent placerat ultrices tempor. Pellentesque posuere sapien eu rutrum efficitur. Quisque"
                    + " ac risus malesuada, tempus diam at, elementum urna. Suspendisse quis posuere leo.";

    private static final String LIPSUM_4 =
            "Maecenas et mollis purus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere "
                    + "cubilia Curae; Pellentesque pulvinar augue at ultricies placerat. Vestibulum faucibus sem "
                    + "vel massa egestas consectetur at et nisi. Nullam consectetur, mi et lacinia commodo, arcu "
                    + "eros tempus risus, nec porta justo metus in orci. Pellentesque mattis tortor a ultrices "
                    + "pharetra. Phasellus tristique urna orci, ut vulputate tortor accumsan sit amet. Nulla sed "
                    + "nunc varius, finibus sapien eget, venenatis tortor. Nam gravida diam ut sapien sodales, ut "
                    + "sodales tellus feugiat. Duis auctor rutrum dictum. Phasellus facilisis, nibh at tempus "
                    + "efficitur, odio sem molestie lectus, at bibendum metus orci in nibh. Mauris facilisis est "
                    + "nibh, vitae iaculis risus lacinia at. Aliquam in lectus est.";

    private static final String EXAMPLE_URL = "http://example.com/";

    private static final String PLAIN_TYPE = "text/plain";

    private static final String THEME_THEATRE = "https://www.wikidata.org/wiki/Q27317";
    private static final String THEME_TEST = "https://www.wikidata.org/wiki/Q27318";
    private static final String THEME_ATEAM = "https://www.wikidata.org/wiki/Q27319";
    private static final String THEME_EXAMPLE = "https://purl.org/example#theme";

    private final MetadataFactory metadataFactory;

    @Autowired
    public RdfMetadataFixtures(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    public Model fdpMetadata(String fdpUrl) {
        return metadataFactory.createFDPMetadata(
                "My FAIR Data Point",
                LIPSUM_1,
                fdpUrl
        );
    }

    public Model catalog1(String fdpUrl, IRI fdp) {
        return metadataFactory.createCatalogMetadata(
                "Bio Catalog",
                LIPSUM_2,
                "catalog-1",
                Arrays.asList(THEME_THEATRE, THEME_EXAMPLE),
                fdpUrl,
                fdp
        );
    }

    public Model catalog2(String fdpUrl, IRI fdp) {
        return metadataFactory.createCatalogMetadata(
                "Tech Catalog",
                LIPSUM_2,
                "catalog-2",
                Arrays.asList(THEME_TEST, THEME_EXAMPLE),
                fdpUrl,
                fdp
        );
    }

    public Model catalog3(String fdpUrl, IRI fdp) {
        return metadataFactory.createCatalogMetadata(
                "IT Catalog",
                LIPSUM_2,
                "catalog-3",
                Arrays.asList(THEME_TEST, THEME_EXAMPLE),
                fdpUrl,
                fdp
        );
    }

    public Model dataset1(String fdpUrl, IRI catalog) {
        return metadataFactory.createDatasetMetadata(
                "Cat Dataset",
                LIPSUM_3,
                "dataset-1",
                Arrays.asList(THEME_TEST, THEME_ATEAM, THEME_EXAMPLE),
                Arrays.asList("Text Mining", "Natural Language Processing"),
                fdpUrl,
                catalog
        );
    }

    public Model dataset2(String fdpUrl, IRI catalog) {
        return metadataFactory.createDatasetMetadata(
                "Dog Dataset",
                LIPSUM_3,
                "dataset-2",
                Arrays.asList(THEME_TEST, THEME_EXAMPLE),
                Arrays.asList("Animal", "Dog", "Behaviour"),
                fdpUrl,
                catalog
        );
    }

    public Model dataset3(String fdpUrl, IRI catalog) {
        return metadataFactory.createDatasetMetadata(
                "Pig Dataset",
                LIPSUM_3,
                "dataset-3",
                Arrays.asList(THEME_TEST, THEME_EXAMPLE),
                Arrays.asList("Pig", "Liver"),
                fdpUrl,
                catalog
        );
    }

    public Model distribution1(String fdpUrl, IRI dataset) {
        return metadataFactory.createDistributionMetadata(
                "Downloadable Distribution",
                LIPSUM_4,
                "distribution-1",
                EXAMPLE_URL,
                null,
                PLAIN_TYPE,
                fdpUrl,
                dataset
        );
    }

    public Model distribution2(String fdpUrl, IRI dataset) {
        return metadataFactory.createDistributionMetadata(
                "Accessible Distribution",
                LIPSUM_4,
                "distribution-2",
                null,
                EXAMPLE_URL,
                PLAIN_TYPE,
                fdpUrl,
                dataset
        );
    }

    public Model distribution3(String fdpUrl, IRI dataset) {
        return metadataFactory.createDistributionMetadata(
                "Nice Distribution",
                LIPSUM_4,
                "distribution-3",
                null,
                EXAMPLE_URL,
                PLAIN_TYPE,
                fdpUrl,
                dataset
        );
    }

}
