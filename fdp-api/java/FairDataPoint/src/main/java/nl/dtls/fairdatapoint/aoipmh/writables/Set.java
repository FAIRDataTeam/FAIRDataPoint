/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.writables;

import com.lyncode.xml.exceptions.XmlWriteException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 *
 * @author Shamanou van Leeuwen
 */
public class Set implements Writable {
    private String spec;
    private String name;
    private final List<Description> descriptions = new ArrayList<>();
    
    public String getSpec() {
        return spec;
    }

    public Set withSpec(String value) {
        this.spec = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public Set withName(String value) {
        this.name = value;
        return this;
    }

    public List<Description> getDescriptions() {
        return this.descriptions;
    }

    @Override
    public void write(XmlWriter writer) throws XmlWriteException {
        writer.writeElement("setSpec", spec);
        writer.writeElement("setName", name);
        if (this.descriptions != null && !this.descriptions.isEmpty()) {
            for (Description desc : this.descriptions)
                writer.writeElement("setDescription", desc);
        }
    }

    @Override
    public void write(StringWriter writer, String format){
        if (this.descriptions != null && !this.descriptions.isEmpty()) {
            for (Description desc : this.descriptions){
                desc.write(writer, format);
            }
        }
    }
    
    public Set toOAIPMH () {
        Set set = new Set();
        set.withName(getName());
        set.withSpec(getSpec());
        return set;
    }
    
    public boolean hasCondition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}