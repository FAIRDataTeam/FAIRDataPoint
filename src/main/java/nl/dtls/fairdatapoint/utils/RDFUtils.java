/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-07
 * @version 0.1
 */
public class RDFUtils {
    
    private final static Logger LOGGER 
            = LogManager.getLogger(RDFUtils.class);
    
    public static String writeToString(List<Statement> statements, 
            RDFFormat format) throws Exception {		
        StringWriter sw = new StringWriter();		
        RDFWriter writer = Rio.createWriter(format, sw);
        try {
            propagateToHandler(statements, writer);
        } catch (RepositoryException | RDFHandlerException ex) {
            LOGGER.error("Error reading RDF statements");
            throw (new Exception(ex.getMessage()));
        }        
        return sw.toString();	
    }
    public static Literal getCurrentTime() throws 
            DatatypeConfigurationException {
        Date date = new Date();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().
                newXMLGregorianCalendar(c);
        Literal currentTime = new LiteralImpl(xmlDate.toXMLFormat(),
                    XMLSchema.DATETIME);
        return currentTime;
    }
	
    private static void propagateToHandler(List<Statement> 
            statements, RDFHandler handler) 
            throws RDFHandlerException, RepositoryException{            
        handler.startRDF();	   
        handler.handleNamespace("rdf", 
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#");			
        handler.handleNamespace("rdfs", 
                "http://www.w3.org/2000/01/rdf-schema#");			
        handler.handleNamespace("dcat", "http://www.w3.org/ns/dcat#");			
        handler.handleNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");			
        handler.handleNamespace("owl", "http://www.w3.org/2002/07/owl#");			
        handler.handleNamespace("dct", "http://purl.org/dc/terms/");
        handler.handleNamespace("lang", 
                "http://id.loc.gov/vocabulary/iso639-1/");
        for(Statement st: statements){
            handler.handleStatement(st);            
        }  
        handler.endRDF();
    }
    
}
