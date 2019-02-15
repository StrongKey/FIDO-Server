/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.strongkey.fido2mds.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dpatterson
 */
public class MemoryStorage extends Storage {

    private final Map<String, Map<String, String>> mapOfMaps = new HashMap<>();

    public MemoryStorage() {
        super();
    }

    @Override
    public String loadData(String namespace, String key) {
        String data = null;
        Map<String, String> map = mapOfMaps.get(namespace);
        if (map != null) {
            data = map.get(key);
        }
        return data;
    }

    @Override
    public void saveData(String namespace, String key, String data) {
        Map<String, String> map = mapOfMaps.get(namespace);
        if (map == null) {
            map = new HashMap<>();
            mapOfMaps.put(namespace,map);
        }
        map.put(key, data);
    }

}
