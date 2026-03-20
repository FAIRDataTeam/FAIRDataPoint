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
package nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.factory;

import nl.dtls.fairdatapoint.entity.metadata.Agent;
import nl.dtls.fairdatapoint.util.ValueFactoryHelper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataSetter.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

@Service
public class MetadataFactoryImpl implements MetadataFactory {

    public Model createFDPMetadata(String title, String description, String fdpUrl) {
        final Model metadata = new LinkedHashModel();
        setCommonMetadata(metadata, i(fdpUrl), title, description, null);
        return metadata;
    }

    public Model createCatalogMetadata(String title, String description, String identifier,
                                       List<String> themeTaxonomies, String fdpUrl, IRI fdp) {
        final Model metadata = new LinkedHashModel();
        final IRI catalogUri = i(fdpUrl + "/catalog/" + identifier);
        setCommonMetadata(metadata, catalogUri, title, description, fdp);
        setThemeTaxonomies(metadata, catalogUri,
                themeTaxonomies.stream().map(ValueFactoryHelper::i).collect(Collectors.toList()));
        return metadata;
    }

    public Model createDatasetMetadata(String title, String description, String identifier,
                                       List<String> themes, List<String> keywords, String fdpUrl,
                                       IRI catalog) {
        final Model metadata = new LinkedHashModel();
        final IRI datasetUri = i(fdpUrl + "/dataset/" + identifier);
        setCommonMetadata(metadata, datasetUri, title, description, catalog);
        setThemes(metadata, datasetUri, themes.stream().map(ValueFactoryHelper::i).collect(Collectors.toList()));
        setKeywords(metadata, datasetUri, keywords.stream().map(ValueFactoryHelper::l).collect(Collectors.toList()));
        return metadata;
    }

    public Model createDistributionMetadata(String title, String description, String identifier,
                                            String downloadUrl, String accessUrl, String mediaType,
                                            String fdpUrl, IRI dataset) {
        final Model metadata = new LinkedHashModel();
        final IRI distributionUri = i(fdpUrl + "/distribution/" + identifier);
        setCommonMetadata(metadata, distributionUri, title, description, dataset);

        if (downloadUrl != null) {
            setDownloadURL(metadata, distributionUri, i(downloadUrl));
        }

        if (accessUrl != null) {
            setAccessURL(metadata, distributionUri, i(accessUrl));
        }

        setMediaType(metadata, distributionUri, l(mediaType));
        return metadata;
    }

    private void setCommonMetadata(Model metadata, IRI uri, String title,
                                   String description, IRI parent) {
        setTitle(metadata, uri, l(title));
        setDescription(metadata, uri, l(description));
        setVersion(metadata, uri, l(1.0f));
        setPublisher(metadata, uri, new Agent(
                i("http://example.com/publisher"),
                i("http://example.com/publisher/mbox"),
                i(FOAF.AGENT),
                l("Publisher")));

        if (parent != null) {
            setParent(metadata, uri, parent);
        }
    }
}
