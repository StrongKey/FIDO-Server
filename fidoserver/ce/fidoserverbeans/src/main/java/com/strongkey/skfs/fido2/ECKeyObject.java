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
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/fido2/ECKeyObject.java $
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORParser;
import com.strongkey.skfs.utilities.skfsLogger;
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public class ECKeyObject extends FIDO2KeyObject {

    private byte[] x, y;
    private int crv;

    public void decode(byte[] cbor) throws IOException {

        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        CBORParser parser = f.createParser(cbor);

        Map<String, Object> pkObjectMap = mapper.readValue(parser, new TypeReference<Map<String, Object>>() {
        });
        
        for (String key : pkObjectMap.keySet()) {
            skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "key : " + key);
            switch (key) {
                case "1":
                    kty = (int) pkObjectMap.get(key);
                    break;
                case "-1":
                    crv = (int) pkObjectMap.get(key);
                    break;
                case "3":
                    alg = (int) pkObjectMap.get(key);
                    break;
                case "-2":
                    x = (byte[]) pkObjectMap.get(key);
                    break;
                case "-3":
                    y = (byte[]) pkObjectMap.get(key);
                    break;
            }
        }
    }

    public byte[] getX() {
        return x;
    }

    public byte[] getY() {
        return y;
    }

    public int getCrv() {
        return crv;
    }

}
