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
import com.strongauth.skce.pojos.FidoKeysInfo;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.utilities.skceMaps;
import com.strongauth.skfe.entitybeans.FidoKeys;
import com.strongauth.skfe.messaging.replicateSKFEObjectBeanLocal;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeLogger;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class updateFidoKeysStatus implements updateFidoKeysStatusLocal, updateFidoKeysStatusRemote {

    /**
     ** This class's name - used for logging & not persisted
     *
     */
    @SuppressWarnings("FieldMayBeFinal")
    private String classname = this.getClass().getName();

    /**
     * EJB's used by the Bean
     */
    @EJB
    getFidoKeysLocal getkeysejb;
    @EJB
    replicateSKFEObjectBeanLocal replObj;

    @EJB
    getDomainsBeanLocal getdomain;

    /**
     * Persistence context for derby
     */
    @Resource
    private SessionContext sc;
    @PersistenceContext
    private EntityManager em;

    /**
     *
     * @param sid
     * @param did
     * @param fkid - Unique identifier for the key in the DB
     * @param modify_location - Location where the key was last used.
     * @param status - Updated status of the Key
     * @return - Returns a JSON string containing the status and the
     * error/success message
     */
    @Override
    public String execute(Short sid, Long did, String username, Long fkid, String modify_location, String status) {
        //Declaring variables
        Boolean outputstatus = true;
        String errmsg;
        JsonObject retObj;

        //Input Validation
        //sid
        //NULL Argument
        if (sid == null) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "sid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " sid";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }

        // fkid is negative, zero or larger than max value (becomes negative)
        if (sid < 1) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "sid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " sid";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "sid=" + sid);

        //did
        //NULL Argument
        if (did == null) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "did");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " did";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }

        // fkid is negative, zero or larger than max value (becomes negative)
        if (did < 1) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "did");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " did";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "did=" + did);

        //fkid
        //NULL Argument
        if (fkid == null) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "fkid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " fkid";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }

        // fkid is negative, zero or larger than max value (becomes negative)
        if (fkid < 1) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "fkid");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " fkid";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "fkid=" + fkid);

        //USER modify_location
        if (modify_location == null) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "MODIFY LOCATION");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " MODIFY LOCATION";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        } else if (modify_location.trim().length() == 0) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1003", "MODIFY LOCATION");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1003") + " MODIFY LOCATION";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        } else if (modify_location.trim().length() > applianceCommon.getMaxLenProperty("appliance.cfg.maxlen.256charstring")) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "MODIFY LOCATION");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " MODIFY LOCATION";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "MODIFY LOCATION=" + modify_location);

        //key status
        if (status == null) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "STATUS");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " STATUS";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        } else if (status.trim().length() == 0) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1003", "STATUS");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1003") + " STATUS";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }

        if (status.trim().equalsIgnoreCase(applianceConstants.ACTIVE_STATUS)) {
        } else if (status.trim().equalsIgnoreCase(applianceConstants.INACTIVE_STATUS)) {
        } else {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1002", "STATUS");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1002") + " STATUS";
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDOJPA-MSG-2001", "STATUS=" + status);

        //  Verify if the fkid exists.
        FidoKeys rk = null;
        try {
            FidoKeysInfo fkinfo = (FidoKeysInfo) skceMaps.getMapObj().get(skfeConstants.MAP_FIDO_KEYS, sid + "-" + did + "-" + username + "-" + fkid);
            if (fkinfo != null) {
                rk = fkinfo.getFk();
            }
            if (rk == null) {
                rk = getkeysejb.getByfkid(sid, did, username, fkid);
            }
        } catch (SKFEException ex) {
            Logger.getLogger(updateFidoKeysStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (rk == null) {
            outputstatus = false;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-2002", "");
            errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-2002");
            retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
            return retObj.toString();
        }

        //modify the DB
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String moddate = df.format(new Date());
        Date modifyDateFormat = null;
        try {
            modifyDateFormat = df
                    .parse(moddate);
        } catch (ParseException e) {
        }
        String primarykey = sid + "-" + did + "-" + rk.getFidoKeysPK().getUsername() + "-" + fkid;
        rk.setModifyLocation(modify_location);
        rk.setModifyDate(modifyDateFormat);
        rk.setStatus(status);
        rk.setId(primarykey);

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
                jaxbContext = JAXBContext.newInstance(FidoKeys.class);
                marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(rk, writer);
            } catch (javax.xml.bind.JAXBException ex) {
                Logger.getLogger(updateFidoKeysStatus.class.getName()).log(Level.SEVERE, null, ex);
            }
            String efsXml = writer.toString();
            if (efsXml == null) {
                outputstatus = false;
                skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "FK Xml");
                errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " FK Xml";
                retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
                return retObj.toString();
            }

            //  get signature for the xml    
            Domains d = getdomain.byDid(did);

            String signedxml = null;
            try {
                signedxml = initCryptoModule.getCryptoModule().signDBRow(did.toString(), d.getSkceSigningdn(), efsXml, Boolean.valueOf(standalone), signingKeystorePassword);
            } catch (CryptoException ex) {
                Logger.getLogger(updateFidoKeysStatus.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (signedxml == null) {
                outputstatus = false;
                skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDOJPA-ERR-1001", "SignedXML");
                errmsg = skfeCommon.getMessageProperty("FIDOJPA-ERR-1001") + " SignedXML";
                retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", errmsg).build();
                return retObj.toString();
            } else {
                String xmlsignature = new String(signedxml);
                rk.setSignature(xmlsignature);
            }
        }

        em.merge(rk);
        em.flush();

        try {
            if (applianceCommon.replicate()) {
                String response = replObj.execute(applianceConstants.ENTITY_TYPE_FIDO_KEYS, applianceConstants.REPLICATION_OPERATION_UPDATE, primarykey, rk);
                if(response != null){
                    return response;
                }
            }
        } catch (Exception e) {
            sc.setRollbackOnly();
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER, classname, "execute");
            throw new RuntimeException(e.getLocalizedMessage());
        }

        //return a success message
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDOJPA-MSG-2004", "");
        retObj = Json.createObjectBuilder().add("status", outputstatus).add("message", skfeCommon.getMessageProperty("FIDOJPA-MSG-2004")).build();
        skfeLogger.exiting(skfeConstants.SKFE_LOGGER, classname, "execute");
        return retObj.toString();
    }

    @Override
    public String remoteExecute(Short sid, Long did, String username, Long fkid, String modify_location, String status) {
        return execute(sid, did, username, fkid, modify_location, status);
    }

}
