/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import static com.google.common.io.Files.readLines;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.Logger;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-12-01
 * @version 0.1
 */
public class ExampleTurtle {
    
    public static final String FDP_METADATA = "dtl-fdp.ttl";
    public static final String CATALOG_METADATA = "plant-breeding-catalog.ttl";
    public static final String DATASET_METADATA = "breedDB-dataset.ttl";
    public static final RDFFormat FILES_RDF_FORMAT = RDFFormat.TURTLE;
    private final static Logger LOGGER = Logger.getLogger(ExampleTurtle.class);
    
    public static String getTurtleAsString(String fileName)  {        
        String content = "";        
        URL fileURL = ExampleTurtle.class.getResource(fileName);
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
        URL fileURL = ExampleTurtle.class.getResource(fileName);
        try {
            
            npFile = new File(fileURL.toURI());        
        } catch (URISyntaxException ex) {
            LOGGER.error("Error getting turle file",ex);        
        }        
        return npFile;
    }
    
}
