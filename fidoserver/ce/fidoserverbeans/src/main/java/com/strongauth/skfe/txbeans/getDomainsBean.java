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
 * This Session EJB is responsible for getting entity objects from the
 * database, verifying their integrity and returning them to the calling
 * applications - which will usually be the servlets.
 *
 */

package com.strongauth.skfe.txbeans;

import com.strongauth.appliance.entitybeans.Domains;
import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import java.util.Collection;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * This ejb is meant to retrieve domain entries from the database.
 * There are multiple methods with different types of lookup.
 * 
 */
@Stateless
public class getDomainsBean implements getDomainsBeanLocal, getDomainsBeanRemote {

    /**
     ** This class's name - used for logging
     **/
    private final String classname = this.getClass().getName();

    /**
     ** Resources used by this bean
     **/
    @PersistenceContext private EntityManager   em;         // For JPA management
    
    /************************************************************************
             888 888
             888 888
             888 888
     8888b.  888 888
        "88b 888 888
    .d888888 888 888
    888  888 888 888
    "Y888888 888 888
     *************************************************************************/

    /**
     * The method returns all Domain objects.
     * @return Collection of Domains objects if any; null otherwise
     */
    @Override
    public Collection<Domains> getAll() {
        skfeLogger.entering(skfeConstants.SKFE_LOGGER,classname, "getAll");
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "getAll", skfeCommon.getMessageProperty("SKCE-MSG-1023"), 
                "createNamedQuery(Domains.getAll)");
        try {
            return (Collection<Domains>) em.createNamedQuery("Domains.findAll").getResultList();
        } catch (NoResultException ex) {
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "findAll");
            return null;
        }
    }
    
    /************************************************************************
     *
     *  888               8888888b.  d8b      888
     *  888               888  "Y88b Y8P      888
     *  888               888    888          888
     *  88888b.  888  888 888    888 888  .d88888
     *  888 "88b 888  888 888    888 888 d88" 888
     *  888  888 888  888 888    888 888 888  888
     *  888 d88P Y88b 888 888  .d88P 888 Y88b 888
     *  88888P"   "Y88888 8888888P"  888  "Y88888
     *                888
     *           Y8b d88P
     *            "Y88P"
     ************************************************************************/
    /**
     * The method finds a single Domains entity based on the primary
     * key - the Domain ID.  It does not return any children objects
     * in the Domain object.
     *
     * @param did Short - the unique identifier of the Domain
     * @return Domains - the entity that identifies a Domain in SKCE
     */
    @Override
    public Domains byDid(final Long did) {
        skfeLogger.entering(skfeConstants.SKFE_LOGGER,classname, "byDid");
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "byDid", skfeCommon.getMessageProperty("SKCE-MSG-1023"), 
                "createNamedQuery(Domains.findByDid)");
        try {
            return (Domains) em.createNamedQuery("Domains.findByDid")
                    .setParameter("did", did).getSingleResult();
        } catch (NoResultException ex) {
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "byDid");
            return null;
        }
    }

    /**
     * Checks if a domain entry with the did exists in the database.
     * 
     * @param did   Short containing the domain id to be looked up
     * @return boolean containing the search result
     */
    @Override
    public boolean domainExists(final Long did) {
        skfeLogger.entering(skfeConstants.SKFE_LOGGER,classname, "byDid");
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "byDid", skfeCommon.getMessageProperty("SKCE-MSG-1023"), 
                "createNamedQuery(Domains.findByDid)");
        try {
            if(em.createNamedQuery("Domains.findByDid")
                    .setParameter("did", did)
                    .getSingleResult() !=null){
                return true;
            }
        } catch (NoResultException ex) {
            skfeLogger.exiting(skfeConstants.SKFE_LOGGER,classname, "byDid");
        }
        return false;
    }
    
    /************************************************************************
     *
     *   8888888b.                              888            
     *   888   Y88b                             888            
     *   888    888                             888            
     *   888   d88P .d88b. 88888b.d88b.  .d88b. 888888 .d88b.  
     *   8888888P" d8P  Y8b888 "888 "88bd88""88b888   d8P  Y8b 
     *   888 T88b  88888888888  888  888888  888888   88888888 
     *   888  T88b Y8b.    888  888  888Y88..88PY88b. Y8b.     
     *   888   T88b "Y8888 888  888  888 "Y88P"  "Y888 "Y8888  
     * 
     * 
     * 
     *   888b     d888        888   888                  888         
     *   8888b   d8888        888   888                  888         
     *   88888b.d88888        888   888                  888         
     *   888Y88888P888 .d88b. 88888888888b.  .d88b.  .d88888.d8888b  
     *   888 Y888P 888d8P  Y8b888   888 "88bd88""88bd88" 88888K      
     *   888  Y8P  88888888888888   888  888888  888888  888"Y8888b. 
     *   888   "   888Y8b.    Y88b. 888  888Y88..88PY88b 888     X88 
     *   888       888 "Y8888  "Y888888  888 "Y88P"  "Y88888 88888P' 
     * 
     ************************************************************************/
    /**
     * Re-direct for the method in remote interface
     * @return Collection of Domains objects if any; null otherwise
     */
    @Override
    public Collection<Domains> remoteGetAll() {
        return getAll();
    }
    
    /**
     * Re-direct for the method in remote interface
     * @return Collection of Domains objects if any; null otherwise
     */
    @Override
    public Domains remoteByDid(final Long did) {
        return byDid(did);
    }
    
    /**
     * Re-direct for the method in remote interface
     * @return Collection of Domains objects if any; null otherwise
     */
    @Override
    public boolean remoteDomainExists(final Long did) {
        return domainExists(did);
    }
}
