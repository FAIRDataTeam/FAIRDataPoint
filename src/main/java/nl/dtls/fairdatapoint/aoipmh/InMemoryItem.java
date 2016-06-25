/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh;

import com.google.common.base.Function;
import com.lyncode.builder.ListBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.dtls.fairdatapoint.aoipmh.writables.About;
import nl.dtls.fairdatapoint.aoipmh.writables.Element;
import nl.dtls.fairdatapoint.aoipmh.writables.Metadata;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class InMemoryItem implements Item {
    public static InMemoryItem item () {
        return new InMemoryItem();
    }

    private final Map<String, Object> values = new HashMap<>();
    private final static Logger LOGGER = LogManager.getLogger(InMemoryItem.class);
    
    
    public static InMemoryItem randomItem() {
        return new InMemoryItem()
                .with("identifier", randomAlphabetic(10))
                .with("datestamp", new Date())
                .with("sets", new ListBuilder<String>().add(randomAlphabetic(3)).build())
                .with("deleted", Integer.parseInt(randomNumeric(1)) > 5);
    }

    public InMemoryItem with(String name, Object value) {
        values.put(name, value);
        return this;
    }

    public InMemoryItem withSet(String name) {
        ((List<String>) values.get("sets")).add(name);
        return this;
    }

    @Override
    public List<About> getAbout() {
        return new ArrayList<>();
    }

    @Override
    public Metadata getMetadata() {
        String tmp = toMetadata().toString();
        return new Metadata(toMetadata().toString());
    }

    private OAIMetadata toMetadata() {
        OAIMetadata builder = new OAIMetadata();
        for (String key : values.keySet()) {
            Element elementBuilder = new Element(key);
            Object value = values.get(key);
            if (value instanceof String){
                elementBuilder.withField(key, (String) value);
            } else if (value instanceof Date){
                elementBuilder.withField(key, ((Date) value).toString());
            }else if (value instanceof List) {
                List<String> obj = (List<String>) value;
                int i = 1;
                for (String e : obj){
                    elementBuilder.withField(key + (i++), e);
                }
            }
            builder.withElement(elementBuilder);
        }
        return builder;
    }

    @Override
    public String getIdentifier() {
        return (String) values.get("identifier");
    }

    @Override
    public Date getDatestamp() {
        return (Date) values.get("datestamp");
    }

    @Override
    public List<Set> getSets() {
        List<String> list = ((List<String>) values.get("sets"));
        return new ListBuilder<String>().add(list.toArray(new String[list.size()])).build(new Function<String, Set>() {
            @Override
            public Set apply(String elem) {
                return new Set(elem);
            }
        });
    }

    @Override
    public boolean isDeleted() {
        return (Boolean) values.get("deleted");
    }

    public InMemoryItem withDefaults() {
        this
                .with("identifier", randomAlphabetic(10))
                .with("datestamp", new Date())
                .with("sets", new ListBuilder<String>().add(randomAlphabetic(3)).build())
                .with("deleted", Integer.parseInt(randomNumeric(1)) > 5);
        return this;
    }

    public InMemoryItem withIdentifier(String identifier) {
        this.with("identifier", identifier);
        return this;
    }
}

