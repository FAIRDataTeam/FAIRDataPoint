package nl.dtls.fairdatapoint.api.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import nl.dtls.fairdatapoint.api.domain.Metadata;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtls.fairdatapoint.utils.RDFUtils;
import nl.dtls.fairdatapoint.utils.vocabulary.DCAT;
import org.apache.logging.log4j.LogManager;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-08-16
 * @version 0.1
 */
public class DistributionMetadata extends Metadata {
    
    private URI datasetURI;
    private URI accessURL;
    private URI downloadURL;
    private Literal mediaType;
    private Literal format;
    private Literal byteSize;
    
    private static final org.apache.logging.log4j.Logger LOGGER
            = LogManager.getLogger(DistributionMetadata.class);

    public DistributionMetadata(String distributionMetaData, 
            String distributionID, String datasetURI, RDFFormat format) throws 
            MetadataExeception, DatatypeConfigurationException {
       StringReader reader = new StringReader(distributionMetaData);
        String baseURL = datasetURI + "/" +  distributionID;
        URI distributionURI = new URIImpl(baseURL);
        Literal id = new LiteralImpl(distributionID, XMLSchema.STRING);
        org.openrdf.model.Model metadata;
        try {
            metadata = Rio.parse(reader, baseURL, format);
            Iterator<Statement> it = metadata.iterator();
            List<Statement> statements = ImmutableList.copyOf(it);
            extractDistributionMetadata(distributionURI, statements);            
            this.setDatasetURI(new URIImpl(datasetURI));
            this.setUri(distributionURI);
            this.setIdentifier(id);
            this.setIssued(RDFUtils.getCurrentTime());
            this.setModified(this.getIssued());
            buildDistributionMetadataModel();
        } catch (IOException ex) {
            String errMsg = "Error reading distribution metadata content"
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } catch (RDFParseException ex) {
            String errMsg = "Error parsing distribution metadata content. "
                    + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } catch (UnsupportedRDFormatException ex) {
            String errMsg = "Unsuppoerted RDF format. " + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } 
    }
    
    public DistributionMetadata(String distributionURI, 
            List<Statement> metadata) 
            throws MetadataExeception, 
            DatatypeConfigurationException {
        try {
            this.setUri(new URIImpl(distributionURI));
            extractDistributionMetadata(this.getUri(), metadata);
            this.setStatements(metadata);
        } catch (UnsupportedRDFormatException ex) {
            String errMsg = "Unsuppoerted RDF format. " + ex.getMessage();
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        } 
    }
    
    private void extractDistributionMetadata(URI distributionURI,
            List<Statement> metadata) 
            throws MetadataExeception {
        Iterator<Statement> statements = metadata.iterator();        
        extractMetadata(distributionURI, metadata);
        while (statements.hasNext()) {
            Statement st = statements.next();
            if (st.getSubject().equals(distributionURI)
                    && st.getPredicate().equals(DCAT.ACCESS_URL)) {
                URI accessURL = (URI) st.getObject();
                this.setAccessURL(accessURL);
            } else if (st.getSubject().equals(distributionURI)
                    && st.getPredicate().equals(DCAT.DOWNLOAD_URL)) {
                URI downloadURL = (URI) st.getObject();
                this.setDownloadURL(downloadURL);
            } else if (st.getSubject().equals(distributionURI)
                    && st.getPredicate().equals(DCAT.FORMAT)) {
                 Literal format = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.setFormat(format);
            } else if (st.getSubject().equals(distributionURI)
                    && st.getPredicate().equals(DCAT.BYTE_SIZE)) {
                 Literal byteSize = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.setByteSize(byteSize);
            } else if (st.getSubject().equals(distributionURI)
                    && st.getPredicate().equals(DCAT.MEDIA_TYPE)) {
                 Literal mediaType = new LiteralImpl(st.getObject().
                        stringValue(), XMLSchema.STRING);
                this.setMediaType(mediaType);
            }
        }
        if (this.getAccessURL() == null && this.getDownloadURL() == null ) {
            String errMsg = 
                    "No dcat:accessURL or dcat:downloadURL URL is provided";
            LOGGER.error(errMsg);
            throw (new MetadataExeception(errMsg));
        }
    }
    
    private void buildDistributionMetadataModel() {
        org.openrdf.model.Model model = new LinkedHashModel();
        model.add(this.getUri(), RDF.TYPE, DCAT.TYPE_DISTRIBUTION);        
        if (this.getAccessURL() != null) {
           model.add(this.getUri(), DCAT.ACCESS_URL, this.getAccessURL()); 
        } else if(this.getDownloadURL() != null) {
           model.add(this.getUri(), DCAT.DOWNLOAD_URL, this.getDownloadURL()); 
        }
        if (this.getByteSize() != null) {
            model.add(this.getUri(), DCAT.BYTE_SIZE, this.getByteSize());
        }
        if (this.getFormat() != null) {
            model.add(this.getUri(), DCAT.FORMAT, this.getFormat());
        }
        if (this.getMediaType() != null) {
            model.add(this.getUri(), DCAT.MEDIA_TYPE, this.getMediaType());
        }
        if(this.getDatasetURI() != null) {
           model.add(this.getDatasetURI(), DCAT.DISTRIBUTION, this.getUri());            
           model.add(this.getDatasetURI(), DCTERMS.MODIFIED, 
                   this.getModified()); 
        }
        
        this.setStatements(model);
        
    }

    /**
     * @param datasetURI the datasetURI to set
     */
    protected void setDatasetURI(URI datasetURI) {
        this.datasetURI = datasetURI;
    }

    /**
     * @param accessURL the accessURL to set
     */
    protected void setAccessURL(URI accessURL) {
        this.accessURL = accessURL;
    }

    /**
     * @param downloadURL the downloadURL to set
     */
    protected void setDownloadURL(URI downloadURL) {
        this.downloadURL = downloadURL;
    }

    /**
     * @param mediaType the mediaType to set
     */
    protected void setMediaType(Literal mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * @param format the format to set
     */
    protected void setFormat(Literal format) {
        this.format = format;
    }

    /**
     * @param byteSize the byteSize to set
     */
    protected void setByteSize(Literal byteSize) {
        this.byteSize = byteSize;
    }

    /**
     * @return the datasetURI
     */
    public URI getDatasetURI() {
        return datasetURI;
    }

    /**
     * @return the accessURL
     */
    public URI getAccessURL() {
        return accessURL;
    }

    /**
     * @return the downloadURL
     */
    public URI getDownloadURL() {
        return downloadURL;
    }

    /**
     * @return the mediaType
     */
    public Literal getMediaType() {
        return mediaType;
    }

    /**
     * @return the format
     */
    public Literal getFormat() {
        return format;
    }

    /**
     * @return the byteSize
     */
    public Literal getByteSize() {
        return byteSize;
    }
    
}
