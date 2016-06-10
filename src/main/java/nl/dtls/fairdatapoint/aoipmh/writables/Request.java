package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import java.util.Date;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;

/**
 * 
 * @author Shamanou van Leeuwen
 */
public class Request implements Writable {

    private final String baseUrl;
    private String verbType;
    private String identifier;
    private String metadataPrefix;
    private Date from;
    private Date until;
    private String set;
    private String resumptionToken;
    private String fromString;
    private String untilString;

    public Request(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public Request withIdentifier(String identifier){
        this.identifier = identifier;
        return this;
    }

    public Verb.Type getVerbType() {
        return Verb.Type.fromValue(verbType);
    }

    public String getVerb () {
        return verbType;
    }

    public Request withVerbType(Verb.Type value) {
        this.verbType = value.displayName();
        return this;
    }
    
    public Request withVerbType (String verb) {
        this.verbType = verb;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public Request withMetadataPrefix(String value) {
        this.metadataPrefix = value;
        return this;
    }

    public Date getFrom() {
        return from;
    }

    public Request withFrom(Date value) {
        this.from = value;
        return this;
    }

    public Date getUntil() {
        return until;
    }

    public Request withUntil(Date value) {
        this.until = value;
        return this;
    }

    public String getSet() {
        return set;
    }

    public Request withSet(String value) {
        this.set = value;
        return this;
    }

    public String getResumptionToken() {
        return resumptionToken;
    }

    public Request withResumptionToken(String value) {
        this.resumptionToken = value;
        return this;
    }

    public Request withFrom(String value) {
        this.fromString = value;
        return this;
    }

    public Request withUntil(String value) {
        this.untilString = value;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            writer.writeAttribute("verb", verbType);
            writer.writeAttribute("identifier", identifier);
            writer.writeAttribute("metadataPrefix", metadataPrefix);
            if (from != null) {
                writer.writeAttribute("from", from);
            } else {
                writer.writeAttribute("from", fromString);
            } if (until != null) {
                writer.writeAttribute("until", until);
            } else {
                writer.writeAttribute("until", untilString);
            }
            writer.writeAttribute("set", set);
            writer.writeAttribute("resumptionToken", resumptionToken);
            writer.writeCharacters(baseUrl);
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }
    
    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        Resource rec = rdfModel.createResource(this.baseUrl);
        rec.addLiteral(DCTerms.identifier, this.identifier);
        if (from != null){
           rec.addLiteral(DCTerms.date, this.from);
        } else if(this.fromString != null) {
           rec.addLiteral(DCTerms.date, this.fromString);
        } if (until != null){
           rec.addLiteral(DCTerms.date, this.until);
        } else if(this.untilString != null){ 
           rec.addLiteral(DCTerms.date, this.untilString);
        }
        rdfModel.write(writer, format);
    }
}
