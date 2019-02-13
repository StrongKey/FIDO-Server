/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.strongkey.fido2mds.data;

/**
 *
 * @author dpatterson
 */
public abstract class Storage {

    public Storage() {
    }

    public abstract String loadData(String namespace, String key);

    public abstract void saveData(String namespace, String key, String data);
}
