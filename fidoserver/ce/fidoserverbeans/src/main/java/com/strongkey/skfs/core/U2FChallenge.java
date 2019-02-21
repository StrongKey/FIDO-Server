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
 * This is a generic class that holds common parameters between U2F registration
 * and authentication challenge objects.
 *
 * The constructor of this class is responsible for creating the challege parameters.
 * 
 */
package com.strongkey.skfs.core;

import com.strongkey.appliance.utilities.applianceCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsLogger;
import java.io.Serializable;
import java.util.logging.Level;

/**
 * Super class for U2F Challenges that have to be sent back to the RP application
 * from the FIDO server.
 * 
 */
public class U2FChallenge implements Serializable {

    /**
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();
    
    /**
     * Supported versions for U2F protocol
     */
    final String U2F_VERSION_V2 = "U2F_V2";
    final String FIDO = "FIDO20";
    
    /**
     * Common parameters for a challenge in U2F
     */
    String version;
//    String sessionid;

    /**
     * Constructor that constructs U2F registration challenge parameters for the
     * user specified by username and complying to U2F protocol version specified
     * by u2fversion.
     * @param u2fversion - Version of the U2F protocol being communicated in; 
     *                      example : "U2F_V2"
     * @param username   - any non-empty username
     * @throws IllegalArgumentException 
     *                   - In case of any error
     */
    public U2FChallenge(String u2fversion, String username) throws IllegalArgumentException {

        //  Input checks
        if ( u2fversion==null || u2fversion.trim().isEmpty() ) {
            skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FChallenge", skfsCommon.getMessageProperty("FIDO-ERR-5001"), " protocol");
            throw new IllegalArgumentException(skfsCommon.getMessageProperty("FIDO-ERR-5001") + " protocol");
        }
        
        if (username==null || username.trim().isEmpty() ) {
            skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FChallenge", skfsCommon.getMessageProperty("FIDO-ERR-5001"), " username");
            throw new IllegalArgumentException(skfsCommon.getMessageProperty("FIDO-ERR-5001") + " username");
        }
        
        if (username.trim().length() > Integer.parseInt(applianceCommon.getApplianceConfigurationProperty("appliance.cfg.maxlen.256charstring"))) {
            skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FChallenge", skfsCommon.getMessageProperty("FIDO-ERR-0027"), " username should be limited to 256 characters");
            throw new IllegalArgumentException(skfsCommon.getMessageProperty("FIDO-ERR-0027") + " username should be limited to 256 characters");
        }
               
        //  u2f version specific code
        if ( u2fversion.equalsIgnoreCase(U2F_VERSION_V2) || u2fversion.equalsIgnoreCase(FIDO)) {
            version = u2fversion;
//            sessionid = U2FUtility.generateSessionid(this.nonce);
        } else {
            skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FChallenge", skfsCommon.getMessageProperty("FIDO-ERR-5002"), " protocol passed=" + u2fversion);
            throw new IllegalArgumentException(skfsCommon.getMessageProperty("FIDO-ERR-5002") + " protocol passed=" + u2fversion);
        }
    }
    
    /**
     * Empty constructor since this class implements java.io.Serializable
     */
    protected U2FChallenge() {
    }
}
