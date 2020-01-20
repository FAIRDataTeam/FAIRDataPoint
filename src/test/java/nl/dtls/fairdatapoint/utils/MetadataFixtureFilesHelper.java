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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import nl.dtls.fairmetadata4j.io.*;
import nl.dtls.fairmetadata4j.model.*;
import nl.dtls.fairmetadata4j.utils.MetadataParserUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Contains references to the example metadata rdf files which are used in the Junit tests.
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @version 0.1
 * @since 2016-08-10
 */
public class MetadataFixtureFilesHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataFixtureFilesHelper.class);

    // Metadata
    // - repository
    public static final String REPOSITORY_METADATA_FILE = "repository.ttl";
    public static final String REPOSITORY_URI = "http://localhost";
    // - catalog
    public static final String CATALOG_METADATA_FILE = "catalog.ttl";
    public static final String CATALOG_ID = "textmining";
    public static final String CATALOG_URI = "http://localhost/" + CATALOG_ID;
    // - dataset
    public static final String DATASET_METADATA_FILE = "dataset.ttl";
    public static final String DATASET_ID = "gene-disease-association_lumc";
    public static final String DATASET_URI = "http://localhost/textmining/" + DATASET_ID;
    // - distribution
    public static final String DISTRIBUTION_METADATA_FILE = "distribution.ttl";
    public static final String DISTRIBUTION_ID = "sparql";
    public static final String DISTRIBUTION_URI =
            "http://localhost/textmining/gene-disease-association_lumc/" + DISTRIBUTION_ID;
    // - data record
    public static final String DATARECORD_METADATA_FILE = "datarecord.ttl";
    public static final String DATARECORD_ID = "datarecord";

    // Test RDF
    public static final String TEST_RDF_FILE = "test-rdf.ttl";
    public static final String TEST_RDF_URI = "http://www.dtls.nl/test";

    // Other
    public static final RDFFormat FILE_FORMAT = RDFFormat.TURTLE;
    public static final String REPOSITORY_URI_FILE = "getRepositoryUriContent.ttl";

    public static String getFileContentAsString(String fileName) {
        String content = "";
        try {
            URL fileURL = MetadataFixtureFilesHelper.class.getResource(fileName);
            content = Resources.toString(fileURL, Charsets.UTF_8);
        } catch (IOException ex) {
            LOGGER.error("Error getting turle file {}", ex);
        }
        return content;
    }

    public static List<Statement> getFileContentAsStatements(String fileName, String baseURI) {
        List<Statement> statements = null;
        try {
            String content = getFileContentAsString(fileName);
            StringReader reader = new StringReader(content);
            Model model = Rio.parse(reader, baseURI, FILE_FORMAT);
            Iterator<Statement> it = model.iterator();
            statements = Lists.newArrayList(it);
        } catch (IOException | RDFParseException | UnsupportedRDFormatException ex) {
            LOGGER.error("Error getting turle file {}", ex);
        }
        return statements;
    }

    public static FDPMetadata getFDPMetadata(String uri) {
        LOGGER.info("Generating example FDP metadata object");
        FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        FDPMetadata metadata = parser.parse(getFileContentAsStatements(REPOSITORY_METADATA_FILE, uri),
                f.createIRI(uri));
        return metadata;
    }

    public static CatalogMetadata getCatalogMetadata(String uri, String parentURI) {
        LOGGER.info("Generating example catalog metadata object");
        CatalogMetadataParser parser = MetadataParserUtils.getCatalogParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        CatalogMetadata metadata = parser.parse(getFileContentAsStatements(CATALOG_METADATA_FILE,
                uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    }

    public static DatasetMetadata getDatasetMetadata(String uri, String parentURI) {
        LOGGER.info("Generating example dataset metadata object");
        DatasetMetadataParser parser = MetadataParserUtils.getDatasetParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        DatasetMetadata metadata = parser.parse(getFileContentAsStatements(DATASET_METADATA_FILE,
                uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    }

    public static DistributionMetadata getDistributionMetadata(String uri, String parentURI) {
        LOGGER.info("Generating example distribution metadata object");
        DistributionMetadataParser parser = MetadataParserUtils.getDistributionParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        DistributionMetadata metadata = parser.parse(getFileContentAsStatements(
                DISTRIBUTION_METADATA_FILE, uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    }

    public static DataRecordMetadata getDataRecordMetadata(String uri, String parentURI) {
        LOGGER.info("Generating example datarecord metadata object");
        DataRecordMetadataParser parser = MetadataParserUtils.getDataRecordParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        DataRecordMetadata metadata = parser.parse(getFileContentAsStatements(
                DATARECORD_METADATA_FILE, uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    }
}
