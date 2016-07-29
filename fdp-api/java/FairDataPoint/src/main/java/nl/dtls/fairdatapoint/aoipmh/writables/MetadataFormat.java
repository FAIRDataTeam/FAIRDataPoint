package nl.dtls.fairdatapoint.aoipmh.writables;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class MetadataFormat implements Writable {
    protected String metadataPrefix;
    protected String schema;
    protected String metadataNamespace;
    
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public MetadataFormat withMetadataPrefix(String value) {
        this.metadataPrefix = value;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public MetadataFormat withSchema(String value) {
        this.schema = value;
        return this;
    }

    public String getMetadataNamespace() {
        return metadataNamespace;
    }

    public MetadataFormat withMetadataNamespace(String value) {
        this.metadataNamespace = value;
        return this;
    }

    /**
     *
     * @param writer
     */
    @Override
    public void write(XmlWriter writer) {
        if (metadataPrefix != null)
            try {
                writer.writeElement("metadataPrefix", metadataPrefix);
        } catch (XmlWriteException ex) {
            Logger.getLogger(MetadataFormat.class.getName()).log(Level.SEVERE, null, ex);
        } if (null != schema)
            try {
                writer.writeElement("schema", schema);
        } catch (XmlWriteException ex) {
            Logger.getLogger(MetadataFormat.class.getName()).log(Level.SEVERE, null, ex);
        } if (null != metadataNamespace)
            try {
                writer.writeElement("metadataNamespace", metadataNamespace);
        } catch (XmlWriteException ex) {
            Logger.getLogger(MetadataFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    @Override
    public void write(StringWriter writer, String format) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
