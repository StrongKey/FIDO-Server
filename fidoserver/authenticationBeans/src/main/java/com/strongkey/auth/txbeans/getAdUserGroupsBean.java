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

import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.skce.utilities.skceCommon;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

@Stateless
public class getAdUserGroupsBean implements getAdUserGroupsBeanLocal, getAdUserGroupsBeanRemote {
    
    // The connection uses a service credential to bind to the Directory
    private DirContext context;
    String ldaptype = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldaptype");
    String ldapurl  = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapurl");
    String binduser = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn");
    String password = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn.password");
    String dnprefix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnprefix");
    String dnsuffix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnsuffix");
    
    String grouprestriction = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.grouprestriction.type");
    String groupsearchbase = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.subtree.base");
    String groupmethod = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.groupname.method");
    String groupprefix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.groupname.prefix");
    String groupsuffix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.groupname.suffix");

    String searchbasedn = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.basedn");
    String reversesearchbasedn = applianceCommon.reversedn(searchbasedn);

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
     *************************************************************************/
    
    @Override
    @SuppressWarnings("empty-statement")
    public List<String> execute(String userdn) 
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

            // Set up attributes to search for
            String[] searchAttributes = new String[1];
            searchAttributes[0] = "Memberof";
            
            // Get attributes and evaluate
            Attributes attributes = context.getAttributes(userdn, searchAttributes);
            if (attributes != null) {
                Attribute memberAtts = attributes.get("Memberof");
                if (memberAtts != null) {
                    NamingEnumeration<?> vals = memberAtts.getAll();
                    while (vals.hasMoreElements()) {
                        String newelement = (String) vals.nextElement();
                        if (newelement.startsWith(reversesearchbasedn)) {
                            newelement = applianceCommon.reversedn(newelement);
                        }
                        if (grouprestriction.equalsIgnoreCase("Subtree")) {
                            if (newelement.endsWith(groupsearchbase)) {
//                                groups.add(Common.lookupGroupCN(newelement));
                                groups.add(newelement.toLowerCase());
                            }
                        } else if (grouprestriction.equalsIgnoreCase("groupname")) {
                            String parsedgroupname = skceCommon.lookupGroupCN(newelement);
                            if (groupmethod.equalsIgnoreCase("prefix")) {
                                if (parsedgroupname.startsWith(groupprefix)) {
//                                    groups.add(parsedgroupname);
                                    groups.add(newelement.toLowerCase());
                                }
                            } else if (groupmethod.equalsIgnoreCase("suffix")) {
                                if (parsedgroupname.endsWith(groupsuffix)) {
//                                    groups.add(parsedgroupname);
                                    groups.add(newelement.toLowerCase());
                                }
                            } else {
                                if (parsedgroupname.startsWith(groupprefix)
                                        && parsedgroupname.endsWith(groupsuffix)) {
//                                    groups.add(parsedgroupname);
                                        groups.add(newelement.toLowerCase());
                                }
                            }

                        } else {
//                            groups.add(Common.lookupGroupCN(newelement));
                            groups.add(newelement.toLowerCase());
                        }

                    }
                }
            }
        } catch (NamingException ex) {
            strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER,Level.WARNING, "getAdUserGroupsBean", "execute", "APPL-ERR-1000", ex.getLocalizedMessage());
            return null;
        }
        return groups;
    }

    @Override
    public List<String> remoteexecute(String username) throws NamingException {
        return execute(username);
    }
}
