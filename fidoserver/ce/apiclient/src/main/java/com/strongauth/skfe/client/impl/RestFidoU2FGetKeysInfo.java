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
package com.strongauth.skfe.client.impl;

import com.strongauth.skceclient.common.Constants;
import com.strongauth.skceclient.common.common;
import com.strongauth.skfe.client.interfaces.FIDOClientGetKeysInfo;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class RestFidoU2FGetKeysInfo implements FIDOClientGetKeysInfo {

    @Override
    public String u2fGetKeysInfo(String REST_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String accesskey, 
                                String secretkey, 
                                String accountname) 
    {
        String gkresponse = null;

        try {

            System.out.println("Get user keys information test");
            System.out.println("******************************************");

            String resourceLoc = REST_URI + "/domains/" + skcedid + Constants.GETKEYSINFO_ENDPOINT + "?username=" + accountname;

            System.out.println("\nCalling getkeysinfo @ " + resourceLoc);
            
            String contentMD5 = "";
            String contentType = "";
            String currentDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date());

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(resourceLoc);
            String requestToHmac = httpGet.getMethod() + "\n"
                    + contentMD5 + "\n"
                    + contentType + "\n"
                    + currentDate + "\n"
                    + httpGet.getURI().getPath() + "?" + httpGet.getURI().getQuery();

            String hmac = common.calculateHMAC(secretkey, requestToHmac);
            httpGet.addHeader("Authorization", "HMAC " + accesskey + ":" + hmac);
            httpGet.addHeader("Date", currentDate);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            String result;
            try {
                StatusLine responseStatusLine = response.getStatusLine();
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);

                switch (responseStatusLine.getStatusCode()) {
                    case 200:
                        break;
                    case 401:
                        System.out.println("Error during getkeysinfo : 401 HMAC Authentication Failed");
                        return null;
                    case 404:
                        System.out.println("Error during getkeysinfo : 404 Resource not found");
                        return null;
                    case 400:
                    case 500:
                    default:
                        System.out.println("Error during getkeysinfo : " + responseStatusLine.getStatusCode() + " " + result);
                        return null;
                }

            } finally {
                response.close();
            }

            //  Build a json object out of response
            StringReader s = new StringReader(result);
            JsonObject responseJSON;
            try (JsonReader jsonReader = Json.createReader(s)) {
                responseJSON = jsonReader.readObject();
            }

            //  Check to see if there is any
            try {
                String error = responseJSON.getString("Error");
                if (error != null && !error.equalsIgnoreCase("")) {
                    System.out.println("*******************************");
                    return " Error during getkeysinfo : " + error;
                }

                gkresponse = responseJSON.getJsonObject("Response").toString();
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println("\nGet user keys information test complete.");
            System.out.println("******************************************");
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FGetKeysInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(RestFidoU2FGetKeysInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FGetKeysInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("GetKeys response : " + gkresponse);
        return gkresponse;
    }
}
