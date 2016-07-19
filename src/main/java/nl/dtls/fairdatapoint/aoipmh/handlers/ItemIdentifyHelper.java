package nl.dtls.fairdatapoint.aoipmh.handlers;

import nl.dtls.fairdatapoint.aoipmh.ItemIdentifier;


public class ItemIdentifyHelper {
    private final ItemIdentifier item;

    public ItemIdentifyHelper(ItemIdentifier item) {
        this.item = item;
    }
//
//    public List<ReferenceSet> getSets(XOAIContext context) {
//        List<ReferenceSet> list = this.item.getSets();
//        for (Set set : context.getStaticSets()) {
//            if (set.hasCondition() && set.getCondition().getFilter().isItemShown(item))
//                list.add(set);
//            else
//                list.add(set);
//        }
//        return list;
//    }


}
