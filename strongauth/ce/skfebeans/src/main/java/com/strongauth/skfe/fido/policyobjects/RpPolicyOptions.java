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
import javax.json.JsonObject;

public class RpPolicyOptions {
    private final String name;
    private final String id;
    private final String icon;
    
    private RpPolicyOptions(String name, String id, String icon){
        this.name = name;
        this.id = id;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }
    
    public static RpPolicyOptions parse(JsonObject rpJson) {
        return new RpPolicyOptions.RpPolicyOptionsBuilder(
                rpJson.getString(skfeConstants.POLICY_RP_NAME, null))
                .setId(rpJson.getString(skfeConstants.POLICY_RP_ID, null))
                .setIcon(rpJson.getString(skfeConstants.POLICY_RP_ICON, null))
                .build();
    }
    
    public static class RpPolicyOptionsBuilder{
        private final String builderName;
        private String builderId;
        private String builderIcon;
        
        public RpPolicyOptionsBuilder(String name){
            this.builderName = name;
        }
        
        public RpPolicyOptionsBuilder setId(String id) {
            this.builderId = id;
            return this;
        }
        
        public RpPolicyOptionsBuilder setIcon(String icon) {
            this.builderIcon = icon;
            return this;
        }
        
        public RpPolicyOptions build(){
            return new RpPolicyOptions(builderName, builderId, builderIcon);
        }
    }
}
