/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.client;

import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import com.lyncode.xoai.model.oaipmh.Granularity;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class Context {
    private static final TransformerFactory factory = TransformerFactory.newInstance();

    private Transformer transformer;
    private final Map<String, Transformer> metadataTransformers = new HashMap<>();
    private String baseUrl;
    private Granularity granularity;
    private OAIClient client;

    public Context() {
        try {
            this.withMetadataTransformer("xoai", factory.newTransformer());
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Unable to initialize identity transformer");
        }
    }

    public Context withTransformer (Transformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public boolean hasTransformer () {
        return transformer != null;
    }

    public Transformer getTransformer () {
        return transformer;
    }

    public boolean hasMetadataTransformerForPrefix (String prefix) {
        return metadataTransformers.containsKey(prefix);
    }

    public Context withMetadataTransformer (String prefix, Transformer transformer) {
        metadataTransformers.put(prefix, transformer);
        return this;
    }

    public Context withMetadataTransformer (String prefix, KnownTransformer knownTransformer) {
        return withMetadataTransformer(prefix, knownTransformer.transformer());
    }

    public Transformer getMetadataTransformer (String prefix) {
        return metadataTransformers.get(prefix);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Context withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public Context withGranularity(Granularity granularity) {
        this.granularity = granularity;
        return this;
    }

    public Context withOAIClient (OAIClient client) {
        this.client = client;
        return this;
    }

    public OAIClient getClient () {
        return client;
    }

    public enum KnownTransformer {
        OAI_DC("to_xoai/oai_dc.xsl");

        private final String location;

        KnownTransformer(String location) {
            this.location = location;
        }

        public Transformer transformer () {
            try {
                return factory.newTransformer(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(location)));
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException("Unable to load resource file '"+location+"'", e);
            }
        }
    }
}

