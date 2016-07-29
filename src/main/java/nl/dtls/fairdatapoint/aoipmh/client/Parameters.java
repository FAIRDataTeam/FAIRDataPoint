/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.client;

import com.lyncode.xoai.model.oaipmh.Granularity;
import com.lyncode.xoai.serviceprovider.parameters.GetRecordParameters;
import com.lyncode.xoai.serviceprovider.parameters.ListIdentifiersParameters;
import com.lyncode.xoai.serviceprovider.parameters.ListMetadataParameters;
import com.lyncode.xoai.serviceprovider.parameters.ListRecordsParameters;
import com.lyncode.xoai.services.api.DateProvider;
import com.lyncode.xoai.services.impl.UTCDateProvider;
import com.lyncode.xoai.util.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.writables.Verb;
import org.apache.commons.lang3.StringUtils;
import static com.lyncode.xoai.util.URLEncoder.encode;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class Parameters {
    private static final DateProvider formatter = new UTCDateProvider();

    public static Parameters parameters () {
        return new Parameters();
    }

    private Verb.Type verb;
    private String metadataPrefix;
    private String set;
    private Date from;
    private Date until;
    private String identifier;
    private String resumptionToken;
	private String granularity;

    public Parameters withVerb(Verb.Type verb) {
        this.verb = verb;
        return this;
    }

    public Parameters withUntil(Date until) {
        this.until = until;
        return this;
    }


    public Parameters withFrom(Date from) {
        this.from = from;
        return this;
    }

    public Parameters withSet(String value) {
        this.set = value;
        return this;
    }


    public Parameters identifier(String value) {
        this.identifier = value;
        return this;
    }

    public Parameters withResumptionToken(String value) {
        this.resumptionToken = value;
        this.metadataPrefix = null;
        this.until = null;
        this.set = null;
        this.from = null;
        return this;
    }

    public Parameters withoutResumptionToken () {
        this.resumptionToken = null;
        return this;
    }

    public Parameters withMetadataPrefix(String value) {
        this.metadataPrefix = value;
        return this;
    }

    public String toUrl(String baseUrl) {
        List<String> string = new ArrayList<>();
        string.add("verb=" + this.verb.name());
        Granularity granularity = granularity();
        if (set != null) string.add("set=" + encode(set));
        if (from != null) string.add("from=" + encode(formatter.format(from,granularity)));
        if (until != null) string.add("until=" + encode(formatter.format(until,granularity)));
        if (identifier != null) string.add("identifier=" + encode(identifier));
        if (metadataPrefix != null) string.add("metadataPrefix=" + encode(metadataPrefix));
        if (resumptionToken != null) string.add("resumptionToken=" + encode(resumptionToken));
        return baseUrl + "?" + StringUtils.join(string, URLEncoder.SEPARATOR);
    }

    /**
     * If a valid granularity field exists, return corresponding granularity.
     * Defaults to: Second
     * @return
     */
    private Granularity granularity() {
		if(granularity != null){
                    for (Granularity possibleGranularity : Granularity.values()) {
                        if(granularity.equals(possibleGranularity.toString())){
                            return possibleGranularity;
                        }
                    }
			
		}
		return Granularity.Second;
	}

	public Parameters include(ListMetadataParameters parameters) {
        this.identifier = parameters.getIdentifier();
        return this;
    }

    public Parameters include(GetRecordParameters parameters) {
        this.identifier = parameters.getIdentifier();
        this.metadataPrefix = parameters.getMetadataPrefix();
        return this;
    }

    public Parameters include(ListRecordsParameters parameters) {
        this.metadataPrefix = parameters.getMetadataPrefix();
        this.set = parameters.getSetSpec();
        this.until = parameters.getUntil();
        this.from = parameters.getFrom();
        this.granularity = parameters.getGranularity();

        return this;
    }

    public Parameters include(ListIdentifiersParameters parameters) {
        this.metadataPrefix = parameters.getMetadataPrefix();
        this.set = parameters.getSetSpec();
        this.until = parameters.getUntil();
        this.from = parameters.getFrom();
        this.granularity = parameters.getGranularity();

        return this;
    }

    public Verb.Type getVerb() {
        return verb;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public String getSet() {
        return set;
    }

    public Date getFrom() {
        return from;
    }

    public Date getUntil() {
        return until;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getResumptionToken() {
        return resumptionToken;
    }

	public void withGranularity(String granularity) {
		this.granularity = granularity;
		
	}

	public Object getGranularity() {
		return granularity;
	}
}