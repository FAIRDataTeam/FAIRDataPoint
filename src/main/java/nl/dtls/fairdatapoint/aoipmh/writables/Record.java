package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 * 
 * @author Shamanou van Leeuwen
 */
public class Record implements Writable {

    protected Header header;
    protected Metadata metadata;
    protected List<About> abouts = new ArrayList<>();

    public Header getHeader() {
        return header;
    }

    public Record withHeader(Header value) {
        this.header = value;
        return this;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public Record withMetadata(Metadata value) {
        this.metadata = value;
        return this;
    }

    public Record withAbout(About itemAbout) {
        this.abouts.add(itemAbout);
        return this;
    }

    public List<About> getAbouts() {
        return this.abouts;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            if (this.header != null) {
                writer.writeStartElement("header");
                this.header.write(writer);
                writer.writeEndElement();
            } if (this.metadata != null) {
                writer.writeStartElement("metadata");
                this.metadata.write(writer);
                writer.writeEndElement();
            } for (About about : getAbouts()) {
                writer.writeStartElement("about");
                about.write(writer);
                writer.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        if (this.header != null) {
            this.header.write(writer, format);
        } if (this.metadata != null){
            this.metadata.write(writer, format);
        } for (About about : getAbouts()) {
            about.write(writer, format);
        }
    }
}
