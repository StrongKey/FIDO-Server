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
 * This EJB is responsible for executing the pre-registration process of the 
 * FIDO U2F protocol. 
 * 
 */
package com.strongkey.skfs.txbeans;

import com.strongkey.skfs.utilities.skfsLogger;
import com.strongkey.skfs.utilities.SKFEException;
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.core.U2FRegistrationChallenge;
import com.strongkey.skfs.utilities.FEreturn;
import java.util.logging.Level;
import javax.ejb.Stateless;

@Stateless
public class u2fPreregisterBean implements u2fPreregisterBeanLocal {

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
     * Executes the pre-registration process which primarily includes generating
     * registration challenge parameters for the given username complying to the
     * protocol specified. did and secretkey are the FIDO domain credentials.
     * 
     * NOTE : The did and secretkey will be used for the production
     * version of the FIDO server software. They can be ignored for the open-source
     * version.
     * @param did       - FIDO domain id
     * @param protocol  - U2F protocol version to comply with.
     * @param username  - username 
     * @return          - FEReturn object that binds the U2F registration challenge
     *                      parameters in addition to a set of messages that explain
     *                      the series of actions happened during the process.
     * @throws SKFEException - 
     *                      Thrown in case of any error scenario.
     */
    @Override
    public FEreturn execute(Long did, 
                            String protocol,
                            String username) throws SKFEException  {
        
        //  Log the entry and inputs
        skfsLogger.entering(skfsConstants.SKFE_LOGGER,classname, "execute");
        skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.FINE, classname, "execute", skfsCommon.getMessageProperty("FIDO-MSG-5001"), 
                        " EJB name=" + classname + 
                        " did=" + did + 
                        " protocol=" + protocol + 
                        " username=" + username);
        
        //  Generate a new U2FRegistrationChallenge object and returns the same
        FEreturn fer = new FEreturn();
        fer.setResponse(new U2FRegistrationChallenge(protocol, username));
        
        //  log the exit and return
        skfsLogger.logp(skfsConstants.SKFE_LOGGER,Level.FINE, classname, "execute", skfsCommon.getMessageProperty("FIDO-MSG-5002"), "");
        skfsLogger.exiting(skfsConstants.SKFE_LOGGER,classname, "execute");
        return fer;
    }
}
