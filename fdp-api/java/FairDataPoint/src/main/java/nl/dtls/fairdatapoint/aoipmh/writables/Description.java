package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.DC;
import java.io.StringWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class Description implements Writable {
    
    @Autowired
    private String baseURI;
    protected String value;
    

    public Description withMetadata(String metadata) {
        this.value = metadata;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (this.value != null) {
            EchoElement echo = new EchoElement("<description>"+value+"</description>");
            echo.write(writer);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        if (this.value != null) {
            rdfModel.createResource(this.baseURI)
                .addLiteral(DC.description, this.value);
        }
        rdfModel.write(writer, format);
    }
}
