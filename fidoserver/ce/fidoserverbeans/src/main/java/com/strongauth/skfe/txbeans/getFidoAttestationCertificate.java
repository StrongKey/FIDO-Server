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
 * $Date: 
 * $Revision:
 * $Author: mishimoto $
 * $URL: 
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
 *
 *
 */
package com.strongauth.skfe.txbeans;

import com.strongauth.skfe.entitybeans.AttestationCertificates;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mishimoto
 */
@Stateless
public class getFidoAttestationCertificate implements getFidoAttestationCertificateLocal {

    /**
     * Persistence context for derby
     */
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public AttestationCertificates getByPK(Long did, Long sid, Long attcid){
        try {
            Query q = em.createNamedQuery("AttestationCertificates.findBySidDidAttcid");
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("did", did);
            q.setParameter("sid", sid);
            q.setParameter("attcid", attcid);
            AttestationCertificates ac = (AttestationCertificates) q.getSingleResult();
//            if (ac != null) {                       //TODO verify signature
//                verifyDBRecordSignature(did, fp);
//            }
            return ac;
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public AttestationCertificates getByIssuerDnSerialNumber(String issuerDn, String serialNumber){
        try {
            Query q = em.createNamedQuery("AttestationCertificates.findByIssuerDnSerialNumber");
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("issuerDn", issuerDn);
            q.setParameter("serialNumber", serialNumber);
            AttestationCertificates ac = (AttestationCertificates) q.getSingleResult();
//            if (ac != null) {                       //TODO verify signature
//                verifyDBRecordSignature(did, fp);
//            }
            return ac;
        } catch (NoResultException ex) {
            return null;
        }
    }
}
