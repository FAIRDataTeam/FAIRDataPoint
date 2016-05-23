/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.PatternFilenameFilter;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.openrdf.rio.RDFFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains references to the example metadata rdf files which are used in the 
 * Junit tests. This class also has static methods to pre populate inmemory 
 * triple store  
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2015-12-01
 * @version 0.1
 */
public class ExampleTurtleFiles {
    
    public static final String FDP_METADATA = "dtl-fdp.ttl";
    public static final ImmutableList<String>  CATALOG_METADATA = 
        ImmutableList.of("textmining-catalog.ttl");
    public static final ImmutableList<String> DATASET_METADATA = 
            ImmutableList.of("disgenet.ttl", "gda-lumc.ttl");
    public static final ImmutableList<String> DATASET_DISTRIBUTIONS = 
        ImmutableList.of("disgenet-html-page.ttl" , 
                "disgenet-nanopubs-gzip.ttl", "disgenet-textfile-gzip.ttl", 
                "gda-lumc-textfile.ttl", "gda-lumc-sparql.ttl");
    public static final String EXAMPLE_CATALOG_ID = "textmining";
    public static final String EXAMPLE_DATASET_ID = 
            "gene-disease-association_lumc";
    public static final String EXAMPLE_DISTRIBUTION_ID = "textfile";
    public static final RDFFormat FILES_RDF_FORMAT = RDFFormat.TURTLE;
    private final static Logger LOGGER = LogManager.getLogger(
            ExampleTurtleFiles.class.getName());
    public final static String BASE_URI = "http://semlab1.liacs.nl:8080/";
    public final static String FDP_URI = "http://semlab1.liacs.nl:8080/fdp";
    
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
    
    public static List<String> getExampleTurtleFileNames () { 
        
        List<String> fileNames = new ArrayList();    
        URL fdpFileURL = ExampleTurtleFiles.class.getResource(FDP_METADATA);
        String sourceFileURI = fdpFileURL.getPath();
        sourceFileURI = sourceFileURI.replace(FDP_METADATA, "");
        Pattern pattern = Pattern.compile("^.*.ttl");    
        FilenameFilter filterByExtension = new PatternFilenameFilter(pattern);
        File dir = new File(sourceFileURI);
        File[] files = dir.listFiles(filterByExtension);  
        for (File file: files) {
            fileNames.add(file.getName());
        }
        LOGGER.info(fileNames.toString());
        return fileNames;
    }
    
    
    
}
