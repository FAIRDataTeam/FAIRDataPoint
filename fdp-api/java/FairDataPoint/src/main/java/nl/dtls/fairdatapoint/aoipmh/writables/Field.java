package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import com.lyncode.xml.exceptions.XmlWriteException;

import javax.xml.stream.XMLStreamException;

import static com.lyncode.xml.matchers.AttributeMatchers.attributeName;
import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.*;
import java.io.StringWriter;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class Field implements Writable {
    public static Field parse (XmlReader reader) throws XmlReaderException {
        if (!reader.current(allOf(aStartElement(), elementName(localPart(equalTo("field"))))))
            throw new XmlReaderException("Invalid XML. Expecting entity 'field'");

        Field field = new Field();

        if (reader.hasAttribute(attributeName(localPart(equalTo("name")))))
            field.withName(reader.getAttributeValue(localPart(equalTo("name"))));

        if (reader.next(anElement(), text()).current(text())) {
            field.withValue(reader.getText());
            reader.next(anElement());
        }

        if (!reader.current(allOf(anEndElement(), elementName(localPart(equalTo("field"))))))
            throw new XmlReaderException("Invalid XML. Expecting end of entity 'field'");

        return field;
    }

    public Field() {
    }

    public Field(String value, String name) {
        this.value = value;
        this.name = name;
    }

    protected String value;
    protected String name;

    public String getValue() {
        return value;
    }

    public Field withValue(String value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public Field withName(String value) {
        this.name = value;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            if (this.value != null){
                writer.writeCharacters(value);
            }
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
