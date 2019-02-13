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
 * POJO that represents browser data in both registration and authentication
 * cases. The way this object is constructed is by giving the Json string of 
 * the browser data that comes back to the FIDO server as part of registration
 * or authentication response parameters; along with the request type which is
 * either registration or authentication.
 *
 */
package com.strongauth.skfe.core;

import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.SKFEException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.commons.codec.binary.Base64;

/**
 * POJO that represents browser data
 */
public class BrowserData implements Serializable {

    /**
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    /**
     * Components of browser data
     */
    private String origin;
    private String challenge;
    private String channelid = null;
    
    /**
     * Possible request types
     */
    public static final int REGISTRATION_RESPONSE = 0;
    public static final int AUTHENTICATION_RESPONSE = 1;

    /**
     * Variables for internal user
     */
    private String requesttype;

    /**
     * Constructor; receives Base64 encoded browser data and the request type to
     * signify if the case is a registration or authentication.
     * 
     * This method constructs the browser data POJO by processing the browser 
     * data given for the request type.
     * 
     * @param browserdataB64Encoded - Base64 encoded browser data
     * @param requesttype           - request type
     *                                  Number 0 for registration
     *                                  Number 1 for authentication
     * @throws SKFEException  - In case of any error
     */
    public BrowserData(String browserdataB64Encoded, int requesttype) throws SKFEException {
        processBrowserData(browserdataB64Encoded, requesttype);
    }

    /**
     * 
     * @param browserdataB64Encoded
     * @param requesttype
     * @throws SKFEException 
     */
    private void processBrowserData(String browserdataB64Encoded, int requesttype) throws SKFEException {
        String browserdataJson = null;

        try {
            browserdataJson = new String(Base64.decodeBase64(browserdataB64Encoded), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processBrowserData", 
                        skfeCommon.getMessageProperty("FIDO-ERR-5013"), ex.getLocalizedMessage());
            throw new SKFEException(ex);
        }

        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processBrowserData", 
                        skfeCommon.getMessageProperty("FIDO-MSG-5028"), "");
        parseBrowserDataJson(browserdataJson);

        if (requesttype == 0 && !this.requesttype.equals(skfeConstants.REGISTER_CLIENT_DATA_OPTYPE)) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processBrowserData", 
                        skfeCommon.getMessageProperty("FIDO-ERR-5014"), this.requesttype);
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5014") + this.requesttype);
        }
        
        if (requesttype == 1 && !this.requesttype.equals(skfeConstants.AUTHENTICATE_CLIENT_DATA_OPTYPE)) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processBrowserData", 
                        skfeCommon.getMessageProperty("FIDO-ERR-5014"), this.requesttype);
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5014") + this.requesttype);
        }
      
        try {
            byte[] challengebytes = Base64.decodeBase64(this.challenge);
        } catch (Exception ex) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processBrowserData", 
                        skfeCommon.getMessageProperty("FIDO-ERR-5015"), ex.getLocalizedMessage());
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5015") + ex);
        }
    }

    /**
     * Parses the browser data supplied in the form of stringified Json.
     * Looks for the needed key-value pairs that define a correct browser data
     * and fills up BrowserData pojo object.
     * 
     * @param browserdataJson       - browser data in the form of a string
     * @throws SKFEException  - in case of any error
     */
    private void parseBrowserDataJson(String browserdataJson) throws SKFEException {

        try {
            JsonReader jsonReader = Json.createReader(new StringReader(browserdataJson));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();
        
            this.requesttype = jsonObject.getString(skfeConstants.JSON_KEY_REQUESTTYPE);
            this.challenge = jsonObject.getString(skfeConstants.JSON_KEY_NONCE);
            this.origin = jsonObject.getString(skfeConstants.JSON_KEY_SERVERORIGIN);
            try {
                this.channelid = jsonObject.getString(skfeConstants.JSON_KEY_CHANNELID);
            } catch (Exception ex) {
                //do nothing, channel ID is optional
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.WARNING, classname, "parseBrowserDataJson", 
                        skfeCommon.getMessageProperty("FIDO-WARN-5002"), " Channelid is optional; so proceeding ahead");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "parseBrowserDataJson", 
                        skfeCommon.getMessageProperty("FIDO-ERR-5011"), ex.getLocalizedMessage());
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5011") + ex);
        }
    }
    
    /**
     * Get methods to access the browser data parameters
     * @return 
     */
    public String getRequestType() {
        return requesttype;
    }

    public String getOrigin() {
        return origin;
    }

    public String getChallenge() {
        return challenge;
    }

    public String getChannelid() {
        return channelid;
    }
}
