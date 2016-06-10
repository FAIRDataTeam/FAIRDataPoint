/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class GetRecord implements Verb {
    private final Record record;

    public GetRecord(Record record) {
        this.record = record;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            writer.writeStartElement("record");
            writer.write(record);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public Verb.Type getType() {
        return Verb.Type.GetRecord;
    }

    @Override
    public void write(StringWriter writer, String format) {
        this.record.write(writer, format);
    }
}
