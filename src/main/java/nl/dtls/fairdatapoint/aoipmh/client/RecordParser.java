package nl.dtls.fairdatapoint.aoipmh.client;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import org.hamcrest.Matcher;

import javax.xml.stream.events.XMLEvent;

import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.*;
import com.lyncode.xoai.serviceprovider.exceptions.InternalHarvestException;
import nl.dtls.fairdatapoint.aoipmh.writables.About;
import nl.dtls.fairdatapoint.aoipmh.writables.Record;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;

public class RecordParser {
    private final Context context;
    private final String metadataPrefix;

    public RecordParser(Context context, String metadataPrefix) {
        this.context = context;
        this.metadataPrefix = metadataPrefix;
    }

    public Record parse (XmlReader reader) throws XmlReaderException, InternalHarvestException {
        HeaderParser headerParser = new HeaderParser();

        reader.next(elementName(localPart(equalTo("header"))));
        Record record = new Record()
                .withHeader(headerParser.parse(reader));

        if (reader.next(aboutElement(), endOfRecord()).current(aboutElement())) {
            reader.next(aStartElement());
            record.withAbout(new About(reader.retrieveCurrentAsString()));
        }
        return record;
    }

    private Matcher<XMLEvent> endOfRecord() {
        return allOf(anEndElement(), elementName(localPart(equalTo("record"))));
    }

    private Matcher<XMLEvent> aboutElement() {
        return elementName(localPart(equalTo("about")));
    }
}
