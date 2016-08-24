package nl.dtls.fairdatapoint.aoipmh;

import nl.dtls.fairdatapoint.aoipmh.writables.Verb;
import nl.dtls.fairdatapoint.aoipmh.writables.MetadataFormat;
import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
*/
public class ListMetadataFormats implements Verb {
    protected List<MetadataFormat> metadataFormats = new ArrayList<>();
    
    public List<MetadataFormat> getMetadataFormats() {
        return this.metadataFormats;
    }

    public ListMetadataFormats withMetadataFormat (MetadataFormat mdf) {
        metadataFormats.add(mdf);
        return this;
    }
    
    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (!this.metadataFormats.isEmpty()){
            for (MetadataFormat format : this.metadataFormats){
                writer.writeElement("metadataFormat",format);
            }
        }
    }

    @Override
    public void write(StringWriter writer, String format){
        if (!this.metadataFormats.isEmpty()){
            for (MetadataFormat f : this.metadataFormats){
                f.write(writer, format);
            }
        }
    }

    @Override
    public Type getType() {
        return Type.ListMetadataFormats; 
    }
}
