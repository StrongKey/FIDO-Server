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
 * $Author$
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
package com.strongkey.skfs.fido2.tpm;

import com.strongkey.skce.utilities.TPMConstants;
import java.util.Arrays;

/**
 *
 * @author mishimoto
 */
public class TPMAttest implements TPMMarshallable {
    private final int magic;
    private final short type;
    private final TPM2B qualifiedSigner;
    private final TPM2B extraData;
    private final TPMClockInfo clockInfo;
    private final long firmwareVersion;
    private final TPMCertifyInfo attested; //TODO fix hardcoded type (implementation is more restrictive
                             //than the specification).
    
    public TPMAttest(int magic, short type, TPM2B qualifiedSigner,
            TPM2B extraData, TPMClockInfo clockInfo, long firmwareVersion,
            TPMCertifyInfo attested){
        this.magic = magic;
        this.type = type;
        this.qualifiedSigner = qualifiedSigner;
        this.extraData = extraData;
        this.clockInfo = clockInfo;
        this.firmwareVersion = firmwareVersion;
        this.attested = attested;
    }
    
    public static TPMAttest unmarshal(byte[] bytes){
        int pos = 0;
        int magic = Marshal.stream32ToInt(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFINT));
        pos += TPMConstants.SIZEOFINT;
        short type = Marshal.stream16ToShort(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFTAG));
        pos += TPMConstants.SIZEOFTAG;
        short qualifiedSignerSize = Marshal.stream16ToShort(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFSHORT));
        pos += TPMConstants.SIZEOFSHORT;
        TPM2B qualifiedSigner = new TPM2B(Arrays.copyOfRange(bytes, pos, pos+qualifiedSignerSize));
        pos += qualifiedSignerSize;
        short extraDataSize = Marshal.stream16ToShort(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFSHORT));
        pos += TPMConstants.SIZEOFSHORT;
        TPM2B extraData = new TPM2B(Arrays.copyOfRange(bytes, pos, pos+extraDataSize));
        pos += extraDataSize;
        int sizeOfClockInfo = TPMConstants.SIZEOFLONG+TPMConstants.SIZEOFINT+TPMConstants.SIZEOFINT+TPMConstants.SIZEOFBYTE;
        TPMClockInfo clockInfo = TPMClockInfo.unmarshal(Arrays.copyOfRange(bytes, pos, pos+sizeOfClockInfo));
        pos += sizeOfClockInfo;
        long firmwareVersion = Marshal.stream64ToLong(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFLONG));
        pos += TPMConstants.SIZEOFLONG;
        TPMCertifyInfo attested = TPMCertifyInfo.unmarshal(Arrays.copyOfRange(bytes, pos, bytes.length));
        return new TPMAttest(magic, type, qualifiedSigner, extraData, clockInfo, firmwareVersion, attested);
    }

    public int getMagic() {
        return magic;
    }

    public short getType() {
        return type;
    }

    public TPM2B getExtraData() {
        return extraData;
    }

    public TPMCertifyInfo getAttested() {
        return attested;
    }

    @Override
    public byte[] marshalData() {
        return Marshal.marshalObjects(
                magic,
                type,
                qualifiedSigner,
                extraData,
                clockInfo,
                firmwareVersion,
                attested);
    }
    
    private static String bytesToHexString(byte[] rawBytes, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            if (i % 16 == 0) {
                sb.append('\n');
            }
            sb.append(String.format("%02x ", rawBytes[i]));
        }
        return sb.toString();
    }
    
}
