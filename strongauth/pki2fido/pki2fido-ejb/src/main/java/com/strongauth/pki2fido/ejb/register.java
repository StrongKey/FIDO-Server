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
 * An EJB to call the register() webservice operation on the StrongKey
 * FIDO Engine (SKFE) module.
 *
 */
package com.strongauth.pki2fido.ejb;

import com.strongauth.pki2fido.utilities.Common;
import com.strongauth.pki2fido.utilities.Constants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.json.JsonObject;

@Stateless
public class register implements registerLocal {

    /************************************************************************
     *                                d8b          888
     *                                Y8P          888
     *                                             888
     *      888d888  .d88b.   .d88b.  888 .d8888b  888888  .d88b.  888d888
     *      888P"   d8P  Y8b d88P"88b 888 88K      888    d8P  Y8b 888P"
     *      888     88888888 888  888 888 "Y8888b. 888    88888888 888
     *      888     Y8b.     Y88b 888 888      X88 Y88b.  Y8b.     888
     *      888      "Y8888   "Y88888 888  88888P'  "Y888  "Y8888  888
     *                            888
     *                       Y8b d88P
     *                        "Y88P"
     ************************************************************************/
     
    /**
     * Step-2 for FIDO U2F Authenticator registration.  
     * 
     * * This methods makes a register() REST web-service call (denoted by
     * Constants.REGISTER_ENDPOINT) to SKFE with the signed challenge from
     * the preregister() call earlier.  The preregister() and register() 
     * webservice methods on the SKFE are time-linked; meaning, register() 
     * should be called within a limited time after preregister() is finished - 
     * otherwise, the user session is invalidated on SKFE.
     *
     * @param location String containing information from where the user is
     * attempting to register the authenticator from.  This is used to store
     * meta-data about the registration event.
     * @param tokendata JsonObject containing the response from the FIDO U2F
     * Token after it generates a new key-pair and digitally signs the SKFE
     * challenge from preregister() with the newly minted private-key
     * @return String JsonObject response from SKFE is shown below:
     *
     * If the registration request is successful:
     * 
     *      {
     *          "Response" : "Successfully processed registration response",
     *          "Message" : "....",
     *          "Error" : "...."
     *      }
     *
     * If the registration request failed:
     * 
     *      {
     *          "Response" : "",
     *          "Message" : "....",
     *          "Error" : "FIDO-ERR-[CODE]: Error registering key"
     *      }
     * 
     * @throws MalformedURLException URISyntaxException IOException
     */
    @Override
    public String execute(final String location, final JsonObject tokendata) throws 
                            MalformedURLException, URISyntaxException, IOException 
    {
        if (location == null || location.isEmpty() || tokendata == null) {
            Common.log(Level.WARNING, "PKI2FIDO-ERR-1001", "Null location and/or tokendata");
            return null;
        }

        // Submit the FIDO Authenticator response to register a new key
        String skferesponse = Common.submitFidoResponse(location, tokendata, Constants.REGISTER);
        Common.log(Level.INFO, "PKI2FIDO-MSG-3000", "Register() response from SKFE: " + skferesponse);
        return skferesponse;
    }
}
