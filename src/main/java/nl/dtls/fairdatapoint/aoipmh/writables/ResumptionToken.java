package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.ResumptionToken.Value;
import javax.xml.stream.XMLStreamException;
import java.util.Date;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import static com.lyncode.xoai.model.oaipmh.Granularity.Second;
import java.io.StringWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class ResumptionToken implements Writable {
    
    public final Value value;
    private Date expirationDate;
    private Long completeListSize;
    private Long cursor;

    public ResumptionToken (Value value) {
        this.value = value;
    }
    public ResumptionToken () {
        this.value = new Value();
    }

    public Value getValue() {
        return value;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public ResumptionToken withExpirationDate(Date value) {
        this.expirationDate = value;
        return this;
    }

    public Long getCompleteListSize() {
        return completeListSize;
    }

    public ResumptionToken withCompleteListSize(long value) {
        this.completeListSize = value;
        return this;
    }

    public Long getCursor() {
        return cursor;
    }

    public ResumptionToken withCursor(long value) {
        this.cursor = value;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            if (this.expirationDate != null)
                writer.writeAttribute("expirationDate", this.expirationDate, Second);
            if (this.completeListSize != null)
                writer.writeAttribute("completeListSize", "" + this.completeListSize);
            if (this.cursor != null)
                writer.writeAttribute("cursor", "" + this.cursor);
            if (this.value != null)
                writer.write(this.value);
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        if (this.expirationDate != null){
            rdfModel.createTypedLiteral(this.expirationDate);
        } if (this.completeListSize != null) {
           rdfModel.createTypedLiteral(this.completeListSize); 
        } if (this.cursor != null){
            rdfModel.createTypedLiteral(this.cursor);
        }  if (this.value != null){
            rdfModel.createTypedLiteral(this.value);
        }
        rdfModel.write(writer, format);
    }
}
