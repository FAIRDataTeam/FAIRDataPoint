package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.DoesNotSupportSetsException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import com.lyncode.xoai.dataprovider.exceptions.NoMatchesException;
import com.lyncode.xoai.dataprovider.exceptions.NoMetadataFormatsException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import com.lyncode.xoai.model.oaipmh.ResumptionToken.Value;
import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.ItemIdentifier;
import nl.dtls.fairdatapoint.aoipmh.ListItemIdentifiersResult;
import nl.dtls.fairdatapoint.aoipmh.MetadataFormat;
import nl.dtls.fairdatapoint.aoipmh.Repository;
import nl.dtls.fairdatapoint.aoipmh.Set;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.writables.Header;
import nl.dtls.fairdatapoint.aoipmh.writables.ListIdentifiers;
import nl.dtls.fairdatapoint.aoipmh.writables.VerbHandler;


public class ListIdentifiersHandler extends VerbHandler<ListIdentifiers> {
    private final ItemRepositoryHelper itemRepositoryHelper;

    public ListIdentifiersHandler(Context context, Repository repository) {
        super(context, repository);
        this.itemRepositoryHelper = new ItemRepositoryHelper(repository.getItemRepository());
    }

    @Override
    public ListIdentifiers handle(OAICompiledRequest parameters) throws OAIException, HandlerException {
        ListIdentifiers result = new ListIdentifiers();

        if (parameters.hasSet() && !getRepository().getSetRepository().supportSets())
            throw new DoesNotSupportSetsException();

        int length = getRepository().getConfiguration().getMaxListIdentifiers();
        int offset = getOffset(parameters);
        ListItemIdentifiersResult listItemIdentifiersResult;
        if (!parameters.hasSet()) {
            if (parameters.hasFrom() && !parameters.hasUntil())
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiers(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getFrom());
            else if (!parameters.hasFrom() && parameters.hasUntil())
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiersUntil(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getUntil());
            else if (parameters.hasFrom() && parameters.hasUntil())
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiers(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getFrom(),
                        parameters.getUntil());
            else
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiers(getContext(), offset, length,
                        parameters.getMetadataPrefix());
        } else {
            if (!getRepository().getSetRepository().exists(parameters.getSet()) && !getContext().hasSet(parameters.getSet()))
                throw new NoMatchesException();

            if (parameters.hasFrom() && !parameters.hasUntil())
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiers(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getSet(),
                        parameters.getFrom());
            else if (!parameters.hasFrom() && parameters.hasUntil())
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiersUntil(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getSet(),
                        parameters.getUntil());
            else if (parameters.hasFrom() && parameters.hasUntil())
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiers(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getSet(),
                        parameters.getFrom(), parameters.getUntil());
            else
                listItemIdentifiersResult = itemRepositoryHelper.getItemIdentifiers(getContext(), offset, length,
                        parameters.getMetadataPrefix(), parameters.getSet());
        }

        List<ItemIdentifier> itemIdentifiers = listItemIdentifiersResult.getResults();
        if (itemIdentifiers.isEmpty()) throw new NoMatchesException();

        for (ItemIdentifier itemIdentifier : itemIdentifiers)
            result.getHeaders().add(createHeader(parameters, itemIdentifier));

        Value currentResumptionToken = new Value();
        if (parameters.hasResumptionToken()) {
            currentResumptionToken = parameters.getResumptionToken();
        } else if (listItemIdentifiersResult.hasMore()) {
            currentResumptionToken = parameters.extractResumptionToken();
        }

        ResumptionTokenHelper resumptionTokenHelper = new ResumptionTokenHelper(currentResumptionToken,
                getRepository().getConfiguration().getMaxListIdentifiers());
        result.withResumptionToken(resumptionTokenHelper.resolve(listItemIdentifiersResult.hasMore()));

        return result;
    }

    private int getOffset(OAICompiledRequest parameters) {
        if (!parameters.hasResumptionToken())
            return 0;
        if (parameters.getResumptionToken().getOffset() == null)
            return 0;
        return parameters.getResumptionToken().getOffset().intValue();
    }


    private Header createHeader(OAICompiledRequest parameters,
            ItemIdentifier itemIdentifier) throws BadArgumentException,
            OAIException,NoMetadataFormatsException {
        MetadataFormat format = getContext().formatForPrefix(parameters
                .getMetadataPrefix());
        if (!itemIdentifier.isDeleted() && !canDisseminate(itemIdentifier, format))
            throw new InternalOAIException("The item repository is currently providing items which cannot be disseminated with format "+format.getPrefix());

        Header header = new Header();
        header.withDatestamp(itemIdentifier.getDatestamp());
        header.withIdentifier(itemIdentifier.getIdentifier());
        if (itemIdentifier.isDeleted())
            header.withStatus(Header.Status.DELETED);

        for (Set set : getContext().getSets())
            if (set.getCondition().getFilter(getRepository().getFilterResolver()).isItemShown(itemIdentifier))
                header.withSetSpec(set.getSpec());

        for (Set set : itemIdentifier.getSets())
            header.withSetSpec(set.getSpec());

        return header;
    }

    private boolean canDisseminate(ItemIdentifier itemIdentifier, MetadataFormat format) {
        return !format.hasCondition() ||
                format.getCondition().getFilter(getRepository().getFilterResolver()).isItemShown(itemIdentifier);
    }
}
