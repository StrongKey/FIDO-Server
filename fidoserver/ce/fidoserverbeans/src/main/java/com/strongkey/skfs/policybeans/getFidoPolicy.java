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

import com.strongkey.skfs.entitybeans.FidoPolicies;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class getFidoPolicy implements getFidoPolicyLocal {
    /**
     * Persistence context for derby
     */
    @PersistenceContext
    private EntityManager em; 
    
    
    @Override
    public FidoPolicies getbyPK(Long did, Long sid, Long pid) {
        try {
            Query q = em.createNamedQuery("FidoPolicies.findBySidDidPid");
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("did", did);
            q.setParameter("sid", sid);
            q.setParameter("pid", pid);
            FidoPolicies fp = (FidoPolicies) q.getSingleResult();
//            if (fp != null) {                       //TODO verify signature
//                verifyDBRecordSignature(did, fp);
//            }
            return fp;
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public Collection<FidoPolicies> getAllActive() {
        try {
            Query q = em.createNamedQuery("FidoPolicies.findByStatus");
            q.setHint("javax.persistence.cache.storeMode", "REFRESH");
            q.setParameter("status", "Active");
            Collection<FidoPolicies> fidoPoliciesColl = q.getResultList();
            Collection<FidoPolicies> validPoliciesColl = fidoPoliciesColl;
//            if(!fidoPoliciesColl.isEmpty()){      //TODO verify signature
//                for (FidoPolicies fp : fidoPoliciesColl) {
//                    if(fp!=null){
//                        try {
//                            verifyDBRecordSignature(did, fp);
//                        } catch (SKFEException ex) {
//                            validPoliciesColl.remove(fp);
//                        }
//                    }
//                }
//            }
            return validPoliciesColl;
        } catch (NoResultException ex) {
            return null;
        }
    }
}
