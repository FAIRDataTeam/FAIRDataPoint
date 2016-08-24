package nl.dtls.fairdatapoint.aoipmh;

import nl.dtls.fairdatapoint.aoipmh.writables.Verb;
import nl.dtls.fairdatapoint.aoipmh.writables.ResumptionToken;
import nl.dtls.fairdatapoint.aoipmh.writables.Record;
import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class ListRecords implements Verb {

    protected List<Record> records = new ArrayList<>();
    protected ResumptionToken resumptionToken;

    public List<Record> getRecords() {
        return this.records;
    }

    public ResumptionToken getResumptionToken() {
        return resumptionToken;
    }

    public ListRecords withResumptionToken(ResumptionToken value) {
        this.resumptionToken = value;
        return this;
    }

    public ListRecords withRecord(Record record) {
        this.records.add(record);
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (!this.records.isEmpty()){
            for (Record record : this.records){
                writer.writeElement("record", record);
            }
        }
        writer.writeElement("resumptionToken", resumptionToken);
    }

    @Override
    public Type getType() {
        return Type.ListRecords;
    }

    @Override
    public void write(StringWriter writer, String format) {
        if (!this.records.isEmpty()){
            for (Record record : this.records){
                record.write(writer, format);
            }
        }
    }
}
