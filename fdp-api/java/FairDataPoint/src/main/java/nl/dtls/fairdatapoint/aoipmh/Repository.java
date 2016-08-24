package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xoai.services.api.ResumptionTokenFormat;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class Repository {
    private FilterResolver filterResolver;

    @Autowired
    private RepositoryConfiguration configuration;
    private ItemRepository itemRepository;
    private SetRepository setRepository;
    private ResumptionTokenFormat resumptionTokenFormatter;

    public RepositoryConfiguration getConfiguration() {
        return configuration;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public Repository withItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        return this;
    }

    public SetRepository getSetRepository() {
        return setRepository;
    }

    public Repository withSetRepository(SetRepository setRepository) {
        this.setRepository = setRepository;
        return this;
    }

    public ResumptionTokenFormat getResumptionTokenFormatter() {
        return resumptionTokenFormatter;
    }

    public Repository withResumptionTokenFormatter(ResumptionTokenFormat resumptionTokenFormatter) {
        this.resumptionTokenFormatter = resumptionTokenFormatter;
        return this;
    }

    public FilterResolver getFilterResolver() {
        return filterResolver;
    }
}
