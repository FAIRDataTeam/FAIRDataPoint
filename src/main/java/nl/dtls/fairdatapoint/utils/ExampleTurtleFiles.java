/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import com.google.common.base.Charsets;
import com.google.common.io.PatternFilenameFilter;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.rio.RDFFormat;

/**
 * Contains references to the example metadata rdf files which are used in the 
 * Junit tests. This class also has static methods to pre populate inmemory 
 * triple store  
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2015-12-01
 * @version 0.2
 */
public class ExampleTurtleFiles {
    
    public static final String EXAMPLE_FDP_METADATA_FILE = "dtl-fdp.ttl";
    public static final String EXAMPLE_CATALOG_ID = "textmining";
    public static final String EXAMPLE_DATASET_ID = 
            "gene-disease-association_lumc";
    public static final String EXAMPLE_DISTRIBUTION_ID = "textfile";
    public static final RDFFormat FILES_RDF_FORMAT = RDFFormat.TURTLE;
    private final static Logger LOGGER = LogManager.getLogger(
            ExampleTurtleFiles.class.getName());
    public final static String EXAMPLE_FILES_BASE_URI = 
            "http://www.dtls.nl/";
    public final static String FDP_URI = "http://www.dtls.nl/fdp";
    
    /**
     * Method to read the content of a turtle file
     * 
     * @param fileName Turtle file name
     * @return File content as a string
     */
    public static String getTurtleAsString(String fileName)  {        
        String content = "";  
        try {
            URL fileURL = ExampleTurtleFiles.class.getResource(fileName);
            content = Resources.toString(fileURL, Charsets.UTF_8);
        } catch (IOException ex) {
            LOGGER.error("Error getting turle file",ex);          
        }        
        return content;
    }    
    /**
     * Method to get file names from the util package
     * 
     * @return File names as List<>
     */
    public static List<String> getExampleTurtleFileNames () { 
        
        List<String> fileNames = new ArrayList();    
        URL fdpFileURL = ExampleTurtleFiles.class.getResource(
                EXAMPLE_FDP_METADATA_FILE);
        String sourceFileURI = fdpFileURL.getPath();
        sourceFileURI = sourceFileURI.replace(EXAMPLE_FDP_METADATA_FILE, "");
        // Matches only turtle files
        Pattern pattern = Pattern.compile("^.*.ttl");    
        FilenameFilter filterByExtension = new PatternFilenameFilter(pattern);
        File dir = new File(sourceFileURI);
        File[] files = dir.listFiles(filterByExtension);  
        for (File file: files) {
            fileNames.add(file.getName());
        }
        LOGGER.info("Turtle files in util packaage " + fileNames.toString());
        return fileNames;
    }      
}
