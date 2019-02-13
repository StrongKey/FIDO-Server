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

import com.strongauth.skfe.fido.policyobjects.AuthenticatorSelection.AuthenticatorSelectionBuilder;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

public class RegistrationPolicyOptions {
    private final String icon;
    private final String displayName;
    private final Integer useridLength;
    private final String excludeCredentials;
    private final AuthenticatorSelection authenticatorSelection;
    private final List<String> attestation;
    
    private RegistrationPolicyOptions(String icon, String displayName, 
            Integer useridLength, String excludeCredentials,
            AuthenticatorSelection authenticatorSelection, List<String> attestation){
        this.icon = icon;
        this.displayName = displayName;
        this.useridLength = useridLength;
        this.excludeCredentials = excludeCredentials;
        this.authenticatorSelection = authenticatorSelection;
        this.attestation = attestation;
    }

    public String getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public Integer getUseridLength(){
        return useridLength;
    }

    public String getExcludeCredentials() {
        return excludeCredentials;
    }

    public AuthenticatorSelection getAuthenticatorSelection() {
        return authenticatorSelection;
    }

    public List<String> getAttestation() {
        return attestation;
    }
    
    public static RegistrationPolicyOptions parse(JsonObject registrationJson) {
        JsonObject authenticatorSelectionJson = registrationJson.getJsonObject(
                skfeConstants.POLICY_REGISTRATION_AUTHENTICATORSELECTION);
        AuthenticatorSelection authenticatorSelection = new AuthenticatorSelectionBuilder(
                new ArrayList<>(authenticatorSelectionJson.getJsonArray(skfeConstants.POLICY_REGISTRATION_AUTHENTICATORATTACHMENT).stream()
                        .map(x -> (JsonString) x)
                        .map(x -> x.getString())
                        .collect(Collectors.toList())),
                new ArrayList<>(authenticatorSelectionJson.getJsonArray(skfeConstants.POLICY_REGISTRATION_REQUIRERESIDENTKEY).stream()
                        .map(x -> x.equals(JsonValue.TRUE))         //TODO find a better method of getting the value
                        .collect(Collectors.toList())),
                new ArrayList<>(authenticatorSelectionJson.getJsonArray(skfeConstants.POLICY_REGISTRATION_USERVERIFICATION).stream()
                        .map(x -> (JsonString) x)
                        .map(x -> x.getString())
                        .collect(Collectors.toList()))).build();

        return new RegistrationPolicyOptions.RegistrationPolicyOptionsBuilder(
                registrationJson.getString(skfeConstants.POLICY_REGISTRATION_DISPLAYNAME),
                registrationJson.getString(skfeConstants.POLICY_REGISTRATION_EXCLUDECREDENTIALS),
                new ArrayList<>(registrationJson.getJsonArray(skfeConstants.POLICY_REGISTRATION_ATTESTATION).stream()
                        .map(x -> (JsonString) x)
                        .map(x -> x.getString())
                        .collect(Collectors.toList())))
                .setIcon(registrationJson.getString(skfeConstants.POLICY_REGISTRATION_ICON, null))
                .setAuthenticatorSelection(authenticatorSelection)
                .build();
    }
    
    public static class RegistrationPolicyOptionsBuilder{
        private String builderIcon;
        private final String builderDisplayName;
        private Integer builderUseridLength;
        private final String builderExcludeCredentials;
        private AuthenticatorSelection builderAuthenticatorSelection;
        private final List<String> builderAttestation;
        
        public RegistrationPolicyOptionsBuilder(String displayName, String excludeCredentials, List<String> attestation){
            this.builderDisplayName = displayName;
            this.builderExcludeCredentials = excludeCredentials;
            this.builderAttestation = attestation;
        }
        
        public RegistrationPolicyOptionsBuilder setIcon(String icon){
            this.builderIcon = icon;
            return this;
        }
        
        public RegistrationPolicyOptionsBuilder setUseridLength(Integer useridLength){
            this.builderUseridLength = useridLength;
            return this;
        }
        
        public RegistrationPolicyOptionsBuilder setAuthenticatorSelection(AuthenticatorSelection authenticatorSelection){
            this.builderAuthenticatorSelection = authenticatorSelection;
            return this;
        }
        
        public RegistrationPolicyOptions build(){
            return new RegistrationPolicyOptions(builderIcon, builderDisplayName,
                    builderUseridLength, builderExcludeCredentials, 
                    builderAuthenticatorSelection, builderAttestation);
        }
    }
}
