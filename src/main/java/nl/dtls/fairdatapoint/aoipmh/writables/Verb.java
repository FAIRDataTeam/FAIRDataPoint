/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dtls.fairdatapoint.aoipmh.writables;

/**
 *
 * @author Shamanou van Leeuwen
 */
public interface Verb extends Writable {
    public static enum Type {
        Identify("Identify"),
        ListMetadataFormats("ListMetadataFormats"),
        ListSets("ListSets"),
        GetRecord("GetRecord"),
        ListIdentifiers("ListIdentifiers"),
        ListRecords("ListRecords");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String displayName() {
            return value;
        }

        public static Type fromValue(String value) {
            for (Type c : Type.values()) {
                if (c.value.equals(value)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(value);
        }
        
    }
    
    Type getType ();
}
