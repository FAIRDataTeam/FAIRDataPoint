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
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import nl.dtls.fairdatapoint.api.repository.StoreManagerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

/** 
 * Contains references to the example metadata rdf files which are used in the 
 * Junit tests.
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-10
 * @version 0.1
 */
public class ExampleFilesUtils {
    private final static Logger LOGGER = 
            LogManager.getLogger(ExampleFilesUtils.class.getName());
    public static final String FDP_METADATA_FILE = "dtl-fdp.ttl";
    public static final String VALID_TEST_FILE = "valid-test-file.ttl";
    public static final RDFFormat FILE_FORMAT = RDFFormat.TURTLE;
    public final static String FDP_URI = "http://localhost/fdp";
    public final static String CATALOG_URI = "http://localhost/fdp/textmining";
    public final static String DATASET_URI = 
            "http://localhost/fdp/textmining/gene-disease-association_lumc";
    public final static String DISTRIBUTION_URI = 
            "http://localhost/fdp/textmining/gene-disease-association_lumc/sparql";
    public final static String BASE_URI = "http://localhost/";
    
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
            LOGGER.error("Error getting turle file",ex);          
        }        
        return content;
    } 
    
    /**
     * Method to read the content of a turtle file
     * 
     * @param fileName Turtle file name
     * @return File content as a string
     */
    public static List<Statement>  getFileContentAsStatements(String fileName)  {        
        List<Statement> statements = null;  
        try {
            String content = getFileContentAsString(fileName);
            StringReader reader = new StringReader(content);
            org.openrdf.model.Model model;
            model = Rio.parse(reader, BASE_URI, FILE_FORMAT);
            Iterator<Statement> it = model.iterator();
            statements = ImmutableList.copyOf(it);
        } catch (IOException | RDFParseException | 
                UnsupportedRDFormatException ex) {
            LOGGER.error("Error getting turle file",ex);          
        }         
        return statements;
    } 
    
    
    /**
     * Method to get turtle file names from a dir
     * 
     * @return File names as List<>
     */
    
    /**
     * Method to get turtle file names from a dir
     * @return File names as List<>
     */
    public static List<String> getTurtleFileNames() {
        List<String> fileNames = new ArrayList();   
        URL placeHolderFile = ExampleFilesUtils.class.
                getResource(FDP_METADATA_FILE);
        
        String sourceFileURI = placeHolderFile.getPath();
        sourceFileURI = sourceFileURI.replace(FDP_METADATA_FILE, "");
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
    
    public static Repository getRepository() throws RepositoryException, 
            IOException, RDFParseException{ 
        Sail store = new MemoryStore(); 
        Repository repository = new SailRepository(store);
        repository.initialize();
        RepositoryConnection conn = repository.getConnection();
        for (String fileName:ExampleFilesUtils.getTurtleFileNames()) {
            String content = ExampleFilesUtils.getFileContentAsString(fileName);
            StringReader reader = new StringReader(content);
            conn.add(reader, BASE_URI, FILE_FORMAT); 
        }
        conn.close();
        return repository;
    }
    
}
