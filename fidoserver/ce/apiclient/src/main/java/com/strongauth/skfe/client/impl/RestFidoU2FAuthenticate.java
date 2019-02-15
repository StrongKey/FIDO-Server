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
import com.strongauth.skfe.client.interfaces.FIDOClientAuthenticate;
import com.strongauth.skfe.fido2.Fido2TokenSim;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.apache.commons.codec.DecoderException;

public class RestFidoU2FAuthenticate implements FIDOClientAuthenticate {

    @Override
    public String u2fAuthenticate(String REST_URI, 
            String fidoprotocol, 
            String skcedid, 
            String svcuser, 
            String svcpass, 
            String accountname, 
            String origin,
            int auth_counter,
            boolean goodisg) 
    {
        HttpURLConnection conn = null;
        String response, authresponse = "";
        try {
            System.out.println("Authentication test");
            System.out.println("*******************************");

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
            System.out.println("\nCalling preauthenticate @ " 
                    + REST_URI + Constants.PRE_AUTH_ENDPOINT);
            response = common.callSKFERestApi(REST_URI, Constants.PRE_AUTH_ENDPOINT, 
                                            svcinfo, payload);
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
                    return " Error during preauth : " + error;
                }
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println("\n Authentication Parameters:\n");
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


            System.out.println("\n Pre-Authentication Complete.");
            System.out.println("\n Generating Authentication response...\n");
            JsonObject input = null;
            JsonParserFactory factory = Json.createParserFactory(null);

            if ("U2F_V2".compareTo(fidoprotocol) == 0) {

            JsonArray jarray = resJsonObj.getJsonArray("registeredKeys");
            String s2 = jarray.getJsonObject(0).toString();
            s = new StringReader(s2);
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

            try {
                    input = FIDOU2FTokenSimulator.generateAuthenticationResponse(appid, nonce, s2, origin, auth_counter, goodisg);
                } catch (NoSuchAlgorithmException
                        | NoSuchProviderException
                        | DecoderException
                        | InvalidParameterSpecException
                        | UnsupportedEncodingException
                        | NoSuchPaddingException
                        | InvalidKeyException
                        | InvalidAlgorithmParameterException
                        | ShortBufferException
                        | IllegalBlockSizeException
                        | BadPaddingException
                        | InvalidKeySpecException
                        | SignatureException ex) {
                System.out.println("\n Exception : " + ex.getLocalizedMessage());
            } 
            } else if ("FIDO20".compareTo(fidoprotocol) == 0) {
                Fido2TokenSim sim = new Fido2TokenSim(origin);
                JsonObjectBuilder in = Json.createObjectBuilder();

                in.add(Constants.WebAuthn.RELYING_PARTY_RPID, "");
                in.add(Constants.WebAuthn.CHALLENGE,nonce);
                
                /*
                    {
                        "challenge": "asdfasdfasdfasdf",
                        "rpId": "example.com"
                    }
                */
                input = sim.get(in.build());
            }

            StringReader regresreader = new StringReader(input.toString());
            JsonParser parser = factory.createParser(regresreader);
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
            
            //  Build payload
            JsonObject auth_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is just the first revision of the code
                    .add("last_used_location", "Bangalore, India")
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname).
                    build();
            payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, auth_metadata)
                    .add(Constants.JSON_KEY_SERVLET_INPUT_RESPONSE, input)
                    .build().toString();

            //  Make SKFE rest call and get response from the server
            System.out.println("\nCalling authenticate @ " 
                    + REST_URI + Constants.AUTHENTICATE_ENDPOINT);
            authresponse = common.callSKFERestApi(REST_URI, Constants.AUTHENTICATE_ENDPOINT, 
                                            svcinfo, payload);
            System.out.println(" Response   : " + authresponse);

            System.out.println("\nAuthentication Complete.");
            System.out.println("*******************************");

        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FAuthenticate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FAuthenticate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if ( conn != null ) {
                conn.disconnect();
            }
        }
        return authresponse;
    }
}
