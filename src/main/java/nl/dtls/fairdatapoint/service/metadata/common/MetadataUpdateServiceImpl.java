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

import nl.dtl.fairmetadata4j.model.*;
import nl.dtl.fairmetadata4j.utils.RDFUtils;
import nl.dtl.fairmetadata4j.utils.vocabulary.FDP;
import nl.dtl.fairmetadata4j.utils.vocabulary.R3D;
import nl.dtls.fairdatapoint.api.controller.metadata.MetadataController;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataUpdateServiceImpl implements MetadataUpdateService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MetadataController.class);

    private static final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    @Autowired
    private MetadataRepository<CatalogMetadata> catalogMetadataRepository;

    @Autowired
    private MetadataRepository<DatasetMetadata> datasetMetadataRepository;

    @Autowired
    private MetadataRepository<DistributionMetadata> distributionMetadataRepository;

    @Autowired
    private MetadataRepository<DataRecordMetadata> dataRecordMetadataRepository;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Override
    public void visit(FDPMetadata fdpMetadata) {
        // nothing to update for FDP metadata
    }

    @Override
    public void visit(CatalogMetadata catalogMetadata) {
        try {
            update(catalogMetadata, catalogMetadataRepository, R3D.DATACATALOG);
        } catch (MetadataRepositoryException | DatatypeConfigurationException ex) {
            LOGGER.error("Error updating catalog parent, {}", ex.getMessage());
        }
    }

    @Override
    public void visit(DatasetMetadata datasetMetadata) {
        try {
            update(datasetMetadata, datasetMetadataRepository, DCAT.HAS_DATASET);
            CatalogMetadata parent = catalogMetadataService.retrieve(datasetMetadata.getParentURI());
            visit(parent);
        } catch (MetadataRepositoryException | DatatypeConfigurationException | MetadataServiceException ex) {
            LOGGER.error("Error updating dataset parent, {}", ex.getMessage());
        }

    }

    @Override
    public void visit(DistributionMetadata distributionMetadata) {
        try {
            update(distributionMetadata, distributionMetadataRepository, DCAT.HAS_DISTRIBUTION);
            DatasetMetadata parent = datasetMetadataService.retrieve(distributionMetadata.getParentURI());
            visit(parent);
        } catch (MetadataRepositoryException | DatatypeConfigurationException | MetadataServiceException ex) {
            LOGGER.error("Error updating distribution parent, {}", ex.getMessage());
        }
    }

    @Override
    public void visit(DataRecordMetadata dataRecordMetadata) {
        try {
            update(dataRecordMetadata, dataRecordMetadataRepository, DCAT.HAS_RECORD);
            DatasetMetadata parent = datasetMetadataService.retrieve(dataRecordMetadata.getParentURI());
            visit(parent);
        } catch (MetadataRepositoryException | DatatypeConfigurationException | MetadataServiceException ex) {
            LOGGER.error("Error updating distribution parent, {}", ex.getMessage());
        }
    }

    private <T extends Metadata> void update(T metadata, MetadataRepository<T> metadataRepository, IRI relation) throws MetadataRepositoryException,
            DatatypeConfigurationException {
        IRI parent = metadata.getParentURI();
        List<Statement> stmts = new ArrayList<>();
        stmts.add(VALUE_FACTORY.createStatement(parent, relation, metadata.getUri()));
        metadataRepository.removeStatement(parent, FDP.METADATAMODIFIED, null);
        stmts.add(VALUE_FACTORY.createStatement(parent, FDP.METADATAMODIFIED, RDFUtils.getCurrentTime()));
        metadataRepository.storeStatements(stmts, parent);
    }
}
