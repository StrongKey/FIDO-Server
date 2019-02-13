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
 * An EJB to call the deregister() webservice operation on the StrongKey
 * FIDO Engine (SKFE) module.
 *
 */
package com.strongauth.pki2fido.ejb;

import com.strongauth.pki2fido.utilities.Common;
import com.strongauth.pki2fido.utilities.Constants;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/***************************************************************************
        888                                    d8b          888                     
        888                                    Y8P          888                     
        888                                                 888                     
    .d88888  .d88b.  888d888  .d88b.   .d88b.  888 .d8888b  888888  .d88b.  888d888 
   d88" 888 d8P  Y8b 888P"   d8P  Y8b d88P"88b 888 88K      888    d8P  Y8b 888P"   
   888  888 88888888 888     88888888 888  888 888 "Y8888b. 888    88888888 888     
   Y88b 888 Y8b.     888     Y8b.     Y88b 888 888      X88 Y88b.  Y8b.     888     
    "Y88888  "Y8888  888      "Y8888   "Y88888 888  88888P'  "Y888  "Y8888  888     
                                           888                                      
                                      Y8b d88P                                      
                                       "Y88P"                                       
****************************************************************************/
@Stateless
public class deregister implements deregisterLocal {
    
    /**
     * Deregister key associated with username and random (?) ID
     * @param username String
     * @param randomid
     * @return 
     */
    @SuppressWarnings("null")
    @Override
    public String execute(String username, JsonArray randomids) {
        
        String response = null;
        if (username == null || username.isEmpty() || randomids == null || randomids.isEmpty()) {
            Common.log(Level.WARNING, "PKI2FIDO-ERR-1001", "Username");
            return null;
        }

        // For each key-handle, call the deregister webservice
        for (int i = 0; i < randomids.size(); i++) 
        {    
            JsonObject deregisterJSON = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, username)
                    .add(Constants.JSON_KEY_SERVLET_INPUT_RANDOMID, randomids.getString(i))
                    .build();
            
            String payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, deregisterJSON)
                    .build().toString();

            // Call FIDO service 
            try {
                response = Common.callFido(Constants.DEREGISTER, payload);
                if (response !=null || !response.isEmpty())
                    Common.log(Level.INFO, "PKI2FIDO-MSG-3000", "Deregister response from SKFE: \n" + response);
                else
                    Common.log(Level.WARNING, "PKI2FIDO-ERR-3000", "Server error: Check application logs or contact support");
            } catch (Exception ex) {
                Common.log(Level.WARNING, "PKI2FIDO-ERR-3000", "Server error: " + ex.getLocalizedMessage());
            }
        }
        return response;
    }
}
