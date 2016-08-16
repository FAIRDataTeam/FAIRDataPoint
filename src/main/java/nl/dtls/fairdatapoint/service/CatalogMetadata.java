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
import nl.dtls.fairdatapoint.utils.RDFUtils;
import nl.dtls.fairdatapoint.utils.vocabulary.DCAT;
import nl.dtls.fairdatapoint.utils.vocabulary.LDP;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
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
public final class CatalogMetadata extends Metadata {    
    
    private URI homepage;
    private List<URI> datasets = new ArrayList();
    private List<URI> themeTaxonomy = new ArrayList();
    private URI fdpUri;
    private static final org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(CatalogMetadata.class);
    
    public CatalogMetadata(String catalogMetadata, String catalogID,
            String fdpURI, RDFFormat format) throws MetadataExeception, 
            DatatypeConfigurationException {
        StringReader reader = new StringReader(catalogMetadata);
        String baseURL = fdpURI + "/" +  catalogID;
        URI catalogUri = new URIImpl(baseURL);
        Literal id = new LiteralImpl(catalogID, XMLSchema.STRING);
        org.openrdf.model.Model modelCatalog;
        try {
            modelCatalog = Rio.parse(reader, baseURL, format);
            extractMetadata(catalogUri, modelCatalog);
            extractCatalogMetadata(catalogUri, modelCatalog);            
            this.setFdpUri(new URIImpl(fdpURI));
            this.setUri(catalogUri);
            this.setIdentifier(id);
            this.setIssued(RDFUtils.getCurrentTime());
            this.setModified(this.getIssued());
            buildCatalogMetadataModel();
        } catch (IOException ex) {
            String errMsg = "Error reading catalog metadata content"
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } catch (RDFParseException ex) {
            String errMsg = "Error parsing catalog metadata content. "
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } catch (UnsupportedRDFormatException ex) {
            String errMsg = "Unsuppoerted RDF format. " + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } 
    }
    
    private void extractCatalogMetadata(URI catalogUri,
            org.openrdf.model.Model modelCatalog) 
            throws MetadataExeception {
        Iterator<Statement> statements = modelCatalog.iterator();
        while (statements.hasNext()) {
            Statement st = statements.next();
            if (st.getSubject().equals(catalogUri)
                    && st.getPredicate().equals(FOAF.HOMEPAGE)) {
                URI homePage = (URI) st.getObject();
                this.setHomepage(homePage);
            } else if ( st.getPredicate().equals(DCAT.THEME_TAXONOMY)) {
                URI themeTax = (URI) st.getObject();
                this.getThemeTaxonomy().add(themeTax);
            }
        }
        if (this.getThemeTaxonomy().isEmpty()) {
            String errMsg = "No dcat:themeTaxonomy provided";
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        }
    }
    
    private void buildCatalogMetadataModel() {
        org.openrdf.model.Model model = new LinkedHashModel();
        model.add(this.getUri(), RDF.TYPE, DCAT.TYPE_CATALOG);        
        if (this.getPublisher() != null) {
           model.add(this.getUri(), DCTERMS.PUBLISHER, this.getPublisher()); 
        }
        if (this.getLanguage() != null) {
            model.add(this.getUri(), DCTERMS.LANGUAGE, this.getLanguage());
        }
        if (this.getHomepage() != null) {
           model.add(this.getUri(), FOAF.HOMEPAGE, this.getHomepage()); 
        }
        for(URI themeTax:this.getThemeTaxonomy()) {
            model.add(this.getUri(), DCAT.THEME_TAXONOMY, themeTax);
        }       
        model.add(this.getFdpUri(), LDP.CONTAINS, this.getUri());
        model.add(this.getFdpUri(), DCTERMS.MODIFIED, this.getModified());
        this.setModel(model);
        
    }
    

    /**
     * @param homepage the homepage to set
     */
    protected void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    /**
     * @param datasets the datasets to set
     */
    protected void setDatasets(List<URI> datasets) {
        this.datasets = datasets;
    }

    /**
     * @param themeTaxonomy the themeTaxonomy to set
     */
    protected void setThemeTaxonomy(List<URI> themeTaxonomy) {
        this.themeTaxonomy = themeTaxonomy;
    }

    /**
     * @param fdpUri the fdpUri to set
     */
    protected void setFdpUri(URI fdpUri) {
        this.fdpUri = fdpUri;
    }
    

    /**
     * @return the homepage
     */
    public URI getHomepage() {
        return homepage;
    }

    /**
     * @return the datasets
     */
    public List<URI> getDatasets() {
        return datasets;
    }

    /**
     * @return the themeTaxonomy
     */
    public List<URI> getThemeTaxonomy() {
        return themeTaxonomy;
    }

    /**
     * @return the fdpUri
     */
    public URI getFdpUri() {
        return fdpUri;
    }

}
