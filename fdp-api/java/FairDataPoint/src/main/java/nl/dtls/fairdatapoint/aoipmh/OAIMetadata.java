package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.InputStream;

import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.aoipmh.writables.Element;
import nl.dtls.fairdatapoint.aoipmh.writables.Writable;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;


/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
*/

public class OAIMetadata implements Writable {
    public static OAIMetadata parse (InputStream inputStream) throws XmlReaderException {
        XmlReader reader = new XmlReader(inputStream);
        OAIMetadata OAIMetadata = new OAIMetadata();
        if (!reader.next(aStartElement()).current(allOf(aStartElement(), elementName(localPart(equalTo("metadata")))))){
            throw new XmlReaderException("Invalid XML. Expecting entity 'metadata'");
        }
        while (reader.next(anElement()).current(aStartElement())) {
            if (reader.current(elementName(localPart(equalTo("element"))))) // Nested element
                OAIMetadata.withElement(Element.parse(reader));
            else throw new XmlReaderException("Unexpected element");
        }

        if (!reader.current(allOf(anEndElement(), elementName(localPart(equalTo("metadata")))))){
            throw new XmlReaderException("Invalid XML. Expecting end of entity 'metadata'");
        }
        reader.close();
        return OAIMetadata;
    }

    //Still needs to be set to configurable
    public static final String NAMESPACE_URI = "http://www.openarchives.org/OAI/2.0/oai_dc/";
    public static final String SCHEMA_LOCATION = "http://www.openarchives.org/OAI/2.0/oai_dc.xsd";

    protected List<Element> elements = new ArrayList<>();

    public List<Element> getElements() {
        return this.elements;
    }

    public OAIMetadata withElement (Element element) {
        this.elements.add(element);
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            writer.setPrefix("oai_dc",NAMESPACE_URI);
            writer.writeStartElement("oai_dc","dc",NAMESPACE_URI);
            writer.writeNamespace("oai_dc", NAMESPACE_URI);
            writer.writeNamespace("dc", "http://purl.org/dc/elements/1.1/");
            writer.setPrefix("dc", "http://purl.org/dc/elements/1.1/");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:schemaLocation", NAMESPACE_URI + " " + SCHEMA_LOCATION);

            for (Element element : this.getElements()) {
                if (element.getName().equals("creators")){
                    writer.writeStartElement("dc:creator");
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("title")){
                    writer.writeStartElement("dc:title");
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("description")){
                    writer.writeStartElement("dc:description");
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("type")){
                    writer.writeStartElement("dc:type");
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("identifier")){
                    writer.writeStartElement("dc:identifier");
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("datestamp")){
                    writer.writeStartElement("dc:date");
                    element.write(writer);
                    writer.writeEndElement();
                } 
            }
            writer.writeEndElement();
        } 
        catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return a simple searcher that returns search results as String elements.
     */
//    public MetadataSearch<String> searcher () {
//        return new MetadataSearchImpl(this);
//    }
}

