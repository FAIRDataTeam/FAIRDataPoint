/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nl.dtls.fairdatapoint.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;

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
    private List<URI> publisher = new ArrayList();
    private URI language;
    private List<Statement> statements ;
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
    protected void setStatements(org.openrdf.model.Model model) {       
        model.add(this.getUri(), DCTERMS.TITLE, this.getTitle());
        model.add(this.getUri(), RDFS.LABEL, this.getTitle());
        model.add(this.getUri(), DCTERMS.IDENTIFIER, this.getIdentifier());
        model.add(this.getUri(), DCTERMS.ISSUED, this.getIssued());
        model.add(this.getUri(), DCTERMS.MODIFIED, this.getModified());
        model.add(this.getUri(), DCTERMS.HAS_VERSION, this.getVersion());
        if (!this.getPublisher().isEmpty()) {
            for(URI publisher:this.getPublisher()) {
               model.add(this.getUri(), DCTERMS.PUBLISHER, publisher);  
            }           
        }
        if (this.getLanguage() != null) {
            model.add(this.getUri(), DCTERMS.LANGUAGE, this.getLanguage());
        }
        if (this.getDescription() != null) {
            model.add(this.getUri(), DCTERMS.DESCRIPTION, 
                    this.getDescription());
        }
        if (this.getLicense() != null) {
            model.add(this.getUri(), DCTERMS.LICENSE, this.getLicense());
        }
        if (this.getRights() != null) {
            model.add(this.getUri(), DCTERMS.RIGHTS, this.getRights());
        }
        Iterator<Statement> it = model.iterator();
        List<Statement> statements = ImmutableList.copyOf(it);
        this.statements = statements;
    }
    
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    protected void extractMetadata(URI resourceURI,
            List<Statement> metadata)
            throws MetadataExeception {
        Iterator<Statement> statements = metadata.iterator();
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
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.PUBLISHER)) {
                URI publisher = (URI) st.getObject();
                this.getPublisher().add(publisher);
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.LANGUAGE)) {
                URI language = (URI) st.getObject();
                this.setLanguage(language);
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.IDENTIFIER)
                    && this.getIdentifier() == null) {
                this.setIdentifier((Literal) st.getObject());
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.ISSUED)
                    && this.getIssued() == null) {
                this.setIssued((Literal) st.getObject());
            } else if (st.getSubject().equals(resourceURI)
                    && st.getPredicate().equals(DCTERMS.MODIFIED)
                    && this.getModified() == null) {
                this.setModified((Literal) st.getObject());
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

    public String getMetadataAsRDFString(RDFFormat format) throws 
            MetadataExeception {
        String metadata = null;
        if (!this.getStatements().isEmpty()) {
            try {        
                metadata = RDFUtils.writeToString(this.getStatements(), format);
            } catch (Exception ex) {
                String errMsg = "Error getting metadata as RDF string, Message " 
                        +ex.getMessage();
                LOGGER.error(errMsg);
                throw(new MetadataExeception(errMsg));
            }
        }
        return metadata;
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
     * @return the List<Statement>
     */
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * @return the publisher
     */
    public List<URI> getPublisher() {
        return publisher;
    }

    /**
     * @return the language
     */
    public URI getLanguage() {
        return language;
    }   

    /**
     * @param language the language to set
     */
    protected void setLanguage(URI language) {
        this.language = language;
    }
}
