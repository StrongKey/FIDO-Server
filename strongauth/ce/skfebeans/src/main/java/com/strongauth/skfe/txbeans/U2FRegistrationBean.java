/**
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
 * Copyright (c) 2001-2015 StrongAuth, Inc.
 *
 * $Date: 2018-06-18 14:47:15 -0400 (Mon, 18 Jun 2018) $
 * $Revision: 50 $
 * $Author: pmarathe $
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/txbeans/U2FRegistrationBean.java $
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

import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.skce.pojos.UserSessionInfo;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.utilities.skceMaps;
import com.strongauth.skfe.core.U2FRegistrationResponse;
import com.strongauth.skfe.utilities.FEreturn;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeLogger;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

@Stateless
public class U2FRegistrationBean implements U2FRegistrationBeanLocal, U2FRegistrationBeanRemote {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    @EJB
    addFidoKeysLocal addkeybean;
    @EJB
    originVerfierBeanLocal originverifierbean;
    @EJB
    u2fRegisterBeanLocal u2fregisterbean;
    @EJB
    updateFidoUserBeanLocal updateldapbean;

    @Override
    public String execute(Long did, String registrationresponse, String registrationmetadata, String protocol) {
        String wsresponse="", logs = "", errmsg = "";
        //  check for needed fields in registrationresponse and metadata
        //  fetch needed fields from registrationresponse
        String browserdata = (String) applianceCommon.getJsonValue(registrationresponse,
                skfeConstants.JSON_KEY_CLIENTDATA, "String");
        if (browserdata == null || browserdata.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'clientData'");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                    + " Missing 'clientData'");
        }
        //parse browserdata

        try {
            String browserdataJson = new String(org.apache.commons.codec.binary.Base64.decodeBase64(browserdata), "UTF-8");
            JsonReader jsonReader = Json.createReader(new StringReader(browserdataJson));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();

            String bdreqtype = jsonObject.getString(skfeConstants.JSON_KEY_REQUESTTYPE);
            String bdnonce = jsonObject.getString(skfeConstants.JSON_KEY_NONCE);
            String bdorigin = jsonObject.getString(skfeConstants.JSON_KEY_SERVERORIGIN);
            if (bdreqtype == null || bdnonce == null || bdorigin == null) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Missing 'registrationData'");
                return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                        + " Missing 'registrationData'");
            }
        } catch (Exception ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                    skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'clientDATA'");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                    + " Invalid 'clientDATA'");
        }
        ////
        String regdata = (String) applianceCommon.getJsonValue(registrationresponse,
                skfeConstants.JSON_KEY_REGSITRATIONDATA, "String");
        if (regdata == null || regdata.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'registrationData'");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                    + " Missing 'registrationData'");
        }

        //  fetch version and modifylocation from metadata
        String version = (String) applianceCommon.getJsonValue(registrationmetadata,
                skfeConstants.FIDO_METADATA_KEY_VERSION, "String");
        if (version == null || version.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0018", " Missing metadata - version");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0018")
                    + " Missing metadata - version");
        }
        String createloc = (String) applianceCommon.getJsonValue(registrationmetadata,
                skfeConstants.FIDO_METADATA_KEY_CREATE_LOC, "String");
        if (createloc == null || createloc.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0018", " Missing metadata - createlocation");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0018")
                    + " Missing metadata - createlocation");
        }
        String username_received = (String) applianceCommon.getJsonValue(registrationmetadata,
                skfeConstants.FIDO_METADATA_KEY_USERNAME, "String");
        if (username_received == null || username_received.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0018", " Missing metadata - username");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0018")
                    + " Missing metadata - username");
        }

        String session_username;

        //  5. Validate user session
        //  Look for the sessionid in the sessionmap and retrieve the username
        String ch = skfeCommon.getChallengefromBrowserdata(browserdata);
        String chDigest;
        try {
            chDigest = skfeCommon.getDigest(ch, "SHA-256");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | UnsupportedEncodingException ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0001", " Error generating hash");
            return skfeCommon.buildRegisterResponse("", "",
                    skfeCommon.getMessageProperty("FIDO-ERR-0001") + " Error generating hash");
        }
        UserSessionInfo user = (UserSessionInfo) skceMaps.getMapObj().get(skfeConstants.MAP_USER_SESSION_INFO, chDigest);
        if (user == null) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0006", "");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0006"));
        } else {
            session_username = user.getUsername();
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0022", " username=" + session_username);
        }

        //verify that the call is for the right user
        if (!session_username.equalsIgnoreCase(username_received)) {
            //throw erro saying wrong username sent
            return skfeCommon.buildRegisterResponse("", "",
                    skfeCommon.getMessageProperty("FIDO-ERR-0037"));
        }

        //  6. Verify appid
        String appid = user.getAppid();
        String origin = skfeCommon.getOriginfromBrowserdata(browserdata);
        if (!originverifierbean.execute(appid, origin)) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0032", "");
            return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0032")
                    + " : " + appid + "-" + origin);
        }

        //  7.  Do actual registration; handover the job to an ejb
        try {
            FEreturn ret = u2fregisterbean.execute(did.toString(), protocol, registrationresponse);
            if (ret != null) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0046", ret.toString());
                U2FRegistrationResponse regResponse = (U2FRegistrationResponse) ret.getResponse();
                logs = ret.getLogmsg();

                if (regResponse != null) {
                    //  Fetch the needed information from the reg wsresponse output.
                    String keyhandle = regResponse.getKeyhandle();
                    String publickey = regResponse.getUserpublickey();

                    /**
                     * TO BE DONE - Attestation cert validation
                     */
                    //  Get attestation cert from the local truststore
                    //  Do cert validation
                    //  If everything is found valid,
                    //  8.  Persist key info to the database
                    addkeybean.execute(did, null, session_username, keyhandle, publickey, appid, 
                            (short) skfeConstants.FIDO_TRANSPORT_USB, null, null, null, 0,
                            skfeConstants.FIDO_PROTOCOL_VERSION_U2F_V2, skfeConstants.FIDO_PROTOCOL_U2F,
                            null, null, null, createloc);
                    skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0024", "");

                    //  Update the "FIDOKeysEnabled" attribute of the user to 'true'
                    try {
                        String result = updateldapbean.execute(did, session_username,
                                skfeConstants.LDAP_ATTR_KEY_FIDOENABLED, "true", false);
                        JsonObject jo;
                        try (JsonReader jr = Json.createReader(new StringReader(result))) {
                            jo = jr.readObject();
                        }
                        Boolean status = jo.getBoolean(skfeConstants.JSON_KEY_FIDOJPA_RETURN_STATUS);
                        if (status) {
                            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0029", "true");
                        } else {
                            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0024", "true");
                        }
                    } catch (SKFEException ex) {
                        //  Just throw an err msg and proceed.
                        skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0024", "true");
                    }

                    //  Remove the sessionid from the sessionmap
                    ch = skfeCommon.getChallengefromBrowserdata(regResponse.getBrowserdata());
                    String challhash = null;
                    try {
                        challhash = skfeCommon.getDigest(ch, "SHA-256");
                    } catch (NoSuchAlgorithmException | NoSuchProviderException | UnsupportedEncodingException ex) {
                        skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0001", " Error generating hash");
                        throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-0001") + " Error generating hash");
                    }
                    skceMaps.getMapObj().remove(skfeConstants.MAP_USER_SESSION_INFO, challhash);
                    skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0023", user.toString());

                    wsresponse = "Successfully processed registration response";
                } else {
                    errmsg = "Failed to process registration response";
                }
            }
        } catch (SKFEException ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0001", ex.getLocalizedMessage());
            return skfeCommon.buildRegisterResponse("", "",
                    skfeCommon.getMessageProperty("FIDO-ERR-0001") + ex.getLocalizedMessage());
        } catch (Exception ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0001", ex.getLocalizedMessage());
            return skfeCommon.buildRegisterResponse("", "",
                    skfeCommon.getMessageProperty("FIDO-ERR-0001") + ex.getLocalizedMessage());
        }
        String responseJSON = skfeCommon.buildRegisterResponse(wsresponse, logs, errmsg);
        return responseJSON;
    }

    @Override
    public String remoteExecute(Long did, String registrationresponse, String registrationmetadata, String protocol) {
        return execute(did, registrationresponse, registrationmetadata, protocol);
    }
}
