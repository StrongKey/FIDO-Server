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
import com.strongauth.skfe.entitybeans.FidoUsers;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.skfeLogger;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author pmarathe
 */
@Stateless
public class getFidoUser implements getFidoUserRemote, getFidoUserLocal {

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

    @Override
    public FidoUsers remoteGetByUsername(Long did, String username) throws SKFEException {
        return GetByUsername(did, username);
    }

    @Override
    public FidoUsers GetByUsername(Long did, String username) throws SKFEException {
        try {
            TypedQuery<FidoUsers> q = em.createNamedQuery("FidoUsers.findByDidUsername", FidoUsers.class);
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("username", username);
            q.setParameter("did", did);
            FidoUsers fidoUser = q.getSingleResult();
            if (fidoUser != null) {
                verifyDBRecordSignature(did, fidoUser);
            }
            return fidoUser;
        } catch (NoResultException ex) {
            return null;
        }
    }

    private void verifyDBRecordSignature(Long did, FidoUsers FidoUser)
            throws SKFEException {
        if (FidoUser != null) {
            if (skfeCommon.getConfigurationProperty("skfe.cfg.property.db.signature.rowlevel.verify")
                    .equalsIgnoreCase("true")) {
                Domains d = getdomejb.byDid(did);
                String standalone = skfeCommon.getConfigurationProperty("skfe.cfg.property.standalone.fidoengine");
                String signingKeystorePassword = "";
                if (standalone.equalsIgnoreCase("true")) {
                    signingKeystorePassword = skfeCommon.getConfigurationProperty("skfe.cfg.property.standalone.signingkeystore.password");
                }

                String documentid = FidoUser.getFidoUsersPK().getSid()
                        + "-" + FidoUser.getFidoUsersPK().getDid()
                        + "-" + FidoUser.getFidoUsersPK().getUsername();
                FidoUser.setId(documentid);

                //jaxB conversion
                //converting the databean object to xml
                StringWriter writer = new StringWriter();
                JAXBContext jaxbContext;
                Marshaller marshaller;
                try {
                    jaxbContext = JAXBContext.newInstance(FidoUsers.class);
                    marshaller = jaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    marshaller.marshal(FidoUser, writer);
                } catch (JAXBException ex) {
                    Logger.getLogger(getFidoKeys.class.getName()).log(Level.SEVERE, null, ex);
                }

                //  verify row level signature
                boolean verified = false;
                try {
                    verified = initCryptoModule.getCryptoModule().verifyDBRow(did.toString(), writer.toString(), d.getSkceSigningdn(), Boolean.valueOf(standalone), signingKeystorePassword, FidoUser.getSignature());
                } catch (CryptoException ex) {
                    Logger.getLogger(getFidoKeys.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (!verified) {
                    skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "verifyDBRecordSignature",
                            "SKCE-ERR-5001", "er sid-did-erqid="
                            + FidoUser.getFidoUsersPK().getSid()
                            + "-" + FidoUser.getFidoUsersPK().getDid()
                            + "-" + FidoUser.getFidoUsersPK().getUsername());
                    throw new SKFEException(skfeCommon.getMessageProperty("SKCE-ERR-5001")
                            + "FidoUser sid-did-erqid="
                            + FidoUser.getFidoUsersPK().getSid()
                            + "-" + FidoUser.getFidoUsersPK().getDid()
                            + "-" + FidoUser.getFidoUsersPK().getUsername());
                }
            }
        } else {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "verifyDBRecordSignature",
                    "FIDOJPA-ERR-1001", " er object");
            throw new SKFEException(skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " fk object");
        }
    }
}
