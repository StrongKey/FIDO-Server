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
 * $Date: $
 * $Revision: $
 * $Author: $
 * $URL: $
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

import com.strongkey.appliance.entitybeans.Domains;
import com.strongkey.appliance.utilities.applianceCommon;
import com.strongkey.appliance.utilities.applianceMaps;
import com.strongkey.skfs.utilities.skfsCommon;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class startServices {

    @EJB
    getDomainsBeanLocal getdomejb;

    final private String SIGN_SUFFIX = skfsCommon.getConfigurationProperty("skfs.cfg.property.signsuffix");

    @PostConstruct
    public void initialize() {

        String standalone = skfsCommon.getConfigurationProperty("skfs.cfg.property.standalone.fidoengine");
        if (standalone.equalsIgnoreCase("true")) {
            Collection<Domains> domains = getdomejb.getAll();

            if (domains != null) {
                for (Domains d : domains) {
                    Long did = d.getDid();

                    // Cache domain objects
                    applianceMaps.putDomain(did, d);

//                    cryptoCommon.putPublicKey(did + SIGN_SUFFIX, cryptoCommon.getPublicKeyFromCertificate(d.getSigningCertificate()));
                }
            }
            
            //set replication to false
            applianceCommon.setReplicateStatus(Boolean.FALSE);
        }
    }
}
