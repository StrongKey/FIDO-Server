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
 * Remote interface for getLdapUserInfoBean
 * 
 */
package com.strongkey.auth.txbeans;

import com.strongauth.skce.pojos.LDAPUserMetadata;
import com.strongauth.skce.utilities.SKCEException;
import javax.ejb.Remote;

@Remote
public interface getLdapUserInfoBeanRemote {
    
    /**
     * This method updates the value of a specific ldap user key to a new 
     * value provided. This method does not perform any authentication or 
     * authorization for the user against ldap. If present in ldap, this method
     * will look for the given key presence and will update it with new
     * value.
     * 
     * @param did   - Short, domain id
     * @param username  - String containing name of the ldap user
     * @return LDAPUserMetadata object that contains all the meta data of the user
     * @throws SKCEException in the event there is an error of any kind.
     */
    LDAPUserMetadata remoteExecute(Long did, String basedn,String searchkey, String searchvalue) throws SKCEException;

}
