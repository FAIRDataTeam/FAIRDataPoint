/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtls.fairdatapoint.service.impl.utils.RDFUtils;
import nl.dtls.fairdatapoint.utils.vocabulary.LDP;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Literal;
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
public class FDPMetaData {
    private Literal title = null;
    private Literal identifier;
    private Literal issued;
    private Literal modified;
    private Literal version = null;
    private Literal description;
    private URI publisher;
    private URI language;
    private URI license;
    private URI rights;
    private URI homepage;
    private URI fdpUri;
    private URI swaggerDoc;
    private final static org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(CatalogMetadata.class);
    private org.openrdf.model.Model model = new LinkedHashModel();
    
    public FDPMetaData(String fdpURI) throws MalformedURLException, 
            DatatypeConfigurationException {
        fdpUri = new URIImpl(fdpURI);
        String fdpid = DigestUtils.md5Hex(fdpURI);
        String host = new URL(fdpURI).getAuthority();
        identifier = new LiteralImpl(fdpid, XMLSchema.STRING);
        title = new LiteralImpl(("FDP of " + host), XMLSchema.STRING);
        description = new LiteralImpl(("FDP of " + host), XMLSchema.STRING);
        language = new URIImpl("http://id.loc.gov/vocabulary/iso639-1/en");
        license = new URIImpl(
                "http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0");
        version = new LiteralImpl("1.0", XMLSchema.FLOAT);
        issued = RDFUtils.getCurrentTime();
        modified = RDFUtils.getCurrentTime();
        swaggerDoc = new URIImpl(fdpURI + "/swagger-ui.html");
        buildFDPMetadataModel();
    }
    /**
     * @return the model
     */
    public org.openrdf.model.Model getFDPMetadataModel() {        
        return model;
    }
    
    private void buildFDPMetadataModel() {
        model.add(fdpUri, RDF.TYPE, LDP.CONTAINER);
        model.add(fdpUri, DCTERMS.TITLE, title);
        model.add(fdpUri, RDFS.LABEL, title);
        model.add(fdpUri, DCTERMS.IDENTIFIER, identifier);
        model.add(fdpUri, DCTERMS.ISSUED, issued);
        model.add(fdpUri, DCTERMS.MODIFIED, modified);
        model.add(fdpUri, DCTERMS.HAS_VERSION, version);
        model.add(fdpUri, RDFS.SEEALSO, swaggerDoc);
        if (description != null) {
            model.add(fdpUri, DCTERMS.DESCRIPTION, description);
        }
        if (publisher != null) {
           model.add(fdpUri, DCTERMS.PUBLISHER, publisher); 
        }
        if (language != null) {
            model.add(fdpUri, DCTERMS.LANGUAGE, language);
        }
        if (license != null) {
            model.add(fdpUri, DCTERMS.LICENSE, license);
        }
        if (rights != null) {
            model.add(fdpUri, DCTERMS.RIGHTS, rights);
        }
        if (homepage != null) {
           model.add(fdpUri, FOAF.HOMEPAGE, homepage); 
        }       
    }
    
}
