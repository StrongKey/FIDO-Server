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
 * Copyright (c) 2001-2019 StrongAuth, Inc.
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
 * EJB to perform ldap based authentications and authorizations.
 * Has been tested against OpenDJ 2.0
 *
 */
package com.strongkey.auth.txbeans;

import com.strongauth.appliance.utilities.applianceInputChecks;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.crypto.interfaces.initCryptoModule;
import com.strongauth.crypto.utility.CryptoException;
import com.strongauth.skce.utilities.skceCommon;
import com.strongauth.skce.utilities.skceConstants;
import java.util.logging.Level;
import javax.ejb.Stateless;

/**
 * EJB to perform hmac based authentications and authorizations
 */
@Stateless
public class authenticateRestRequestBean implements authenticateRestRequestBeanLocal {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    /*
     * ****************************************************************************************
     *                                               888             
     *                                               888             
     *                                               888             
     *   .d88b.  888  888  .d88b.   .d8888b 888  888 888888  .d88b.  
     *  d8P  Y8b `Y8bd8P' d8P  Y8b d88P"    888  888 888    d8P  Y8b 
     *  88888888   X88K   88888888 888      888  888 888    88888888 
     *  Y8b.     .d8""8b. Y8b.     Y88b.    Y88b 888 Y88b.  Y8b.     
     *   "Y8888  888  888  "Y8888   "Y8888P  "Y88888  "Y888  "Y8888  
     *  
     *****************************************************************************************
     */
    /**
     * This method authenticates a credential - username and password - against
     *
     * @param did Long the domain identifier for which to authenticate to 
     * @param accesskey String accesskey identifier for this request
     * @param request String the request to be hmac'd 
     * @param requestHmac String hmac from the client request to be checked
     * @return boolean value indicating either True (for authenticated) or False
     * (for unauthenticated or failure in processing)
     */
    @Override
    public boolean execute(
            Long did,
            String accesskey,
            String request,
            String requestHmac) {

        strongkeyLogger.entering(skceConstants.SKEE_LOGGER, classname, "execute");
        strongkeyLogger.logp(skceConstants.SKEE_LOGGER, Level.FINE, classname, "execute", skceCommon.getMessageProperty("SKCE-MSG-5001"),
                "\n EJB name=" + classname +
                "\n did=" + did +
                "\n accesskey=" + accesskey +
                "\n request=" + request +
                "\n requestHmac=" + requestHmac);

        // Input checks
        try {
            applianceInputChecks.checkDid(did);
        } catch (NullPointerException | IllegalArgumentException ex){
            strongkeyLogger.exiting(skceConstants.SKEE_LOGGER, classname, "execute");
            return false;
        }
        String signingKeystorePassword = skceCommon.getConfigurationProperty("skce.cfg.property.standalone.signingkeystore.password");
        try {
            String hmac = initCryptoModule.getCryptoModule().hmacRequest(accesskey, request, signingKeystorePassword);
            strongkeyLogger.logp(skceConstants.SKEE_LOGGER, Level.FINE, classname, "execute", skceCommon.getMessageProperty("SKCE-MSG-1015"), hmac);
            strongkeyLogger.exiting(skceConstants.SKEE_LOGGER, classname, "execute");
            return (requestHmac == null ? hmac == null : requestHmac.equals(hmac));
            
        } catch (CryptoException ex) {
            strongkeyLogger.log(skceConstants.SKEE_LOGGER, Level.SEVERE, "SKCEWS-ERR-3055", accesskey); // TODO
            strongkeyLogger.exiting(skceConstants.SKEE_LOGGER, classname, "execute");
            return false;
        }
    }
}