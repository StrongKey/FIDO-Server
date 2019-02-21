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
 * Copyright (c) 2001-2016 StrongAuth, Inc.
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 *
 * *********************************************
 *                    888
 *                    888
 *                    888
 *  88888b.   .d88b.  888888  .d88b.  .d8888b
 *  888 "88b d88""88b 888    d8P  Y8b 88K
 *  888  888 888  888 888    88888888 "Y8888b.
 *  888  888 Y88..88P Y88b.  Y8b.          X88
 *  888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 *
 */
package com.strongkey.skfs.txbeans;

import com.strongkey.appliance.utilities.applianceCommon;
import com.strongkey.skfs.utilities.skfsLogger;
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SuppressWarnings("FieldMayBeFinal")
@Singleton
public class SequenceGeneratorBean implements SequenceGeneratorBeanLocal {

    /**
     * To query the database for current maximum ID values
     */
    @PersistenceContext
    private EntityManager em;

    /**
     ** Private map of request ID numbers. This allows encryption and *
     * decryption requests to get unique ID numbers faster than having * to
     * query the database.
     *
     */
    private static ConcurrentMap<Short, FIDOKeyID> idmap = new ConcurrentSkipListMap<>();
    
    private static ConcurrentMap<Short, PolicyID> pidmap = new ConcurrentSkipListMap<>();
    
    private static ConcurrentMap<Short, AttestationCertificateID> attcidmap = new ConcurrentSkipListMap<>();

    /**
     * The server id of the server executing this code.
     */
    private static Short ssid = applianceCommon.getServerId().shortValue();

    /**
     ** RequestID
     *
     * @return
     *
     */
    @Override
    synchronized public Long nextFIDOKeyID() {
        // Check if rqid is in the idmap; if so return it
        if (idmap.containsKey(ssid)) {
            return idmap.get(ssid).nextFKid();
        } else {
            // Populate the map and return rqid
            if (populateFIDOKeyIDMap() == null) {
                return null;
            }
            return idmap.get(ssid).nextFKid();
        }
    }
    
    @Override
    synchronized public Integer nextPolicyID() {
        // Check if pid is in the idmap; if so return it
        if (pidmap.containsKey(ssid)) {
            return pidmap.get(ssid).nextPid();
        } else {
            // Populate the map and return pid
            if (populatePolicyIDMap() == null) {
                return null;
            }
            return pidmap.get(ssid).nextPid();
        }
    }
    
    @Override
    synchronized public Integer nextAttestationCertificateID() {
        // Check if pid is in the idmap; if so return it
        if (attcidmap.containsKey(ssid)) {
            return attcidmap.get(ssid).nextAttcid();
        } else {
            // Populate the map and return pid
            if (populateAttestationCertificateIDMap() == null) {
                return null;
            }
            return attcidmap.get(ssid).nextAttcid();
        }
    }

    /**
     ** A method to initialize the RequestID maps in memory so that * requests
     * after the first one will get their ID value quickly. * Since access to
     * the map is synchronized, there will be no * collisions. If the system
     * crashes, then the first request * after a restart will pick up where the
     * last one left off since * this map gets its last-used value from the
     * database.
     *
     */
    synchronized private FIDOKeyID populateFIDOKeyIDMap() {
        // Initialize local variables
        Long fkid;
        FIDOKeyID Keyid = new FIDOKeyID();

        try {
            fkid = (Long) em.createNamedQuery("FidoKeys.maxpk")
                    .setParameter("sid", ssid)
                    .setHint("javax.persistence.cache.storeMode", "REFRESH")
                    .getSingleResult() + 1;
        } catch (NullPointerException ex) { // First request for the server
            fkid = 1L;
        }

        Keyid.setFKid(fkid);
        skfsLogger.log(skfsConstants.SKFE_LOGGER,Level.INFO, skfsCommon.getMessageProperty("SKCE-MSG-1085"), "SID-FKID=" + ssid + "-" + fkid);

        idmap.put(ssid, Keyid);
        return idmap.get(ssid);

    }
    
