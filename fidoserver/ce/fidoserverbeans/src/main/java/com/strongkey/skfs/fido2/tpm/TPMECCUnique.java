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
import java.util.InputMismatchException;

/**
 *
 * @author mishimoto
 */
public class TPMECCUnique implements TPMUnique {
    private TPM2B x;
    private TPM2B y;
    
    private TPMECCUnique(TPM2B x, TPM2B y){
        this.x = x;
        this.y = y;
    }
    
    public static TPMECCUnique unmarshal(byte[] bytes){
        int pos = 0;
        short xSize = Marshal.stream16ToShort(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFSHORT));
        pos += TPMConstants.SIZEOFSHORT;
        TPM2B x = new TPM2B(Arrays.copyOfRange(bytes, pos, pos+xSize));
        pos += xSize;
        
        short ySize = Marshal.stream16ToShort(Arrays.copyOfRange(bytes, pos, pos+TPMConstants.SIZEOFSHORT));
        pos += TPMConstants.SIZEOFSHORT;
        TPM2B y = new TPM2B(Arrays.copyOfRange(bytes, pos, pos+ySize));
        pos += ySize;
        
        if(pos != bytes.length){
            throw new InputMismatchException("TPMCertifyInfo failed to unmarshal");
        }
    
        return new TPMECCUnique(x, y);
    }
    
    @Override
    public boolean equals(TPMUnique unique) {
        if(!(unique instanceof TPMECCUnique)){
            return false;
        }
        
        TPMECCUnique eccunique = (TPMECCUnique) unique;
        
        return Arrays.equals(this.x.getData(), eccunique.getX().getData())
                && Arrays.equals(this.y.getData(), eccunique.getY().getData());
        
    }
    
    public TPM2B getX(){
        return x;
    }
    
    public TPM2B getY(){
        return y;
    }

    @Override
    public byte[] marshalData() {
        return Marshal.marshalObjects(
                x,
                y);
    }
}
