/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-11
 * @version 0.1
 */
public class Metadata {

    private Literal title;
    private Literal identifier;
    private Literal issued;
    private Literal modified;
    private Literal version;
    private Literal description;
    private URI license;
    private URI rights;
    private URI uri;
    private org.openrdf.model.Model model;
    private static final org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(Metadata.class);

    /**
     * @param title the title to set
     */
    protected void setTitle(Literal title) {
        this.title = title;
    }

    /**
     * @param identifier the identifier to set
     */
    protected void setIdentifier(Literal identifier) {
        this.identifier = identifier;
    }

    /**
     * @param issued the issued to set
     */
    protected void setIssued(Literal issued) {
        this.issued = issued;
    }

    /**
     * @param modified the modified to set
     */
    protected void setModified(Literal modified) {
        this.modified = modified;
    }

    /**
     * @param version the version to set
     */
    protected void setVersion(Literal version) {
        this.version = version;
    }

    /**
     * @param description the description to set
     */
    protected void setDescription(Literal description) {
        this.description = description;
    }

    /**
     * @param license the license to set
     */
    protected void setLicense(URI license) {
        this.license = license;
    }

    /**
     * @param rights the rights to set
     */
    protected void setRights(URI rights) {
        this.rights = rights;
    }

    /**
     * @param uri the uri to set
     */
    protected void setUri(URI uri) {
        this.uri = uri;
    }

    /**
     * @param model the model to set
     */
    protected void setModel(org.openrdf.model.Model model) {
        this.model = model;
        this.model.add(this.getUri(), DCTERMS.TITLE, this.getTitle());
        this.model.add(this.getUri(), RDFS.LABEL, this.getTitle());
        this.model.add(this.getUri(), DCTERMS.IDENTIFIER, this.getIdentifier());
        this.model.add(this.getUri(), DCTERMS.ISSUED, this.getIssued());
        this.model.add(this.getUri(), DCTERMS.MODIFIED, this.getModified());
        this.model.add(this.getUri(), DCTERMS.HAS_VERSION, this.getVersion());
        if (this.getDescription() != null) {
            this.model.add(this.getUri(), DCTERMS.DESCRIPTION, 
                    this.getDescription());
        }
        if (this.getLicense() != null) {
            model.add(this.getUri(), DCTERMS.LICENSE, this.getLicense());
        }
        if (this.getRights() != null) {
            model.add(this.getUri(), DCTERMS.RIGHTS, this.getRights());
        }
    }
    
    protected void extractMetadata(URI resourceURI,
            org.openrdf.model.Model modelDataset) 
            throws MetadataExeception {
        Iterator<Statement> statements = modelDataset.iterator();
        while (statements.hasNext()) {
            Statement st = statements.next();
            if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.HAS_VERSION)) {
                Literal version = new LiteralImpl(st.getObject().stringValue(),
                        XMLSchema.FLOAT);
                this.setVersion(version);
            } else if (st.getSubject().equals(resourceURI)
                    && (st.getPredicate().equals(RDFS.LABEL)
                    || st.getPredicate().equals(DCTERMS.TITLE))) {
                Literal title = new LiteralImpl(st.getObject().stringValue(),
                        XMLSchema.STRING);
                this.setTitle(title);
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.DESCRIPTION)) {
                Literal description = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.setDescription(description);
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.LICENSE)) {
                URI license = (URI) st.getObject();
                this.setLicense(license);
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.RIGHTS)) {
                URI rights = (URI) st.getObject();
                this.setRights(rights);
            } 
        }  
        checkMetadata();
    }    
    
    protected void checkMetadata() throws MetadataExeception {
        if (this.getVersion() == null) {
            String errMsg = "No version number provided";
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } else if (this.getTitle() == null) {
            String errMsg = "No title or label provided";
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        }
    }

    /**
     * @return the title
     */
    public Literal getTitle() {
        return title;
    }

    /**
     * @return the identifier
     */
    public Literal getIdentifier() {
        return identifier;
    }

    /**
     * @return the issued
     */
    public Literal getIssued() {
        return issued;
    }

    /**
     * @return the modified
     */
    public Literal getModified() {
        return modified;
    }

    /**
     * @return the version
     */
    public Literal getVersion() {
        return version;
    }

    /**
     * @return the description
     */
    public Literal getDescription() {
        return description;
    }

    /**
     * @return the license
     */
    public URI getLicense() {
        return license;
    }

    /**
     * @return the rights
     */
    public URI getRights() {
        return rights;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return the model
     */
    public org.openrdf.model.Model getModel() {
        return model;
    }
}
