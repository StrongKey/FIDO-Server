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
package com.strongkey.skfs.policybeans;

import com.strongkey.skfe.entitybeans.FidoKeys;
import com.strongkey.skfs.fido.policyobjects.FidoPolicyObject;
import com.strongkey.skfs.utilities.SKFEException;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skce.utilities.skceMaps;
import com.strongkey.skfs.pojos.FidoPolicyMDSObject;
import com.strongkey.skfs.txbeans.getFidoKeysLocal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class getCachedFidoPolicyMDSObject implements getCachedFidoPolicyMDSLocal {
    
    @EJB
    getFidoKeysLocal getFidoKeysBean;

    //TODO fix logic to return the FidoPolicyMDSObject rather than the policy
    @Override
    public FidoPolicyObject getPolicyByDidUsername(Long did, String username){       
        FidoKeys fk = null;
        try {
            fk = getFidoKeysBean.getNewestKeyByUsernameStatus(did, username, "Active");
        } catch (SKFEException ex) {
            Logger.getLogger(getCachedFidoPolicyMDSObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lookupPolicyFromNewestKey(did, fk);
    }
    
    @Override
    public FidoPolicyMDSObject getByMapKey(String policyMapKey) {
        return (FidoPolicyMDSObject) skceMaps.getMapObj().get(skfsConstants.MAP_FIDO_POLICIES, policyMapKey);
    }
    
    
    //TODO optimize. Current thought is that inefficiently performing a look up on less than
    //10 Active policies is cheaper than efficiently looking up the correct policy
    //from the DB and parsing from the DB into an object.
    //TODO if the policy's end date has passed, the policy should be set to Inactive.
    private FidoPolicyObject lookupPolicyFromNewestKey(Long did, FidoKeys fk){
        //Only check policies from the listed domain, that have started, and whose end_date has not passed
        Date currentDate = new Date();
        Collection<FidoPolicyObject> fpCol
                = ((Collection<FidoPolicyMDSObject>) skceMaps.getMapObj().values(skfsConstants.MAP_FIDO_POLICIES))
                .stream()
                .map(fpm -> fpm.getFp())
                .filter(fp -> fp.getDid().equals(did))
                .filter(fp -> fp.getStartDate().before(currentDate))
                .filter(fp -> fp.getEndDate() == null || fp.getEndDate().after(currentDate))
                .collect(Collectors.toList());
        
        //If the user has no registered keys, return policy with the latest start_date
        if(fk == null){
            return findNewestPolicy(fpCol);
        }
        else{   //attempt to find policy based on registration time of key
            FidoPolicyObject result = findPolicyDuringRegistration(fpCol, fk);

            if (result == null) {
                return findOldestPolicySinceKeyCreation(fpCol, fk);
            }

            return result;
        }
    }
    
    //Find the newest Active Policy
    private FidoPolicyObject findNewestPolicy(Collection<FidoPolicyObject> fpCol) {
        try {
            return fpCol.stream()
                    .max(Comparator.comparing(FidoPolicyObject::getStartDate))
                    .get();
        } catch (NullPointerException | NoSuchElementException ex) {
            return null;
        }
    }
    
    //Find the Active policy whose start_date is before the key's creation date
    //and whose end_date is after the key's creation date. If multiple policies
    //are found, use returns the policy with the later start_date. If no policies
    //are found, return null
    private FidoPolicyObject findPolicyDuringRegistration(Collection<FidoPolicyObject> fpCol, FidoKeys fk){
        try {
            return fpCol.stream()
                    .filter(fp -> fp.getStartDate().before(fk.getCreateDate()))
                    .filter(fp -> fp.getEndDate() == null || fp.getEndDate().after(fk.getCreateDate()))
                    .max(Comparator.comparing(FidoPolicyObject::getStartDate))
                    .get();
        } catch (NullPointerException | NoSuchElementException ex) {
            return null;
        }
    }
    
    //Return the first Active policy whose start_date is after the key's creation date
    private FidoPolicyObject findOldestPolicySinceKeyCreation(Collection<FidoPolicyObject> fpCol, FidoKeys fk){
        try {
            return fpCol.stream()
                    .filter(fp -> fp.getStartDate().after(fk.getCreateDate()))
                    .min(Comparator.comparing(FidoPolicyObject::getStartDate))
                    .get();
        } catch (NullPointerException | NoSuchElementException ex) {
            return null;
        }
    }
}
