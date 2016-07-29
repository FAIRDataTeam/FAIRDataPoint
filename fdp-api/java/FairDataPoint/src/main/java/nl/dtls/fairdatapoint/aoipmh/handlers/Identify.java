package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import com.lyncode.xoai.model.oaipmh.Granularity;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.writables.Description;
import nl.dtls.fairdatapoint.aoipmh.writables.Verb;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VCARD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Identify implements Verb {
    @Autowired
    protected String repositoryName;
    @Autowired
    @Qualifier("baseURI")
    protected String baseURL;
    @Autowired
    protected String protocolVersion;
    @Autowired
    protected ArrayList<String> adminEmails;
    protected Date earliestDatestamp;
    protected DeletedRecord deletedRecord = DeletedRecord.NO;
    protected Granularity granularity = Granularity.Second;
    protected List<String> compressions = new ArrayList<>();
    protected Description description;
    
    public String getProtocolVersion() {
        return protocolVersion;
    }

    public ArrayList<String> getAdminEmails() {
        return this.adminEmails;
    }

    public Date getEarliestDatestamp() {
        return earliestDatestamp;
    }

    public Identify withEarliestDatestamp(Date value) {
        this.earliestDatestamp = value;
        return this;
    }

    public DeletedRecord getDeletedRecord() {
        return deletedRecord;
    }

    public Identify withDeletedRecord(DeletedRecord value) {
        this.deletedRecord = value;
        return this;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public Identify withGranularity(Granularity value) {
        this.granularity = value;
        return this;
    }

    public List<String> getCompressions() {
        return this.compressions;
    }

    public Description getDescription() {
        return this.description;
    }

    public Identify withCompression (String compression) {
        this.compressions.add(compression);
        return this;
    }
    public Identify withDescription (Description description) {
        this.description = description;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (this.repositoryName == null) throw new XmlWriteException("Repository Name cannot be null");
        if (this.baseURL == null) throw new XmlWriteException("Base URL cannot be null");
        if (this.protocolVersion == null) throw new XmlWriteException("Protocol version cannot be null");
        if (this.earliestDatestamp == null) throw new XmlWriteException("Eerliest datestamp cannot be null");
        if (this.deletedRecord == null) throw new XmlWriteException("Deleted record persistency cannot be null");
        if (this.granularity == null) throw new XmlWriteException("Granularity cannot be null");
        if (this.adminEmails == null) throw new XmlWriteException("List of admin emails cannot be null or empty");

        writer.writeElement("repositoryName", repositoryName);
        writer.writeElement("baseURL", baseURL);
        writer.writeElement("protocolVersion", protocolVersion);

        for (String email : this.adminEmails){
            writer.writeElement("adminEmail", email);
        }
        writer.writeElement("earliestDatestamp", earliestDatestamp, Granularity.Second);
        writer.writeElement("deletedRecord", deletedRecord.value());
        writer.writeElement("granularity", granularity.toString());

        if (!this.compressions.isEmpty()){
            for (String compression : this.compressions){
                writer.writeElement("compression", compression);
            }
        }
        if (this.description != null){
            writer.writeElement("description", this.description);
        }
    }

    @Override
    public Type getType() {
        return Type.Identify;
    }

    @Override
    public void write(StringWriter writer, String format) {
        Model rdfModel = ModelFactory.createDefaultModel();
        Resource rdfRec = rdfModel.createResource(this.baseURL);
        rdfRec.addProperty(DCTerms.title, this.repositoryName)
                .addProperty(DCTerms.hasVersion, this.protocolVersion);
        for (String email : this.adminEmails){
            rdfRec.addProperty(VCARD.EMAIL, email);
        } 
        rdfModel.createTypedLiteral(this.earliestDatestamp);
        rdfModel.createTypedLiteral(this.deletedRecord);
        rdfModel.createTypedLiteral(this.granularity);
        if (!this.compressions.isEmpty()){
            for (String compression : this.compressions){
                rdfModel.createTypedLiteral(compression);
            } 
        }
        rdfModel.write(writer);
        if (this.description != null) {
            description.write(writer, format);
        }
    }
}
