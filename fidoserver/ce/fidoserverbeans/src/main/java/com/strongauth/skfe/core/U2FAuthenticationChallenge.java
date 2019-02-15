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
 * POJO that represents the U2F Authentication parameters. Construct the object by
 * passing in the U2F protocol version, username and the specific key handle.
 * This POJO is capable of handling multiple U2F protocol versions.
 *
 * There are get methods along with toString and toJsonString methods that give
 * String and Json string representation of this class object.
 *
 */
package com.strongauth.skfe.core;

import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.SKFEException;
import java.io.Serializable;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class U2FAuthenticationChallenge extends U2FChallenge implements Serializable {

    /**
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    private final String keyhandle;

    private String appid;

    private JsonArray transports;

    /**
     * Constructor that constructs U2F authentication challenge parameters for
     * the user specified by username and complying to U2F protocol version
     * specified by u2fversion. The nonce is generated in the super class
     * 'U2FChallenge'.
     *
     * @param u2fversion - Version of the U2F protocol being communicated in;
     * example : "U2F_V2"
     * @param username - any non-empty username
     * @param keyhandlefromDB - The user could have multiple fido authenticators
     * registered successfully. An authentication challenge can pertain to only
     * one unique fido authenticator (key handle).
     * @param appidfromDB
     * @param transport_list
     * @throws SKFEException - In case of any error
     */
    public U2FAuthenticationChallenge(String u2fversion, String username, String keyhandlefromDB, String appidfromDB, JsonArray transport_list) throws SKFEException {
        super(u2fversion, username);

        if (keyhandlefromDB == null || keyhandlefromDB.trim().isEmpty()) {
            throw new SKFEException("keyhandle cannot be null or empty");
        }

        keyhandle = keyhandlefromDB;
        appid = appidfromDB;
        transports = transport_list;
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "U2FAuthenticationChallenge", skfeCommon.getMessageProperty("FIDO-MSG-5004"), "");
    }

    /**
     * Get methods to access the challenge parameters
     *
     * @return
     */
    public String getKeyhandle() {
        return keyhandle;
    }

    public String getVersion() {
        return version;
    }

    public JsonArray getTransports() {
        return transports;
    }

//    public String getAppid() {
//        return appid;
//    }
//    public String getSessionid() {
//        return sessionid;
//    }
//    
//    public String getNonce() {
//        return nonce;
//    }
    /**
     * Converts this POJO into a JsonObject and returns the same.
     *
     * @param appidfromfile
     * @return JsonObject
     */
    public final JsonObject toJsonObject(String appidfromfile) {
        JsonObject jsonObj;
        if (appid.equalsIgnoreCase(appidfromfile)) {
            jsonObj = Json.createObjectBuilder()
                    .add(skfeConstants.JSON_USER_KEY_HANDLE_SERVLET, this.keyhandle)
                    .add(skfeConstants.JSON_KEY_TRANSPORT, transports)
                    .add(skfeConstants.JSON_KEY_VERSION, version)
                    .build();
        } else {
            jsonObj = Json.createObjectBuilder()
                    .add(skfeConstants.JSON_USER_KEY_HANDLE_SERVLET, this.keyhandle)
                    .add(skfeConstants.JSON_KEY_TRANSPORT, transports)
                    .add(skfeConstants.JSON_KEY_VERSION, version)
                    .add(skfeConstants.JSON_KEY_APP_ID, appid)
                    .build();
        }

        return jsonObj;
    }

    /**
     * Converts this POJO into a JsonObject and returns the String form of it.
     *
     * @param appidfromfile
     * @return String containing the Json representation of this POJO.
     */
    public final String toJsonString(String appidfromfile) {
        return toJsonObject(appidfromfile).toString();
    }
}
