package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xoai.dataprovider.exceptions.DoesNotSupportSetsException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.NoMatchesException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import com.lyncode.xoai.model.oaipmh.ResumptionToken.Value;
import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.ListSets;
import nl.dtls.fairdatapoint.aoipmh.ListSetsResult;
import nl.dtls.fairdatapoint.aoipmh.Repository;
import nl.dtls.fairdatapoint.aoipmh.Set;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.writables.VerbHandler;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class ListSetsHandler extends VerbHandler<ListSets> {
    private final SetRepositoryHelper setRepositoryHelper;
    
    public ListSetsHandler(Context context, Repository repository) {
        super(context, repository);
        this.setRepositoryHelper = new SetRepositoryHelper(getRepository().getSetRepository());
    }

    @Override
    public ListSets handle(OAICompiledRequest parameters) throws OAIException, HandlerException {
        ListSets result = new ListSets();
        
        if (!getRepository().getSetRepository().supportSets()){
            throw new DoesNotSupportSetsException();
        }
        int length = getRepository().getConfiguration().getMaxListSets();
        ListSetsResult listSetsResult = setRepositoryHelper.getSets(getContext(), getOffset(parameters), length);
        List<Set> sets = listSetsResult.getResults();

        if (sets.isEmpty() && parameters.getResumptionToken().isEmpty()){
            throw new NoMatchesException();
        } if (sets.size() > length){
            sets = sets.subList(0, length);
        }
        for (Set set : sets) {
            result.getSets().add(set.toOAIPMH());
        }

        Value currentResumptionToken = new Value();
        if (parameters.hasResumptionToken()) {
            currentResumptionToken = parameters.getResumptionToken();
        } else if (listSetsResult.hasMore()) {
            currentResumptionToken = parameters.extractResumptionToken();
        }

        ResumptionTokenHelper resumptionTokenHelper = new ResumptionTokenHelper(currentResumptionToken,
                getRepository().getConfiguration().getMaxListSets());
        result.withResumptionToken(resumptionTokenHelper.resolve(listSetsResult.hasMore()));
        return result;
    }

    private int getOffset(OAICompiledRequest parameters) {
        if (!parameters.hasResumptionToken()){
            return 0;
        } if (parameters.getResumptionToken().getOffset() == null){
            return 0;
        }
        return parameters.getResumptionToken().getOffset().intValue();
    }

}
