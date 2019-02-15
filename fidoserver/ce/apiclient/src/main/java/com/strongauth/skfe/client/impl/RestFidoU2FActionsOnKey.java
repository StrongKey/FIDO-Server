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
import com.strongauth.skfe.client.interfaces.FIDOClientActionsOnKey;
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

public class RestFidoU2FActionsOnKey implements FIDOClientActionsOnKey {

    @Override
    public String u2fDeactivate(String REST_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname, 
                                String randomid, 
                                String modifylocation) 
    {
        String deactresponse = "";
        HttpURLConnection conn = null;
        String response = "";
        try {

            System.out.println("Deactivate key test");
            System.out.println("******************************************");

            //  Build svcinfo
            String svcinfo = Json.createObjectBuilder()
                    .add("did", skcedid)
                    .add("svcusername", svcuser)
                    .add("svcpassword", svcpass)
                    .add("protocol", fidoprotocol)
                    .build().toString();
            
            //  Build payload
            //  metadata
            JsonObject md_json = Json.createObjectBuilder()
                    .add("version", "1.0")
                    .add("last_used_location", modifylocation).build();

            //  deactivate request
            JsonObject deactreq_json = Json.createObjectBuilder()
                    .add("username", accountname)
                    .add("randomid", randomid).build();
            
            //  finally payload
            String payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, md_json)
                    .add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, deactreq_json)
                    .build().toString();

            //  Make SKFE rest call and get response from the server
            System.out.println("\nCalling deactivate @ " 
                    + REST_URI + Constants.DEACTIVATE_ENDPOINT);
            response = common.callSKFERestApi(REST_URI, Constants.DEACTIVATE_ENDPOINT, 
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
                    return " Error during deactivate : " + error;
                }

                deactresponse = responseJSON.getString("Response");
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println("\nDeactivate key test complete.");
            System.out.println("******************************************");

        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return deactresponse;
    }

    @Override
    public String u2fActivate(String REST_URI, 
                            String fidoprotocol, 
                            String skcedid, 
                            String svcuser, 
                            String svcpass, 
                            String accountname, 
                            String randomid, 
                            String modifylocation) 
    {
        String actresponse = "";
        HttpURLConnection conn = null;
        String response = "";
        try {

            System.out.println("Activate key test");
            System.out.println("******************************************");

            //  Build svcinfo
            String svcinfo = Json.createObjectBuilder()
                    .add("did", skcedid)
                    .add("svcusername", svcuser)
                    .add("svcpassword", svcpass)
                    .add("protocol", fidoprotocol)
                    .build().toString();
            
            //  Build payload
            //  metadata
            JsonObject md_json = Json.createObjectBuilder()
                    .add("version", "1.0")
                    .add("last_used_location", modifylocation).build();

            //  activate request
            JsonObject actreq_json = Json.createObjectBuilder()
                    .add("username", accountname)
                    .add("randomid", randomid).build();
            
            //  finally payload
            String payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, md_json)
                    .add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, actreq_json)
                    .build().toString();

            //  Make SKFE rest call and get response from the server
            System.out.println("\nCalling activate @ " 
                    + REST_URI + Constants.ACTIVATE_ENDPOINT);
            response = common.callSKFERestApi(REST_URI, Constants.ACTIVATE_ENDPOINT, 
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
                    return " Error during activate : " + error;
                }

                actresponse = responseJSON.getString("Response");
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println("\nActivate key test complete.");
            System.out.println("******************************************");

        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return actresponse;
    }

    /**
     * deregister call
     * 
     * @param REST_URI
     * @param fidoprotocol
     * @param skcedid
     * @param svcuser
     * @param svcpass
     * @param accountname
     * @param randomid
     * @return 
     */
    @Override
    public String u2fDeregister(String REST_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname, 
                                String randomid) 
    {
        HttpURLConnection conn = null;
        String response, deregresponse = "";
        
        try {
            System.out.println("Deregister key test");
            System.out.println("******************************************");

            //  Build svcinfo
            String svcinfo = Json.createObjectBuilder()
                    .add("did", skcedid)
                    .add("svcusername", svcuser)
                    .add("svcpassword", svcpass)
                    .add("protocol", fidoprotocol)
                    .build().toString();
            
            //  Build payload
            //  deregister request
            JsonObject dereg_json = Json.createObjectBuilder()
                    .add("username", accountname)
                    .add("randomid", randomid).build();
            
            //  finally payload
            String payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, dereg_json)
                    .build().toString();

            //  Make SKFE rest call and get response from the server
            System.out.println("\nCalling deregister @ " 
                    + REST_URI + Constants.DEREGISTER_ENDPOINT);
            response = common.callSKFERestApi(REST_URI, Constants.DEREGISTER_ENDPOINT, 
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
                    return " Error during deregister : " + error;
                }

                deregresponse = responseJSON.getString("Response");
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println("\nDeregister key test complete.");
            System.out.println("******************************************");

        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FActionsOnKey.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return deregresponse;
    }
}
