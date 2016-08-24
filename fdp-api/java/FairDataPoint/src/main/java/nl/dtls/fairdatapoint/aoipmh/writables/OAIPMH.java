package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.xml.XSISchema;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dtls.fairdatapoint.aoipmh.Error;
/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class OAIPMH implements Writable {
    public static final String NAMESPACE_URI = "http://www.openarchives.org/OAI/2.0/";
    public static final String SCHEMA_LOCATION = "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
    private String contentType;

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

    public OAIPMH withResponseDate(Date responseDate) {
        this.responseDate = responseDate;
        return this;
    }

    public OAIPMH withRequest(Request request) {
        this.request = request;
        return this;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public String withError(Error error) {
        this.errors.add(error);
        return this.toString();
    }

    public boolean hasErrors () {
        return !this.errors.isEmpty();
    }

    public Verb getVerb() {
        return verb;
    }

    public OAIPMH withVerb(Verb verb) {
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
                for (Error error : errors){
                    writer.writeElement("error", error.getMessage());
                }
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
        request.write(writer, format);
        if (!errors.isEmpty()) {
            for (Error error : errors){
                error.write(writer,format);
            }
        }
    }

    @Override
    public String toString(){
        String out = null;
        try {;
            XmlWriter xmlwriter = new XmlWriter(new ByteArrayOutputStream());
//            StringWriter rdfWriter = new StringWriter();
//            switch (this.contentType){ 
//                case "text/xml":
                    this.write(xmlwriter);
                    xmlwriter.flush();
                    out = xmlwriter.getOutputStream().toString();
//                    break;
//                case "application/xml":
//                    this.write(xmlwriter);
//                    xmlwriter.flush();
//                    out = xmlwriter.getOutputStream().toString();
//                    break;
//                case "text/turtle":
//                    this.write(rdfWriter, "TTL");
//                    rdfWriter.flush();
//                    out = rdfWriter.getBuffer().toString();
//                    break;
//            }
        } catch (XMLStreamException | XmlWriteException ex) {
            Logger.getLogger(OAIPMH.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        return out;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

