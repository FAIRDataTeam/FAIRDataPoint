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
import nl.dtls.fairdatapoint.api.controller.MetadataController;
import nl.dtls.fairdatapoint.database.rdf.repository.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.MetadataRepositoryException;
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
    private MetadataRepository storeManager;

    @Autowired
    private MetadataService<CatalogMetadata> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata> datasetMetadataService;

    @Override
    public void visit(FDPMetadata fdpMetadata) {
        // nothing to update for FDP metadata
    }

    @Override
    public void visit(CatalogMetadata catalogMetadata) {
        try {
            update(catalogMetadata, R3D.DATACATALOG);
        } catch (MetadataRepositoryException | DatatypeConfigurationException ex) {
            LOGGER.error("Error updating catalog parent, {}", ex.getMessage());
        }
    }

    @Override
    public void visit(DatasetMetadata datasetMetadata) {
        try {
            update(datasetMetadata, DCAT.HAS_DATASET);
            CatalogMetadata parent = catalogMetadataService.retrieve(datasetMetadata.getParentURI());
            visit(parent);
        } catch (MetadataRepositoryException | DatatypeConfigurationException | MetadataServiceException ex) {
            LOGGER.error("Error updating dataset parent, {}", ex.getMessage());
        }

    }

    @Override
    public void visit(DistributionMetadata distributionMetadata) {
        try {
            update(distributionMetadata, DCAT.HAS_DISTRIBUTION);
            DatasetMetadata parent = datasetMetadataService.retrieve(distributionMetadata.getParentURI());
            visit(parent);
        } catch (MetadataRepositoryException | DatatypeConfigurationException | MetadataServiceException ex) {
            LOGGER.error("Error updating distribution parent, {}", ex.getMessage());
        }
    }

    @Override
    public void visit(DataRecordMetadata dataRecordMetadata) {
        try {
            update(dataRecordMetadata, DCAT.HAS_RECORD);
            DatasetMetadata parent = datasetMetadataService.retrieve(dataRecordMetadata.getParentURI());
            visit(parent);
        } catch (MetadataRepositoryException | DatatypeConfigurationException | MetadataServiceException ex) {
            LOGGER.error("Error updating distribution parent, {}", ex.getMessage());
        }
    }

    private void update(Metadata metadata, IRI relation) throws MetadataRepositoryException,
            DatatypeConfigurationException {
        IRI parent = metadata.getParentURI();
        List<Statement> stmts = new ArrayList<>();
        stmts.add(VALUE_FACTORY.createStatement(parent, relation, metadata.getUri()));
        storeManager.removeStatement(parent, FDP.METADATAMODIFIED, null);
        stmts.add(VALUE_FACTORY.createStatement(parent, FDP.METADATAMODIFIED, RDFUtils.getCurrentTime()));
        storeManager.storeStatements(stmts, parent);
    }
}
