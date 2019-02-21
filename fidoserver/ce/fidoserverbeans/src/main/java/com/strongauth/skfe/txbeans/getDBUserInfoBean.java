/*
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
 */
package com.strongauth.skfe.txbeans;

import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skce.pojos.LDAPUserMetadata;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.entitybeans.FidoUsers;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author pmarathe
 */
@Stateless
public class getDBUserInfoBean implements getDBUserInfoBeanLocal {

    /*
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();
    
    @EJB
    getFidoUserLocal getfidouserbean;
     @EJB
    addFidoUserBeanLocal addfidouserbean;
     
    @Override
    public LDAPUserMetadata execute(Long did, String username) throws SKFEException {
        //  Inputs check
        skfeCommon.inputValidateSKCEDid(Long.toString(did));
        if (username == null || username.trim().isEmpty()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER,Level.WARNING, "SKCE-ERR-1000", "NULL or empty argument for username : " + username);
            throw new SKFEException("NULL or empty argument for username : " + username);
        }

        LDAPUserMetadata authres = null;

        FidoUsers FIDOUser = getfidouserbean.GetByUsername(did, username);
        if (FIDOUser == null) {
            addfidouserbean.execute(did, username);
            FIDOUser = getfidouserbean.GetByUsername(did, username);
        }
        //  Build the auth result object
        authres = new LDAPUserMetadata(username,
                FIDOUser.getUserdn(),
                "",
                "",
                "",
                FIDOUser.getRegisteredEmails(),
                FIDOUser.getPrimaryEmail(),
                FIDOUser.getRegisteredPhoneNumbers(),
                FIDOUser.getPrimaryPhoneNumber(),
                FIDOUser.getTwoStepTarget(),
                FIDOUser.getFidoKeysEnabled(),
                FIDOUser.getTwoStepVerification(),
                did);

        skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "execute");
        return authres;
    }
}
