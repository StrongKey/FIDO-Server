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
package com.strongkey.skfs.txbeans;

import com.strongkey.appliance.entitybeans.Servers;
import com.strongkey.skfs.utilities.skfsLogger;
import com.strongkey.skfs.utilities.SKFEException;
import com.strongkey.skfs.utilities.skfsConstants;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;


@Stateless
public class getServerBean implements getServerBeanLocal {

    /**
     ** This class's name - used for logging & not persisted
     **/
    private final String classname = this.getClass().getName();
    
        /**
     ** Resources used by this bean
     **/
    @PersistenceContext private EntityManager   em;         // For JPA management

    /**
     * The method finds a Collection of Servers that are active subscribers and NOT this FQDN
     *
     * @param fqdn String the unique name of the Server to exclude from the search
     * @return Collection - a collection of active SAKA subscribers
     * @throws com.strongauth.skce.utilities.SKFEException
     */
    @Override
     public Collection<Servers> byActiveSubscribers(String fqdn) throws SKFEException {
        skfsLogger.entering(skfsConstants.SKFE_LOGGER,classname, "byActiveSubscribers");
        try {
            TypedQuery<Servers> q = em.createNamedQuery("Servers.findByActiveSubscribers", Servers.class);
            q.setParameter("fqdn", fqdn);
            return q.getResultList();
        } catch (NoResultException ex) {
            skfsLogger.exiting(skfsConstants.SKFE_LOGGER,classname, "byActiveSubscribers");
            return null;
        }
    }

     /**
     * The method finds a single Servers entity based on the name of the
     * Server.
     *
     * @param name String the unique name of the Server
     * @return Servers - a Server in the enterprise
     */
    @Override
     public Servers byFqdn(String fqdn) throws SKFEException {
        skfsLogger.entering(skfsConstants.SKFE_LOGGER,classname, "byFqdn");
        try {
            return (Servers) em.createNamedQuery("Servers.findByFqdn").setParameter("fqdn", fqdn).getSingleResult();
        } catch (NoResultException ex) {
            skfsLogger.exiting(skfsConstants.SKFE_LOGGER,classname, "byFqdn");
            return null;
        }
    }
}
