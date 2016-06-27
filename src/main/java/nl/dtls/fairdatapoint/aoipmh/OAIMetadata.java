/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.InputStream;

import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.*;
import com.lyncode.xoai.xml.XSISchema;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.aoipmh.writables.Element;
import nl.dtls.fairdatapoint.aoipmh.writables.Writable;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;
import org.xml.sax.SAXException;


/**
 *
 * @author Shamanou van Leeuwen
 */

public class OAIMetadata implements Writable {
    private final static Logger LOGGER = LogManager.getLogger(OAIMetadata.class);    
    private static final String DEFAULT_FIELD = "value";

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
    public static final String NAMESPACE_URI = "http://dublincore.org/schemas/xmls/qdc/2008/02/11/dcterms.xsd";
    public static final String SCHEMA_LOCATION = "dc.xsd";

    protected List<Element> elements = new ArrayList<>();

    public List<Element> getElements() {
        return this.elements;
    }

    public OAIMetadata withElement (Element element) {
        this.elements.add(element);
        return this;
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            this.write(new XmlWriter(out));
        } catch (XmlWriteException | XMLStreamException ex) {
        }
        return out.toString();
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {  
            System.setProperty("org.xml.sax.driver",  "org.apache.xerces.parsers.SAXParser");  
            System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
            writer.setDefaultNamespace(NAMESPACE_URI);
            writer.writeStartElement("metadata");
            writer.writeDefaultNamespace(NAMESPACE_URI);
            writer.writeNamespace(XSISchema.PREFIX, XSISchema.NAMESPACE_URI);
            writer.writeNamespace("dc", NAMESPACE_URI);
            writer.writeAttribute(XSISchema.PREFIX, XSISchema.NAMESPACE_URI, "schemaLocation", NAMESPACE_URI + " " + SCHEMA_LOCATION);

            for (Element element : this.getElements()) {
                if (element.getName().equals("creators")){
                    writer.writeStartElement("dc","creator",NAMESPACE_URI);
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("title")){
                    writer.writeStartElement("dc","title",NAMESPACE_URI);
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("description")){
                    writer.writeStartElement("dc","description",NAMESPACE_URI);
                    element.write(writer);
                    writer.writeEndElement();
                } if (element.getName().equals("type")){
                    writer.writeStartElement("dc","type",NAMESPACE_URI);
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

