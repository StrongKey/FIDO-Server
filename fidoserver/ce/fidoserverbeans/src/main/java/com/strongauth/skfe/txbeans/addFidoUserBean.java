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
import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.crypto.interfaces.initCryptoModule;
import com.strongauth.crypto.utility.CryptoException;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.entitybeans.FidoUsers;
import com.strongauth.skfe.entitybeans.FidoUsersPK;
import com.strongauth.skfe.messaging.replicateSKFEObjectBeanLocal;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeLogger;
import java.io.StringWriter;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


@Stateless
public class addFidoUserBean implements addFidoUserBeanLocal {

     /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();
    
    @Resource private SessionContext            sc;
    @PersistenceContext private EntityManager   em;  
    
    @EJB
    getFidoUserLocal getfidoUserbean;
    @EJB
    replicateSKFEObjectBeanLocal replObj;
    @EJB
    getDomainsBeanLocal getdomain;
    
    @Override
    public String execute(Long did, String username) throws SKFEException {
        
        skfeLogger.entering(skfeConstants.SKFE_LOGGER,classname, "execute");
        
        //Json return object
        JsonObject retObj;
        
        //Declaring variables
        Boolean status = true;
        String errmsg;
        
        //Input Validation
         if(did == null){
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "did");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " did";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        } else if (did < 1) {
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "did");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " did";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        } 
         skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "did=" + did);
         
        //USERNAME
        if (username == null) {
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "USERNAME");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " USERNAME";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        } else if (username.trim().length() == 0) {
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1003", "USERNAME");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1003") + " USERNAME";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        } else if (username.trim().length() > applianceCommon.getMaxLenProperty("appliance.cfg.maxlen.256charstring")) {
            status = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "USERNAME");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " USERNAME";
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "USERNAME=" + username);
        
        FidoUsers fidoUser = null;
        try {
            fidoUser = getfidoUserbean.GetByUsername(did,username);
        } catch (SKFEException ex) {
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-0001")+ ex.getLocalizedMessage());
        }
        if(fidoUser != null){
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-2004","");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-2004");
            status = false;
            retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
            return retObj.toString();
        }
        
        Long sid = applianceCommon.getServerId();
        String primarykey = sid+"-"+did+"-"+username;
        fidoUser = new FidoUsers();
        FidoUsersPK fpk = new FidoUsersPK(sid.shortValue(), did.shortValue(), username);
        fidoUser.setFidoUsersPK(fpk);
        fidoUser.setTwoStepVerification(String.valueOf(false));
        fidoUser.setFidoKeysEnabled(String.valueOf(false));
        fidoUser.setPrimaryEmail("");
        fidoUser.setPrimaryPhoneNumber("");
        fidoUser.setRegisteredEmails("");
        fidoUser.setRegisteredPhoneNumbers("");
        fidoUser.setTwoStepTarget(null);
        fidoUser.setUserdn("");
        fidoUser.setStatus("Active");
        fidoUser.setId(primarykey);
        
        if (skfeCommon.getConfigurationProperty("skfe.cfg.property.db.signature.rowlevel.add")
                .equalsIgnoreCase("true")) {
            
            String standalone = skfeCommon.getConfigurationProperty("skfe.cfg.property.standalone.fidoengine");
            String signingKeystorePassword = "";
            if (standalone.equalsIgnoreCase("true")) {
                signingKeystorePassword = skfeCommon.getConfigurationProperty("skfe.cfg.property.standalone.signingkeystore.password");
            }
            //  convert the java object into xml to get it signed.
            StringWriter writer = new StringWriter();
            JAXBContext jaxbContext;
            Marshaller marshaller;
            try {
                jaxbContext = JAXBContext.newInstance(FidoUsers.class);
                marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(fidoUser, writer);
            } catch (javax.xml.bind.JAXBException ex) {
                
            }
            String efsXml = writer.toString();
            if (efsXml == null) {
                status = false;
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "FK Xml");
                errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " FK Xml";
                retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
                return retObj.toString();
            }
            Domains d = getdomain.byDid(did);
            //  get signature for the xml    
            String signedxml = null;
            try {
                signedxml = initCryptoModule.getCryptoModule().signDBRow(did.toString(), d.getSkceSigningdn(), efsXml, Boolean.valueOf(standalone), signingKeystorePassword);
            } catch (CryptoException ex) {
                Logger.getLogger(addFidoUserBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (signedxml == null) {
                status = false;
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "SignedXML");
                errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " SignedXML";
                retObj = Json.createObjectBuilder().add("status", status).add("message", errmsg).build();
                return retObj.toString();
            } else {
                fidoUser.setSignature(signedxml);
            }
        }
        
        em.persist(fidoUser);
        em.flush();
        em.clear();
        
        try {
            if(applianceCommon.replicate()){
                String response = replObj.execute(applianceConstants.ENTITY_TYPE_FIDO_USERS, applianceConstants.REPLICATION_OPERATION_ADD, primarykey, fidoUser);
                if(response != null){
                    return response;
                }
            }
        } catch (Exception e) {
            sc.setRollbackOnly();
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "execute");
            throw new RuntimeException(e.getLocalizedMessage());
        }
        
        //return a successful json string
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.INFO, classname, "execute", "FIDOJPA-MSG-2008","");
        retObj = Json.createObjectBuilder().add("status", status).add("message", skfeCommon.getMessageProperty("FIDOJPA-MSG-2008")).build();
        skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "execute");
        return retObj.toString();
    }
}
