package nl.dtls.fairdatapoint.aoipmh;

import nl.dtls.fairdatapoint.aoipmh.writables.Verb;
import nl.dtls.fairdatapoint.aoipmh.writables.Set;
import nl.dtls.fairdatapoint.aoipmh.writables.ResumptionToken;
import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 * 
 * @author Shamanou van Leeuwen
 */
public class ListSets implements Verb {    
    protected List<Set> sets = new ArrayList<>();
    protected ResumptionToken resumptionToken;

    public List<Set> getSets() {
        return this.sets;
    }

    public ResumptionToken getResumptionToken() {
        return resumptionToken;
    }

    public ListSets withResumptionToken(ResumptionToken value) {
        this.resumptionToken = value;
        return this;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        if (!this.sets.isEmpty()){
            for (Set set : this.sets){
                writer.writeElement("set", set);
            }
        }
        writer.writeElement("resumptionToken", resumptionToken);
    }

    @Override
    public void write(StringWriter writer, String format) {
        if (!this.sets.isEmpty()){
            for (Set set : this.sets){
                set.write(writer, format);
            }
        }
    }
    
    @Override
    public Type getType() {
        return Type.ListSets;
    }
}
