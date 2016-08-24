package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.xml.XSISchema;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class Response implements Writable {
    public static final String NAMESPACE_URI = "http://www.openarchives.org/OAI/2.0/";
    public static final String SCHEMA_LOCATION = "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";

    private Date responseDate = new Date();
    private final List<Error> errors = new ArrayList<>();
    private Request request;
    private Verb verb;
    
    public Date getResponseDate() {
        return responseDate;
    }

    public Request getRequest() {
        return request;
    }

    public Response withResponseDate(Date responseDate) {
        this.responseDate = responseDate;
        return this;
    }

    public Response withRequest(Request request) {
        this.request = request;
        return this;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Response withError(Error error) {
        this.errors.add(error);
        return this;
    }

    public boolean hasErrors () {
        return !this.errors.isEmpty();
    }

    public Verb getVerb() {
        return verb;
    }

    public Response withVerb(Verb verb) {
        this.verb = verb;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            writer.writeStartElement("OAI-PMH");
            writer.writeDefaultNamespace(NAMESPACE_URI);
            writer.writeNamespace(XSISchema.PREFIX, XSISchema.NAMESPACE_URI);
            writer.writeAttribute(XSISchema.PREFIX, XSISchema.NAMESPACE_URI, "schemaLocation",
                    NAMESPACE_URI + " " + SCHEMA_LOCATION);

            writer.writeElement("responseDate", this.responseDate, Granularity.Second);
            writer.writeElement("request", request);

            if (!errors.isEmpty()) {
                for (Error error : errors)
                    writer.writeElement("error", error.toString());
            } else {
                if (verb == null) throw new XmlWriteException("An error or a valid response must be set");
                writer.writeElement(verb.getType().displayName(), verb);
            }

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        rdfModel.createTypedLiteral(this.responseDate);
        this.request.write(writer, format);
        rdfModel.write(writer, format);
    }
}
