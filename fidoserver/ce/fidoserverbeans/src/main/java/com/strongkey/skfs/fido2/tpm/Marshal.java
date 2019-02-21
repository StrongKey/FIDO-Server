
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
import com.strongkey.skfs.utilities.skfsConstants;
import com.strongkey.skfs.utilities.skfsLogger;
import java.nio.ByteBuffer;
import java.util.logging.Level;

public class Marshal {
    //Marshal Data
    public static byte[] marshalObjects(Object... objectsToMarshal){
        byte[][] marshalledData = new byte[objectsToMarshal.length][];
        int resultArraySize = marshalDataAndReturnSize(0, marshalledData, objectsToMarshal);
        return concatenateArrays(resultArraySize, marshalledData);
    }
    
    public static byte[] marshalObjectsWithPrependedSizeShort(Object... objectsToMarshal){
        byte[][] marshalledData = new byte[objectsToMarshal.length+1][];
        int resultArraySize = marshalDataAndReturnSize(1, marshalledData, objectsToMarshal);
        marshalledData[0] = shortToStream((short) resultArraySize);
        resultArraySize += TPMConstants.SIZEOFSHORT;
        return concatenateArrays(resultArraySize, marshalledData);
    }
    
    public static byte[] marshalObjectsWithPrependedSizeInt(Object... objectsToMarshal){
        byte[][] marshalledData = new byte[objectsToMarshal.length + 1][];
        int resultArraySize = marshalDataAndReturnSize(1, marshalledData, objectsToMarshal);
        marshalledData[0] = intToStream(resultArraySize);
        resultArraySize += TPMConstants.SIZEOFINT;
        return concatenateArrays(resultArraySize, marshalledData);
    }
    
    private static int marshalDataAndReturnSize(int offset, byte[][] outBuffer, Object... objectsToMarshal){
        int resultArraySize = 0;
        for(int i = offset; i < objectsToMarshal.length+offset; i++){
            Object obj = objectsToMarshal[i-offset];
            if(obj instanceof Byte){
                outBuffer[i] = new byte[]{(byte) obj};
            } else if (obj instanceof Short) {
                outBuffer[i] = Marshal.shortToStream((short) obj);
            } else if (obj instanceof Integer) {
                outBuffer[i] = Marshal.intToStream((int) obj);
            } else if (obj instanceof Long) {
                outBuffer[i] = Marshal.longToStream((long) obj);
            } else if(obj instanceof String){
                outBuffer[i] = Marshal.stringToStream((String) obj);
            } else if(obj instanceof TPMMarshallable[]){
                outBuffer[i] = marshalObjects((Object[])obj);
            } else if (obj instanceof TPMMarshallable) {
                byte[] byteArray = ((TPMMarshallable) obj).marshalData();
                outBuffer[i] = byteArray;
            } else if (obj instanceof byte[]) {
                byte[] byteArray = ((byte[]) obj);
                outBuffer[i] = byteArray;
            } else {
                skfsLogger.log(skfsConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015",
                        "Marshal Object failure!!");
                outBuffer[i] = new byte[0];
                continue;
            }
            
            resultArraySize += outBuffer[i].length;
        }
        
        return resultArraySize;
    }
    
    private static byte[] concatenateArrays(int resultArraySize, byte[][] marshalledData){
        int index = 0;
        byte[] result = new byte[resultArraySize];
        for (byte[] data : marshalledData) {
            System.arraycopy(data, 0, result, index, data.length);
            index += data.length;
        }
        
        return result;
    }
    
    public static byte[] longToStream(long i) {
        return ByteBuffer.allocate(8).putLong(i).array();
    }
    
    public static byte[] intToStream(int i){
        return ByteBuffer.allocate(4).putInt(i).array();
    }
    
    public static byte[] shortToStream(short s){
        return ByteBuffer.allocate(2).putShort(s).array();
    }
    
    public static byte[] byteToStream(byte s) {
        return new byte[]{s};
    }
    
    public static byte[] stringToStream(String s){
        return s.getBytes();
    }
    
    
    //Unmarshal Data
    public static long stream64ToLong(byte[] bytes) {
        return Byte.toUnsignedLong(bytes[0]) << 56
                | Byte.toUnsignedLong(bytes[1]) << 48
                | Byte.toUnsignedLong(bytes[2]) << 40
                | Byte.toUnsignedLong(bytes[3]) << 32
                | Byte.toUnsignedLong(bytes[4]) << 24
                | Byte.toUnsignedLong(bytes[5]) << 16
                | Byte.toUnsignedLong(bytes[6]) << 8
                | Byte.toUnsignedLong(bytes[7]);
    }
    
    public static int stream32ToInt(byte[] bytes){
        return  Byte.toUnsignedInt(bytes[0]) << 24     |
                Byte.toUnsignedInt(bytes[1]) << 16     |
                Byte.toUnsignedInt(bytes[2]) << 8      |
                Byte.toUnsignedInt(bytes[3]);
    }
    
    public static int stream16ToInt(byte[] bytes){
        return  Byte.toUnsignedInt(bytes[0]) << 8      |
                Byte.toUnsignedInt(bytes[1]);
    }
    
    public static short stream16ToShort(byte[] bytes){
        return  (short) stream16ToInt(bytes);
    }
}
