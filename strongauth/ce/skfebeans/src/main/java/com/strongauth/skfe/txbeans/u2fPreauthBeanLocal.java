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
 * Local interface for u2fPreauthBean
 *
 */

package com.strongauth.skfe.txbeans;

import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.FEreturn;
import javax.ejb.Local;
import javax.json.JsonArray;

/**
 * Local interface for u2fPreauthBean
 * 
 */
@Local
public interface u2fPreauthBeanLocal {
    
    /**
     * Executes the pre-authentication process which primarily includes generating
     * authentication challenge parameters for the given username complying to the
     * protocol specified. did is the FIDO domain credentials.
     * 
     * NOTE : The did and secretkey will be used for the production
     * version of the FIDO server software. They can be ignored for the open-source
     * version.
     * @param did       - FIDO domain id
     * @param protocol  - U2F protocol version to comply with.
     * @param username  - username 
     * @param KeyHandle - The user could have multiple fido authenticators registered
     *                      successfully. An authentication challenge can pertain
     *                      to only one unique fido authenticator (key handle).
     * @param appidfromDB
     * @param transports
     * @return          - FEReturn object that binds the U2F registration challenge
     *                      parameters in addition to a set of messages that explain
     *                      the series of actions happened during the process.
     * @throws SKFEException - 
     *                      Thrown in case of any error scenario.
     */
    FEreturn execute(String did, 
                    String protocol,
                    String username, 
                    String KeyHandle,
                            String appidfromDB,
                            JsonArray transports) throws SKFEException;
}
