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

import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skce.pojos.MDSClient;
import com.strongauth.skfe.entitybeans.FidoPolicies;
import com.strongauth.skfe.entitybeans.FidoPoliciesPK;
import com.strongauth.skfe.fido.policyobjects.FidoPolicyObject;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.utilities.skceMaps;
import com.strongauth.skfe.pojos.FidoPolicyMDSObject;
import com.strongkey.fido2mds.MDS;
import java.util.Collection;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class cachePolicies {
    
    @EJB 
    getFidoPolicyLocal getFidoPolicies;

    @PostConstruct
    public void initialize() {
        Collection<FidoPolicies> fpCol = getFidoPolicies.getAllActive();
        for(FidoPolicies fp: fpCol){
            FidoPoliciesPK fpPK = fp.getFidoPoliciesPK();
            try{
                FidoPolicyObject fidoPolicyObject = FidoPolicyObject.parse(
                        fp.getPolicy(),
                        fp.getVersion(),
                        (long) fpPK.getDid(),
                        (long) fpPK.getSid(),
                        (long) fpPK.getPid(),
                        fp.getStartDate(),
                        fp.getEndDate());
                
                MDSClient mds = null;
                if(fidoPolicyObject.getMdsOptions() != null){
                    mds = new MDS(fidoPolicyObject.getMdsOptions().getEndpoints());
                }
                 
                String mapkey = fpPK.getSid() + "-" + fpPK.getDid() + "-" + fpPK.getPid();
                skceMaps.getMapObj().put(skfeConstants.MAP_FIDO_POLICIES, mapkey, new FidoPolicyMDSObject(fidoPolicyObject, mds));
            }
            catch(SKFEException ex){
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "SKCE-ERR-1000", "Unable to cache policy: " + ex);
            }
        }
        
    }
}
