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

import com.google.common.primitives.Bytes;
import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.crypto.utility.cryptoCommon;
import com.strongauth.skfe.entitybeans.FidoKeys;
import com.strongauth.skce.pojos.FidoKeysInfo;
import com.strongauth.skce.pojos.UserSessionInfo;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.utilities.skceMaps;
import com.strongauth.skfe.fido2.FIDO2AuthenticatorData;
import com.strongauth.skfe.pojos.RegistrationSettings;
import com.strongauth.skfe.policybeans.verifyFido2AuthenticationPolicyLocal;
import java.io.StringReader;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.bouncycastle.util.encoders.Base64;

@Stateless
public class FIDO2AuthenticateBean implements FIDO2AuthenticateBeanLocal, FIDO2AuthenticateBeanRemote {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    @EJB
    getFidoKeysLocal getkeybean;
    @EJB
    updateFidoKeysLocal updatekeybean;
    @EJB
    verifyFido2AuthenticationPolicyLocal verifyPolicyBean;

    @Override
    public String execute(Long did, String authresponse, String authmetadata, String method) {

        String wsresponse = "", logs = "", errmsg = "";
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDO-MSG-2001", "AuthResponse : " + authresponse);
        String id = (String) applianceCommon.getJsonValue(authresponse,
                skfeConstants.JSON_KEY_ID, "String");
        String rawId = (String) applianceCommon.getJsonValue(authresponse,
                skfeConstants.JSON_KEY_RAW_ID, "String");
        String credential_type = (String) applianceCommon.getJsonValue(authresponse,
                skfeConstants.JSON_KEY_REQUEST_TYPE, "String");
        String responseObject = ((JsonObject) applianceCommon.getJsonValue(authresponse,
                skfeConstants.JSON_KEY_SERVLET_INPUT_RESPONSE, "JsonObject")).toString();

        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDO-MSG-2001", "Extracted AuthResponse : " + "\nid : " + id
                + "\nrawId : " + rawId + "\ncredential_type : " + credential_type + "\nresponseObject : " + responseObject);

