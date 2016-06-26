package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.annotation.XmlValue;
import nl.dtls.fairdatapoint.aoipmh.OAIMetadata;
import nl.dtls.fairdatapoint.utils.XmlWriter;
/**
 * 
 * @author Shamanou van Leeuwen
 */
public class Metadata implements Writable {
    private String string;

    public Metadata(String value) {
        this.string = value;
    }
    
    @XmlValue
    protected OAIMetadata value;

    public Metadata(OAIMetadata value) {
        this.value = value;
    }

    public Metadata(InputStream value) throws IOException {
        this.string = IOUtils.toString(value);
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (this.value != null)
            this.value.write(writer);
        else {
            EchoElement elem = new EchoElement(string);
            elem.write(writer);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        EchoElement elem = new EchoElement(string);
        elem.write(writer, format);
    }
    
    public OAIMetadata getValue () {
        return value;
    }
}
