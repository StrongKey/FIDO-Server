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
 * Copyright (c) 2001-2016 StrongAuth, Inc.
 *
 * $Date: 2018-06-18 14:47:15 -0400 (Mon, 18 Jun 2018) $
 * $Revision: 50 $
 * $Author: pmarathe $
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skfe/core/FIDO2RegistrationChallenge.java $
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
 * POJO that represents the U2F Registration parameters. Construct the object by
 * passing in the U2F protocol version and the username. This POJO is capable of
 * handling multiple U2F protocol versions.
 *
 * There are get methods along with toString and toJsonString methods that give 
 * String and Json string representation of this class object.
 *
 */
package com.strongkey.skfs.core;

import com.strongkey.skfs.utilities.skfsLogger;
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.utilities.SKFEException;
import java.io.Serializable;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;

public class FIDO2RegistrationChallenge extends U2FChallenge implements Serializable {

    /**
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();
    
    private String nonce;
    private String userID;

    /**
     * Constructor that constructs U2F registration challenge parameters for the
     * user specified by username and complying to U2F protocol version specified
     * by u2fversion. The nonce is generated in the super class 'U2FChallenge'.
     * 
     * @param u2fversion - Version of the U2F protocol being communicated in; 
     *                      example : "U2F_V2"
     * @param username  - any non-empty username
     * @throws SKFEException
     *                  - In case of any error
     */
   public FIDO2RegistrationChallenge(String u2fversion, String username) throws SKFEException {
        super(u2fversion, username);
        nonce = U2FUtility.getRandom(Integer.parseInt(skfsCommon.getConfigurationProperty("skfs.cfg.property.entropylength")));
        userID = U2FUtility.getRandom(Integer.parseInt(skfsCommon.getConfigurationProperty("skfs.cfg.property.fido.userid.length")));   //TODO this should not be randomized if a user already exists
        skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.FINE, classname, "U2FRegistrationChallenge", skfsCommon.getMessageProperty("FIDO-MSG-5003"), "");
   }

   /**
    * Get methods to access the challenge parameters
     * @return 
    */
    public String getVersion() {
        return version;
    }

    public String getNonce() {
        return nonce;
    }

    public String getUsersId() {
        return userID;
    }

//    public String getAppId() {
//        return appid;
//    }

//    public String getSessionId() {
//        return sessionid;
//    }
 
    /**
     * Converts this POJO into a JsonObject and returns the same.
     * @return JsonObject
     */
    public final JsonObject toJsonObject() {
        
        JsonObject jsonObj = Json.createObjectBuilder()
                .add(skfsConstants.JSON_KEY_NONCE, this.nonce)
                .add(skfsConstants.JSON_KEY_VERSION, this.version)
                .add(skfsConstants.JSON_KEY_USER_ID, this.userID)
                .build();
        
        return jsonObj;
    }
    
    /**
     * Converts this POJO into a JsonObject and returns the String form of it.
     * @return String containing the Json representation of this POJO.
     */
    public final String toJsonString() {
        return toJsonObject().toString();
    }
}
