/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtls.fairdatapoint.utils.RDFUtils;
import nl.dtls.fairdatapoint.utils.vocabulary.DCAT;
import nl.dtls.fairdatapoint.utils.vocabulary.LDP;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-09
 * @version 0.1
 */
public final class FDPMetadata extends Metadata {
    
    private URI homepage;
    private URI swaggerDoc;
    private static org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(CatalogMetadata.class);
    
    public FDPMetadata(String fdpURI) throws MalformedURLException, 
            DatatypeConfigurationException {
        this.setUri(new URIImpl(fdpURI));
        String fdpid = DigestUtils.md5Hex(fdpURI);
        String host = new URL(fdpURI).getAuthority();
        this.setIdentifier(new LiteralImpl(fdpid, XMLSchema.STRING));
        this.setTitle(new LiteralImpl(("FDP of " + host), XMLSchema.STRING));
        this.setDescription(new LiteralImpl(("FDP of " + host), 
                XMLSchema.STRING));
        this.setLanguage(new URIImpl(
                "http://id.loc.gov/vocabulary/iso639-1/en"));
        this.setLicense(new URIImpl(
                "http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
        this.setVersion(new LiteralImpl("1.0", XMLSchema.FLOAT));
        this.setIssued(RDFUtils.getCurrentTime());
        this.setModified(RDFUtils.getCurrentTime());
        this.setSwaggerDoc(new URIImpl(fdpURI + "/swagger-ui.html"));
        buildFDPMetadataModel();
    }  
    
    public FDPMetadata(URI fdpURI, List<Statement> metadata) 
            throws MetadataExeception {
        extractMetadata(fdpURI, metadata);
        extractFDPMetadata(fdpURI, metadata);
        this.setStatements(metadata);
    } 
    
    private void extractFDPMetadata(URI fdpURI,
            List<Statement> metadata) 
            throws MetadataExeception {
        Iterator<Statement> statements = metadata.iterator();        
        extractMetadata(fdpURI, metadata);
        while (statements.hasNext()) {
            Statement st = statements.next();
            if (st.getSubject().equals(fdpURI)
                    && st.getPredicate().equals(FOAF.HOMEPAGE)) {
                URI homePage = (URI) st.getObject();
                this.setHomepage(homePage);
            } else if (st.getSubject().equals(fdpURI)
                    && st.getPredicate().equals(RDFS.SEEALSO)) {
                this.setSwaggerDoc((URI) st.getObject());
            }
        }
    }
    
    private void buildFDPMetadataModel() {
        org.openrdf.model.Model model = new LinkedHashModel();
        LOGGER.info("Creating FDP metadata rdf model");
        model.add(this.getUri(), RDF.TYPE, LDP.CONTAINER);
        model.add(this.getUri(), RDFS.SEEALSO, this.getSwaggerDoc());
        
        if (this.getHomepage() != null) {
           model.add(this.getUri(), FOAF.HOMEPAGE, this.getHomepage()); 
        }
        this.setStatements(model);
    }
    
    /**
     * @param homepage the homepage to set
     */
    protected void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    /**
     * @param swaggerDoc the swaggerDoc to set
     */
    protected void setSwaggerDoc(URI swaggerDoc) {
        this.swaggerDoc = swaggerDoc;
    }

    /**
     * @return the homepage
     */
    public URI getHomepage() {
        return homepage;
    }

    /**
     * @return the swaggerDoc
     */
    public URI getSwaggerDoc() {
        return swaggerDoc;
    }
    
}
