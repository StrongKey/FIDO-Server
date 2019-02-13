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
package com.strongauth.skfe.policybeans;

import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skce.pojos.MDSClient;
import com.strongauth.skfe.fido.policyobjects.FidoPolicyObject;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.utilities.skceMaps;
import com.strongauth.skfe.entitybeans.FidoPolicies;
import com.strongauth.skfe.entitybeans.FidoPoliciesPK;
import com.strongauth.skfe.txbeans.SequenceGeneratorBeanLocal;
import com.strongauth.skfe.messaging.replicateSKFEObjectBeanLocal;
import com.strongauth.skfe.pojos.FidoPolicyMDSObject;
import com.strongkey.fido2mds.MDS;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

@Stateless
public class addFidoPolicy implements addFidoPolicyLocal {

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
    public Integer execute(Long did,
            Date startDate,
            Date endDate,
            String certificateProfileName,
            String Policy,
            Integer version,
            String status,
            String notes) throws SKFEException {
        skfeLogger.entering(skfeConstants.SKFE_LOGGER, classname, "execute");

        //Base64 Policy
        String policyBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(Policy.getBytes());

        Long sid = applianceCommon.getServerId();
        Integer pid = seqgenejb.nextPolicyID();
        FidoPoliciesPK fidopolicyPK = new FidoPoliciesPK();
        FidoPolicies fidopolicy = new FidoPolicies();
        fidopolicyPK.setDid(did.shortValue());
        fidopolicyPK.setPid(pid.shortValue());
        fidopolicyPK.setSid(sid.shortValue());
        fidopolicy.setFidoPoliciesPK(fidopolicyPK);
        fidopolicy.setStartDate(startDate);
        fidopolicy.setEndDate(endDate);
        fidopolicy.setCertificateProfileName(certificateProfileName);
        fidopolicy.setPolicy(policyBase64);
        fidopolicy.setVersion(version);
        fidopolicy.setStatus(status);
        fidopolicy.setNotes(notes);
        fidopolicy.setCreateDate(new Date());
        fidopolicy.setModifyDate(null);

        //TODO add signing code(?)
        try {
            em.persist(fidopolicy);
            em.flush();
            em.clear();
        } catch (PersistenceException ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDOJPA-ERR-2006", ex.getLocalizedMessage());
            throw new SKFEException(skfeCommon.getMessageProperty("FIDOJPA-ERR-2006") + "Check server logs for details.");
        }

        //Replicate
        String primarykey = sid + "-" + did + "-" + pid;
        try {
            if (applianceCommon.replicate()) {

                String response = replObj.execute(applianceConstants.ENTITY_TYPE_FIDO_POLICIES, applianceConstants.REPLICATION_OPERATION_ADD, primarykey, fidopolicy);
                if (response != null) {
                    throw new SKFEException(skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + response);
                }
            }
        } catch (Exception e) {
            sc.setRollbackOnly();
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER, classname, "execute");
            throw new RuntimeException(e.getLocalizedMessage());
        }

        //add to local map
        String fpMapkey = sid + "-" + did + "-" + pid;
        FidoPolicyObject fidoPolicyObject = FidoPolicyObject.parse(
                policyBase64,
                version,
                did,
                sid,
                pid.longValue(),
                startDate,
                endDate);
        MDSClient mds = null;
        if (fidoPolicyObject.getMdsOptions() != null) {
            mds = new MDS(fidoPolicyObject.getMdsOptions().getEndpoints());
        }
        skceMaps.getMapObj().put(skfeConstants.MAP_FIDO_POLICIES, fpMapkey, new FidoPolicyMDSObject(fidoPolicyObject, mds));

        skfeLogger.exiting(skfeConstants.SKFE_LOGGER, classname, "execute");

        return pid;
    }
}
