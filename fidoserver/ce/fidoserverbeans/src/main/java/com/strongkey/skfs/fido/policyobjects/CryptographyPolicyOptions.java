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

package com.strongkey.skfs.fido.policyobjects;

import com.strongkey.skfs.utilities.skfsConstants;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonString;

public class CryptographyPolicyOptions {
    private final ArrayList<String> supportedEllipticCurves;
    private final ArrayList<String> allowedECSignatures;
    private final ArrayList<String> allowedRSASignatures;
    private final ArrayList<String> allowedAttestationFormats;
    private final ArrayList<String> allowedAttestationTypes;
    private final Integer challengeLength;
    
    private CryptographyPolicyOptions(
            ArrayList<String> supportedEllipticCurves, ArrayList<String> allowedECSignatures,
            ArrayList<String> allowedRSASignatures, ArrayList<String> allowedAttestationFormats,
            ArrayList<String> allowedAttestationTypes, Integer challengeLength){
        this.supportedEllipticCurves = supportedEllipticCurves;
        this.allowedECSignatures = allowedECSignatures;
        this.allowedRSASignatures = allowedRSASignatures;
        this.allowedAttestationFormats = allowedAttestationFormats;
        this.allowedAttestationTypes = allowedAttestationTypes;
        this.challengeLength = challengeLength;
    }

    public ArrayList<String> getSupportedEllipticCurves() {
        return supportedEllipticCurves;
    }

    public ArrayList<String> getAllowedECSignatures() {
        return allowedECSignatures;
    }

    public ArrayList<String> getAllowedRSASignatures() {
        return allowedRSASignatures;
    }

    public ArrayList<String> getAllowedAttestationFormats() {
        return allowedAttestationFormats;
    }

    public ArrayList<String> getAllowedAttestationTypes() {
        return allowedAttestationTypes;
    }
    
    public Integer getChallengeLength(){
        return challengeLength;
    }
    
    public static CryptographyPolicyOptions parse(JsonObject cryptoJson) {
        CryptographyPolicyOptionsBuilder cryptoPolicyBuilder = 
                new CryptographyPolicyOptions.CryptographyPolicyOptionsBuilder(
                new ArrayList<>(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ATTESTATION_FORMATS).stream().map(x -> ((JsonString) x).getString()).collect(Collectors.toList())),
                new ArrayList<>(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ATTESTATION_TYPES).stream().map(x -> ((JsonString) x).getString()).collect(Collectors.toList())));
        if(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ALLOWED_EC_SIGNATURES) != null){
            cryptoPolicyBuilder.setAllowedECSignatures(new ArrayList<>(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ALLOWED_EC_SIGNATURES).stream().map(x -> ((JsonString) x).getString()).collect(Collectors.toList())));
        }
        if(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ALLOWED_RSA_SIGNATURES) != null){
            cryptoPolicyBuilder.setAllowedRSASignatures(new ArrayList<>(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ALLOWED_RSA_SIGNATURES).stream().map(x -> ((JsonString) x).getString()).collect(Collectors.toList())));
        }
        if(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ELLIPTIC_CURVES) != null){
            cryptoPolicyBuilder.setSupportedEllipticCurves(new ArrayList<>(cryptoJson.getJsonArray(skfsConstants.POLICY_CRYPTO_ELLIPTIC_CURVES).stream().map(x -> ((JsonString) x).getString()).collect(Collectors.toList())));
        }
        return cryptoPolicyBuilder.build();
    }
    
    public static class CryptographyPolicyOptionsBuilder{
        private ArrayList<String> builderSupportedEllipticCurves;
        private ArrayList<String> builderAllowedECSignatures;
        private ArrayList<String> builderAllowedRSASignatures;
        private final ArrayList<String> builderAllowedAttestationFormats;
        private final ArrayList<String> builderAllowedAttestationTypes;
        private Integer builderChallengeLength;
        
        public CryptographyPolicyOptionsBuilder(
                ArrayList<String> allowedAttestationFormats,
                ArrayList<String> allowedAttestationTypes){
            this.builderAllowedAttestationFormats = allowedAttestationFormats;
            this.builderAllowedAttestationTypes = allowedAttestationTypes;
        }
        
        public CryptographyPolicyOptionsBuilder setSupportedEllipticCurves(ArrayList<String> supportedEllipticCurves) {
            this.builderSupportedEllipticCurves = supportedEllipticCurves;
            return this;
        }
        
        public CryptographyPolicyOptionsBuilder setAllowedECSignatures(ArrayList<String> allowedECSignatures) {
            this.builderAllowedECSignatures = allowedECSignatures;
            return this;
        }
        
        public CryptographyPolicyOptionsBuilder setAllowedRSASignatures(ArrayList<String> allowedRSASignatures) {
            this.builderAllowedRSASignatures = allowedRSASignatures;
            return this;
        }
        
        public CryptographyPolicyOptionsBuilder setChallengeLength(Integer challengeLength){
            this.builderChallengeLength = challengeLength;
            return this;
        }
        
        public CryptographyPolicyOptions build(){
            return new CryptographyPolicyOptions(builderSupportedEllipticCurves,
                builderAllowedECSignatures, builderAllowedRSASignatures,
                builderAllowedAttestationFormats, builderAllowedAttestationTypes,
                builderChallengeLength);
        }
    }
}
