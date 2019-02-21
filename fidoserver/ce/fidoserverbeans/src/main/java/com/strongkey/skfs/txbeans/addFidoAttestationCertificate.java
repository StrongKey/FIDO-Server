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
package com.strongkey.skfs.txbeans;

import com.strongkey.appliance.utilities.applianceCommon;
import com.strongkey.appliance.utilities.applianceConstants;
import com.strongkey.skfs.utilities.skfsLogger;
import com.strongkey.skfs.utilities.SKFEException;
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.entitybeans.AttestationCertificates;
import com.strongkey.skfs.entitybeans.AttestationCertificatesPK;
import com.strongkey.skfs.messaging.replicateSKFEObjectBeanLocal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

@Stateless
public class addFidoAttestationCertificate implements addFidoAttestationCertificateLocal {

    private final String classname = this.getClass().getName();
    @Resource
    private SessionContext sc;
    @PersistenceContext
    private EntityManager em;

    @EJB
    replicateSKFEObjectBeanLocal replObj;
    @EJB
    SequenceGeneratorBeanLocal seqgenejb;
    
    @Override
    public AttestationCertificatesPK execute(Long did, X509Certificate attCert, 
            AttestationCertificatesPK parentPK) throws CertificateEncodingException, SKFEException{
        skfsLogger.entering(skfsConstants.SKFE_LOGGER, classname, "execute");

        Long sid = applianceCommon.getServerId();
        Integer attcid = seqgenejb.nextAttestationCertificateID();
        AttestationCertificatesPK attestationCertificatePK = new AttestationCertificatesPK();
        AttestationCertificates attestationCertificate = new AttestationCertificates();
        attestationCertificatePK.setSid(sid.shortValue());
        attestationCertificatePK.setDid(did.shortValue());
        attestationCertificatePK.setAttcid(attcid);
        attestationCertificate.setAttestationCertificatesPK(attestationCertificatePK);
        attestationCertificate.setParentSid((parentPK != null)?parentPK.getSid():null);
        attestationCertificate.setParentDid((parentPK != null)?parentPK.getDid():null);
        attestationCertificate.setParentAttcid((parentPK != null)?parentPK.getAttcid():null);
        attestationCertificate.setCertificate(Base64.getUrlEncoder().encodeToString(attCert.getEncoded()));
        attestationCertificate.setIssuerDn(attCert.getIssuerDN().getName());
//        if(attCert.getSubjectDN().getName().length() == 0){
//            attestationCertificate.setSubjectDn("Not Specified");
//        }else{
            attestationCertificate.setSubjectDn(attCert.getSubjectDN().getName());
//        }
        
        attestationCertificate.setSerialNumber(attCert.getSerialNumber().toString());
        

        //TODO add signing code(?)
        try {
            em.persist(attestationCertificate);
            em.flush();
            em.clear();
        } catch (ConstraintViolationException ex) {
            ex.getConstraintViolations().stream().forEach(x -> skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    x.toString()));
            skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.SEVERE, "FIDOJPA-ERR-2006", ex.getLocalizedMessage());
            throw new SKFEException(skfsCommon.getMessageProperty("FIDOJPA-ERR-2006") + "Check server logs for details.");
        }

        //TODO Replicate
        String primarykey = sid + "-" + did + "-" + attcid;
        try {
            if (applianceCommon.replicate()) {
                String response = replObj.execute(applianceConstants.ENTITY_TYPE_ATTESTATION_CERTIFICATES, applianceConstants.REPLICATION_OPERATION_ADD, primarykey, attestationCertificate);
                if(response != null){
                    throw new SKFEException(skfsCommon.getMessageProperty("FIDOJPA-ERR-1001") + response);
                }
            }
        } catch (Exception e) {
            sc.setRollbackOnly();
            skfsLogger.exiting(skfsConstants.SKFE_LOGGER, classname, "execute");
            throw new RuntimeException(e.getLocalizedMessage());
        }
        skfsLogger.exiting(skfsConstants.SKFE_LOGGER, classname, "execute");

        return attestationCertificatePK;
    }
}
