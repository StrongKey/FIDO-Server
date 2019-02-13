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
 * $Date: 
 * $Revision:
 * $Author: mishimoto $
 * $URL: 
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
 *
 *
 */
package com.strongauth.skfe.policybeans;

import com.strongauth.skce.pojos.UserSessionInfo;
import com.strongauth.skfe.entitybeans.FidoKeys;
import com.strongauth.skfe.fido.policyobjects.AuthenticationPolicyOptions;
import com.strongauth.skfe.fido.policyobjects.CounterPolicyOptions;
import com.strongauth.skfe.fido.policyobjects.FidoPolicyObject;
import com.strongauth.skfe.fido2.FIDO2AuthenticatorData;
import com.strongauth.skfe.txbeans.getFidoKeysLocal;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.skfeLogger;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.JsonObject;

@Stateless
public class verifyFido2AuthenticationPolicy implements verifyFido2AuthenticationPolicyLocal {
    
    @EJB
    getCachedFidoPolicyMDSLocal getpolicybean;
    @EJB
    getFidoKeysLocal getfidokeysbean;
    
    @Override
    public void execute(UserSessionInfo userInfo, long did, JsonObject clientJson,
            FIDO2AuthenticatorData authData, FidoKeys signingKey) throws SKFEException {
        //Get policy from userInfo
        FidoPolicyObject fidoPolicy = getpolicybean.getByMapKey(userInfo.getPolicyMapKey()).getFp();
        FidoKeys fk = getfidokeysbean.getByfkid(userInfo.getSkid(), did, userInfo.getUsername(), userInfo.getFkid());
        
        //Verify Counter
        verifyCounter(fidoPolicy.getCounterOptions(), clientJson, authData, signingKey, fidoPolicy.getVersion());
        
        //Verify userVerification was given if required
        verifyUserVerification(fidoPolicy.getAuthenticationOptions(), authData, userInfo.getUserVerificationReq(), fidoPolicy.getVersion());
        
        //TODO add additional checks to ensure the the authentication data meets the standard of the policy
        
        //TODO add checks to ensure the stored information about the key (attestation certificates, MDS, etc) still meets the standard
    }
    
    private void verifyCounter(CounterPolicyOptions counterOp, JsonObject clientJson,
            FIDO2AuthenticatorData authData, FidoKeys signingKey, Integer version) throws SKFEException {
        int oldCounter = signingKey.getCounter();
        int newCounter = authData.getCounterValueAsInt();
        skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                "COUNTER TEST - OLD - NEW = " + oldCounter + " - " + newCounter);
        if(counterOp.getIsCounterRequired()){
            if(oldCounter == 0 && newCounter <= oldCounter){
                throw new SKFEException("Policy requires counter");
            }
        }
        if(counterOp.getIsCounterIncreaseRequired()){
            if((oldCounter != 0 && newCounter <= oldCounter)){
                throw new SKFEException("Policy requires counter increase");
            }
        }
    }
    
    private void verifyUserVerification(AuthenticationPolicyOptions authOp, 
            FIDO2AuthenticatorData authData, String userVerificationReq, Integer version){
        //Default blank to Webauthn defined defaults
        userVerificationReq = (userVerificationReq == null) ? skfeConstants.POLICY_CONST_PREFERRED : userVerificationReq;
        
        //Double check that what was stored in UserSessionInfo is valid for the policy
        if (!authOp.getUserVerification().contains(userVerificationReq)) {
            throw new IllegalArgumentException("Policy Exception: Preauth userVerificationRequirement does not meet policy");
        }

        //If User Verification was required, verify it was provided
        if (userVerificationReq.equalsIgnoreCase(skfeConstants.POLICY_CONST_REQUIRED) && !authData.isUserVerified()) {
            throw new IllegalArgumentException("User Verification required by policy");
        }
    }
}
