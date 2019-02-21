/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License, as published by the Free Software Foundation and
 * available at http://www.fsf.org/licensing/licenses/lgpl.html,
 * version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 *
 * **********************************************
 *
 *  888b    888          888
 *  8888b   888          888
 *  88888b  888          888
 *  888Y88b 888  .d88b.  888888  .d88b.  .d8888b
 *  888 Y88b888 d88""88b 888    d8P  Y8b 88K
 *  888  Y88888 888  888 888    88888888 "Y8888b.
 *  888   Y8888 Y88..88P Y88b.  Y8b.          X88
 *  888    Y888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * **********************************************
 *
 *
 * An object to hold to hold the Domains object.  To improve performance,
 * each DomainObject will also now have its own SecureRandom (from SunJCE)
 * seeded by the cryptographic hardware module on the appliance.  It will
 * also hold domain-specific configuration instead of the configuration-map
 * so that changes through DACTool go into effect without GF-restarts.
 */

package com.strongkey.appliance.utilities;

import com.strongkey.appliance.entitybeans.Domains;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import javax.xml.bind.DatatypeConverter;

public class DomainObject
{
    private Domains domain = null;
    private SecureRandom secrand = null;
    private Boolean useprng = Boolean.FALSE;
    private static SortedMap<String, String> config = null;
    private int counter = 0;
    private long lastused;

    DomainObject(Domains domain) {
        this.domain = domain;
        config = new ConcurrentSkipListMap<>();
        lastused = System.currentTimeMillis();
    }

    protected void setDomain(Domains domain) {
        this.domain = domain;
        lastused = System.currentTimeMillis();
    }

    protected Domains getDomain() {
        return domain;
    }

    protected void setSecureRandom(String algorithm, byte[] seed) throws NoSuchAlgorithmException {
        secrand = SecureRandom.getInstance(algorithm);
        secrand.setSeed(seed);
        useprng = Boolean.TRUE;
    }

    protected SecureRandom getSecureRandom() {
        return secrand;
    }

    protected String getIv(int size) {
        byte[] randbytes = new byte[size];
        secrand.nextBytes(randbytes);
        String iv = DatatypeConverter.printBase64Binary(randbytes);
        strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER, Level.FINE, "DomainObject", "getIv", "APPL-MSG-1047", iv + " [DID=" + domain.getDid().toString() + ", COUNTER=" + counter + "]");
        counter++;
        return iv;
    }

    protected String setConfiguration(String key, String value) {
        config.put(key, value);
        return this.getConfiguration(key);
    }

    protected String getConfiguration(String key) {
        if (config.containsKey(key))
            return config.get(key);
        else
            return null;
    }

    protected void setUsePrng(Boolean useprng) {
        this.useprng = useprng;
    }

    protected Boolean getUsePrng() {
        return useprng;

    }

    protected void setLastUsed(long timeinms) {
        this.lastused = timeinms;
    }

    public long getLastUsed() {
        return lastused;
    }
}