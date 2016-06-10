package nl.dtls.fairdatapoint.aoipmh.client;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import com.lyncode.xoai.model.xoai.Element;
import com.lyncode.xoai.model.xoai.Field;
import org.hamcrest.Matcher;

import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;

public class MetadataParser {

    private Element parseElement(XmlReader reader) throws XmlReaderException {
        Element element = new Element(reader.getAttributeValue(name()));
        while (reader.next(startElement(), startField(), endOfMetadata()).current(startElement())) {
            element.withElement(parseElement(reader));
        }

        while (reader.current(startField())) {
            Field field = new Field()
                    .withName(reader.getAttributeValue(name()));

            if (reader.next(anEndElement(), text()).current(text()))
                field.withValue(reader.getText());

            element.withField(field);
            reader.next(startField(), endElement());
        }

        return element;
    }

    private Matcher<XMLEvent> startField() {
        return allOf(aStartElement(), elementName(localPart(equalTo("field"))));
    }

    private Matcher<XMLEvent> endOfMetadata() {
        return allOf(anEndElement(), elementName(localPart(equalTo("metadata"))));
    }

    private Matcher<QName> name() {
        return localPart(equalTo("name"));
    }

    private Matcher<XMLEvent> startElement() {
        return allOf(aStartElement(), elementName(localPart(equalTo("element"))));
    }
    private Matcher<XMLEvent> endElement() {
        return allOf(anEndElement(), elementName(localPart(equalTo("element"))));
    }
}
