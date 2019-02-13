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
 * Copyright (c) 2001-2015 StrongAuth, Inc.
 *
 * $Date: 2018-06-18 14:47:15 -0400 (Mon, 18 Jun 2018) $
 * $Revision: 50 $
 * $Author: pmarathe $
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/txbeans/changeLDAPUserPasswordBean.java $
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
package com.strongkey.auth.txbeans;

import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.skce.utilities.SKCEException;
import com.strongauth.skce.utilities.skceCommon;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;

@Stateless
public class changeLDAPUserPasswordBean implements changeLDAPUserPasswordBeanLocal, changeLDAPUserPasswordBeanRemote {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();

    @EJB
    authenticateLdapUserBeanLocal authenticateLdapuserejb;

    @Override
    public boolean execute(Long did, String username, String oldpassword, String newpassword) throws SKCEException {
        strongkeyLogger.entering(applianceConstants.APPLIANCE_LOGGER,classname, "execute");
        if (username == null || username.trim().isEmpty()
                || oldpassword == null || oldpassword.trim().isEmpty()
                || newpassword == null || newpassword.trim().isEmpty()) {
            throw new SKCEException("SKCEWS-ERR-8003: NULL argument: username, oldpassword, or newpassword");
        }

        if(!authenticateLdapuserejb.execute(did, username, oldpassword)){
            throw new SKCEException(skceCommon.getMessageProperty("SKCE-ERR-4051"));
        }
        
        String ldapurl = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapurl");
        String dnprefix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnprefix");
        String usersdn = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnsuffix");
        String binddn = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn");
        String securityPassword = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn.password");

        String securityprincipal = binddn;
        String userdn = dnprefix + username + skceCommon.getSEARCH_OU_PREFIX() + did + usersdn;
        // Setup initial JNDI context parameters
        @SuppressWarnings("UseOfObsoleteCollectionType")
        java.util.Hashtable<String, String> env = new java.util.Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapurl);
        env.put(Context.SECURITY_PRINCIPAL, securityprincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityPassword);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        DirContext ctx;

        try {
            ctx = new InitialDirContext(env);
            ModificationItem[] mods = new ModificationItem[1];

            Attribute mod0 = new BasicAttribute("userpassword", newpassword);

            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);

            ctx.modifyAttributes(userdn, mods);
            
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw new SKCEException(ex.getLocalizedMessage());
        } catch (NamingException ex) {
            ex.printStackTrace();
            throw new SKCEException(ex.getLocalizedMessage());
        } catch (Exception ex) {    // Any other exception
            ex.printStackTrace();
            throw new SKCEException(ex.getLocalizedMessage());
        }
        strongkeyLogger.exiting(applianceConstants.APPLIANCE_LOGGER,classname, "execute");
        return true;
    }

    @Override
    public boolean remoteExecute(Long did, String username, String oldpassword, String newpassword) throws SKCEException {
        return execute(did, username, oldpassword, newpassword);
    }
}
