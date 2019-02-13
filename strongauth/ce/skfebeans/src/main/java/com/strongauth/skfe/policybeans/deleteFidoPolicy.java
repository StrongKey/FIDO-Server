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
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.utilities.skceMaps;
import com.strongauth.skfe.entitybeans.FidoPolicies;
import com.strongauth.skfe.messaging.replicateSKFEObjectBeanLocal;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class deleteFidoPolicy implements deleteFidoPolicyLocal {

    @EJB
    getFidoPolicyLocal getpolicybean;
    @EJB
    replicateSKFEObjectBeanLocal replObj;

    /**
     * Persistence context for derby
     */
    @Resource
    private SessionContext sc;
    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("FieldMayBeFinal")
    private String classname = this.getClass().getName();

    @Override
    public void execute(Long did, Long sid, Long pid) throws SKFEException {
        FidoPolicies policy = getpolicybean.getbyPK(did, sid, pid);

        if (policy == null) {
            throw new SKFEException(skfeCommon.getMessageProperty("FIDOJPA-ERR-2005"));
        }

        em.remove(policy);
        em.flush();

        //Replicate
        String primarykey = sid + "-" + did + "-" + pid;
        try {
            if (applianceCommon.replicate()) {

                String response = replObj.execute(applianceConstants.ENTITY_TYPE_FIDO_POLICIES, applianceConstants.REPLICATION_OPERATION_DELETE, primarykey, policy);
                if (response != null) {
                    throw new SKFEException(skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + response);

                }
            }
        } catch (Exception e) {
            sc.setRollbackOnly();
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER, classname, "execute");
            throw new RuntimeException(e.getLocalizedMessage());
        }

        //remove from local map
        String fpMapkey = sid + "-" + did + "-" + pid;
        skceMaps.getMapObj().remove(skfeConstants.MAP_FIDO_POLICIES, fpMapkey);
    }
}
