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
 * An EJB to call the preregister() webservice operation on the StrongKey
 * FIDO Engine (SKFE) module.
 *
 */
package com.strongauth.pki2fido.ejb;

import com.strongauth.pki2fido.utilities.Common;
import com.strongauth.pki2fido.utilities.Constants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;

/************************************************************************
 *                                                        d8b          888
 *                                                        Y8P          888
 *                                                                     888
 *    88888b.  888d888  .d88b.  888d888  .d88b.   .d88b.  888 .d8888b  888888  .d88b.  888d888
 *    888 "88b 888P"   d8P  Y8b 888P"   d8P  Y8b d88P"88b 888 88K      888    d8P  Y8b 888P"
 *    888  888 888     88888888 888     88888888 888  888 888 "Y8888b. 888    88888888 888
 *    888 d88P 888     Y8b.     888     Y8b.     Y88b 888 888      X88 Y88b.  Y8b.     888
 *    88888P"  888      "Y8888  888      "Y8888   "Y88888 888  88888P'  "Y888  "Y8888  888
 *    888                                             888
 *    888                                        Y8b d88P
 *    888                                         "Y88P"
 ***********************************************************************/

@Stateless
public class preregister implements preregisterLocal {
     
    /**
     * Step-1 for FIDO U2F Authenticator registration.  
     * 
     * This methods makes a preregister() REST web-service call (denoted by
     * Constants.PRE_REGISTER_ENDPOINT) to SKFE, which returns a challenge.
     * The response from the SKFE is a JSON string whose format is:
     * 
     *      {
     *          "Challenge" : "....",
     *          "Message" : "....",
     *          "Error" : "...."
     *      }
     *
     * It then parses through the SKFE response to extract the "Challenge", a
     * JSON string containing a FIDO-U2F compliant challenge to be digitally 
     * signed by the Token during registration.
     *
     * @param username - String Name of the user attempting to register a
     * FIDO U2F authenticator to his/her account.
     * @return JsonObject Response sent back by the SKFE server.
     * @throws URISyntaxException, ProtocolException, MalformedURLException, 
     * IOException
     */
    @Override
    public JsonObject execute(final String username) throws URISyntaxException, 
            ProtocolException, MalformedURLException, IOException 
    {
        // Check parameter
        if (username == null || username.isEmpty()) {
            Common.log(Level.WARNING, "PKI2FIDO-ERR-1001", "Null username");
            return null;
        }

        // Private method to call the SKFE preregister() web-service 
        String skferesponse = Common.getFidoChallenge(username, Constants.PREREGISTER);
        JsonObject error = Common.checkForError(skferesponse);
        if (error != null)
            return error;
        
        // Read the "Challenge", a JsonObject element in the response
        JsonObject challenge = (JsonObject) Common.getJsonValue(skferesponse, "Challenge", "JsonObject");
        if (challenge == null)
            return Json.createObjectBuilder().add(Constants.REST_SERVICE_ERROR, "Challenge is empty").build();
        
        // Return response from SKFE
        Common.log(Level.INFO, "PKI2FIDO-MSG-3000", "Preregister() challenge from SKFE: \n" + challenge);
        return Common.stringToJSON(skferesponse);
    }
}
