package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.aoipmh.writables.Writable;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class Error implements Writable {
    private final String value;
    private Code code;
    @Autowired
    @Qualifier("baseURI")
    private String baseUrl;
    
    public Error (String message) {
        this.value = message;
    }

    public String getMessage() {
        return value;
    }

    public Code getCode() {
        return code;
    }

    public Error withCode(Code value) {
        this.code = value;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            if (this.code != null){
                writer.writeAttribute("code", this.code.toString());
            }
            writer.writeCharacters(value);
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        Resource rec = rdfModel.createResource();
        if (this.code != null){
            rec.addProperty(rdfModel.createProperty("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#hasCode"), this.code.toString());
        }
        rec.addProperty(rdfModel.createProperty("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/rlog#message"), this.value);
        rdfModel.write(writer, format);
    }

    public static enum Code {

        CANNOT_DISSEMINATE_FORMAT("cannotDisseminateFormat"),
        ID_DOES_NOT_EXIST("idDoesNotExist"),
        BAD_ARGUMENT("badArgument"),
        BAD_VERB("badVerb"),
        NO_METADATA_FORMATS("noMetadataFormats"),
        NO_RECORDS_MATCH("noRecordsMatch"),
        BAD_RESUMPTION_TOKEN("badResumptionToken"),
        NO_SET_HIERARCHY("noSetHierarchy");

        private final String code;

        Code(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }



        public static Code fromCode(String code) {
            for (Code c : Code.values()) {
                if (c.code.equals(code)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(code);
        }

        @Override
        public String toString() {
            return code;
        }
    }
}
