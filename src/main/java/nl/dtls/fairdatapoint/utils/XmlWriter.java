/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.utils;

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.model.oaipmh.ResumptionToken;
import com.lyncode.xoai.services.api.DateProvider;
import com.lyncode.xoai.services.api.ResumptionTokenFormat;
import com.lyncode.xoai.services.impl.SimpleResumptionTokenFormat;
import com.lyncode.xoai.services.impl.UTCDateProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import javax.xml.stream.XMLStreamException;
import nl.dtls.fairdatapoint.aoipmh.writables.Writable;

/**
 *
 * @author Shamanou van Leeuwen
 */

public class XmlWriter extends com.lyncode.xml.XmlWriter {
    public static String toString (Writable writable) throws XMLStreamException, XmlWriteException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmlWriter writer = new XmlWriter(outputStream, defaultContext());
        writable.write(writer);
        writer.close();
        return outputStream.toString();
    }

    public static WriterContext defaultContext () {
        return new WriterContext(Granularity.Second, new SimpleResumptionTokenFormat());
    }

    public static class WriterContext {
        private final Granularity granularity;
        public ResumptionTokenFormat formatter;

        public WriterContext(Granularity granularity, ResumptionTokenFormat formatter) {
            this.granularity = granularity;
            this.formatter = formatter;
        }
    }

    private final DateProvider dateProvider;
    protected final WriterContext writerContext;

    public XmlWriter(OutputStream output) throws XMLStreamException {
        super(output);
        this.dateProvider = new UTCDateProvider();
        this.writerContext = defaultContext();
    }

    public XmlWriter(OutputStream output, WriterContext writerContext) throws XMLStreamException {
        super(output);
        this.dateProvider = new UTCDateProvider();
        this.writerContext = writerContext;
    }

    public void writeDate(Date date) throws XmlWriteException, XMLStreamException {
        this.writeCharacters(dateProvider.format(date, writerContext.granularity));
    }

    public void writeDate(Date date, Granularity granularity) throws XmlWriteException, XMLStreamException {
        this.writeCharacters(dateProvider.format(date, granularity));
    }

    public void writeElement(String elementName, String elementValue) throws XmlWriteException {
        try {
            this.writeStartElement(elementName);
            this.writeCharacters(elementValue);
            this.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    public void writeElement (String elementName, Writable writable) throws XmlWriteException {
        try {
            if (writable != null) {
                this.writeStartElement(elementName);
                writable.write(this);
                this.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new XmlWriteException(e);
        }
    }

    public void writeElement(String elementName, Date date, Granularity granularity) 
            throws XmlWriteException {
        this.writeElement(elementName, dateProvider.format(date, granularity));
    }
    public void writeElement(String elementName, Date date) throws XmlWriteException {
        this.writeElement(elementName, dateProvider.format(date, writerContext.granularity));
    }

    public void writeAttribute(String name, Date date) throws XmlWriteException, XMLStreamException {
        this.writeAttribute(name, dateProvider.format(date, writerContext.granularity));
    }

    public void writeAttribute(String name, Date value, Granularity granularity) 
            throws XmlWriteException, XMLStreamException {
        this.writeAttribute(name, dateProvider.format(value, granularity));
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (value != null){
            super.writeAttribute(localName, value);
        }
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        if (text != null){
            super.writeCharacters(text);
        }
    }

    public void write(Writable writable) throws XmlWriteException {
        if (writable != null){
            writable.write(this);
        }
    }

    public void write(ResumptionToken.Value value) throws XmlWriteException, XMLStreamException {
        if (!value.isEmpty())
            writeCharacters(this.writerContext.formatter.format(value));
    }
}
