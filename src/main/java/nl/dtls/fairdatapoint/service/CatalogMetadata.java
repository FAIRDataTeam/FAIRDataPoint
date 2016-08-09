/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtls.fairdatapoint.service.impl.utils.RDFUtils;
import nl.dtls.fairdatapoint.utils.vocabulary.DCAT;
import nl.dtls.fairdatapoint.utils.vocabulary.LDP;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-08
 * @version 0.1
 */
public class CatalogMetadata {

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
    private List<URI> datasets;
    private List<URI> themeTaxonomy = new ArrayList();
    private URI fdpUri;
    private URI catalogUri;
    private final static org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(CatalogMetadata.class);
    private org.openrdf.model.Model model = new LinkedHashModel();
    
    public CatalogMetadata(String catalogMetadata, String catalogID,
            String fdpURI, RDFFormat format) throws CatalogMetadataExeception, 
            DatatypeConfigurationException {
        StringReader reader = new StringReader(catalogMetadata);
        String baseURL = fdpURI + "/" +  catalogID;
        URI fdpUri = new URIImpl(fdpURI);
        URI catalogUri = new URIImpl(baseURL);
        Literal id = new LiteralImpl(catalogID, XMLSchema.STRING);
        org.openrdf.model.Model modelCatalog;
        try {
            modelCatalog = Rio.parse(reader, baseURL, format);
            extractCatalogMetadata(catalogUri, modelCatalog);            
            this.fdpUri = fdpUri;
            this.catalogUri = catalogUri;
            this.identifier = id;
            this.issued = RDFUtils.getCurrentTime();
            this.modified = issued;
            buildCatalogMetadataModel();
        } catch (IOException ex) {
            String errMsg = "Error reading catalog metadata content"
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new CatalogMetadataExeception(errMsg));
        } catch (RDFParseException ex) {
            String errMsg = "Error parsing catalog metadata content. "
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new CatalogMetadataExeception(errMsg));
        } catch (UnsupportedRDFormatException ex) {
            String errMsg = "Unsuppoerted RDF format. " + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new CatalogMetadataExeception(errMsg));
        } 
    }   
    
    /**
     * @return the model
     */
    public org.openrdf.model.Model getCatalogMetadataModel() {        
        return model;
    }

    private void extractCatalogMetadata(URI catalogUri,
            org.openrdf.model.Model modelCatalog) 
            throws CatalogMetadataExeception {
        Iterator<Statement> statements = modelCatalog.iterator();
        while (statements.hasNext()) {
            Statement st = statements.next();
            if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(DCTERMS.HAS_VERSION)) {
                Literal version = new LiteralImpl(st.getObject().stringValue(),
                        XMLSchema.FLOAT);
                this.version = version;
            } else if (st.getSubject().equals(catalogUri)
                    && (st.getPredicate().equals(RDFS.LABEL)
                    || st.getPredicate().equals(DCTERMS.TITLE))) {
                Literal title = new LiteralImpl(st.getObject().stringValue(),
                        XMLSchema.STRING);
                this.title = title;
            } else if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(DCTERMS.DESCRIPTION)) {
                Literal description = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.description = description;
            } else if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(DCTERMS.PUBLISHER)) {
                URI publisher = (URI) st.getObject();
                this.publisher = publisher;
            } else if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(DCTERMS.LANGUAGE)) {
                URI language = (URI) st.getObject();
                this.language = language;
            } else if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(DCTERMS.LICENSE)) {
                URI license = (URI) st.getObject();
                this.license = license;
            } else if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(DCTERMS.RIGHTS)) {
                URI rights = (URI) st.getObject();
                this.rights = rights;
            } else if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(FOAF.HOMEPAGE)) {
                URI homePage = (URI) st.getObject();
                this.homepage = homePage;
            } else if ( st.getPredicate().equals(DCAT.THEME_TAXONOMY)) {
                URI themeTax = (URI) st.getObject();
                this.themeTaxonomy.add(themeTax);
            }
        }
        
        if (this.version == null) {
            String errMsg = "No version number provided";
            LOGGER.error(errMsg);
            throw (new CatalogMetadataExeception(errMsg));
        } else if (this.title == null) {
            String errMsg = "No title or label provided";
            LOGGER.error(errMsg);
            throw (new CatalogMetadataExeception(errMsg));
        } else if (this.themeTaxonomy.isEmpty()) {
            String errMsg = "No dcat:themeTaxonomy provided";
            LOGGER.error(errMsg);
            throw (new CatalogMetadataExeception(errMsg));
        }
    }
    
    private void buildCatalogMetadataModel() {
        model.add(getCatalogUri(), RDF.TYPE, DCAT.CATALOG);
        model.add(getCatalogUri(), DCTERMS.TITLE, title);
        model.add(getCatalogUri(), RDFS.LABEL, title);
        model.add(getCatalogUri(), DCTERMS.IDENTIFIER, identifier);
        model.add(getCatalogUri(), DCTERMS.ISSUED, issued);
        model.add(getCatalogUri(), DCTERMS.MODIFIED, modified);
        model.add(getCatalogUri(), DCTERMS.HAS_VERSION, version);
        if (description != null) {
            model.add(getCatalogUri(), DCTERMS.DESCRIPTION, description);
        }
        if (publisher != null) {
           model.add(getCatalogUri(), DCTERMS.PUBLISHER, publisher); 
        }
        if (language != null) {
            model.add(getCatalogUri(), DCTERMS.LANGUAGE, language);
        }
        if (license != null) {
            model.add(getCatalogUri(), DCTERMS.LICENSE, license);
        }
        if (rights != null) {
            model.add(getCatalogUri(), DCTERMS.RIGHTS, rights);
        }
        if (homepage != null) {
           model.add(getCatalogUri(), FOAF.HOMEPAGE, homepage); 
        }
        for(URI themeTax:themeTaxonomy) {
            model.add(getCatalogUri(), DCAT.THEME_TAXONOMY, themeTax);
        }       
        model.add(fdpUri, LDP.CONTAINS, catalogUri);
        model.add(fdpUri, DCTERMS.MODIFIED, modified);
        
    }

    /**
     * @return the catalogUri
     */
    public URI getCatalogUri() {
        return catalogUri;
    }

}
