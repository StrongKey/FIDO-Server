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
import com.strongauth.crypto.utility.cryptoCommon;
import com.strongauth.skce.utilities.skceCommon;
import com.strongauth.skce.utilities.skceConstants;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;

/**
 * EJB to perform hmac based authentications and authorizations
 */
@Stateless
public class authenticateRestRequestBean implements authenticateRestRequestBeanLocal {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    private final String signingKeystorePassword = skceCommon.getConfigurationProperty("skce.cfg.property.standalone.signingkeystore.password");
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
     * @param request HttpServletRequest full request object in which to gather
     * headers and other parts of the request
     * @param requestBody String body of the request to be md5'd 
     * @return boolean value indicating either True (for authenticated) or False
     * (for unauthenticated or failure in processing)
     */
    @Override
    public boolean execute(
            Long did,
            HttpServletRequest request,
            String requestBody) {

        strongkeyLogger.logp(skceConstants.SKEE_LOGGER, Level.FINE, classname, "execute", "APPL-MSG-1051",
                "\n EJB name=" + classname +
                "\n did=" + did);

        // Input checks
        try {
            applianceInputChecks.checkDid(did);
        } catch (NullPointerException | IllegalArgumentException ex){
            return false;
        }

        String generatedmd5 = "";
        String contenttype = "";

        if (requestBody != null) {
            String contentmd5  = request.getHeader("Content-MD5");
            contenttype = request.getHeader("content-type");

            generatedmd5 = cryptoCommon.calculateMD5(requestBody);

            if (!generatedmd5.equals(contentmd5)) {
                strongkeyLogger.logp(skceConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "APPL-ERR-1041", "Received: " + contentmd5 + " Expected: " + generatedmd5);
                return false;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            strongkeyLogger.logp(skceConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "APPL-ERR-1040", "");
            return false;
        }

        Pattern r = Pattern.compile("HMAC ([^:]+):(.*)");
        Matcher m = r.matcher(authHeader);

        String requestHmac;
        String accessKey;
        if (m.find()) {
            accessKey = m.group(1);
            requestHmac = m.group(2);
        } else {
            strongkeyLogger.logp(skceConstants.SKFE_LOGGER, Level.SEVERE, classname, "execute", "APPL-ERR-1039", authHeader);
            return false;
        }

        String queryParams = request.getQueryString();
        if (queryParams != null) {
            queryParams = "?" + queryParams;
        } else {
            queryParams = "";
        }
        
        String requestToHmac = request.getMethod() + "\n"
                + generatedmd5 + "\n"
                + contenttype + "\n"
                + request.getHeader("Date") + "\n"
                + request.getRequestURI() + queryParams;

        strongkeyLogger.logp(skceConstants.SKFE_LOGGER, Level.FINE, classname, "execute", "APPL-MSG-1054", "\n" + requestToHmac);

        try {
            String hmac = initCryptoModule.getCryptoModule().hmacRequest(signingKeystorePassword, accessKey, requestToHmac);
            strongkeyLogger.logp(skceConstants.SKEE_LOGGER, Level.FINE, classname, "execute", "APPL-MSG-1015", hmac);
            if (requestHmac.equals(hmac)) {
                strongkeyLogger.logp(skceConstants.SKEE_LOGGER, Level.FINER, classname, "execute", "APPL-MSG-1016", "");
                return true;
            } else {
                strongkeyLogger.logp(skceConstants.SKEE_LOGGER, Level.WARNING, classname, "execute", "APPL-ERR-1016", "Expected HMAC: " + requestHmac + " Produced HMAC: " + hmac);
                return false;
            }
            
        } catch (CryptoException ex) {
            return false;
        }
    }
}