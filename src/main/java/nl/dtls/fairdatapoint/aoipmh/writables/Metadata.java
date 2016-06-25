package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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

    public Metadata(InputStream value) throws IOException {
        this.string = IOUtils.toString(value);
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        EchoElement elem = new EchoElement(string);
        elem.write(writer);
    }

    @Override
    public void write(StringWriter writer, String format) {
        EchoElement elem = new EchoElement(string);
        elem.write(writer, format);
    }
}
