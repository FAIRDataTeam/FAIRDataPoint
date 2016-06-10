
package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xoai.model.oaipmh.DeletedRecord;
import com.lyncode.xoai.model.oaipmh.Granularity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RepositoryConfiguration {
    @Autowired
    private String repositoryName;
    @Autowired
    private ArrayList<String> adminEmails;
    @Autowired
    @Qualifier("baseURI")
    private String baseUrl;
    private Date earliestDate = new Date();
    private int maxListIdentifiers = 100;
    private int maxListSets = 100;
    private int maxListRecords = 100;
    private Granularity granularity = Granularity.Second;
    private DeletedRecord deleteMethod = DeletedRecord.NO;
    @Autowired
    private String description;
    private ArrayList<String> compressions;
    
    public String getRepositoryName() {
        return repositoryName;
    }

    public ArrayList<String> getAdminEmails() {
        return adminEmails;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Date getEarliestDate() {
        return earliestDate;
    }

    public int getMaxListIdentifiers() {
        return this.maxListIdentifiers;
    }

    public int getMaxListSets() {
        return this.maxListSets;
    }

    public int getMaxListRecords() {
        return this.maxListRecords;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public DeletedRecord getDeleteMethod() {
        return deleteMethod;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCompressions () {
        return compressions;
    }

    public RepositoryConfiguration withMaxListSets(int maxListSets) {
        this.maxListSets = maxListSets;
        return this;
    }

    public RepositoryConfiguration withGranularity(Granularity granularity) {
        this.granularity = granularity;
        return this;
    }

    public RepositoryConfiguration and () {
        return this;
    }

    public RepositoryConfiguration withDeleteMethod(DeletedRecord deleteMethod) {
        this.deleteMethod = deleteMethod;
        return this;
    }


    public RepositoryConfiguration withEarliestDate(Date earliestDate) {
        this.earliestDate = earliestDate;
        return this;
    }

    public RepositoryConfiguration withCompression (String compression) {
        if (compressions == null)
            compressions = new ArrayList<>();
        compressions.add(compression);
        return this;
    }

    public RepositoryConfiguration withMaxListRecords(int maxListRecords) {
        this.maxListRecords = maxListRecords;
        return this;
    }

    public boolean hasCompressions() {
        return compressions != null && !compressions.isEmpty();
    }

    public RepositoryConfiguration withMaxListIdentifiers(int maxListIdentifiers) {
        this.maxListIdentifiers = maxListIdentifiers;
        return this;
    }
}
