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

import nl.dtls.fairmetadata4j.model.*;
import nl.dtls.fairdatapoint.api.dto.metadata.*;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data.MetadataFixtures;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataFactory;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;

@Service
public class TestMetadataFixtures extends MetadataFixtures {

    @Autowired
    protected MetadataFactory metadataFactory;

    @Value("${instance.url}")
    private String instanceUrl;

    public String alternativeInstanceUrl = "https://lorentz.fair-dtls.surf-hosted.nl/fdp";

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    private MetadataService<DistributionMetadata, DistributionMetadataChangeDTO> distributionMetadataService;

    @Autowired
    private MetadataService<DataRecordMetadata, DataRecordMetadataChangeDTO> dataRecordMetadataService;

    public FDPMetadata repositoryMetadata() throws DatatypeConfigurationException {
        FDPMetadata metadata = super.repositoryMetadata(instanceUrl);
        repositoryMetadataService.enhance(metadata);
        return metadata;
    }

    public CatalogMetadata catalog1() throws DatatypeConfigurationException {
        CatalogMetadata metadata = super.catalog1(instanceUrl, repositoryMetadata());
        catalogMetadataService.enhance(metadata);
        return metadata;
    }

    public CatalogMetadata catalog2() throws DatatypeConfigurationException {
        CatalogMetadata metadata = super.catalog2(instanceUrl, repositoryMetadata());
        catalogMetadataService.enhance(metadata);
        return metadata;
    }

    public CatalogMetadata catalog3() throws DatatypeConfigurationException {
        CatalogMetadata metadata = super.catalog3(instanceUrl, repositoryMetadata());
        catalogMetadataService.enhance(metadata);
        return metadata;
    }

    public CatalogMetadata alternative_catalog3() throws DatatypeConfigurationException {
        CatalogMetadata metadata = super.catalog3(alternativeInstanceUrl,
                super.repositoryMetadata(alternativeInstanceUrl));
        catalogMetadataService.enhance(metadata);
        return metadata;
    }

    public DatasetMetadata c1_dataset1() throws DatatypeConfigurationException {
        DatasetMetadata metadata = super.dataset1(instanceUrl, catalog1());
        datasetMetadataService.enhance(metadata);
        return metadata;
    }

    public DatasetMetadata c1_dataset2() throws DatatypeConfigurationException {
        DatasetMetadata metadata = super.dataset2(instanceUrl, catalog1());
        datasetMetadataService.enhance(metadata);
        return metadata;
    }

    public DatasetMetadata c2_dataset3() throws DatatypeConfigurationException {
        DatasetMetadata metadata = super.dataset3(instanceUrl, catalog2());
        datasetMetadataService.enhance(metadata);
        return metadata;
    }

    public DistributionMetadata c1_d1_distribution1() throws DatatypeConfigurationException {
        DistributionMetadata metadata = super.distribution1(instanceUrl, c1_dataset1());
        distributionMetadataService.enhance(metadata);
        return metadata;
    }

    public DistributionMetadata c1_d1_distribution2() throws DatatypeConfigurationException {
        DistributionMetadata metadata = super.distribution2(instanceUrl, c1_dataset1());
        distributionMetadataService.enhance(metadata);
        return metadata;
    }

    public DistributionMetadata c1_d2_distribution3() throws DatatypeConfigurationException {
        DistributionMetadata metadata = super.distribution3(instanceUrl, c1_dataset2());
        distributionMetadataService.enhance(metadata);
        return metadata;
    }

    public DataRecordMetadata c1_d1_datarecord1() throws DatatypeConfigurationException {
        DataRecordMetadata metadata = super.datarecord1(instanceUrl, c1_dataset1());
        dataRecordMetadataService.enhance(metadata);
        return metadata;
    }

    public DataRecordMetadata c1_d2_datarecord2() throws DatatypeConfigurationException {
        DataRecordMetadata metadata = super.datarecord2(instanceUrl, c1_dataset2());
        dataRecordMetadataService.enhance(metadata);
        return metadata;
    }

}
