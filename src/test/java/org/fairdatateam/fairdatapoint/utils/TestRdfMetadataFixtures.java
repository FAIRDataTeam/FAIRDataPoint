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
package nl.dtls.fairdatapoint.utils;

import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data.RdfMetadataFixtures;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.factory.MetadataFactory;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.enhance.MetadataEnhancer;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;

@Service
public class TestRdfMetadataFixtures extends RdfMetadataFixtures {

    public String alternativePersistentUrl = "https://lorentz.fair-dtls.surf-hosted.nl/fdp";

    private final String persistentUrl;

    private final MetadataEnhancer metadataEnhancer;

    private final ResourceDefinitionFixtures resourceDefinitionFixtures;

    @Autowired
    public TestRdfMetadataFixtures(MetadataFactory metadataFactory,
                                   @Qualifier("persistentUrl") String persistentUrl,
                                   MetadataEnhancer metadataEnhancer,
                                   ResourceDefinitionFixtures resourceDefinitionFixtures) {
        super(metadataFactory);
        this.persistentUrl = persistentUrl;
        this.metadataEnhancer = metadataEnhancer;
        this.resourceDefinitionFixtures = resourceDefinitionFixtures;
    }

    public Model fdpMetadata() {
        Model metadata = super.fdpMetadata(persistentUrl);
        ResourceDefinition rd = resourceDefinitionFixtures.fdpDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model catalog1() {
        Model metadata = super.catalog1(persistentUrl, getUri(fdpMetadata()));
        ResourceDefinition rd = resourceDefinitionFixtures.catalogDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model catalog2() {
        Model metadata = super.catalog2(persistentUrl, getUri(fdpMetadata()));
        ResourceDefinition rd = resourceDefinitionFixtures.catalogDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model catalog3() {
        Model metadata = super.catalog3(persistentUrl, getUri(fdpMetadata()));
        ResourceDefinition rd = resourceDefinitionFixtures.catalogDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model alternative_catalog3() {
        Model metadata = super.catalog3(alternativePersistentUrl,
                getUri(super.fdpMetadata(alternativePersistentUrl)));
        ResourceDefinition rd = resourceDefinitionFixtures.catalogDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model c1_dataset1() {
        Model metadata = super.dataset1(persistentUrl, getUri(catalog1()));
        ResourceDefinition rd = resourceDefinitionFixtures.datasetDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model c1_dataset2() {
        Model metadata = super.dataset2(persistentUrl, getUri(catalog1()));
        ResourceDefinition rd = resourceDefinitionFixtures.datasetDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model c2_dataset3() {
        Model metadata = super.dataset3(persistentUrl, getUri(catalog2()));
        ResourceDefinition rd = resourceDefinitionFixtures.datasetDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model c1_d1_distribution1() {
        Model metadata = super.distribution1(persistentUrl, getUri(c1_dataset1()));
        ResourceDefinition rd = resourceDefinitionFixtures.distributionDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model c1_d1_distribution2() {
        Model metadata = super.distribution2(persistentUrl, getUri(c1_dataset1()));
        ResourceDefinition rd = resourceDefinitionFixtures.distributionDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

    public Model c1_d2_distribution3() {
        Model metadata = super.distribution3(persistentUrl, getUri(c1_dataset2()));
        ResourceDefinition rd = resourceDefinitionFixtures.distributionDefinition();
        metadataEnhancer.enhance(metadata, getUri(metadata), rd);
        return metadata;
    }

}
