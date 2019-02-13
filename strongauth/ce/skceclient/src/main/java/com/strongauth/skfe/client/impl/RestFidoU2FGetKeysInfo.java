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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class RestFidoU2FGetKeysInfo implements FIDOClientGetKeysInfo {

    @Override
    public String u2fGetKeysInfo(String REST_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname) 
    {
        HttpURLConnection conn = null;
        String gkresponse = "";
        String response = "";

        try {

            System.out.println("Get user keys information test");
            System.out.println("******************************************");

            //  Build svcinfo
            String svcinfo = Json.createObjectBuilder()
                    .add("did", skcedid)
                    .add("svcusername", svcuser)
                    .add("svcpassword", svcpass)
                    .add("protocol", fidoprotocol)
                    .build().toString();
            
            //  Build payload
            String payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname)
                    .build().toString();

            //  Make SKFE rest call and get response from the server
            System.out.println("\nCalling getkeysinfo @ " 
                    + REST_URI + Constants.GETKEYSINFO_ENDPOINT);
            response = common.callSKFERestApi(REST_URI, Constants.GETKEYSINFO_ENDPOINT, 
                                                svcinfo, payload);
            System.out.println(" Response : " + response);
            
            //  Build a json object out of response
            StringReader s = new StringReader(response);
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
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return gkresponse;
    }
}
