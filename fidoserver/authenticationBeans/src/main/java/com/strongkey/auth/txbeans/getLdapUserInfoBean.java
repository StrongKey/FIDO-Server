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
 * EJB to perform ldap based user data retrieval.
 * Has been tested against OpenDJ 2.0
 *
 */
package com.strongkey.auth.txbeans;

import com.strongauth.appliance.utilities.applianceConstants;
import com.strongauth.appliance.utilities.applianceInputChecks;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.skce.pojos.LDAPUserMetadata;
import com.strongauth.skce.utilities.SKCEException;
import com.strongauth.skce.utilities.skceCommon;
import com.strongauth.skce.utilities.skceConstants;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * EJB to perform ldap based user data retrieval
 */
@Stateless
public class getLdapUserInfoBean implements getLdapUserInfoBeanLocal, getLdapUserInfoBeanRemote {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();
    
    /*************************************************************************
                                                 888             
                                                 888             
                                                 888             
     .d88b.  888  888  .d88b.   .d8888b 888  888 888888  .d88b.  
    d8P  Y8b `Y8bd8P' d8P  Y8b d88P"    888  888 888    d8P  Y8b 
    88888888   X88K   88888888 888      888  888 888    88888888 
    Y8b.     .d8""8b. Y8b.     Y88b.    Y88b 888 Y88b.  Y8b.     
     "Y8888  888  888  "Y8888   "Y8888P  "Y88888  "Y888  "Y8888  

     *************************************************************************/
    /**
     * This method updates the value of a specific ldap user key to a new 
     * value provided. This method does not perform any authentication or 
     * authorization for the user against ldap. If present in ldap, this method
     * will look for the given key presence and will update it with new
     * value.
     * 
     * @param did
     * @param searchkey
     * @param searchvalue
     * @param basedn  - String containing name of the ldap user
     * @return LDAPUserMetadata object that contains all the meta data of the user
     * @throws SKCEException in the event there is an error of any kind.
     */
    @Override
    public LDAPUserMetadata execute(Long did, String basedn, String searchkey, String searchvalue) throws SKCEException
    {
        strongkeyLogger.entering(applianceConstants.APPLIANCE_LOGGER,classname, "execute");
        strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER,Level.FINE, classname, "execute", "APPL-MSG-1000", 
                        " Input Received : \nEJB name=" + classname + 
                        " did=" + did +
                        " searchkey=" + searchkey +
                        " searchvalue=" + searchvalue);
        
