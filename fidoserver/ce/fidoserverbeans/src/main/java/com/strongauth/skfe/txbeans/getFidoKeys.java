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
package com.strongauth.skfe.txbeans;

import com.strongauth.appliance.entitybeans.Domains;
import com.strongauth.crypto.interfaces.initCryptoModule;
import com.strongauth.crypto.utility.CryptoException;
import com.strongauth.skfe.entitybeans.FidoKeys;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.skfeLogger;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Stateless
public class getFidoKeys implements getFidoKeysLocal {

    /**
     ** This class's name - used for logging & not persisted
     *
     */
    private final String classname = this.getClass().getName();

    final private String SIGN_SUFFIX = skfeCommon.getConfigurationProperty("skfe.cfg.property.signsuffix");
    /**
     * Persistence context for derby
     */
    @PersistenceContext
    private EntityManager em;

    @EJB
    getDomainsBeanLocal getdomejb;

    /**
     *
     * @param did
     * @param username - Name of the user whose keys need to be fetched.
     * @return - List of keys for the supplied username
     */
    @Override
    public Collection<FidoKeys> getByUsername(Long did, String username) throws SKFEException {
        try {
            TypedQuery<FidoKeys> q = em.createNamedQuery("FidoKeys.findByUsername", FidoKeys.class);
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("username", username);
            q.setParameter("did", did);
            Collection<FidoKeys> fidoKeysColl = q.getResultList();
            Collection<FidoKeys> validFidoKeysColl = fidoKeysColl;
            if (!fidoKeysColl.isEmpty()) {
                for (FidoKeys fk : fidoKeysColl) {
                    if (fk != null) {
                        try {
                            verifyDBRecordSignature(did, fk);
                        } catch (SKFEException ex) {
                            validFidoKeysColl.remove(fk);
                        }
                    }
                }
            }
            return validFidoKeysColl;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     *
     * @param did
     * @return - all keys in the DB
     */
    @Override
    public Collection<FidoKeys> getAll(Long did) throws SKFEException {
        try {
            TypedQuery<FidoKeys> q = em.createNamedQuery("FidoKeys.findAllbyDid", FidoKeys.class);
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("did", did);
            Collection<FidoKeys> fidoKeysColl = q.getResultList();
            Collection<FidoKeys> validFidoKeysColl = fidoKeysColl;
            if (!fidoKeysColl.isEmpty()) {
                for (FidoKeys fk : fidoKeysColl) {
                    if (fk != null) {
                        try {
                            verifyDBRecordSignature(did, fk);
                        } catch (SKFEException ex) {
                            validFidoKeysColl.remove(fk);
                        }
                    }
                }
            }
            return validFidoKeysColl;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     *
     * @param did
     * @param username - Name of the user whose keys have been requested
     * @param KH - User Key Handle for the key to be retrieved
     * @return - Returns the unique registered key identified by the parameters
     * passed
     * @throws com.strongauth.skce.utilities.SKFEException
     */
    @Override
    public FidoKeys getByUsernameKH(Long did, String username, String KH) throws SKFEException {
        try {
            Query q = em.createNamedQuery("FidoKeys.findByUsernameKH");
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("username", username);
            q.setParameter("did", did);
            q.setParameter("keyhandle", KH);
            FidoKeys rk = (FidoKeys) q.getSingleResult();
            if (rk != null) {
                verifyDBRecordSignature(did, rk);
            }
            return rk;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     *
     * @param sid
     * @param did
     * @param username
     * @param fkid - Unique identifier for a key
     * @return - Returns a Key identified by the fkid parameter.
     */
    @Override
    public FidoKeys getByfkid(Short sid, Long did, String username, Long fkid) throws SKFEException {
        try {
            Query q = em.createNamedQuery("FidoKeys.findBySidDidFkid");
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("fkid", fkid);
            q.setParameter("did", did);
            q.setParameter("username", username);
            q.setParameter("sid", sid);
            FidoKeys rk = (FidoKeys) q.getSingleResult();
            if (rk != null) {
                verifyDBRecordSignature(did, rk);
            }
            return rk;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     *
     * @param did
     * @param username - Name of the user whose keys need to be fetched.
     * @param status - Status of keys that belong to the user.
     * @return - List of keys for the supplied username with the supplied
     * password
     */
    @Override
    public Collection<FidoKeys> getByUsernameStatus(Long did, String username, String status) throws SKFEException {
        try {
            TypedQuery<FidoKeys> q = em.createNamedQuery("FidoKeys.findByUsernameStatus", FidoKeys.class);
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("username", username);
            q.setParameter("did", did);
            q.setParameter("status", status);
            Collection<FidoKeys> fidoKeysColl = q.getResultList();
            Collection<FidoKeys> validFidoKeysColl = new ArrayList<>();
            validFidoKeysColl.addAll(fidoKeysColl);
            if (!fidoKeysColl.isEmpty()) {
                for (FidoKeys fk : fidoKeysColl) {
                    if (fk != null) {
                        try {
                            verifyDBRecordSignature(did, fk);
                        } catch (SKFEException ex) {
                            validFidoKeysColl.remove(fk);
                        }
                    }
                }
            }
            return validFidoKeysColl;
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     *
     * @param did
     * @param username - Name of the user whose keys need to be fetched.
     * @param status - status of Fido key (Active vs Inactive)
     * @return - List of keys for the supplied username
     */
    @Override
    public FidoKeys getNewestKeyByUsernameStatus(Long did, String username, String status) throws SKFEException {
        try {
            Query q = em.createNamedQuery("FidoKeys.findNewestKeyByUsernameStatus", FidoKeys.class);
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("username", username);
            q.setParameter("did", did);
            q.setParameter("status", status);

            List<FidoKeys> fkList = q.getResultList();
            if (fkList == null || fkList.isEmpty()) {
                return null;
            }
            FidoKeys rk = fkList.get(0);
            if (rk != null) {
                verifyDBRecordSignature(did, rk);
            }
            return rk;
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    synchronized public int nextfkid(Short sid, Long did, String username) {
        try {
            Query q = em.createNamedQuery("FidoKeys.maxpk");
            q.setParameter("sid", sid);
            q.setParameter("did", did);
            q.setParameter("username", username);
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            int fkid = (int) q.getSingleResult();
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    "nextfkid : " + fkid);
            return fkid + 1;
        } catch (NoResultException | NullPointerException ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    "nextfkid exception: " + ex.getLocalizedMessage());
            return 1;
        } finally {
            em.flush();
        }
    }

    /**
     * Verifies the database row level signature of the given object and returns
     * successfully if verified and throws exception if not verified.
     *
     * @param did CDA domain id
     * @param er Object whose signature has to be verified
     * @throws CDAException
     */
    private void verifyDBRecordSignature(Long did, FidoKeys fk)
            throws SKFEException {
        if (fk != null) {
            if (skfeCommon.getConfigurationProperty("skfe.cfg.property.db.signature.rowlevel.verify")
                    .equalsIgnoreCase("true")) {
                Domains d = getdomejb.byDid(did);
                String standalone = skfeCommon.getConfigurationProperty("skfe.cfg.property.standalone.fidoengine");
                String signingKeystorePassword = "";
                if (standalone.equalsIgnoreCase("true")) {
                    signingKeystorePassword = skfeCommon.getConfigurationProperty("skfe.cfg.property.standalone.signingkeystore.password");
                }

                String documentid = fk.getFidoKeysPK().getSid()
                        + "-" + fk.getFidoKeysPK().getDid()
                        + "-" + fk.getFidoKeysPK().getUsername()
                        + "-" + fk.getFidoKeysPK().getFkid();
                fk.setId(documentid);

                //jaxB conversion
                //converting the databean object to xml
                StringWriter writer = new StringWriter();
                JAXBContext jaxbContext;
                Marshaller marshaller;
                try {
                    jaxbContext = JAXBContext.newInstance(FidoKeys.class);
                    marshaller = jaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marshaller.marshal(fk, writer);
                } catch (JAXBException ex) {
                    Logger.getLogger(getFidoKeys.class.getName()).log(Level.SEVERE, null, ex);
                }

                //  verify row level signature
                boolean verified = false;
                try {
                    verified = initCryptoModule.getCryptoModule().verifyDBRow(did.toString(), writer.toString(), d.getSkceSigningdn(), Boolean.valueOf(standalone), signingKeystorePassword, fk.getSignature());
                } catch (CryptoException ex) {
                    Logger.getLogger(getFidoKeys.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!verified) {
                    skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "verifyDBRecordSignature",
                            "SKCE-ERR-5001", "er sid-did-erqid="
                            + fk.getFidoKeysPK().getSid()
                            + "-" + fk.getFidoKeysPK().getDid()
                            + "-" + fk.getFidoKeysPK().getUsername()
                            + "-" + fk.getFidoKeysPK().getFkid());
                    throw new SKFEException(skfeCommon.getMessageProperty("SKCE-ERR-5001")
                            + "fk sid-did-erqid="
                            + fk.getFidoKeysPK().getSid()
                            + "-" + fk.getFidoKeysPK().getDid()
                            + "-" + fk.getFidoKeysPK().getUsername()
                            + "-" + fk.getFidoKeysPK().getFkid());
                }
            }
        } else {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "verifyDBRecordSignature",
                    "FIDOJPA-ERR-1001", " er object");
            throw new SKFEException(skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " fk object");
        }
    }
}
