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
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/fido2/FIDO2AttestationObject.java $
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
import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.utilities.skfsLogger;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Map;
import java.util.logging.Level;

public class FIDO2AttestationObject {

    String attFormat;
    FIDO2AuthenticatorData authData;
    FIDO2AttestationStatement attStmt;

    public String getAttFormat() {
        return attFormat;
    }

    public FIDO2AuthenticatorData getAuthData() {
        return authData;
    }

    public FIDO2AttestationStatement getAttStmt() {
        return attStmt;
    }

    public void decodeAttestationObject(String attestationObject) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidParameterSpecException {
        CBORFactory f = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(f);
        byte[] authenticatorData = null;
        Object attestationStmt = null;
        CBORParser parser = f.createParser(org.apache.commons.codec.binary.Base64.decodeBase64(attestationObject));
        Map<String, Object> attObjectMap = mapper.readValue(parser, new TypeReference<Map<String, Object>>() {
        });
        
        //Verify cbor is properly formatted cbor (no extra bytes)
        if(parser.nextToken() != null){
            throw new IllegalArgumentException("FIDO2AttestationObject contains invalid CBOR");
        }
        
        for (String key : attObjectMap.keySet()) {
            if (key.equalsIgnoreCase("fmt")) {
                attFormat = attObjectMap.get(key).toString();
            } else if (key.equalsIgnoreCase("authData")) {
                authenticatorData = (byte[]) attObjectMap.get(key);
            } else if (key.equalsIgnoreCase("attStmt")) {
                attestationStmt = attObjectMap.get(key);
            }
        }
        authData = new FIDO2AuthenticatorData();
        authData.decodeAuthData(authenticatorData);

        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "ATTFORMAT = "  +attFormat);
        switch (attFormat) {
            case "fido-u2f":
                attStmt = new U2FAttestationStatment();
                attStmt.decodeAttestationStatement(attestationStmt);
                break;

            case "packed":
                attStmt = new PackedAttestationStatement();
                attStmt.decodeAttestationStatement(attestationStmt);
                break;
            
            case "tpm":
                attStmt = new TPMAttestationStatement();
                attStmt.decodeAttestationStatement(attestationStmt);
                break;
                
            case "android-key":
                attStmt = new AndroidKeyAttestationStatement();
                attStmt.decodeAttestationStatement(attestationStmt);
                break;
                
            case "android-safetynet":
                attStmt = new AndroidSafetynetAttestationStatement();
                attStmt.decodeAttestationStatement(attestationStmt);
                break;
                
            case "none":
                attStmt = new NoneAttestationStatement();
                attStmt.decodeAttestationStatement(attestationStmt);
                break;
                
            default:
                throw new IllegalArgumentException("Invalid attestation format");
        }

    }
}
