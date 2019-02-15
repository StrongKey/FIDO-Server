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
 * Copyright (c) 2001-2019 StrongAuth, Inc.
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

import com.strongauth.skfe.fido.policyobjects.extensions.ExampleFido2Extension;
import com.strongauth.skfe.fido.policyobjects.extensions.Fido2Extension;
import com.strongauth.skfe.utilities.skfeConstants;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.json.JsonObject;

public class ExtensionsPolicyOptions {
    private static final Set<String> KNOWNEXTENSIONS = new HashSet<>(
            Arrays.asList(new String[]{
                skfeConstants.POLICY_EXTENSIONS_EXAMPLE,
                skfeConstants.POLICY_EXTENSIONS_APPID
            })
    );
    
    private final Set<Fido2Extension> extensions;
    
    public ExtensionsPolicyOptions(Set<Fido2Extension> extensions){
        this.extensions = extensions;
    }
    
    public static ExtensionsPolicyOptions parse(JsonObject extensionsJson){
        Set<Fido2Extension> extensions = new HashSet<>();
        
        if(extensionsJson == null){
            return new ExtensionsPolicyOptions(extensions);
        }
        
        for(String extensionIdentifier: extensionsJson.keySet()){
            if(!KNOWNEXTENSIONS.contains(extensionIdentifier)){
                throw new IllegalArgumentException("Unknown extension defined in policy");
            }
            
            Fido2Extension ext;
            switch(extensionIdentifier){
                case skfeConstants.POLICY_EXTENSIONS_EXAMPLE:
                    ext = new ExampleFido2Extension();
                    break;
                case skfeConstants.POLICY_EXTENSIONS_APPID:
                    ext = new ExampleFido2Extension();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown exception defined in policy");
            }
            extensions.add(ext);
        }
        return new ExtensionsPolicyOptions(extensions);
    }
    
    //TODO ensure all Extensions are immutable to prevent issues with
    //shallow copying
    public Set<Fido2Extension> getExtensions(){
        return new HashSet(extensions);
    }
}