    /**
     ** A method to initialize the RequestID maps in memory so that * requests
     * after the first one will get their ID value quickly. * Since access to
     * the map is synchronized, there will be no * collisions. If the system
     * crashes, then the first request * after a restart will pick up where the
     * last one left off since * this map gets its last-used value from the
     * database.
     *
     */
    synchronized private PolicyID populatePolicyIDMap() {
        // Initialize local variables
        Integer pid;
        PolicyID Keyid = new PolicyID();

        try {
            pid = (Integer) em.createNamedQuery("FidoPolicies.maxpid")
                    .setParameter("sid", ssid)
                    .setHint("javax.persistence.cache.storeMode", "REFRESH")
                    .getSingleResult() + 1;
        } catch (NullPointerException ex) { // First request for the server
            pid = 1;
        }

        Keyid.setPid(pid);
        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.INFO, skfsCommon.getMessageProperty("SKCE-MSG-1085"), "SID-PID=" + ssid + "-" + pid);

        pidmap.put(ssid, Keyid);
        return pidmap.get(ssid);
    }
    
    /**
     ** A method to initialize the RequestID maps in memory so that * requests
     * after the first one will get their ID value quickly. * Since access to
     * the map is synchronized, there will be no * collisions. If the system
     * crashes, then the first request * after a restart will pick up where the
     * last one left off since * this map gets its last-used value from the
     * database.
     *
     */
    synchronized private AttestationCertificateID populateAttestationCertificateIDMap() {
        // Initialize local variables
        Integer attcid;
        AttestationCertificateID Certid = new AttestationCertificateID();

        try {
            attcid = (Integer) em.createNamedQuery("AttestationCertificates.maxattcid")
                    .setParameter("sid", ssid)
                    .setHint("javax.persistence.cache.storeMode", "REFRESH")
                    .getSingleResult() + 1;
        } catch (NullPointerException ex) { // First request for the server
            attcid = 1;
        }

        Certid.setAttcid(attcid);
        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.INFO, skfsCommon.getMessageProperty("SKCE-MSG-1085"), "SID-ATTCID=" + ssid + "-" + attcid);

        attcidmap.put(ssid, Certid);
        return attcidmap.get(ssid);
    }
}

/**
 ** Local class to maintain the two ID values in a map that can be retrieved *
 * rapidly for quick returns on ID values; otherwise, each call to this bean *
 * will involve a database access, which will progressively take longer as *
 * more requests come in.
 */
@SuppressWarnings("FieldMayBeFinal")
class FIDOKeyID {

    private AtomicLong kid = new AtomicLong();

    // REQUEST
    Long nextFKid() {
        return this.kid.getAndIncrement();
    }

    void setFKid(Long kid) {
        this.kid.set(kid);
    }

}

/**
 ** Local class to maintain the two ID values in a map that can be retrieved *
 * rapidly for quick returns on ID values; otherwise, each call to this bean *
 * will involve a database access, which will progressively take longer as *
 * more requests come in.
 */
@SuppressWarnings("FieldMayBeFinal")
class PolicyID {

    private AtomicInteger pid = new AtomicInteger();

    // REQUEST
    Integer nextPid() {
        return this.pid.getAndIncrement();
    }

    void setPid(Integer pid) {
        this.pid.set(pid);
    }

}

/**
 ** Local class to maintain the two ID values in a map that can be retrieved *
 * rapidly for quick returns on ID values; otherwise, each call to this bean *
 * will involve a database access, which will progressively take longer as *
 * more requests come in.
 */
@SuppressWarnings("FieldMayBeFinal")
class AttestationCertificateID {

    private AtomicInteger attcid = new AtomicInteger();

    // REQUEST
    Integer nextAttcid() {
        return this.attcid.getAndIncrement();
    }

    void setAttcid(Integer attcid) {
        this.attcid.set(attcid);
    }

}