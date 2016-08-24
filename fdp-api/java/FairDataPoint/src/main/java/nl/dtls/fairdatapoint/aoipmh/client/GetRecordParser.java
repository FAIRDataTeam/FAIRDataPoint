package nl.dtls.fairdatapoint.aoipmh.client;

import com.lyncode.xml.XmlReader;
import com.lyncode.xml.exceptions.XmlReaderException;
import org.hamcrest.Matcher;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import static com.lyncode.xml.matchers.QNameMatchers.localPart;
import static com.lyncode.xml.matchers.XmlEventMatchers.elementName;
import com.lyncode.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import com.lyncode.xoai.dataprovider.exceptions.IdDoesNotExistException;
import static com.lyncode.xoai.model.oaipmh.Error.Code.CANNOT_DISSEMINATE_FORMAT;
import static com.lyncode.xoai.model.oaipmh.Error.Code.ID_DOES_NOT_EXIST;
import com.lyncode.xoai.serviceprovider.exceptions.InternalHarvestException;
import com.lyncode.xoai.serviceprovider.exceptions.InvalidOAIResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dtls.fairdatapoint.aoipmh.writables.Record;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class GetRecordParser {

    private final XmlReader reader;
    private final Context context;
    private final String metadataPrefix;

    public GetRecordParser(InputStream stream, Context context, String metadataPrefix) {
        this.context = context;
        this.metadataPrefix = metadataPrefix;
        try {
            this.reader = new XmlReader(stream);
        } catch (XmlReaderException e) {
            throw new InvalidOAIResponse(e);
        }
    }

    public Record parse () throws IdDoesNotExistException, CannotDisseminateFormatException {
        try {
            reader.next(errorElement(), recordElement());
            if (reader.current(errorElement())) {
                String code = reader.getAttributeValue(localPart(equalTo("code")));
                if (ID_DOES_NOT_EXIST.code().equals(code))
                    throw new IdDoesNotExistException();
                else if (CANNOT_DISSEMINATE_FORMAT.code().equals(code))
                    throw new CannotDisseminateFormatException();
                else
                    throw new InvalidOAIResponse("OAI responded with error code: "+code);
            } else {
                return new RecordParser(context, metadataPrefix).parse(reader);
            }
        } catch (XmlReaderException e) {
            throw new InvalidOAIResponse(e);
        } catch (InternalHarvestException ex) {
            Logger.getLogger(GetRecordParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    private Matcher<XMLEvent> errorElement() {
        return elementName(localPart(equalTo("error")));
    }

    private Matcher<XMLEvent> recordElement() {
        return elementName(localPart(equalTo("record")));
    }
}
