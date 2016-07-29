package nl.dtls.fairdatapoint.aoipmh.client;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import org.hamcrest.Matcher;

import javax.xml.stream.events.XMLEvent;

import static com.lyncode.xml.matchers.AttributeMatchers.attributeName;
import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.*;
import static com.lyncode.xoai.serviceprovider.xml.IslandParsers.dateParser;
import nl.dtls.fairdatapoint.aoipmh.writables.Header;
import static nl.dtls.fairdatapoint.aoipmh.writables.Header.Status.DELETED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;

public class HeaderParser {
    public Header parse (XmlReader reader) throws XmlReaderException {
        Header header = new Header();
        if (reader.hasAttribute(attributeName(localPart(equalTo("status")))))
            header.withStatus(DELETED);
        reader.next(elementName(localPart(equalTo("identifier")))).next(text());
        header.withIdentifier(reader.getText());
        reader.next(elementName(localPart(equalTo("datestamp")))).next(text());
        header.withDatestamp(reader.get(dateParser()));
        while (reader.next(endOfHeader(), setSpecElement()).current(setSpecElement()))
            header.withSetSpec(reader.next(text()).getText());
        return header;
    }


    private Matcher<XMLEvent> setSpecElement() {
        return allOf(aStartElement(), elementName(localPart(equalTo("setSpec"))));
    }

    private Matcher<XMLEvent> endOfHeader() {
        return allOf(anEndElement(), elementName(localPart(equalTo("header"))));
    }
}
