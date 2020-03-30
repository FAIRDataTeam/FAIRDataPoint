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
package nl.dtls.fairdatapoint.service.metadata.common;

import nl.dtls.fairmetadata4j.util.ValueFactoryHelper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.*;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.l;

@Service
public class MetadataFactoryImpl implements MetadataFactory {

    public Model createFDPMetadata(String title, String description, String repositoryUrl) {
        Model metadata = new LinkedHashModel();
        setCommonMetadata(metadata, i(repositoryUrl), title, description, null);
        return metadata;
    }

    public Model createCatalogMetadata(String title, String description, String identifier,
                                       List<String> themeTaxonomies, String repositoryUrl,
                                       IRI repository) {
        Model metadata = new LinkedHashModel();
        IRI catalogUri = i(repositoryUrl + "/catalog/" + identifier);
        setCommonMetadata(metadata, catalogUri, title, description, repository);
        setThemeTaxonomies(metadata, catalogUri,
                themeTaxonomies.stream().map(ValueFactoryHelper::i).collect(Collectors.toList()));
        return metadata;
    }

    public Model createDatasetMetadata(String title, String description, String identifier,
                                       List<String> themes, List<String> keywords, String repositoryUrl,
                                       IRI catalog) {
        Model metadata = new LinkedHashModel();
        IRI datasetUri = i(repositoryUrl + "/dataset/" + identifier);
        setCommonMetadata(metadata, datasetUri, title, description, catalog);
        setThemes(metadata, datasetUri, themes.stream().map(ValueFactoryHelper::i).collect(Collectors.toList()));
        setKeywords(metadata, datasetUri, keywords.stream().map(ValueFactoryHelper::l).collect(Collectors.toList()));
        return metadata;
    }

    public Model createDistributionMetadata(String title, String description, String identifier,
                                            String downloadUrl, String accessUrl, String mediaType,
                                            String repositoryUrl, IRI dataset) {
        Model metadata = new LinkedHashModel();
        IRI distributionUri = i(repositoryUrl + "/distribution/" + identifier);
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

    private void setCommonMetadata(Model metadata, IRI uri, String title, String description, IRI parent) {
        setTitle(metadata, uri, l(title));
        setDescription(metadata, uri, l(description));
        setVersion(metadata, uri, l(1.0f));

        if (parent != null) {
            setParent(metadata, uri, parent);
        }
    }
}
