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
 * Remote interface for getDomainsBean
 * 
 */

package com.strongauth.skfe.txbeans;

import com.strongauth.appliance.entitybeans.Domains;
import java.util.Collection;
import javax.ejb.Remote;

/**
 * Remote interface for getDomainsBean
 */
@Remote
public interface getDomainsBeanRemote {
    
    /**
     * The method returns all Domain objects.
     * @return Collection<Domains> 
     */
    Collection<Domains> remoteGetAll();
    
    /**
     * The method finds a single Domains entity based on the primary
     * key - the Domain ID.  It does not return any children objects
     * in the Domain object.
     *
     * @param did Short - the unique identifier of the Domain
     * @return Domains - the entity that identifies a Domain in the SKLES
     */
    Domains remoteByDid(final Long did);
    
    /**
     * Checks if a domain entry with the did exists in the database.
     * 
     * @param did   Short containing the domain id to be looked up
     * @return boolean containing the search result
     */
    boolean remoteDomainExists(final Long did);
}
