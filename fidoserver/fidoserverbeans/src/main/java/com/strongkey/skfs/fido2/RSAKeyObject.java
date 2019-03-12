/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skfs.fido2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.utilities.skfsLogger;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class RSAKeyObject extends FIDO2KeyObject {

    private byte[] n, e;

    public void decode(byte[] cbor) throws IOException {

        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        CBORParser parser = f.createParser(cbor);

        Map<String, Object> pkObjectMap = mapper.readValue(parser, new TypeReference<Map<String, Object>>() {
        });
        
        for (String key : pkObjectMap.keySet()) {
            skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "key : " + key + ", Value : " + pkObjectMap.get(key).toString());
            switch (key) {
                case "1":
                    kty = (int) pkObjectMap.get(key);
                    break;
                case "3":
                    alg = (int) pkObjectMap.get(key);
                    break;
                case "-1":
                    n = (byte[]) pkObjectMap.get(key);
                    break;
                case "-2":
                    e = (byte[]) pkObjectMap.get(key);
                    break;
            }
        }
    }

    public byte[] getN() {
        return n;
    }

    public byte[] getE() {
        return e;
    }
    
    
}
