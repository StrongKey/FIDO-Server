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
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/fido2/FIDO2AuthenticatorData.java $
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

import com.strongkey.skfs.utilities.skfsCommon;
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.utilities.skfsLogger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;

public class FIDO2AuthenticatorData {

    private byte[] rpIdHash;
    private byte flags;
    private boolean isUserPresent;
    private boolean isUserVerified;
    private boolean isAttestedCredentialData;
    private boolean isExtensionData;
    private byte[] counterValue;
    private Integer counter;
    private FIDO2AttestedCredentialData attCredData;
    private FIDO2Extensions ext;
    private byte[] authDataDecoded;

    public static int COUNTER_VALUE_BYTES = 4;

    public byte[] getRpIdHash() {
        return rpIdHash;
    }

    public byte getFlags() {
        return flags;
    }

    public boolean isUserPresent() {
        return isUserPresent;
    }

    public boolean isUserVerified() {
        return isUserVerified;
    }

    public boolean isAttestedCredentialData() {
        return isAttestedCredentialData;
    }

    public boolean isExtensionData() {
        return isExtensionData;
    }

    public byte[] getCounterValue() {
        return counterValue;
    }

    public int getCounterValueAsInt() {
        return Integer.parseInt(Hex.encodeHexString(counterValue), 16);
    }

    public FIDO2AttestedCredentialData getAttCredData() {
        return attCredData;
    }

    public FIDO2Extensions getExt() {
        return ext;
    }

    public byte[] getAuthDataDecoded() {
        return authDataDecoded;
    }
    

    public void decodeAuthData(byte[] authData) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidParameterSpecException {
        authDataDecoded = authData;
        int index = 0;
        rpIdHash = new byte[32];
        System.arraycopy(authData, 0, rpIdHash, 0, 32);
        index += 32;
        flags = authData[index++];

        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "rpidHash : " + Base64.toBase64String(rpIdHash));

        isUserPresent = ((flags >> 0) & 1) == 1;
        isUserVerified = ((flags >> 2) & 1) == 1;
        isAttestedCredentialData = ((flags >> 6) & 1) == 1;
        isExtensionData = ((flags >> 7) & 1) == 1;

        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "The user is " + (isUserPresent ? "present" : "not present"));
        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "The user is " + (isUserVerified ? "verified" : "not verified"));
        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "Credential Data is " + (isAttestedCredentialData ? "present" : "not present"));
        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "ExtensionData is " + (isExtensionData ? "present" : "not present"));

        counterValue = new byte[COUNTER_VALUE_BYTES];
        System.arraycopy(authData, index, counterValue, 0, COUNTER_VALUE_BYTES);
        index += COUNTER_VALUE_BYTES;
        counter = ByteBuffer.wrap(counterValue).getInt();
        skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001", 
                    "counter Hex: " + Hex.encodeHexString(counterValue));

        int attestedCredentialDataLength;
        if (isAttestedCredentialData) {
            attCredData = new FIDO2AttestedCredentialData();
            byte[] remainingData = new byte[authData.length - index];
            System.arraycopy(authData, index, remainingData, 0, authData.length - index);
            attestedCredentialDataLength = attCredData.decodeAttCredData(remainingData);
            index += attestedCredentialDataLength;
        }
        
        //ignore extensions for now
        int extensionsLength;
        if(isExtensionData){
            ext = new FIDO2Extensions();
            byte[] remainingData = new byte[authData.length - index];
            System.arraycopy(authData, index, remainingData, 0, authData.length - index);
            extensionsLength = ext.decodeExtensions(remainingData);
            index += extensionsLength;
        }
        
        if(authData.length != index){
            int extraData = authData.length - index;
            throw new IllegalArgumentException("AuthenicatorData contains invalid CBOR: " 
                + extraData + " unknown bytes");
        }
    }

}
