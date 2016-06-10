package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xml.exceptions.XmlWriteException;
import com.lyncode.xoai.dataprovider.exceptions.BadArgumentException;
import com.lyncode.xoai.dataprovider.exceptions.CannotDisseminateFormatException;
import com.lyncode.xoai.dataprovider.exceptions.CannotDisseminateRecordException;
import com.lyncode.xoai.dataprovider.exceptions.DoesNotSupportSetsException;
import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.NoMatchesException;
import com.lyncode.xoai.dataprovider.exceptions.NoMetadataFormatsException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import com.lyncode.xoai.model.oaipmh.ResumptionToken.Value;
import com.lyncode.xoai.xml.XSLPipeline;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.Item;
import nl.dtls.fairdatapoint.aoipmh.ListItemsResults;
import nl.dtls.fairdatapoint.aoipmh.ListRecords;
import nl.dtls.fairdatapoint.aoipmh.MetadataFormat;
import nl.dtls.fairdatapoint.aoipmh.Repository;
import nl.dtls.fairdatapoint.aoipmh.Set;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.writables.About;
import nl.dtls.fairdatapoint.aoipmh.writables.Header;
import nl.dtls.fairdatapoint.aoipmh.writables.Metadata;
import nl.dtls.fairdatapoint.aoipmh.writables.Record;
import nl.dtls.fairdatapoint.aoipmh.writables.VerbHandler;
import nl.dtls.fairdatapoint.utils.XmlWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class ListRecordsHandler extends VerbHandler<ListRecords> {
    private static final Logger log = Logger.getLogger(ListRecordsHandler.class);
    private final ItemRepositoryHelper itemRepositoryHelper;
    private final SetRepositoryHelper setRepositoryHelper;

    public ListRecordsHandler(Context context, Repository repository) {
        super(context, repository);
        this.itemRepositoryHelper = new ItemRepositoryHelper(getRepository().getItemRepository());
        this.setRepositoryHelper = new SetRepositoryHelper(getRepository().getSetRepository());
    }

    @Override
    public ListRecords handle(OAICompiledRequest parameters) throws OAIException, HandlerException {ListRecords res = new ListRecords();
        int length = getRepository().getConfiguration().getMaxListRecords();

        if (parameters.hasSet() && !getRepository().getSetRepository().supportSets())
            throw new DoesNotSupportSetsException();

        log.debug("Getting items from data source");
        int offset = getOffset(parameters);
        ListItemsResults result;
        if (!parameters.hasSet()) {
            if (parameters.hasFrom() && !parameters.hasUntil())
                result = itemRepositoryHelper.getItems(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getFrom());
            else if (!parameters.hasFrom() && parameters.hasUntil())
                result = itemRepositoryHelper.getItemsUntil(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getUntil());
            else if (parameters.hasFrom() && parameters.hasUntil())
                result = itemRepositoryHelper.getItems(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getFrom(), parameters.getUntil());
            else
                result = itemRepositoryHelper.getItems(getContext(), offset,
                        length, parameters.getMetadataPrefix());
        } else {
            if (!setRepositoryHelper.exists(getContext(), parameters.getSet()))
                throw new NoMatchesException();
            if (parameters.hasFrom() && !parameters.hasUntil())
                result = itemRepositoryHelper.getItems(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getSet(), parameters.getFrom());
            else if (!parameters.hasFrom() && parameters.hasUntil())
                result = itemRepositoryHelper.getItemsUntil(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getSet(), parameters.getUntil());
            else if (parameters.hasFrom() && parameters.hasUntil())
                result = itemRepositoryHelper.getItems(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getSet(), parameters.getFrom(),
                        parameters.getUntil());
            else
                result = itemRepositoryHelper.getItems(getContext(), offset,
                        length, parameters.getMetadataPrefix(),
                        parameters.getSet());
        }
        log.debug("Items retrieved from data source");

        List<Item> results = result.getResults();
        if (results.isEmpty()) throw new NoMatchesException();
        log.debug("Now adding records to the OAI-PMH Output");
        for (Item i : results)
            res.withRecord(this.createRecord(parameters, i));


        Value currentResumptionToken = new Value();
        if (parameters.hasResumptionToken()) {
            currentResumptionToken = parameters.getResumptionToken();
        } else if (result.hasMore()) {
            currentResumptionToken = parameters.extractResumptionToken();
        }

        ResumptionTokenHelper resumptionTokenHelper = new ResumptionTokenHelper(currentResumptionToken,
                getRepository().getConfiguration().getMaxListRecords());
        res.withResumptionToken(resumptionTokenHelper.resolve(result.hasMore()));

        return res;
    }


    private int getOffset(OAICompiledRequest parameters) {
        if (!parameters.hasResumptionToken())
            return 0;
        if (parameters.getResumptionToken().getOffset() == null)
            return 0;
        return parameters.getResumptionToken().getOffset().intValue();
    }

    private Record createRecord(OAICompiledRequest parameters, Item item)
            throws BadArgumentException, CannotDisseminateRecordException,
            OAIException, NoMetadataFormatsException, CannotDisseminateFormatException {
        MetadataFormat format = getContext().formatForPrefix(parameters.getMetadataPrefix());
        Header header = new Header();
        Record record = new Record().withHeader(header);
        header.withIdentifier(item.getIdentifier());

        ItemHelper itemHelperWrap = new ItemHelper(item);

        header.withDatestamp(item.getDatestamp());
        for (Set set : itemHelperWrap.getSets(getContext(), getRepository().getFilterResolver()))
            header.withSetSpec(set.getSpec());
        if (item.isDeleted())
            header.withStatus(Header.Status.DELETED);

        if (!item.isDeleted()) {
            Metadata metadata = null;
            try {
                if (getContext().hasTransformer()) {
                    metadata = new Metadata(toPipeline(item)
                            .apply(getContext().getTransformer())
                            .apply(format.getTransformer())
                            .process());
                } else {
                    metadata = new Metadata(toPipeline(item)
                            .apply(format.getTransformer())
                            .process());
                }
            } catch (    XMLStreamException | XmlWriteException e) {
                throw new OAIException(e);
            } catch (    TransformerException | IOException ex) {
                java.util.logging.Logger.getLogger(ListRecordsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            record.withMetadata(metadata);

            log.debug("Outputting ItemAbout");
            if (item.getAbout() != null) {
                for (About about : item.getAbout()) {
                    record.withAbout(about);
                }
            }
        }
        return record;
    }


    private XSLPipeline toPipeline(Item item) throws XmlWriteException, XMLStreamException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        XmlWriter writer = new XmlWriter(output);
        Metadata metadata = item.getMetadata();
        metadata.write(writer);
        writer.close();
        return new XSLPipeline(new ByteArrayInputStream(output.toByteArray()), true);
    }
}

