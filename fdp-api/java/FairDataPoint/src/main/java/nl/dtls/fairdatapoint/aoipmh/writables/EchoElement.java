/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import org.codehaus.stax2.XMLInputFactory2;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class EchoElement implements Writable {
    private static final XMLInputFactory factory = XMLInputFactory2.newFactory();
    private String element;
    private final List<String> declaredPrefixes = new ArrayList<>();    
    public EchoElement(String element) {
        this.element = element;
    }
    
    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        try {
            XMLEventReader reader = factory.createXMLEventReader(new ByteArrayInputStream(element.getBytes()));
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isCharacters() && !event.isEndElement() && !event.isStartElement()) {
                    writer.writeCharacters(event.asCharacters().getData());
                }
            }
        } catch (XMLStreamException e) {
            throw new XmlWriteException("Error trying to output '"+this.element+"'", e);
        }
    }

    private void addNamespaceIfRequired(XmlWriter writer, QName name) throws XMLStreamException {
        if (!declaredPrefixes.contains(name.getPrefix())) {
            writer.writeNamespace(name.getPrefix(), name.getNamespaceURI());
            declaredPrefixes.add(name.getPrefix());
        }
    }

    @Override
    public void write(StringWriter writer, String format) {
       Model rdfModel = ModelFactory.createDefaultModel();
       rdfModel.createLiteral(this.element);
       rdfModel.write(writer, format);
    }
}