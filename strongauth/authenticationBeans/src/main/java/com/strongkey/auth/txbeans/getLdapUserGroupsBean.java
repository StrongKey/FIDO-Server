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
 */
package com.strongkey.auth.txbeans;

import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.skce.utilities.skceCommon;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

@Stateless
public class getLdapUserGroupsBean implements getLdapUserGroupsBeanLocal, getLdapUserGroupsBeanRemote {

    // The OU (organizational unit) to search for groups
    private static final String LOOKUP_GROUPS = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.subtree.base");

    // The connection uses a service credential to bind to the Directory
    private DirContext context;
    String ldaptype = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldaptype");
    String ldapurl  = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapurl");
    String binduser = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn");
    String password = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn.password");
    String dnprefix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnprefix");
    String dnsuffix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnsuffix");
        
    /*************************************************************************
                                                 888             
                                                 888             
                                                 888             
     .d88b.  888  888  .d88b.   .d8888b 888  888 888888  .d88b.  
    d8P  Y8b `Y8bd8P' d8P  Y8b d88P"    888  888 888    d8P  Y8b 
    88888888   X88K   88888888 888      888  888 888    88888888 
    Y8b.     .d8""8b. Y8b.     Y88b.    Y88b 888 Y88b.  Y8b.     
     "Y8888  888  888  "Y8888   "Y8888P  "Y88888  "Y888  "Y8888
     * @param userdn
     * @return 
     * @throws javax.naming.NamingException
     *************************************************************************/
    
    @Override
    public List<String> execute(String userdn) throws NamingException 
    {
        List<String> groups = new ArrayList<>();
        try {
            // Figure out if the bind DN is just a user name or a full DN
            String binddn;
            String first3dnchars = binduser.substring(0, 3);
            switch (first3dnchars) {
                case "cn=":
                case "CN=":
                case "uid":
                case "UID":
                    binddn = binduser;
                    break;
                default:
                    binddn = dnprefix + binduser + dnsuffix;
            }
            // Get LDAP context with credentials
            context = skceCommon.getInitiallookupContext("SKCE",ldapurl, binddn, password);
            
            // Set up criteria to search on
            String filter;
            if (ldaptype.equalsIgnoreCase("AD")) {
                filter = new StringBuffer()
                        .append("(Member=")
                        .append(userdn)
                        .append(")")
                        .toString();
            } else {
                filter = new StringBuffer()
                        .append("(uniqueMember=")
                        .append(userdn)
                        .append(")")
                        .toString();
            }

            // Set up search constraints
            SearchControls cons = new SearchControls();
            cons.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<?> results = context.search("", filter, cons);
            if (results.hasMoreElements())
                strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.FINE, "APPL-MSG-1000", "User Groups found in LDAP for:" + userdn);
            else
                strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.FINE, "APPL-ERR-1000", "User does not belong to any groups" + userdn);
                        
            while (results.hasMore()) {
                SearchResult result = (SearchResult) results.next();
//                groups.add(Common.lookupGroupCN(result.getName()));
                groups.add(result.getName().toLowerCase());
            }
        } catch (NamingException ex) {
            strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER,Level.WARNING, "getLdapUserGroupsBean", "execute", "APPL-ERR-1000", ex.getLocalizedMessage());
            return null;
        }
        return groups;
    }

    @Override
    public List<String> remoteexecute(String username) throws NamingException {
        return execute(username);
    }

}