        //  Inputs check
        //  Input checks
         try{
             applianceInputChecks.checkDid(did);
         }catch(NullPointerException | IllegalArgumentException ex){
             throw new SKCEException(ex.getLocalizedMessage());
         }
        if ( searchkey == null || searchkey.trim().isEmpty() ) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.WARNING, "APPL-ERR-1000", "NULL or empty argument for searchkey : " + searchkey);
            throw new SKCEException("NULL or empty argument for searchkey : " + searchkey);
        }

        LDAPUserMetadata authres = null;
        
        //  LDAP components prefixing/suffixing to a username
        String dnprefix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnprefix"); // cn=
        String dnsuffix = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapdnsuffix"); // ,ou=users,ou=v1,ou=SKCE,ou=StrongAuth,ou=Applications,dc=strongauth,dc=com       
        
        // The connection uses a service credential to bind to the Directory
        String ldapurl = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapurl");   // ldap://localhost:1389
        String binddn = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn");
        String securityPassword = skceCommon.getConfigurationProperty("ldape.cfg.property.service.ce.ldap.search.ldapbinddn.password");

        String securityprincipal;
        String first3dnchars = binddn.substring(0, 3);
        switch (first3dnchars) {
            case "cn=":
            case "CN=":
            case "uid":
            case "UID":
                securityprincipal = binddn;
                break;
            default:
                securityprincipal = dnprefix + binddn + dnsuffix;
        }
            
        // We're assuming that usernames are simple names and not DNs - example
        // "jdoe" username become the following principal using default properties: 
        // cn=jdoe,ou=users,ou=v2,ou=SKCE,ou=StrongAuth,ou=Applications ,dc=strongauth,dc=com       
        
        DirContext ctx;
        try {
            // Instantiate context
            ctx = (DirContext) getcontext("SKCE", ldapurl, securityprincipal, securityPassword);

            // Specify the attributes to match
            // Ask for objects that has a surname ("sn") attribute and a ("cn") attribute
//            Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
//            matchAttrs.put(new BasicAttribute(searchkey, searchvalue));
            
            SearchControls cons = new SearchControls();
            cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            // Search for objects that have those matching attributes
            String searchdn;
            if(basedn ==null || basedn.trim().length() ==0)
            {
                searchdn = skceCommon.getSEARCH_OU_PREFIX().substring(1) + did + dnsuffix;
            }else{
                searchdn = basedn;
                
            }
            boolean isuserfound = false;
            NamingEnumeration<?> answer = ctx.search(searchdn, searchkey +"="+searchvalue, cons);
            while (answer.hasMore()) {
                SearchResult sr = (SearchResult) answer.next();
                isuserfound = true;
                String fname = findAttributeValue(sr.getAttributes(), skceConstants.LDAP_ATTR_KEY_FNAME);
                String uid = findAttributeValue(sr.getAttributes(), skceConstants.LDAP_ATTR_KEY_UID);
                String surname = findAttributeValue(sr.getAttributes(), skceConstants.LDAP_ATTR_KEY_SURNAME);
                String commonname = findAttributeValue(sr.getAttributes(), skceConstants.LDAP_ATTR_KEY_COMMONNAME);
                String principal = dnprefix + commonname + skceCommon.getSEARCH_OU_PREFIX() + did + dnsuffix;
                authres = new LDAPUserMetadata(commonname, 
                        principal, 
                        fname, 
                        surname, 
                        uid);
            }
            // If return value is not null, credentials are valid
            ctx.close();
            
            if ( !isuserfound ) {
                strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.SEVERE, "SKCE-ERR-1000", "user not found; " + searchvalue);
                throw new SKCEException(" user not found; " + searchvalue);
            }
            
        } catch (AuthenticationException ex) {
//            ex.printStackTrace();
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.SEVERE, "SKCE-ERR-1000", "AuthenticationException: " + searchvalue);
            throw new SKCEException(" AuthenticationException: " + searchvalue);
        } catch (NamingException ex) {
//            ex.printStackTrace();
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.SEVERE, "SKCE-ERR-1000", " NamingException: \n" + ex.getLocalizedMessage());
            throw new SKCEException(" NamingException: " + ex.getLocalizedMessage());
        } catch (SKCEException | NumberFormatException ex) {    // Any other exception
//            ex.printStackTrace();
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.SEVERE, "SKCE-ERR-1000", " SKCEException: \n" + ex.getLocalizedMessage());
            throw new SKCEException(" SKCEException: " + ex.getLocalizedMessage());
        }

        strongkeyLogger.exiting(applianceConstants.APPLIANCE_LOGGER,classname, "execute");
        return authres;
    }
    
    /**
     * 
     * @param attrs
     * @param attributename
     * @return 
     */
    private String findAttributeValue(Attributes attrs, String attributename) throws SKCEException {
        String attrvalue = "";

        if (attrs == null) {
        } else {
            /* For each attribute */
            try {
                for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
                    Attribute attr = (Attribute) ae.next();

                    if (attr.getID().equalsIgnoreCase(attributename)) {
                        /* Get each value */
                        NamingEnumeration<?> e = attr.getAll();
                        while (e.hasMore()) {
                            attrvalue = e.next().toString();
                        }
                    }
                }
            } catch (NamingException e) {
                strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER,Level.SEVERE, "SKCEWS-ERR-3000", e.getLocalizedMessage());
                throw new SKCEException(e);
            }
        }

        return attrvalue;
    }

    @Override
    public LDAPUserMetadata remoteExecute(Long did, String basedn, String searchkey, String searchvalue) throws SKCEException {
        return execute(did, basedn, searchkey, searchvalue);
    }
    
    protected Context getcontext(String module, String ldapurl, String principal, String password) throws NamingException{
        return skceCommon.getInitiallookupContext("SKCE", ldapurl, principal, password);
    }
}
