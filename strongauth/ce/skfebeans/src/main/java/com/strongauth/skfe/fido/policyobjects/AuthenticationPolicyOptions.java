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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonString;

public class AuthenticationPolicyOptions {
    private final String allowCredentials;
    private final List<String> userVerification;
    
    public AuthenticationPolicyOptions(String allowCredentials, List<String> userVerification){
        this.allowCredentials = allowCredentials;
        this.userVerification = userVerification;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public List<String> getUserVerification() {
        return userVerification;
    }
    
    public static AuthenticationPolicyOptions parse(JsonObject authenticationJson) {
        return new AuthenticationPolicyOptions.AuthenticationPolicyOptionsBuilder(
                new ArrayList<>(authenticationJson.getJsonArray(skfeConstants.POLICY_AUTHENTICATION_USERVERIFICATION).stream()
                        .map(x -> (JsonString) x)
                        .map(x -> x.getString())
                        .collect(Collectors.toList())))
                .setAllowCredentials(authenticationJson.getString(
                        skfeConstants.POLICY_AUTHENTICATION_ALLOWCREDENTIALS, null))
                .build();
    }
    
    public static class AuthenticationPolicyOptionsBuilder{
        private String builderAllowCredentials;
        private final List<String> builderUserVerification;
        
        public AuthenticationPolicyOptionsBuilder(List<String> userVerification) {
            this.builderUserVerification = userVerification;
        }
        
        public AuthenticationPolicyOptionsBuilder setAllowCredentials(String allowCredentials){
            this.builderAllowCredentials = allowCredentials;
            return this;
        }
        
        public AuthenticationPolicyOptions build(){
            return new AuthenticationPolicyOptions(builderAllowCredentials, builderUserVerification);
        }
    }
}
