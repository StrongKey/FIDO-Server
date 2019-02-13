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
 * EJB to perform ldap based authentications and authorizations.
 * Has been tested against OpenDJ 2.0
 *
 */
package com.strongkey.auth.txbeans;

import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.appliance.utilities.applianceInputChecks;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.skce.utilities.SKCEException;
import com.strongauth.skce.utilities.skceCommon;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

/**
 * EJB to perform ldap based authentications and authorizations
 */
@Stateless
public class authenticateLdapUserBean implements authenticateLdapUserBeanLocal, authenticateLdapUserBeanRemote {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    /**
     * ****************************************************************************************
     * 888 888 888 .d88b. 888 888 .d88b. .d8888b 888 888 888888 .d88b. d8P Y8b
     * `Y8bd8P' d8P Y8b d88P" 888 888 888 d8P Y8b 88888888 X88K 88888888 888 888
     * 888 888 88888888 Y8b. .d8""8b. Y8b. Y88b. Y88b 888 Y88b. Y8b. "Y8888 888
     * 888 "Y8888 "Y8888P "Y88888 "Y888 "Y8888      *
     *****************************************************************************************
     */
    /**
     * This method authenticates a credential - username and password - against
     * the configured LDAP directory. Only LDAP-based authentication is
     * currently supported; both Active Directory and a standards-based,
     * open-source LDAP directories are supported. For the later, this has been
     * tested with OpenDS 2.0 (https://docs.opends.org).
     *
     * @param did
     * @param username String containing the credential's username
     * @param password String containing the user's password
     * @return boolean value indicating either True (for authenticated) or False
     * (for unauthenticated or failure in processing)
     * @throws SKCEException - in case of any error
     */
    @Override
    public boolean execute(
            Long did,
            String username,
            String password)
            throws SKCEException {
        strongkeyLogger.entering(applianceConstants.APPLIANCE_LOGGER, classname, "execute");
        strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER, Level.FINE, classname, "execute", applianceCommon.getMessageProperty("APPL-MSG-1000"),
                " Input Received : \nEJB name=" + classname
                + " did=" + did
                + " username=" + username);

        //  Input checks
         try{
             applianceInputChecks.checkDid(did);
             applianceInputChecks.checkServiceCredentails(username, password);
         }catch(NullPointerException | IllegalArgumentException ex){
             throw new SKCEException(ex.getLocalizedMessage());
         }
        // Get configured parameters for this domain
        String ldapurl = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.ldapurl");
        String dnprefix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.ldapdnprefix");
        String dnsuffix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.ldapdnsuffix");
        // Setup paramters from class variables
        strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.FINE, "APPL-MSG-1000", "setup principal");

        String principal = dnprefix + username + skceCommon.getSERVICE_OU_PREFIX() + did + dnsuffix;
        strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.FINE, "APPL-MSG-1000", principal);
        try {
            // Instantiate context
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.FINE, "APPL-MSG-1000", "new InitialContext");
            Context ctx = getcontext("SKCE", ldapurl, principal, password);
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.FINE, "APPL-MSG-1000", principal);
            LdapContext lc = (LdapContext) ctx.lookup(principal);
            // If return value is not null, credentials are valid
            ctx.close();
            return lc != null;
        } catch (AuthenticationException ex) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1000", username + ex.getLocalizedMessage());
//            ex.printStackTrace();
            throw new SKCEException(skceCommon.getMessageProperty("SKCEWS-ERR-3055").replace("{0}", "") + username + ex.getLocalizedMessage());
        } catch (NamingException ex) {
//            ex.printStackTrace();
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1000", username + ex.getLocalizedMessage());
            throw new SKCEException(skceCommon.getMessageProperty("SKCEWS-ERR-3055").replace("{0}", "") + username + ex.getLocalizedMessage());
        }
    }

    @Override
    public boolean remoteExecute(Long did, String username, String password) throws SKCEException {
        return execute(did, username, password);
    }
    
    protected Context getcontext(String module, String ldapurl, String principal, String password) throws NamingException{
        return skceCommon.getInitiallookupContext("SKCE", ldapurl, principal, password);
    }
}
