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

package com.strongauth.skfe.fido.policyobjects;

import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skce.pojos.MDSEndpoint;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonString;

public class MdsPolicyOptions {
    private final ArrayList<MDSEndpoint> endpoints;
    private final ArrayList<String> allowedCertificationLevel;
    
    private MdsPolicyOptions(ArrayList<MDSEndpoint> endpoints, ArrayList<String> allowedCertificationLevel){
        this.endpoints = endpoints;
        this.allowedCertificationLevel = allowedCertificationLevel;
    }

    public ArrayList<MDSEndpoint> getEndpoints() {
        return endpoints;
    }

    public ArrayList<String> getAllowedCertificationLevel() {
        return allowedCertificationLevel;
    }
    
    public static MdsPolicyOptions parse(JsonObject mdsJson) {
        if(mdsJson == null){
            return null;
        }
        
        return new MdsPolicyOptions.MdsPolicyOptionsBuilder(
                new ArrayList<>(mdsJson.getJsonArray(skfeConstants.POLICY_MDS_ENDPOINTS).stream()
                        .map(x -> (JsonObject) x)
                        .map(x -> new MDSEndpointObject(x.getString(skfeConstants.POLICY_MDS_ENDPOINT_URL, null), 
                                x.getString(skfeConstants.POLICY_MDS_ENDPOINT_TOKEN, null)))
                        .collect(Collectors.toList())),
                new ArrayList<>(mdsJson.getJsonArray(skfeConstants.POLICY_MDS_CERTIFICATION).stream().map(x -> ((JsonString) x).getString()).collect(Collectors.toList())))
                .build();
    }
    
    public static class MdsPolicyOptionsBuilder{
        private final ArrayList<MDSEndpoint> builderEndpoints;
        private final ArrayList<String> builderAllowedCertificateLevel;
        
        public MdsPolicyOptionsBuilder(ArrayList<MDSEndpoint> endpoints, ArrayList<String> allowedCertificationLevel){
            this.builderEndpoints = endpoints;
            this.builderAllowedCertificateLevel = allowedCertificationLevel;
            
        }
        
        public MdsPolicyOptions build(){
            return new MdsPolicyOptions(builderEndpoints, builderAllowedCertificateLevel);
        }
    }
}
