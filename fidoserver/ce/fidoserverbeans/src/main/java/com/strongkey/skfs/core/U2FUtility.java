/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, as published by the Free
 * Software Foundation and available at
 * http://www.fsf.org/licensing/licenses/lgpl.html, version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$ $Revision$
 * $Author$ $URL:
 * https://svn.strongauth.com/repos/jade/trunk/skce/skcebeans/src/main/java/com/strongauth/skfe/core/U2FUtility.java
 * $
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
 * Utility class for FIDO U2F protocol. Contains generic utility methods related
 * to U2F protocol.
 *
 */
package com.strongkey.skfs.core;

import com.strongkey.skfs.utilities.skfsConstants;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

/**
 * Utility class with generic methods. Mostly generic for FIDO based operations.
 *
 */
public class U2FUtility {

    /**
     * Generates a sesson id, using the given random number and a secret key
     * that is maintained in memory.
     *
     * Currently, a SHA-256 digest of (random number + secret key) is termed as
     * session id.
     *
     * @param randomnumber - any random number.
     * @return - String which is the session id.
     * @throws SKFEException - In case of any error.
     */
//    public static String generateSessionid(String randomnumber) throws SKFEException {
//
//        //concatenation of randomnumber,My Secret
//        // H256(random|secret) //unique sessionIDs incase challenge generated on multiple fido engines are the same
//        String sessionID = randomnumber + MY_SECRET_KEY;
//        try {
//            return Common.getDigest(sessionID, "SHA-256");
//        } catch (NoSuchAlgorithmException |
//                NoSuchProviderException |
//                UnsupportedEncodingException ex) {
//            throw new SKFEException(ex);
//        }
//    }

    /**
     * A reverse process for sessionid generation. In this method, a session is
     * validated by re-calculating the sessionid using the nonce + secret_key
     * and comparing the result with the
     *
     * @param sessionid - String, sessionid to be validated.
     * @param nonce - String, nonce using which the sessionid has been claimed
     * to be generated.
     * @param sid
     *
     * @return - boolean, true or false based on validation result.
     * @throws SKFEException - In case of any error.
     */
//    public static boolean validateSessionid(String sessionid, String nonce, String sid) throws SKFEException {
//        String secretKey = Common.getSecretKey(sid);
//        String preImage = nonce + secretKey;
//        String hash = null;
//        try {
//            hash = Common.getDigest(preImage, "SHA-256");
//        } catch (NoSuchAlgorithmException |
//                NoSuchProviderException |
//                UnsupportedEncodingException ex) {
//            throw new SKFEException(ex);
//        }
//
//        return hash.equals(sessionid);
//    }

    /**
     * Generates a series of characters which is random.
     *
     * @param size - int, is the entrophy length to be used.
     * @return - String, random character set.
     */
    public static String getRandom(int size) {

        if (size > skfsConstants.MAX_RANDOM_NUMBER_SIZE_BITS / 8) {
            size = skfsConstants.MAX_RANDOM_NUMBER_SIZE_BITS / 8;
        }
        //Generate seed
        SecureRandom random = new SecureRandom();
        byte seed[] = new byte[20];
        random.nextBytes(seed);

        //Generate Random number
        SecureRandom sr = new SecureRandom(seed);
        byte[] randomBytes = new byte[size];
        sr.nextBytes(randomBytes);

        return Base64.encodeBase64URLSafeString(randomBytes);
    }
}
