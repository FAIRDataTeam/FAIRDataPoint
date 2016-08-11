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
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-11
 * @version 0.1
 */
public final class DatasetMetadata extends Metadata {

    private URI publisher;
    private URI language;
    private List<URI> distribution = new ArrayList();
    private List<URI> themes = new ArrayList();
    private URI contactPoint;
    private List<Literal> keywords = new ArrayList();
    private URI landingPage;
    private URI catalogURI;

    private static final org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(DatasetMetadata.class);
    
    public DatasetMetadata(String datasetMetadata, String datasetID,
            String catalogURI, RDFFormat format) throws MetadataExeception, 
            DatatypeConfigurationException {
        StringReader reader = new StringReader(datasetMetadata);
        String baseURL = catalogURI + "/" +  datasetID;
        URI datasetURI = new URIImpl(baseURL);
        Literal id = new LiteralImpl(datasetID, XMLSchema.STRING);
        org.openrdf.model.Model modelDatasetMetaData;
        try {
            modelDatasetMetaData = Rio.parse(reader, baseURL, format);
            extractDatasetMetadata(datasetURI, modelDatasetMetaData);            
            this.setCatalogURI(new URIImpl(catalogURI));
            this.setUri(datasetURI);
            this.setIdentifier(id);
            this.setIssued(RDFUtils.getCurrentTime());
            this.setModified(this.getIssued());
            buildDatasetMetadataModel();
        } catch (IOException ex) {
            String errMsg = "Error reading dataset metadata content"
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } catch (RDFParseException ex) {
            String errMsg = "Error parsing dataset metadata content. "
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } catch (UnsupportedRDFormatException ex) {
            String errMsg = "Unsuppoerted RDF format. " + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } 
        
    }
    private void extractDatasetMetadata(URI datasetURI,
            org.openrdf.model.Model modelDataset) 
            throws MetadataExeception {
        Iterator<Statement> statements = modelDataset.iterator();
        while (statements.hasNext()) {
            Statement st = statements.next();
            if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCTERMS.HAS_VERSION)) {
                Literal version = new LiteralImpl(st.getObject().stringValue(),
                        XMLSchema.FLOAT);
                this.setVersion(version);
            } else if (st.getSubject().equals(datasetURI)
                    && (st.getPredicate().equals(RDFS.LABEL)
                    || st.getPredicate().equals(DCTERMS.TITLE))) {
                Literal title = new LiteralImpl(st.getObject().stringValue(),
                        XMLSchema.STRING);
                this.setTitle(title);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCTERMS.DESCRIPTION)) {
                Literal description = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.setDescription(description);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCTERMS.PUBLISHER)) {
                URI publisher = (URI) st.getObject();
                this.setPublisher(publisher);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCTERMS.LANGUAGE)) {
                URI language = (URI) st.getObject();
                this.setLanguage(language);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCTERMS.LICENSE)) {
                URI license = (URI) st.getObject();
                this.setLicense(license);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCTERMS.RIGHTS)) {
                URI rights = (URI) st.getObject();
                this.setRights(rights);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCAT.LANDING_PAGE)) {
                URI landingPage = (URI) st.getObject();
                this.setLandingPage(landingPage);
            } else if ( st.getPredicate().equals(DCAT.THEME)) {
                URI theme = (URI) st.getObject();
                this.getThemes().add(theme);
            } else if ( st.getPredicate().equals(DCAT.CONTACT_POINT)) {
                URI contactPoint = (URI) st.getObject();
                this.setContactPoint(contactPoint);
            } else if (st.getSubject().equals(datasetURI)
                    && st.getPredicate().equals(DCAT.KEYWORD)) {
                Literal keyword = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.getKeywords().add(keyword);
            }
        }  
        checkMandatoryMetadata(LOGGER);
        if (this.getThemes().isEmpty()) {
            String errMsg = "No dcat:theme provided";
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        }
    }
    
    private void buildDatasetMetadataModel() {
        org.openrdf.model.Model model = new LinkedHashModel();
        model.add(this.getUri(), RDF.TYPE, DCAT.TYPE_DATASET);        
        if (this.getPublisher() != null) {
           model.add(this.getUri(), DCTERMS.PUBLISHER, this.getPublisher()); 
        }
        if (this.getLanguage() != null) {
            model.add(this.getUri(), DCTERMS.LANGUAGE, this.getLanguage());
        }
        if (this.getContactPoint() != null) {
           model.add(this.getUri(), DCAT.CONTACT_POINT, this.getContactPoint()); 
        }
        if (this.getLandingPage() != null) {
           model.add(this.getUri(), DCAT.LANDING_PAGE, this.getLandingPage()); 
        }
        for(URI theme:this.getThemes()) {
            model.add(this.getUri(), DCAT.THEME, theme);
        }   
        for(Literal keyword:this.getKeywords()) {
            model.add(this.getUri(), DCAT.KEYWORD, keyword);
        }
        model.add(this.getCatalogURI(), DCAT.DATASET, this.getUri());
        model.add(this.getCatalogURI(), DCTERMS.MODIFIED, this.getModified());
        this.setModel(model);
        
    }

    /**
     * @param publisher the publisher to set
     */
    protected void setPublisher(URI publisher) {
        this.publisher = publisher;
    }

    /**
     * @param language the language to set
     */
    protected void setLanguage(URI language) {
        this.language = language;
    }

    /**
     * @param distribution the distribution to set
     */
    protected void setDistribution(List<URI> distribution) {
        this.distribution = distribution;
    }

    /**
     * @param themes the themes to set
     */
    protected void setThemes(List<URI> themes) {
        this.themes = themes;
    }

    /**
     * @param contactPoint the contactPoint to set
     */
    protected void setContactPoint(URI contactPoint) {
        this.contactPoint = contactPoint;
    }

    /**
     * @param keywords the keywords to set
     */
    protected void setKeywords(List<Literal> keywords) {
        this.keywords = keywords;
    }

    /**
     * @param landingPage the landingPage to set
     */
    protected void setLandingPage(URI landingPage) {
        this.landingPage = landingPage;
    }

    /**
     * @return the publisher
     */
    public URI getPublisher() {
        return publisher;
    }

    /**
     * @return the language
     */
    public URI getLanguage() {
        return language;
    }

    /**
     * @return the distribution
     */
    public List<URI> getDistribution() {
        return distribution;
    }

    /**
     * @return the themes
     */
    public List<URI> getThemes() {
        return themes;
    }

    /**
     * @return the contactPoint
     */
    public URI getContactPoint() {
        return contactPoint;
    }

    /**
     * @return the keywords
     */
    public List<Literal> getKeywords() {
        return keywords;
    }

    /**
     * @return the landingPage
     */
    public URI getLandingPage() {
        return landingPage;
    }

    /**
     * @return the catalogURI
     */
    public URI getCatalogURI() {
        return catalogURI;
    }

    /**
     * @param catalogURI the catalogURI to set
     */
    protected void setCatalogURI(URI catalogURI) {
        this.catalogURI = catalogURI;
    }

}
