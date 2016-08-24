package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import com.lyncode.xoai.dataprovider.exceptions.NoMetadataFormatsException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.Item;
import nl.dtls.fairdatapoint.aoipmh.ListMetadataFormats;
import nl.dtls.fairdatapoint.aoipmh.MetadataFormat;
import nl.dtls.fairdatapoint.aoipmh.Repository;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.writables.VerbHandler;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class ListMetadataFormatsHandler extends VerbHandler<ListMetadataFormats> {
    private final ItemRepositoryHelper itemRepositoryHelper;

    public ListMetadataFormatsHandler(Context context, Repository repository) {
        super(context, repository);
        itemRepositoryHelper = new ItemRepositoryHelper(repository.getItemRepository());

        // Static validation
        if (getContext().getMetadataFormats() == null ||
                getContext().getMetadataFormats().isEmpty())
            throw new InternalOAIException("The context must expose at least one metadata format");
    }


    @Override
    public ListMetadataFormats handle(OAICompiledRequest params) throws OAIException, HandlerException {
        ListMetadataFormats result = new ListMetadataFormats();

        if (params.hasIdentifier()) {
            Item item = itemRepositoryHelper.getItem(params.getIdentifier());
            List<MetadataFormat> metadataFormats = getContext().formatFor(getRepository().getFilterResolver(), item);
            if (metadataFormats.isEmpty()){
                throw new NoMetadataFormatsException();
            }
            for (MetadataFormat metadataFormat : metadataFormats) {
                nl.dtls.fairdatapoint.aoipmh.writables.MetadataFormat format = new nl.dtls.fairdatapoint.aoipmh.writables.MetadataFormat()
                    .withMetadataPrefix(metadataFormat.getPrefix())
                    .withMetadataNamespace(metadataFormat.getNamespace())
                    .withSchema(metadataFormat.getSchemaLocation());
                result.withMetadataFormat(format);
            }
        } else {
            for (MetadataFormat metadataFormat : getContext().getMetadataFormats()) {
                nl.dtls.fairdatapoint.aoipmh.writables.MetadataFormat format = new nl.dtls.fairdatapoint.aoipmh.writables.MetadataFormat()
                        .withMetadataPrefix(metadataFormat.getPrefix())
                        .withMetadataNamespace(metadataFormat.getNamespace())
                        .withSchema(metadataFormat.getSchemaLocation());
                result.withMetadataFormat(format);
            }
        }

        return result;
    }

}
