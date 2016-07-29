package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import com.lyncode.xoai.xml.EchoElement;
import java.io.StringWriter;

public class About implements Writable {
    private final String value;

    public About(String xmlValue) {
        this.value = xmlValue;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (this.value != null) {
            EchoElement elem = new EchoElement(value);
            elem.write(writer);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        if (this.value != null){
            rdfModel.createLiteral(this.value);
        }
        rdfModel.write(writer, format);
    }
}