        if (id == null || id.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'id'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                    + " Missing 'id'");
        }
        String b64urlsafeId;
        try {
            b64urlsafeId = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(java.util.Base64.getUrlDecoder().decode(id));
        } catch (Exception ex) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                    skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'id'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                    + " Invalid 'id'");
        }
        skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                "b64urlid = " + b64urlsafeId);
        if (!id.equals(b64urlsafeId)) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                    skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'id'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                    + " Invalid 'id'");
        }

        if (rawId == null || rawId.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'rawId'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                    + " Missing 'rawId'");
        }

        String b64urlsaferawId = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(java.util.Base64.getUrlDecoder().decode(rawId));
        if (!rawId.equals(b64urlsaferawId)) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                    skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'rawId'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                    + " Invalid rawIdid'");
        }

        if (credential_type == null || credential_type.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'credential_type'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                    + " Missing 'credential_type'");
        }

        if (!credential_type.equalsIgnoreCase("public-key")) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                    skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'credential_type'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                    + " Invalid 'credential_type'");
        }

        String browserdata = (String) applianceCommon.getJsonValue(responseObject,
                skfeConstants.JSON_KEY_CLIENTDATAJSON, "String");
        if (browserdata == null || browserdata.isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'clientData'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                    + " Missing 'clientData'");
        }
        //parse browserdata
        skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDO-MSG-2001", "browserdata : " + browserdata);

        try {
            String browserdataJson = new String(java.util.Base64.getDecoder().decode(browserdata), "UTF-8");
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDO-MSG-2001", "browserdataJson : " + browserdataJson);
            String bdreqtype = (String) applianceCommon.getJsonValue(browserdataJson, skfeConstants.JSON_KEY_REQUEST_TYPE, "String"); //jsonObject.getString(skfeConstants.JSON_KEY_REQUEST_TYPE);
            String bdnonce = (String) applianceCommon.getJsonValue(browserdataJson, skfeConstants.JSON_KEY_NONCE, "String"); //jsonObject.getString(skfeConstants.JSON_KEY_NONCE);
            String bdorigin = (String) applianceCommon.getJsonValue(browserdataJson, skfeConstants.JSON_KEY_SERVERORIGIN, "String"); //jsonObject.getString(skfeConstants.JSON_KEY_SERVERORIGIN);
            String bdhashAlgo = (String) applianceCommon.getJsonValue(browserdataJson, skfeConstants.JSON_KEY_HASH_ALGORITHM, "String"); // jsonObject.getString(skfeConstants.JSON_KEY_HASH_ALGORITHM);

            if (bdreqtype == null || bdnonce == null || bdorigin == null) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Missing 'authenticationnData'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                        + " Missing 'authenticationnData'");
            }
            if (!bdreqtype.equalsIgnoreCase("webauthn.get")) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'request type'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                        + " Invalid 'request type'");
            }

            if (bdorigin.isEmpty()) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'bdorigin'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                        + " Invalid 'bdorigin'");
            }
            
            
            String origin = (String) applianceCommon.getJsonValue(authmetadata,
                    skfeConstants.FIDO_METADATA_KEY_ORIGIN, "String");
            
            if(origin == null){
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE,
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'origin'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                        + " Invalid 'origin'");
            }
            
            URI bdoriginURI = new URI(bdorigin);
            URI originURI = new URI(origin);
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    "RPID - BDORIGIN : " + originURI + " - " + bdoriginURI);
            
            if (!bdoriginURI.equals(originURI)) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'origin'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                        + " Invalid 'origin'");
            }

            
            String authenticatorObject = (String) applianceCommon.getJsonValue(responseObject,
                    skfeConstants.JSON_KEY_AUTHENTICATORDATA, "String");
            if (authenticatorObject == null || authenticatorObject.isEmpty()) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'authenticatorObject'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                        + " Missing 'authenticatorObject'");
            }
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDO-MSG-2001", "authenticatorObject : " + authenticatorObject);

            StringReader stringreader = new StringReader(responseObject);
            JsonReader jsonreader = Json.createReader(stringreader);
            JsonObject json = jsonreader.readObject();

            if (json.containsKey(skfeConstants.JSON_KEY_USERHANDLE) && !json.isNull(skfeConstants.JSON_KEY_USERHANDLE)) {
                String userHandle = (String) applianceCommon.getJsonValue(responseObject,
                        skfeConstants.JSON_KEY_USERHANDLE, "String");
                if (userHandle == null) { //|| userHandle.isEmpty()
                    skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'userHandle'");
                    return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                            + " Missing 'userHandle'");
                }
            }

            String signature = (String) applianceCommon.getJsonValue(responseObject,
                    skfeConstants.JSON_KEY_SIGNATURE, "String");
            if (signature == null || signature.isEmpty()) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Missing 'signature'");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                        + " Missing 'signature'");
            }
            skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "FIDO-MSG-2001", "Signature : " + signature);

            byte[] authData = java.util.Base64.getUrlDecoder().decode(authenticatorObject);
            FIDO2AuthenticatorData authenticatorData = new FIDO2AuthenticatorData();
            authenticatorData.decodeAuthData(authData);

            String rpidServletExtracted = originURI.getHost();
            if (!Base64.toBase64String(authenticatorData.getRpIdHash()).equals(Base64.toBase64String(skfeCommon.getDigestBytes(rpidServletExtracted, "SHA256")))) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-2001", " RPID Hash invalid");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-2001")
                        + " RPID Hash invalid'");
            }
            byte[] signedBytes = Bytes.concat(authData, skfeCommon.getDigestBytes(java.util.Base64.getDecoder().decode(browserdata), "SHA-256"));

            String modifyloc = (String) applianceCommon.getJsonValue(authmetadata,
                    skfeConstants.FIDO_METADATA_KEY_MODIFY_LOC, "String");
            if (modifyloc == null || modifyloc.isEmpty()) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0019", " Missing metadata - modifylocation");
                return skfeCommon.buildAuthenticateResponse("", "",
                        skfeCommon.getMessageProperty("FIDO-ERR-0019") + " Missing metadata - modifylocation");
            }

            String username_received = (String) applianceCommon.getJsonValue(authmetadata,
                    skfeConstants.FIDO_METADATA_KEY_USERNAME, "String");
            if (username_received == null || username_received.isEmpty()) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0019", " Missing metadata - username");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0019")
                        + " Missing metadata - username");
            }
            
            //TODO token binding verification (Currently only does basic formatting checks)
            try {
                JsonObject clientJson = skfeCommon.getJsonObjectFromString(browserdataJson);
                JsonObject tokenBinding = clientJson.getJsonObject(skfeConstants.JSON_KEY_TOKENBINDING);
                if (tokenBinding != null) {
                    String tokenBindingStatus = tokenBinding.getString("status", null);
                    Set<String> validTokenBindingStatuses = new HashSet(Arrays.asList("present", "supported", "not-supported"));
                    if (tokenBindingStatus == null || tokenBindingStatus.isEmpty()
                            || !validTokenBindingStatuses.contains(tokenBindingStatus)) {
                        skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0005", " Invalid 'tokenBinding'");
                        return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                                + " Invalid 'tokenBinding'");
                    }
                }
            } catch (ClassCastException ex) {
                return skfeCommon.buildRegisterResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0005")
                        + " Missing 'tokenBinding'");
            }

            long regkeyid;
            short serverid;
            String username = "";
            String KHhash;
            String challenge = null;
            String appid_Received = "";
            //  calculate the hash of keyhandle received
            String kh = id;
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    "kh : " + kh);
            KHhash = skfeCommon.getDigest(kh, "SHA-256");

            //  Look for the sessionid in the sessionmap and retrieve the username
            UserSessionInfo user = (UserSessionInfo) skceMaps.getMapObj().get(skfeConstants.MAP_USER_SESSION_INFO, KHhash);
            if (user == null) {
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0006", "");
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0006"));
            } else if (user.getSessiontype().equalsIgnoreCase(skfeConstants.FIDO_USERSESSION_AUTH)) {
                username = user.getUsername();
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0022", " username=" + username);

                appid_Received = user.getAppid();
                challenge = user.getNonce();
                skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0042", " appid=" + appid_Received);
            }
            
            // Verify username received in metadata matches the username for the received challenge
            if (!username_received.equalsIgnoreCase(username)) {
                return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-0037"));
            }

            //  3. Do processing
            //  fetch the user public key from the session map.
            String userpublickey = user.getUserPublicKey();
            regkeyid = user.getFkid();
            serverid = user.getSkid();

            FidoKeys key = null;
            FidoKeysInfo fkinfo = (FidoKeysInfo) skceMaps.getMapObj().get(skfeConstants.MAP_FIDO_KEYS, serverid + "-" + did + "-" + username + "-" + regkeyid);
            if (fkinfo != null) {
                key = fkinfo.getFk();
            }
            if (key == null) {
                key = getkeybean.getByfkid(serverid, did, username, regkeyid);
            }
            if (key != null) {
                RegistrationSettings rs = RegistrationSettings.parse(key.getRegistrationSettings(), key.getRegistrationSettingsVersion());
                String signingKeyType = getKeyTypeFromRegSettings(rs);
                byte[] publickeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(userpublickey);
                Boolean isSignatureValid;
                KeyFactory kf = KeyFactory.getInstance(signingKeyType, "BCFIPS");
                X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publickeyBytes);
                PublicKey pub = kf.generatePublic(pubKeySpec);
                isSignatureValid = cryptoCommon.verifySignature(
                        org.apache.commons.codec.binary.Base64.decodeBase64(signature),
                        pub, 
                        signedBytes,
                        skfeCommon.getAlgFromIANACOSEAlg(rs.getAlg()));

                if (!isSignatureValid) {
                    skfeLogger.logp(skfeConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "FIDO-MSG-2001", "Authentication Signature verification : " + isSignatureValid);
                    return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-2001")
                            + "Authentication Signature verification : " + isSignatureValid);
                }

                //Check authentication against policy
                verifyPolicyBean.execute(user, did, json, authenticatorData, key);
                
                //  update the sign counter value in the database with the new counter value.
                String jparesult = updatekeybean.execute(serverid, did, username, regkeyid, authenticatorData.getCounterValueAsInt(), modifyloc);
                JsonObject jo;
                try (JsonReader jr = Json.createReader(new StringReader(jparesult))) {
                    jo = jr.readObject();
                }
                Boolean status = jo.getBoolean(skfeConstants.JSON_KEY_FIDOJPA_RETURN_STATUS);
                if (status) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.FINE, "FIDO-MSG-0027", "");
                } else {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0026", " new value=" + authenticatorData.getCounterValueAsInt());
                }
                
                //  Remove the sessionid from the sessionmap
                skceMaps.getMapObj().remove(skfeConstants.MAP_USER_SESSION_INFO, KHhash);
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-0023", " username=" + username);

                switch (method) {
                    case "authentication":
                        wsresponse = "Successfully processed sign response";
                        break;
                    case "authorization":
                        wsresponse = "Successfully processed authorization response";
                        break;
                }
            }
            else{
                throw new IllegalStateException("Unable to retrieve FIDO key from database");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE,
                    skfeCommon.getMessageProperty("FIDO-ERR-5011"), " Invalid 'authenticatorDATA'");
            return skfeCommon.buildAuthenticateResponse("", "", skfeCommon.getMessageProperty("FIDO-ERR-5011")
                    + " Invalid 'authenticatorDATA'");
        }
        String responseJSON = skfeCommon.buildAuthenticateResponse(wsresponse, logs, errmsg);
        return responseJSON;
    }
    
    private String getKeyTypeFromRegSettings(RegistrationSettings rs){
        if(rs.getKty() == 2){
            return "ECDSA";
        }
        else if(rs.getKty() == 3){
            return "RSA";
        }
        else{
            throw new IllegalArgumentException("Unknown Key Type");
        }
    }

    @Override
    public String remoteExecute(Long did, String authneticationresponse, String authenticationmetadata, String method) {
        return execute(did, authneticationresponse, authenticationmetadata, method);
    }
}
