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
 * EJB that handles the actual registration process. This ejb 
 *
 */

package com.strongauth.skfe.txbeans;

import com.strongauth.appliance.utilities.applianceMaps;
import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.core.U2FRegistrationResponse;
import com.strongauth.skfe.utilities.FEreturn;
import java.util.logging.Level;
import javax.ejb.Stateless;

@Stateless
public class u2fRegisterBean implements u2fRegisterBeanLocal {

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
     * Method that builds a u2f registration response object and processes the 
     * same.
     * @param did       - FIDO domain id
     * @param protocol  - U2F protocol version to comply with.
     * @param regresponseJson  - U2F Reg Response parameters as a json string
     * @return          - FEreturn object with result
     * @throws SKFEException 
     *                          - In case of any error
     */
    @Override
    public FEreturn execute(String did, 
                            String protocol,
                            String regresponseJson) throws SKFEException {

        String appid = applianceMaps.getDomain(Long.parseLong(did)).getSkfeAppid();
        //  Log the entry and inputs
        skfeLogger.entering(skfeConstants.SKFE_LOGGER,classname, "execute");
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", skfeCommon.getMessageProperty("FIDO-MSG-5001"), 
                        " EJB name=" + classname + 
                        " did=" + did + 
                        " protocol=" + protocol + 
                        " regresponseJson=" + regresponseJson);
        
        FEreturn fr = new FEreturn();
        
        //  Input checks
        if (protocol == null || protocol.isEmpty()) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", skfeCommon.getMessageProperty("FIDO-ERR-5001"), protocol);
            fr.append(skfeCommon.getMessageProperty("FIDO-ERR-5001") + "protocol=" + protocol);
            return fr;
        }
        
        if (regresponseJson == null || regresponseJson.isEmpty()) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", skfeCommon.getMessageProperty("FIDO-ERR-5001"), regresponseJson);
            fr.append(skfeCommon.getMessageProperty("FIDO-ERR-5001") + "registration response =" + regresponseJson);
            return fr;
        }

        //  Build a U2FRegistrationResponse object and process the same
        U2FRegistrationResponse regresp = new U2FRegistrationResponse(protocol, regresponseJson);
        if (regresp.verify(appid)) {
            fr.setResponse(regresp);
        }

        //  log the exit and return
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "execute", skfeCommon.getMessageProperty("FIDO-MSG-5002"), classname);
        skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "execute");
        return fr;
    }
}
