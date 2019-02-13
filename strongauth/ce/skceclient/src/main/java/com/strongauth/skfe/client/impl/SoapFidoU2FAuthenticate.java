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
import com.strongauth.skfe.client.interfaces.FIDOClientAuthenticate;
import com.strongauth.skfe.soapstubs.*;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.xml.ws.WebServiceException;
import org.bouncycastle.util.encoders.DecoderException;

public class SoapFidoU2FAuthenticate implements FIDOClientAuthenticate {

    @Override
    public String u2fAuthenticate(String SOAP_URI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname,
                                String origin,
                                int auth_counter,
                                boolean goodsign) 
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
            
            System.out.println("Authentication test");
            System.out.println("*******************************");
                       
            //  Make pre-auth call
            System.out.println("\n Calling preauth ...");
            
            //  Build service information
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(skcedid));
            svcinfo.setSvcusername(svcuser);
            svcinfo.setSvcpassword(svcpass);
            svcinfo.setProtocol(fidoprotocol);
            
            //  Build payload
            String payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname).build().toString();
            
            String response = port.preauthenticate(svcinfo, payload);
            System.out.println(" Response : " + response);
            
            //  Build a json object out of response
            StringReader s = new StringReader(response);
            JsonReader jsonReader = Json.createReader(s);
            JsonObject responseJSON = jsonReader.readObject();
            jsonReader.close();
            
            //  Check to see if there is any
            try {
                String error = responseJSON.getString("Error");
                if ( error != null && !error.equalsIgnoreCase("")) {
                    System.out.println("*******************************");
                    return " Error during preauth : " + error;
                }
            } catch (Exception ex) {
                //  continue since there is no error
            }
            
            System.out.println(" Authentication Parameters:");
            String challenge = responseJSON.getJsonObject("Challenge").toString();
            s = new StringReader(challenge);

            jsonReader = Json.createReader(s);
            JsonObject resJsonObj = jsonReader.readObject();
            jsonReader.close();
            
            String appid ;
            try{
                appid = resJsonObj.getString("appId");
            } catch (NullPointerException ex) {
                appid = null;
            }
            String nonce = resJsonObj.getString("challenge");
            JsonArray jarray = resJsonObj.getJsonArray("registeredKeys");
            String s2 = jarray.getJsonObject(0).toString();
            s = new StringReader(s2);
            JsonParserFactory factory = Json.createParserFactory(null);
            JsonParser parser = factory.createParser(s);
            while (parser.hasNext()) {
                JsonParser.Event e = parser.next();
                switch (e) {
                    case KEY_NAME: {
                        System.out.print("\t" + parser.getString() + " = ");
                        break;
                    }
                    case VALUE_STRING: {
                        System.out.println(parser.getString());
                        break;
                    }
                }
            }
            System.out.println("\n Pre-Authentication Complete.");

            System.out.println("\n Generating Authentication response...\n");
            JsonObject input = null;
            try {
                System.out.println("  **********************************  AUTHENTICATE : INPUT = " + s2 );
                input = FIDOU2FTokenSimulator.generateAuthenticationResponse(appid, nonce, s2, origin, auth_counter, goodsign);
            } catch (NoSuchAlgorithmException | 
                    NoSuchProviderException | 
                    UnsupportedEncodingException | 
                    DecoderException | 
                    NoSuchPaddingException | 
                    InvalidKeyException | 
                    InvalidAlgorithmParameterException | 
                    ShortBufferException | 
                    IllegalBlockSizeException | 
                    BadPaddingException | 
                    InvalidKeySpecException | 
                    SignatureException ex) {
                ex.printStackTrace();
                System.out.println("\n Exception : " + ex.getLocalizedMessage());
            }

            StringReader regresreader = new StringReader(input.toString());
            parser = factory.createParser(regresreader);
            while (parser.hasNext()) {
                JsonParser.Event e = parser.next();
                switch (e) {
                    case KEY_NAME: {
                        System.out.print("\t" + parser.getString() + " = ");
                        break;
                    }
                    case VALUE_STRING: {
                        System.out.println(parser.getString());
                        break;
                    }
                }
            }
            System.out.println("\n Finished Generating Authentication Response.");
            System.out.println("\n Authenticating ...");
//Thread.sleep(5000);
            JsonObject auth_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is  just the first revision of the code
                    .add("last_used_location", "Bangalore, India")
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname).
                    build();
            payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, auth_metadata).add(Constants.JSON_KEY_SERVLET_INPUT_RESPONSE, input).build().toString();
            String regresponse = port.authenticate(svcinfo, payload);
            System.out.println(" Response   : " + regresponse);
            
            System.out.println("\n Authentication Complete.");
            System.out.println("*******************************");
            
            return regresponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return " Exception: " + ex.getLocalizedMessage();
        }
    }
}
