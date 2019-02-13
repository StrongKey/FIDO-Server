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

import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import javax.json.JsonObject;

public class CounterPolicyOptions {
    private final Boolean isCounterRequired;
    private final Boolean isCounterIncreaseRequired;
    
    private CounterPolicyOptions(Boolean isCounterRequired, Boolean isCounterIncreaseRequired){
        this.isCounterRequired = isCounterRequired;
        this.isCounterIncreaseRequired = isCounterIncreaseRequired;
    }

    public Boolean getIsCounterRequired() {
        return isCounterRequired;
    }

    public Boolean getIsCounterIncreaseRequired() {
        return isCounterIncreaseRequired;
    }
    
    public static CounterPolicyOptions parse(JsonObject counterJson) {
        return new CounterPolicyOptions.CounterPolicyOptionsBuilder(
                skfeCommon.handleNonExistantJsonBoolean(counterJson, skfeConstants.POLICY_COUNTER_REQUIRECOUNTER),
                skfeCommon.handleNonExistantJsonBoolean(counterJson, skfeConstants.POLICY_COUNTER_REQUIRECOUNTERINCREASE))
                .build();
    }
    
    public static class CounterPolicyOptionsBuilder{
        private final Boolean builderIsCounterRequired;
        private final Boolean builderIsCounterIncreaseRequired;
        
        public CounterPolicyOptionsBuilder(Boolean isCounterRequired, Boolean isCounterIncreaseRequired){
            this.builderIsCounterRequired = isCounterRequired;
            this.builderIsCounterIncreaseRequired = isCounterIncreaseRequired;
        }
        public CounterPolicyOptions build(){
            return new CounterPolicyOptions(builderIsCounterRequired, builderIsCounterIncreaseRequired);
        }
    }
}
