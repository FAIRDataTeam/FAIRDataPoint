package nl.dtls.fairdatapoint.aoipmh.handlers;

import static com.google.common.base.Predicates.isNull;
import com.lyncode.xoai.model.oaipmh.ResumptionToken.Value;
import static java.lang.Math.round;
import nl.dtls.fairdatapoint.aoipmh.writables.ResumptionToken;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class ResumptionTokenHelper {
    private final Value current;
    private final long maxPerPage;
    private Long totalResults;

    public ResumptionTokenHelper(Value current, long maxPerPage) {
        this.current = current;
        this.maxPerPage = maxPerPage;
    }

    public ResumptionTokenHelper withTotalResults(long totalResults) {
        this.totalResults = totalResults;
        return this;
    }

    public ResumptionToken resolve (boolean hasMoreResults) {
        if (isInitialOffset() && !hasMoreResults) return null;
        else {
            if (hasMoreResults) {
                Value next = current.next(maxPerPage);
                return populate(new ResumptionToken(next));
            } else {
                ResumptionToken resumptionToken = new ResumptionToken();
                resumptionToken.withCursor(round((current.getOffset() + maxPerPage) / maxPerPage));
                if (totalResults != null)
                    resumptionToken.withCompleteListSize(totalResults);
                return resumptionToken;
            }
        }
    }

    private boolean isInitialOffset() {
        return isNull().apply(current.getOffset()) || current.getOffset() == 0;
    }

    private ResumptionToken populate(ResumptionToken resumptionToken) {
        if (totalResults != null)
            resumptionToken.withCompleteListSize(totalResults);
        resumptionToken.withCursor(round(resumptionToken.getValue().getOffset() / maxPerPage));
        return resumptionToken;
    }
}
