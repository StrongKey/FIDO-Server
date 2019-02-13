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
import com.strongauth.skfe.client.interfaces.FIDOClientActionsOnKey;
import com.strongauth.skfe.soapstubs.*;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.ws.WebServiceException;

public class SoapFidoU2FActionsOnKey implements FIDOClientActionsOnKey {

    @Override
    public String u2fDeactivate(String SOAP_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname, 
                                String randomid, 
                                String modifylocation) 
    {
        try {
            //  set up the port
            SKFEServlet port = null;
            try {
                // Set up the URL and webService variables
                //  Create port object
                URL soapurl = new URL(SOAP_URI);
                Soap service = new Soap(soapurl);
                port = service.getSKFEServletPort();
            } catch (MalformedURLException ex) {
                throw new Exception("Malformed hostport - " + SOAP_URI);
            } catch (WebServiceException ex) {
                throw new Exception("It appears that the site - " + SOAP_URI
                        + " - is (1) either down or (2) has no access over specified port or (3) has a digital certificate that is not in your JVM's truststore.  "
                        + "In case of (3), Please include it in the JAVA_HOME/jre/lib/security/cacerts file with "
                        + "the keytool -import command before attempting this operation again.  "
                        + "Please refer to the documentation on skceclient.jar at the "
                        + "above-mentioned URL on how to accomplish this.");
            }

            System.out.println("Deactivate key test");
            System.out.println("******************************************");

            String deactresponse = "";

            JsonObject md_json = Json.createObjectBuilder()
                    .add("version", "1.0")
                    .add("last_used_location", modifylocation).build();

            JsonObject deactreq_json = Json.createObjectBuilder()
                    .add("username", accountname)
                    .add("randomid", randomid).build();

            //  Make deactivate call
            System.out.println("\n Calling deactivate ... at " + SOAP_URI);
            
            //  Build service information
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(skcedid));
            svcinfo.setSvcusername(svcuser);
            svcinfo.setSvcpassword(svcpass);
            svcinfo.setProtocol(fidoprotocol);
            
            //  Build payload
            String payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, md_json).add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, deactreq_json).build().toString();
            
            String response = port.deactivate(svcinfo, payload);
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

            return deactresponse;
        } catch (Exception ex) {
            return " Exception: " + ex.getLocalizedMessage();
        }
    }

    @Override
    public String u2fActivate(String SOAP_URI, 
                            String fidoprotocol, 
                            String skcedid, 
                            String svcuser, 
                            String svcpass, 
                            String accountname, 
                            String randomid, 
                            String modifylocation) 
    {
        try {
            //  set up the port
            SKFEServlet port = null;
            try {
                // Set up the URL and webService variables
                //  Create port object
                URL soapurl = new URL(SOAP_URI);
                Soap service = new Soap(soapurl);
                port = service.getSKFEServletPort();
            } catch (MalformedURLException ex) {
                throw new Exception("Malformed hostport - " + SOAP_URI);
            } catch (WebServiceException ex) {
                throw new Exception("It appears that the site - " + SOAP_URI
                        + " - is (1) either down or (2) has no access over specified port or (3) has a digital certificate that is not in your JVM's truststore.  "
                        + "In case of (3), Please include it in the JAVA_HOME/jre/lib/security/cacerts file with "
                        + "the keytool -import command before attempting this operation again.  "
                        + "Please refer to the documentation on skceclient.jar at the "
                        + "above-mentioned URL on how to accomplish this.");
            }

            System.out.println("Activate key test");
            System.out.println("******************************************");

            String actresponse = "";

            JsonObject md_json = Json.createObjectBuilder()
                    .add("version", "1.0")
                    .add("last_used_location", modifylocation).build();

            JsonObject actreq_json = Json.createObjectBuilder()
                    .add("username", accountname)
                    .add("randomid", randomid).build();

            //  Make deactivate call
            System.out.println("\n Calling activate ... at " + SOAP_URI);
            
            //  Build service information
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(skcedid));
            svcinfo.setSvcusername(svcuser);
            svcinfo.setSvcpassword(svcpass);
            svcinfo.setProtocol(fidoprotocol);
            
            //  Build payload
            String payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, md_json).add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, actreq_json).build().toString();
            
            String response = port.activate(svcinfo, payload);
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

            return actresponse;
        } catch (Exception ex) {
            return " Exception: " + ex.getLocalizedMessage();
        }
    }

    @Override
    public String u2fDeregister(String SOAP_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname, 
                                String randomid) 
    {
        try {
            //  set up the port
            SKFEServlet port = null;
            try {
                // Set up the URL and webService variables
                //  Create port object
                URL soapurl = new URL(SOAP_URI);
                Soap service = new Soap(soapurl);
                port = service.getSKFEServletPort();
            } catch (MalformedURLException ex) {
                throw new Exception("Malformed hostport - " + SOAP_URI);
            } catch (WebServiceException ex) {
                throw new Exception("It appears that the site - " + SOAP_URI
                        + " - is (1) either down or (2) has no access over specified port or (3) has a digital certificate that is not in your JVM's truststore.  "
                        + "In case of (3), Please include it in the JAVA_HOME/jre/lib/security/cacerts file with "
                        + "the keytool -import command before attempting this operation again.  "
                        + "Please refer to the documentation on skceclient.jar at the "
                        + "above-mentioned URL on how to accomplish this.");
            }
            System.out.println("Deregister key test");
            System.out.println("******************************************");

            String deregresponse = "";

            JsonObject dereg_json = Json.createObjectBuilder()
                    .add("username", accountname)
                    .add("randomid", randomid).build();

            //  Make deactivate call
            System.out.println("\n Calling deregister ... at " + SOAP_URI);
            
            //  Build service information
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(skcedid));
            svcinfo.setSvcusername(svcuser);
            svcinfo.setSvcpassword(svcpass);
            svcinfo.setProtocol(fidoprotocol);
            
            //  Build payload
            String payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_REQUEST, dereg_json).build().toString();
            
            String response = port.deregister(svcinfo, payload);
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

            return deregresponse;
        } catch (Exception ex) {
            return " Exception: " + ex.getLocalizedMessage();
        }
    }
}
