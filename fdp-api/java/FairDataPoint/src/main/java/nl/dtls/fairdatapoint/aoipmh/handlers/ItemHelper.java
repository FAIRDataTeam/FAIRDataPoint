package nl.dtls.fairdatapoint.aoipmh.handlers;

import com.lyncode.xml.exceptions.XmlWriteException;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.FilterResolver;
import nl.dtls.fairdatapoint.aoipmh.Item;
import nl.dtls.fairdatapoint.aoipmh.Set;
import nl.dtls.fairdatapoint.utils.XmlWriter;

/**
 * 
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public class ItemHelper extends ItemIdentifyHelper {
    private final Item item;

    public ItemHelper(Item item) {
        super(item);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public InputStream toStream() throws XMLStreamException, XmlWriteException {
        if (item.getMetadata() != null) {
            return new ByteArrayInputStream(item.getMetadata().toString().getBytes());
        } else {
            ByteArrayOutputStream mdOUT = new ByteArrayOutputStream();
            XmlWriter writer = new XmlWriter(mdOUT);
            item.getMetadata().write(writer);
            writer.flush();
            writer.close();
            return new ByteArrayInputStream(mdOUT.toByteArray());
        }
    }

    public List<Set> getSets(Context context, FilterResolver resolver) {
        List<Set> result = new ArrayList<>();
        for (Set set : context.getSets()){
            if (set.getCondition().getFilter(resolver).isItemShown(item)){
               result.add(set);
            }
        }
        result.addAll(item.getSets());
        return result;
    }
}
