/**
 * The MIT License
 * Copyright © 2017 DTL
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.repository.fixtures;

import nl.dtl.fairmetadata4j.model.*;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetadataFactoryImpl implements MetadataFactory {

    private static final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    public FDPMetadata createFDPMetadata(String title, String description, String fdpUrl) {
        FDPMetadata metadata = new FDPMetadata();
        setCommonMetadata(metadata, fdpUrl, title, description, null);
        return metadata;
    }

    public CatalogMetadata createCatalogMetadata(String title, String description, String identifier,
                                                 List<String> themeTaxonomies, String fdpUrl, FDPMetadata fdp) {
        CatalogMetadata metadata = new CatalogMetadata();
        setCommonMetadata(metadata, fdpUrl + "/catalog/" + identifier, title, description, fdp);
        metadata.setThemeTaxonomys(themeTaxonomies.stream().map(valueFactory::createIRI).collect(Collectors.toList()));
        return metadata;
    }

    public DatasetMetadata createDatasetMetadata(String title, String description, String identifier,
                                                 List<String> themes, List<String> keywords, String fdpUrl,
                                                 CatalogMetadata catalog) {
        DatasetMetadata metadata = new DatasetMetadata();
        setCommonMetadata(metadata, fdpUrl + "/dataset/" + identifier, title, description, catalog);
        metadata.setThemes(themes.stream().map(valueFactory::createIRI).collect(Collectors.toList()));
        metadata.setKeywords(keywords.stream().map(valueFactory::createLiteral).collect(Collectors.toList()));
        return metadata;
    }

    public DistributionMetadata createDistributionMetadata(String title, String description, String identifier,
                                                           String downloadUrl, String accessUrl, String mediaType,
                                                           String fdpUrl, DatasetMetadata dataset) {
        DistributionMetadata metadata = new DistributionMetadata();
        setCommonMetadata(metadata, fdpUrl + "/distribution/" + identifier, title, description, dataset);

        if (downloadUrl != null) {
            metadata.setDownloadURL(valueFactory.createIRI(downloadUrl));
        }

        if (accessUrl != null) {
            metadata.setAccessURL(valueFactory.createIRI(accessUrl));
        }

        metadata.setMediaType(valueFactory.createLiteral(mediaType));
        return metadata;
    }

    private void setCommonMetadata(Metadata metadata, String uri, String title, String description, Metadata parent) {
        metadata.setUri(valueFactory.createIRI(uri));
        metadata.setTitle(valueFactory.createLiteral(title));
        metadata.setDescription(valueFactory.createLiteral(description));
        metadata.setVersion(valueFactory.createLiteral("1.0", XMLSchema.FLOAT));

        if (parent != null) {
            metadata.setParentURI(parent.getUri());
        }
    }
}
