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
import com.strongauth.skfe.client.interfaces.FIDOClientRegister;
import com.strongauth.skfe.soapstubs.*;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
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

public class SoapFidoU2FRegister implements FIDOClientRegister {

    @Override
    public String u2fRegister(String SOAP_URI, 
                            String fidoprotocol, 
                            String skcedid, 
                            String svcuser, 
                            String svcpass, 
                            String accountname, 
                            String origin,
                            boolean goodsig) 
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

            System.out.println("Registration test");
            System.out.println("*******************************");

            //  Make pre-register call
            System.out.println("\n Calling preregister ...");
            
            //  Build service information
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(skcedid));
            svcinfo.setSvcusername(svcuser);
            svcinfo.setSvcpassword(svcpass);
            svcinfo.setProtocol(fidoprotocol);
            
            //  Build payload
            String payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname).build().toString();
            
            String response = port.preregister(svcinfo, payload);
            System.out.println(" Response : " + response);

            //  Build a json object out of response
            StringReader s = new StringReader(response);
            JsonReader jsonReader = Json.createReader(s);
            JsonObject responseJSON = jsonReader.readObject();
            jsonReader.close();

            //  Check to see if there is any
            try {
                String error = responseJSON.getString("Error");
                if (error != null && !error.equalsIgnoreCase("")) {
                    System.out.println("*******************************");
                    return " Error during preregister : " + error;
                }
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println(" Registration Parameters:");

            String challenge = responseJSON.getJsonObject("Challenge").toString();

            s = new StringReader(challenge);
            jsonReader = Json.createReader(s);
            JsonObject resJsonObj = jsonReader.readObject();
            jsonReader.close();
            String appidfromserver = resJsonObj.getString("appId");
            JsonArray jarray = resJsonObj.getJsonArray("registerRequests");
            String enrollchallenges = jarray.getJsonObject(0).toString();
            s = new StringReader(enrollchallenges);
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
            System.out.println("\n Pre-Registration Complete.");
            System.out.println("\n Generating Registration response...\n");

            JsonObject input = null;
            try {
                input = FIDOU2FTokenSimulator.generateRegistrationResponse(appidfromserver, enrollchallenges, origin, goodsig);
            } catch (NoSuchAlgorithmException |
                    NoSuchProviderException |
                    KeyStoreException |
                    IOException |
                    CertificateException |
                    InvalidAlgorithmParameterException |
                    InvalidKeyException |
                    SignatureException |
                    NoSuchPaddingException |
                    DecoderException |
                    IllegalBlockSizeException |
                    BadPaddingException |
                    ShortBufferException |
                    UnrecoverableKeyException |
                    InvalidKeySpecException ex) {
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
            System.out.println("\n Finished Generating Registration Response.");
            System.out.println("\n Calling Register ...");
          
            //  Build payload
            JsonObject reg_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is  just the first revision of the code
                    .add("create_location", "Sunnyvale, CA")
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname).
                    build();
            payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, reg_metadata).add(Constants.JSON_KEY_SERVLET_INPUT_RESPONSE, input).build().toString();
            
            String regresponse = port.register(svcinfo, payload);
            System.out.println(" Response   : " + regresponse);
            System.out.println("\n Registration Complete.");
            System.out.println("*******************************");
            return regresponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return " Exception: " + ex.getLocalizedMessage();
        }
    }
}
