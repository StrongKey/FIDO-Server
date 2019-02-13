/**
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License, as published by the Free Software Foundation and
 *  available at http://www.fsf.org/licensing/licenses/lgpl.html,
 *  version 2.1 or above.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001-2016 StrongAuth, Inc.
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
 * 
 * *********************************************
 *                     888
 *                     888
 *                     888
 *   88888b.   .d88b.  888888  .d88b.  .d8888b
 *   888 "88b d88""88b 888    d8P  Y8b 88K
 *   888  888 888  888 888    88888888 "Y8888b.
 *   888  888 Y88..88P Y88b.  Y8b.          X88
 *   888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * *********************************************
 *
 * An EJB to call the getKeyInfo() webservice operation on the StrongKey
 * FIDO Engine (SKFE) module.
 *
 */
package com.strongauth.pki2fido.ejb;

import com.strongauth.pki2fido.utilities.Common;
import com.strongauth.pki2fido.utilities.Constants;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.json.Json;

/************************************************************************************
                  888    888    d8P                    8888888           .d888          
                  888    888   d8P                       888            d88P"           
                  888    888  d8P                        888            888             
 .d88b.   .d88b.  888888 888d88K      .d88b.  888  888   888   88888b.  888888  .d88b.  
d88P"88b d8P  Y8b 888    8888888b    d8P  Y8b 888  888   888   888 "88b 888    d88""88b 
888  888 88888888 888    888  Y88b   88888888 888  888   888   888  888 888    888  888 
Y88b 888 Y8b.     Y88b.  888   Y88b  Y8b.     Y88b 888   888   888  888 888    Y88..88P 
 "Y88888  "Y8888   "Y888 888    Y88b  "Y8888   "Y88888 8888888 888  888 888     "Y88P"  
     888                                           888                                  
Y8b d88P                                      Y8b d88P                                  
 "Y88P"                                        "Y88P"                                    
*************************************************************************************/
@Stateless
public class getKeyInfo implements getKeyInfoLocal {

    @Override
    public String execute(String username)
    {    
        if (username == null || username.isEmpty()) {
            Common.log(Level.WARNING, "PKI2FIDO-ERR-1001", "Null username");
            return null;
        }

        // Create a SKFE compliant payload object to pass in the username.
        String payload = Json.createObjectBuilder()
                .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, username)
                .build().toString();

        // Call webservice
        String response = null;
        try {
            response = Common.callFido(Constants.GET_KEYS_INFO, payload);
            if (response == null || response.isEmpty())
                Common.log(Level.WARNING, "PKI2FIDO-ERR-3000", "Server error: Check application logs or contact support");
            else
                Common.log(Level.INFO, "PKI2FIDO-MSG-3000", "getKeyInfo response from SKFE: \n" + response);
        } catch (Exception ex) {
            Common.log(Level.WARNING, "PKI2FIDO-ERR-3000", "Server error : " + ex.getLocalizedMessage());
        }
        return response;
    }
}
