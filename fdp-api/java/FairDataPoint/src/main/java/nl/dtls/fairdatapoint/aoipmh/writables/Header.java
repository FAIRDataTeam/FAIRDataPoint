package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 
 * @author Shamanou van Leeuwen
 */
public class Header implements Writable {
    protected String identifier;
    protected Date datestamp;
    protected List<String> setSpec = new ArrayList<>();
    protected Status status;

    @Autowired
    private String baseURI;
    
    public String getIdentifier() {
        return identifier;
    }

    public Header withIdentifier(String value) {
        this.identifier = value;
        return this;
    }

    public Date getDatestamp() {
        return datestamp;
    }

    public Header withDatestamp(Date value) {
        this.datestamp = value;
        return this;
    }

    public List<String> getSetSpecs() {
        return this.setSpec;
    }

    public Status getStatus() {
        return status;
    }

    public Header withStatus(Status value) {
        this.status = value;
        return this;
    }

    public Header withSetSpec(String setSpec) {
        this.setSpec.add(setSpec);
        return this;
    }

    public boolean isDeleted () {
        return this.status != null;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            if (this.status != null){
                writer.writeAttribute("status", this.status.value());
            }
            writer.writeElement("identifier", identifier);
            writer.writeElement("datestamp", datestamp);
            for (String setSpec : this.getSetSpecs()){
                writer.writeElement("setSpec", setSpec);
            }
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        Resource rdfRec = rdfModel.createResource(this.baseURI)
            .addLiteral(DCTerms.identifier, this.identifier)
            .addLiteral(DCTerms.date, this.datestamp);
        for (String setSpec : this.getSetSpecs()){
            rdfRec.addLiteral(DCTerms.format, setSpec);
        }
        rdfModel.write(writer, format);
    }


    public enum Status {
        DELETED("deleted");

        private final String representation;

        Status(String representation) {
            this.representation = representation;
        }

        public String value() {
            return representation;
        }

        public static Status fromRepresentation(String representation) {
            for (Status status : Status.values()) {
                if (status.representation.equals(representation)) {
                    return status;
                }
            }
            throw new IllegalArgumentException(representation);
        }
    }
}
