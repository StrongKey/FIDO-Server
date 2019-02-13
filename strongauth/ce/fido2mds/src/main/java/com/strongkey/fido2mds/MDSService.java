/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.strongkey.fido2mds;

import com.strongkey.fido2mds.structures.MetadataStatement;
import com.strongkey.fido2mds.structures.MetadataTOCPayloadEntry;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dpatterson
 */
public abstract class MDSService {
    protected Map<String,MetadataTOCPayloadEntry> tocEntryMap;
    protected Map<String,MetadataStatement> metadataStatementMap;

    public abstract void refresh();
    
    public MetadataTOCPayloadEntry getTOCEntry(String key) {
        return tocEntryMap.get(key);   
    }
    
    public MetadataStatement getMetadataStatement(String key) {
        return metadataStatementMap.get(key);
    }
    
    public Set<String> getKeys() {
        HashSet<String> ret = new HashSet<>();
        ret.addAll(tocEntryMap.keySet());
        ret.addAll(metadataStatementMap.keySet());
        return ret;
    }
}
