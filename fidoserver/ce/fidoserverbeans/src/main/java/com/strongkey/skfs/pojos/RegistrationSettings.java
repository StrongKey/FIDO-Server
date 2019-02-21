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

package com.strongkey.skfs.pojos;

import com.strongkey.skfs.utilities.skfsCommon;
import java.util.Base64;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class RegistrationSettings {
    private final Integer alg;
    private final Integer kty;
    private final Integer crv;
    private final Boolean up;
    private final Boolean uv;
    private final String attestationFormat;
    private final String attestationType;
    //private final String[] extensions;        //TODO
    
    private RegistrationSettings(Integer alg, Integer kty, Integer crv, Boolean up, Boolean uv,
            String attestationFormat, String attestationType){
        this.alg = alg;
        this.kty = kty;
        this.crv = crv;
        this.up = up;
        this.uv = uv;
        this.attestationFormat = attestationFormat;
        this.attestationType = attestationType;
    }

    public Integer getAlg() {
        return alg;
    }

    public Integer getKty() {
        return kty;
    }

    public Integer getCrv() {
        return crv;
    }

    public Boolean isUp() {
        return up;
    }

    public Boolean isUv() {
        return uv;
    }

    public String getAttestationFormat() {
        return attestationFormat;
    }

    public String getAttestationType() {
        return attestationType;
    }
    
    public static RegistrationSettings parse(String registrationSettings, Integer registrationVersion){
        String decodedrs = new String(Base64.getUrlDecoder().decode(registrationSettings));
        JsonObject rsJson = skfsCommon.getJsonObjectFromString(decodedrs);
        return new RegistrationSettingsBuilder()
                .setAlg((rsJson.getJsonNumber("ALG") == null)? null : rsJson.getJsonNumber("ALG").intValue())
                .setKty((rsJson.getJsonNumber("KTY") == null)? null : rsJson.getJsonNumber("KTY").intValue())
                .setCrv((rsJson.getJsonNumber("CRV") == null)? null : rsJson.getJsonNumber("CRV").intValue())
                .setUp(rsJson.containsKey("UP") ? rsJson.getBoolean("UP") : null)        //TODO make this check more robust
                .setUv(rsJson.containsKey("UV") ? rsJson.getBoolean("UV") : null)        //TODO make this check more robust
                .setAttestationFormat(rsJson.getString("attestationFormat", null))
                .setAttestationType(rsJson.getString("attestationType", null))
                .build();
    }
    
    private static class RegistrationSettingsBuilder{
        private Integer builderAlg;
        private Integer builderKty;
        private Integer builderCrv;
        private Boolean builderUp;
        private Boolean builderUv;
        private String builderAttestationFormat;
        private String builderAttestationType;
        //private String[] builderExtensions;        //TODO
        
        public RegistrationSettingsBuilder setAlg(Integer alg) {
            this.builderAlg = alg;
            return this;
        }
        
        public RegistrationSettingsBuilder setKty(Integer kty) {
            this.builderKty = kty;
            return this;
        }
        
        public RegistrationSettingsBuilder setCrv(Integer crv) {
            this.builderCrv = crv;
            return this;
        }
        
        public RegistrationSettingsBuilder setUp(Boolean up) {
            this.builderUp = up;
            return this;
        }
        
        
        public RegistrationSettingsBuilder setUv(Boolean uv) {
            this.builderUv = uv;
            return this;
        }
        
        public RegistrationSettingsBuilder setAttestationFormat(String attestationFormat) {
            this.builderAttestationFormat = attestationFormat;
            return this;
        }
        
        
        public RegistrationSettingsBuilder setAttestationType(String attestationType) {
            this.builderAttestationType = attestationType;
            return this;
        }
        
        public RegistrationSettings build(){
            return new RegistrationSettings(builderAlg, builderKty, builderCrv,
                    builderUp, builderUv, builderAttestationFormat,
                    builderAttestationType);
        }
    }
    
}
