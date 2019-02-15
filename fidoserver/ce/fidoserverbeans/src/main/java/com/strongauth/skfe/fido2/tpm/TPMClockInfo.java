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
package com.strongauth.skfe.fido2.tpm;

import com.strongauth.skce.utilities.TPMConstants;
import org.bouncycastle.util.Arrays;

/**
 *
 * @author mishimoto
 */
public class TPMClockInfo implements TPMMarshallable {
    private final long clock;
    private final int resetCount;
    private final int restartCount;
    private final byte safe;
    
    public TPMClockInfo(long clock, int resetCount, int restartCount, byte safe){
        this.clock = clock;
        this.resetCount = resetCount;
        this.restartCount = restartCount;
        this.safe = safe;
    }
    
    public static TPMClockInfo unmarshal(byte[] bytes){
        int pos = 0;
        long clock = Marshal.stream64ToLong(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFLONG));
        pos += TPMConstants.SIZEOFLONG;
        int resetCount = Marshal.stream32ToInt(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFINT));
        pos += TPMConstants.SIZEOFINT;
        int restartCount = Marshal.stream32ToInt(Arrays.copyOfRange(bytes, pos, pos + TPMConstants.SIZEOFINT));
        pos += TPMConstants.SIZEOFINT;
        byte safe = Arrays.copyOfRange(bytes, pos, pos + TPMConstants.SIZEOFBYTE)[0];
        pos += TPMConstants.SIZEOFBYTE;
        return new TPMClockInfo(clock, resetCount, restartCount, safe);
    }

    @Override
    public byte[] marshalData() {
        return Marshal.marshalObjects(
                clock,
                resetCount,
                restartCount,
                safe);
    }
}
