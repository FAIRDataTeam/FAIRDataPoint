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
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import nl.dtl.fairmetadata4j.io.CatalogMetadataParser;
import nl.dtl.fairmetadata4j.io.DataRecordMetadataParser;
import nl.dtl.fairmetadata4j.io.DatasetMetadataParser;
import nl.dtl.fairmetadata4j.io.DistributionMetadataParser;
import nl.dtl.fairmetadata4j.io.FDPMetadataParser;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.utils.MetadataParserUtils;
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

/** 
 * Contains references to the example metadata rdf files which are used in the 
 * Junit tests.
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-08-10
 * @version 0.1
 */
public class ExampleFilesUtils {
    public static final String FDP_METADATA_FILE = "dtl-fdp.ttl";
    public static final String CATALOG_METADATA_FILE = "textmining-catalog.ttl";    
    public static final String DATASET_METADATA_FILE = "gda-lumc.ttl";    
    public static final String DATARECORD_METADATA_FILE = 
            "example-datarecord.ttl";
    public static final String DISTRIBUTION_METADATA_FILE = 
            "gda-lumc-sparql.ttl";     
    public static final String VALID_TEST_FILE = "valid-test-file.ttl";
    public static final String CATALOG_ID = "textmining";
    public static final String DATASET_ID = "gene-disease-association_lumc";    
    public static final String DATARECORD_ID = "datarecord";
    public static final String DISTRIBUTION_ID = "sparql";
    public static final String FDP_URI = "http://localhost/fdp";
    public static final String CATALOG_URI = "http://localhost/fdp/" + 
            CATALOG_ID;
    public static final String DATASET_URI = 
            "http://localhost/fdp/textmining/" + DATASET_ID;
    public static final String DATARECORD_URI = "http://dtls.nl/" + 
            DATARECORD_ID;
    public static final String DISTRIBUTION_URI = 
            "http://localhost/fdp/textmining/gene-disease-association_lumc/" + 
            DISTRIBUTION_ID;
    public static final String BASE_URI = "http://localhost/";   
    public static final String TEST_SUB_URI = "http://www.dtls.nl/test"; 
    public static final RDFFormat FILE_FORMAT = RDFFormat.TURTLE;    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleFilesUtils.class);
    
    /**
     * Method to read the content of a turtle file
     * 
     * @param fileName Turtle file name
     * @return File content as a string
     */
    public static String getFileContentAsString(String fileName)  {        
        String content = "";  
        try {
            URL fileURL = ExampleFilesUtils.class.getResource(fileName);
            content = Resources.toString(fileURL, Charsets.UTF_8);
        } catch (IOException ex) {
            LOGGER.error("Error getting turle file {}", ex);          
        }        
        return content;
    } 
    
    /**
     * Method to read the content of a turtle file
     * 
     * @param fileName Turtle file name
     * @param baseURI
     * @return File content as a string
     */
    public static List<Statement> getFileContentAsStatements(String fileName, 
            String baseURI)  {        
        List<Statement> statements = null;  
        try {
            String content = getFileContentAsString(fileName);
            StringReader reader = new StringReader(content);
            Model model;
            model = Rio.parse(reader, baseURI, FILE_FORMAT);
            Iterator<Statement> it = model.iterator();
            statements =  Lists.newArrayList(it);
        } catch (IOException | RDFParseException | 
                UnsupportedRDFormatException ex) {
            LOGGER.error("Error getting turle file {}", ex);          
        }         
        return statements;
    }
    
    public static FDPMetadata getFDPMetadata(String uri) {        
        LOGGER.info("Generating example FDP metadata object");
        FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        FDPMetadata metadata = parser.parse(getFileContentAsStatements(
                FDP_METADATA_FILE, uri), f.createIRI(uri));
        return metadata;
    }
    
    public static CatalogMetadata getCatalogMetadata(String uri, 
            String parentURI) {        
        LOGGER.info("Generating example catalog metadata object");
        CatalogMetadataParser parser = MetadataParserUtils.getCatalogParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        CatalogMetadata metadata = parser.parse(getFileContentAsStatements(
                CATALOG_METADATA_FILE, uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    }
    
    public static DatasetMetadata getDatasetMetadata(String uri, 
            String parentURI) {        
        LOGGER.info("Generating example dataset metadata object");
        DatasetMetadataParser parser = MetadataParserUtils.getDatasetParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        DatasetMetadata metadata = parser.parse(getFileContentAsStatements(
                DATASET_METADATA_FILE, uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    }
    
    public static DistributionMetadata getDistributionMetadata(String uri, 
            String parentURI) {        
        LOGGER.info("Generating example distribution metadata object");
        DistributionMetadataParser parser = MetadataParserUtils.
                getDistributionParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        DistributionMetadata metadata = parser.parse(getFileContentAsStatements(
                DISTRIBUTION_METADATA_FILE, uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    } 
    
    public static DataRecordMetadata getDataRecordMetadata(String uri, 
            String parentURI) {        
        LOGGER.info("Generating example datarecord metadata object");
        DataRecordMetadataParser parser = MetadataParserUtils.
                getDataRecordParser();
        ValueFactory f = SimpleValueFactory.getInstance();
        DataRecordMetadata metadata = parser.parse(getFileContentAsStatements(
                DATARECORD_METADATA_FILE, uri), f.createIRI(uri));
        metadata.setParentURI(f.createIRI(parentURI));
        return metadata;
    } 
}
