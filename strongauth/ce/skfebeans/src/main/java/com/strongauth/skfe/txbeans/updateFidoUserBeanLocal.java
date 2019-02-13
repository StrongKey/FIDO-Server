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
 * Local interface for updateLdapKeyBean
 *
 */
package com.strongauth.skfe.txbeans;

import com.strongauth.skfe.utilities.SKFEException;
import javax.ejb.Local;

/**
 * Local interface for updateLdapKeyBean
 */
@Local
public interface updateFidoUserBeanLocal {
    
    /**
     * This method updates the value of a specific ldap user key to a new 
     * value provided. This method does not perform any authentication or 
     * authorization for the user against ldap. If present in ldap, this method
     * will look for the given key presence and will update it with new
     * value.
     * 
     * @param did       - short, domain id
     * @param username  - String containing name of the ldap user
     * @param key       - String containing the name of the user key 
     *                     in ldap
     * @param value     - String containing the new value for the user 
     *                     key in ldap
     * @param deletion  - boolean that indicates if it is a delete operation for
     *                      the LDAP attribute (key). Delete means the new value 
     *                      applied to the key is null
     * @return boolean based on if the operation is successful or not
     * @throws SKFEException in the event there is an error of any kind.
     */
    String execute(Long did, 
                  String username, 
                  String key,
                  String value,
                  boolean deletion) throws SKFEException;
}
