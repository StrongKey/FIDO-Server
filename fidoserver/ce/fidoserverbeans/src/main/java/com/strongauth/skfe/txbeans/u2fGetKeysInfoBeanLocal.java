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
 * Local interface for u2fGetKeysInfoBean
 * 
 */

package com.strongauth.skfe.txbeans;

import com.strongauth.skfe.utilities.SKCEReturnObject;
import javax.ejb.Local;

/**
 * Local interface for u2fGetKeysInfoBean
 * 
 */
@Local
public interface u2fGetKeysInfoBeanLocal {
    
    /**
     * This method is responsible for fetching the user registered key from the 
     * persistent storage and return back the metadata. 
     * 
     * If the user has registered multiple fido authenticators, this method will 
     * return an array of registered key metadata, each entry mapped to a random id. 
     * These random ids have a 'ttl (time-to-live)' associated with them. The client 
     * applications have to cache these random ids if they wish to de-register keys.
     * 
     * @param did       - FIDO domain id
     * @param username  - username
     * @return          - returns SKCEReturnObject in both error and success cases.
     *                  In error case, an error key and error msg would be populated
     *                  In success case, a simple msg saying that the process was
     *                  successful would be populated.
     */
    SKCEReturnObject execute(Long did, 
                            String username);
}
