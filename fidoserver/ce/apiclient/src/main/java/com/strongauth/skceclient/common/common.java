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
 * Copyright (c) 2001-2019 StrongAuth, Inc.
 *
 * $Date$
 * $Revision$
 * $Author$
 * $URL$
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
package com.strongauth.skceclient.common;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public final class common {
    
//    public static String makePOSTCallUsingHMAC(String baseuri, String methodname, String body, String accesskey, String secretkey) throws HttpException, IOException, NoSuchAlgorithmException {
////        String contentToEncode = "{" +
////                                    "\"username\" : \"" + username + "\"" +
////                                 "}";
//        String contentType = "application/x-www-form-urlencoded";
//        String currentDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date());
//        String contentMD5 = calculateMD5(body);
//
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost(baseuri + methodname);
//        StringEntity data = new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED); //create(contentType));
//        httpPost.setEntity(data);
//        String requestToHmac = httpPost.getMethod() + "\n" + 
//                        contentMD5 + "\n" +
//                        contentType + "\n" + 
//                        currentDate + "\n" +
//                        httpPost.getURI().getPath();
//
//        String hmac = calculateHMAC(secretkey, requestToHmac);
//        httpPost.addHeader("Authorization", "HMAC " + accesskey + ":" + hmac);
//        httpPost.addHeader("Content-MD5", contentMD5);
//        httpPost.addHeader("content-type", contentType);
//        httpPost.addHeader("Date", currentDate);
//        CloseableHttpResponse response = httpclient.execute(httpPost);
//        try {
//            StatusLine sl = response.getStatusLine();
//            System.out.println(sl.getStatusCode());
//            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//            return result;
//        } finally {
//            response.close();
//        }
//    }

//    public static String makeGETCallUsingHMAC(String baseuri, String methodname, String queryParams, String accesskey, String secretkey) throws HttpException, IOException, NoSuchAlgorithmException {
//        String contentMD5 = "";
//        String contentType = "";
//        String currentDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date());
//
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(baseuri + methodname + queryParams);
//        String requestToHmac = httpGet.getMethod() + "\n" + 
//                        contentMD5 + "\n" +
//                        contentType + "\n" + 
//                        currentDate + "\n" +
//                        httpGet.getURI().getPath() + "?" + httpGet.getURI().getQuery();
//
//        String hmac = calculateHMAC(secretkey, requestToHmac);
//        httpGet.addHeader("Authorization", "HMAC " + accesskey + ":" + hmac);
//        httpGet.addHeader("Date", currentDate);
//        CloseableHttpResponse response = httpclient.execute(httpGet);
//        try {
//            StatusLine sl = response.getStatusLine();
//            System.out.println(sl.getStatusCode());
//            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//            return result;
//        } finally {
//            response.close();
//        }
//    }

    public static String calculateHMAC(String secret, String data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(Hex.decode(secret), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1", new BouncyCastleFipsProvider());
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            return Base64.toBase64String(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException ex) {
            System.out.println("Unexpected error while creating hash: " + ex.getMessage());
            throw new IllegalArgumentException();
        }
    }

    public static String calculateMD5(String contentToEncode) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(contentToEncode.getBytes());
        return Base64.toBase64String(digest.digest());
    }
}
