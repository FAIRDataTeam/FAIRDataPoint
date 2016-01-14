/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import static com.google.common.io.Files.readLines;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.openrdf.rio.RDFFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-12-01
 * @version 0.1
 */
public class ExampleTurtleFiles {
    
    public static final String FDP_METADATA = "dtl-fdp.ttl";
    public static final String PLANT_CATALOG_METADATA = 
            "plant-breeding-catalog.ttl";
    public static final String BREEDDB_DATASET_METADATA = "breedDB-dataset.ttl";
    public static final String BREEDDB_DATASET_SPARQL_DISTRIBUTION = 
            "breedDB-distribution-sparql.ttl";
    public static final String BREEDDB_DATASET_TURTLE_DISTRIBUTION = 
            "breedDB-distribution-turtle.ttl";
    public static final RDFFormat FILES_RDF_FORMAT = RDFFormat.TURTLE;
    private final static Logger LOGGER = LogManager.getLogger(
            ExampleTurtleFiles.class.getName());
    private final static String BASE_URI = "http://semlab1.liacs.nl:8080/";
    
    public static String getTurtleAsString(String fileName)  {        
        String content = "";        
        URL fileURL = ExampleTurtleFiles.class.getResource(fileName);
        try {
            File npFile;
            npFile = new File(fileURL.toURI());
            for (String fileLine : readLines(npFile, StandardCharsets.UTF_8)) {
                content += fileLine+"\n";
            }          
        } catch (IOException | URISyntaxException ex) {
            LOGGER.error("Error getting turle file",ex);   
        
        }        
        return content;
    }
    
    public static File getTurtleAsFile(String fileName)  {        
        File npFile = null;       
        URL fileURL = ExampleTurtleFiles.class.getResource(fileName);
        try {
            
            npFile = new File(fileURL.toURI());        
        } catch (URISyntaxException ex) {
            LOGGER.error("Error getting turle file",ex);        
        }        
        return npFile;
    }
    
    public static void storeTurtleFileToTripleStore (Repository repository, 
            String fileName, Resource context, String baseURI) {
        RepositoryConnection conn = null;        
        try {
            String content = getTurtleAsString(fileName);
            if(baseURI != null && !baseURI.isEmpty()) {                
                content = content.replaceAll(BASE_URI, baseURI);
            }
            else {
                baseURI = BASE_URI;
            }
            StringReader reader = new StringReader(content);
            conn = repository.getConnection();
            if (context == null) {
                conn.add(reader, 
                        baseURI, ExampleTurtleFiles.FILES_RDF_FORMAT);       
            }         
            else {
                conn.add(reader, 
                        baseURI, ExampleTurtleFiles.FILES_RDF_FORMAT, context); 
            }
        }
        catch (RepositoryException | IOException | RDFParseException e) {
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
            }
        }
    }
    
}
