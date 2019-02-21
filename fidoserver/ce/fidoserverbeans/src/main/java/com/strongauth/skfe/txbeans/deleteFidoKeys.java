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

import com.strongauth.skfe.messaging.replicateSKFEObjectBeanLocal;
import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skfe.entitybeans.FidoKeys;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class deleteFidoKeys implements deleteFidoKeysLocal {
 /**
     ** This class's name - used for logging & not persisted
     **/
    @SuppressWarnings("FieldMayBeFinal")
    private String classname = this.getClass().getName();
    
    
    /**
     * EJB's used by the Bean
     */
    @EJB
    getFidoKeysLocal getregkeysejb;
    @EJB
    replicateSKFEObjectBeanLocal replObj;
    
    /**
     * Persistence context for derby
     */
    @Resource private SessionContext            sc;
    @PersistenceContext private EntityManager   em;  
    
    /**
     * 
     * @param sid
     * @param did
     * @param fkid  - The key identifier for the registered key to be deleted
     *                from the derby DB
     * @return      - Returns a JSON string containing the status and the 
     *                error/success message
     */
    @Override
    public String execute(Short sid, Long did, String username,  Long fkid) {
        skfeLogger.entering(skfeConstants.SKFE_LOGGER,classname, "execute");
        
        //Declating variables
        Boolean status = true;
        String errmsg ;
        JsonObject retObj;
        
        //Input Validation

        //fkid
        //NULL Argument
        
        if(sid == null){
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "sid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " sid";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        
        if(did == null){
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "did");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " did";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        
        if(fkid == null){
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "fkid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " fkid";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        
        // fkid is negative, zero or larger than max value (becomes negative)
        if(fkid < 1){
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "fkid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " fkid";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "fkid=" + fkid);
        
        //  Verify if the fkid exists.
        FidoKeys rk = null;
        try {
            rk = getregkeysejb.getByfkid(sid, did, username, fkid);
        } catch (SKFEException ex) {
            Logger.getLogger(deleteFidoKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(rk == null){
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-2002","");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-2002");
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        String primarykey = sid+"-"+did+"-"+rk.getFidoKeysPK().getUsername()+"-"+fkid;
        //delete the key
        em.remove(rk);
        em.flush();

        try {
            if(applianceCommon.replicate()){
                String response = replObj.execute(applianceConstants.ENTITY_TYPE_FIDO_KEYS, applianceConstants.REPLICATION_OPERATION_DELETE, primarykey, rk);
                if(response != null){
                    return response;
                }
            }
        } catch (Exception e) {
            sc.setRollbackOnly();
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "execute");
            throw new RuntimeException(e.getLocalizedMessage());
        }

        //return a success message
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", "FIDOJPA-MSG-2003","");
        retObj = Json.createObjectBuilder().add("status", status).add("message", skfeCommon.getMessageProperty("FIDOJPA-MSG-2003")).build();
        skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "execute");
        return retObj.toString();
    }
}
