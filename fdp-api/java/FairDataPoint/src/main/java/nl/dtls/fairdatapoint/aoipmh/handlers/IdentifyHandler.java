package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xoai.dataprovider.exceptions.HandlerException;
import com.lyncode.xoai.dataprovider.exceptions.InternalOAIException;
import com.lyncode.xoai.dataprovider.exceptions.OAIException;
import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import java.net.MalformedURLException;
import java.net.URL;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.Repository;
import nl.dtls.fairdatapoint.aoipmh.RepositoryConfiguration;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.writables.VerbHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class IdentifyHandler extends VerbHandler<Identify> {
    @Autowired
    private Identify identify;
    
    public IdentifyHandler(Context context, Repository repository) {
        super(context, repository);

        // Static validation
        RepositoryConfiguration configuration = getRepository().getConfiguration();
        if (configuration == null){
            throw new InternalOAIException("No repository configuration provided");
        } if (configuration.getMaxListSets() <= 0){
            throw new InternalOAIException("The repository configuration must return maxListSets greater then 0");
        }if (configuration.getMaxListIdentifiers() <= 0)
            throw new InternalOAIException("The repository configuration must return maxListIdentifiers greater then 0");
        if (configuration.getMaxListRecords() <= 0){
            throw new InternalOAIException("The repository configuration must return maxListRecords greater then 0");
        }if (configuration.getAdminEmails() == null)
            throw new InternalOAIException("The repository configuration must return at least one admin email");
        try {
            if (configuration.getBaseUrl() == null){
                throw new InternalOAIException("The repository configuration must return a valid base url (absolute)");
            }
            URL url = new URL(configuration.getBaseUrl());
        } catch (MalformedURLException e) {
            throw new InternalOAIException("The repository configuration must return a valid base url (absolute)", e);
        }
        if (configuration.getDeleteMethod() == null){
            throw new InternalOAIException("The repository configuration must return a valid delete method");
        }if (configuration.getEarliestDate() == null){
            throw new InternalOAIException("The repository configuration must return a valid earliest date. That's the date of the first inserted item");
        }if (configuration.getRepositoryName() == null){
            throw new InternalOAIException("The repository configuration must return a valid repository name");
        }
    }

    @Override
    public Identify handle(OAICompiledRequest params) throws OAIException, HandlerException {
        RepositoryConfiguration configuration = getRepository().getConfiguration();
        identify.withEarliestDatestamp(configuration.getEarliestDate());
        identify.withDeletedRecord(DeletedRecord.valueOf(configuration.getDeleteMethod().name()));

        identify.withGranularity(configuration.getGranularity());
        if (configuration.hasCompressions()){
            for (String com : configuration.getCompressions()){
                identify.getCompressions().add(com);
            }
        }
//        String description = configuration.getDescription();
//        if (description == null) {
//            identify.withDescription(new Description());
//        } else {
//            identify.withDescription(new Description().withMetadata(description));
//        }
        return identify;
    }
}
