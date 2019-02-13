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

import com.strongauth.skfe.client.interfaces.FIDOClientAuthenticate;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.apache.commons.codec.DecoderException;

public class WsFidoU2FAuthenticate implements FIDOClientAuthenticate {

    @Override
    public String u2fAuthenticate(String HostURI, 
                                String fidoprotocol, 
                                String skcedid, 
                                String svcuser, 
                                String svcpass, 
                                String accountname, 
                                String origin,
                                int auth_counter,
                                boolean goodsig) 
    {
        System.out.println("Authentication test");
        System.out.println("*******************************");

        //  create a client object
        WsFidoClientEndPoint wA = new WsFidoClientEndPoint();
        
        //  make preauth call
        System.out.println("Calling preauth ...\n");
        JsonObject jo = Json.createObjectBuilder()
                .add("did", skcedid)
                .add("secretkey", "dummy_secretkey")
                .add("protocol", fidoprotocol)
                .add("command", "preauth")
                .add("input", Json.createObjectBuilder().add("username", accountname)).build(); 
        
        //  send msg to the web socket and wait for response
        wA.start();
        wA.sendmsg(HostURI,  jo.toString());
        int i = 0;
        while (i < 15 && !wA.getres()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WsFidoU2FAuthenticate.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
        
        //  after waiting for a specific time, retrieve the response
        String response = wA.getresponse();
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
        String signdata = resJsonObj.getJsonArray("registeredKeys").toString();
        s = new StringReader(signdata);
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
        
        System.out.println("\n preauth Complete.");
        System.out.println("\n Generating Authentication response...\n");
        
        JsonArray ja = resJsonObj.getJsonArray("signData");
        String s3 = ja.getJsonObject(0).toString();
        JsonObject authres = null;
        System.out.println(s3);
        try {
            authres = FIDOU2FTokenSimulator.generateAuthenticationResponse(appid, nonce, s3, origin, auth_counter, goodsig);
        } catch (NoSuchAlgorithmException | 
                NoSuchProviderException | 
                UnsupportedEncodingException | 
                NoSuchPaddingException | 
                DecoderException |
                InvalidParameterSpecException |
                InvalidKeyException | 
                InvalidAlgorithmParameterException | 
                ShortBufferException | 
                IllegalBlockSizeException | 
                BadPaddingException | 
                InvalidKeySpecException | 
                SignatureException ex) {
            System.out.println("\n Exception : " + ex.getLocalizedMessage());
        }
        StringReader regresreader = new StringReader(authres.toString());
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
        
        wA.setres(false);
        JsonObject auth_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is  just the first revision of the code
                    .add("last_used_location", "Bangalore, India").
                    build();
            
        jo = Json.createObjectBuilder()
                .add("did", skcedid)
                .add("secretkey", "dummy_secretkey")
                .add("protocol", fidoprotocol)
                .add("command", "authenticate")
                .add("input", authres)
                .add("auth_metadata", auth_metadata).build(); 
        
        //  send web socket message
        wA.sendmsg(HostURI, jo.toString());
        
        int j = 0;
        while (j < 15 && !wA.getres()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WsFidoU2FAuthenticate.class.getName()).log(Level.SEVERE, null, ex);
            }
            j++;
        }

        wA.interrupt();
        String resp = wA.getresponse();
        System.out.println(" Response   : " + resp);
        
        System.out.println("\nAuthentication Complete.");
        System.out.println("*******************************");

        return resp;
    }

}
