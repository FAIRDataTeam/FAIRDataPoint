package nl.dtls.fairdatapoint.aoipmh.parameters;
import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.DuplicateDefinitionException;
import com.lyncode.xoai.dataprovider.exceptions.IllegalVerbException;
import com.lyncode.xoai.dataprovider.exceptions.UnknownParameterException;
import com.lyncode.xoai.exceptions.InvalidResumptionTokenException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static nl.dtls.fairdatapoint.aoipmh.parameters.OAIRequest.Parameter.Verb;
import static nl.dtls.fairdatapoint.aoipmh.writables.Verb.Type.fromValue;
import com.lyncode.xoai.services.api.DateProvider;
import com.lyncode.xoai.services.impl.UTCDateProvider;
import nl.dtls.fairdatapoint.aoipmh.writables.Verb.Type;

/**
 * @author Shamanou van Leeuwen
 */
public class OAIRequest {
    
    public static enum Parameter {
        From("from"),
        Until("until"),
        Identifier("identifier"),
        MetadataPrefix("metadataPrefix"),
        ResumptionToken("resumptionToken"),
        Set("set"),
        Verb("verb");

        private final String representation;

        Parameter (String rep) {
            this.representation = rep;
        }

        @Override
        public String toString () {
            return representation;
        }

        public static Parameter fromRepresentation (String representation) {
            for (Parameter param : Parameter.values())
                if (param.representation.equals(representation))
                    return param;

            throw new IllegalArgumentException("Given representation is not a valid value for Parameter");
        }
    }

    private final Map<String, List<String>> map;
    private final DateProvider dateProvider = new UTCDateProvider();
    private String format = "xml";
    
    public OAIRequest(Map<String, List<String>> map) {
        this.map = map;
    }

    public OAIRequest withFormat(String format) {
        this.format = format;
        return this;
    }
    
    public void validate (Parameter parameter) throws IllegalVerbException, DuplicateDefinitionException {
        List<String> values = this.map.get(parameter);
        if (values != null && !values.isEmpty()) {
            if (parameter == Verb) {
                if (values.size() > 1)
                    throw new IllegalVerbException("Illegal verb");
            } else {
                if (values.size() > 1)
                    throw new DuplicateDefinitionException("Duplicate definition of parameter '" + parameter + "'");
            }
        }
    }

    public boolean has (Parameter parameter) {
        return get(parameter) != null;
    }

    public String get (Parameter parameter) {
        List<String> values = this.map.get(parameter.toString());
        if (values == null || values.isEmpty()) return null;
        else {
            String value = values.get(0);
            return "".equals(value) ? null : value;
        }
    }

    public Date getDate(Parameter parameter) throws BadArgumentException {
        if (!has(parameter)) return null;
        try {
            return dateProvider.parse(get(parameter));
        } catch (ParseException e) {
            throw new BadArgumentException("The " + parameter + " parameter given is not valid");
        }
    }

    public String getString (Parameter parameter) throws DuplicateDefinitionException, IllegalVerbException {
        if (!has(parameter)) return null;
        validate(parameter);
        return get(parameter);
    }

    public Type getVerb () throws DuplicateDefinitionException, IllegalVerbException {
        validate(Verb);
        String verb = get(Verb);
        if (verb == null)
            throw new IllegalVerbException("The verb given by the request is null, assuming identify");
        try {
            return fromValue(verb);
        } catch (IllegalArgumentException e) {
            throw new IllegalVerbException("The verb given by the request is unknown, assuming identify");
        }
    }

    public Collection<String> getParameterNames () {
        return this.map.keySet();
    }

    public OAICompiledRequest compile () throws IllegalVerbException, InvalidResumptionTokenException, 
            UnknownParameterException, BadArgumentException, DuplicateDefinitionException {
        return OAICompiledRequest.compile(this);
    }
}
