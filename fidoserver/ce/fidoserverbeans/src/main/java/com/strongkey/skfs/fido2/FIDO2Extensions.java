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
 * Copyright (c) 2001-2015 StrongAuth, Inc.
 *
 * $Date: 2018-06-18 14:47:15 -0400 (Mon, 18 Jun 2018) $
 * $Revision: 50 $
 * $Author: pmarathe $
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/fido2/FIDO2Extensions.java $
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

package com.strongkey.skfs.fido2;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import java.io.IOException;
import java.util.Map;


public class FIDO2Extensions {
    Map<String, Object> extensionMap;
    
    public int decodeExtensions(byte[] extensionBytes) throws IOException {
        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        CBORParser parser = f.createParser(extensionBytes);
        extensionMap = mapper.readValue(parser, new TypeReference<Map<String, Object>>() {});
        
        //Return size of AttestedCredentialData
        int numRemainingBytes = 0;
        JsonToken leftoverCBORToken;
        while ((leftoverCBORToken = parser.nextToken()) != null) {
            numRemainingBytes += leftoverCBORToken.asByteArray().length;
        }
        return extensionBytes.length - numRemainingBytes;
    }
    
    public Object getExtension(String extensionName){
        return extensionMap.get(extensionName);
    }
    
}
