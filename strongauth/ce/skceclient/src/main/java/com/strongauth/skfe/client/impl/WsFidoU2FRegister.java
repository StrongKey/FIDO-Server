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

import com.strongauth.skfe.client.interfaces.FIDOClientRegister;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.apache.commons.codec.DecoderException;

public class WsFidoU2FRegister implements FIDOClientRegister {

    @Override
    public String u2fRegister(String hostURI, 
                            String fidoprotocol, 
                            String skcedid, 
                            String svcuser, 
                            String svcpass, 
                            String username, 
                            String origin,
                            boolean goodsig) 
    {
        System.out.println("Registration test");
        System.out.println("*******************************");
            
        //  create a client object
        WsFidoClientEndPoint w = new WsFidoClientEndPoint();
        
        //  Make preregister call
        System.out.println("Calling Pre-Registration ... at " + hostURI + "\n");
        JsonObject jo = Json.createObjectBuilder()
                .add("did", skcedid)
                .add("secretkey", "dummy_secretkey")
                .add("protocol", fidoprotocol)
                .add("command", "preregister")
                .add("input", Json.createObjectBuilder().add("username", username)).build(); 
        
        w.start();
        w.sendmsg(hostURI, jo.toString());
        
        int i = 0;
        while (i < 15 && !w.getres()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WsFidoU2FRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }

        String response = w.getresponse();
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
                return " Error during preregister : " + error;
            }
        } catch (Exception ex) {
            //  continue since there is no error
        }
            
        System.out.println("\n Registration Parameters:\n");
        String challenge = responseJSON.getJsonObject("Challenge").toString();

        s = new StringReader(challenge);
        jsonReader = Json.createReader(s);
        JsonObject resJsonObj = jsonReader.readObject();
        jsonReader.close();

        String appidfromserver = resJsonObj.getString("appId");
        String s2 = resJsonObj.getJsonObject("registerRequests").toString();
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
        
        System.out.println("\n Pre-Registration Complete.");
        System.out.println("\n Generating Registration response...\n");
        
        JsonObject regres = null;
        try {
            regres = FIDOU2FTokenSimulator.generateRegistrationResponse(appidfromserver,s2, origin,goodsig);
        } catch (NoSuchAlgorithmException | 
                NoSuchProviderException | 
                KeyStoreException | 
                DecoderException |
                InvalidParameterSpecException |
                IOException | 
                CertificateException | 
                InvalidAlgorithmParameterException | 
                InvalidKeyException | 
                SignatureException | 
                NoSuchPaddingException | 
                IllegalBlockSizeException | 
                BadPaddingException | 
                ShortBufferException | 
                UnrecoverableKeyException | 
                InvalidKeySpecException ex) {
            System.out.println("\n Exception : " + ex.getLocalizedMessage());
        }
        
        StringReader regresreader = new StringReader(regres.toString());
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
        System.out.println("\n Registering ...");

        w.setres(false);
        JsonObject reg_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is  just the first revision of the code
                    .add("create_location", "Sunnyvale, CA").
                    build();
        jo = Json.createObjectBuilder().add("did", skcedid).add("secretkey", "dummy_secretkey").add("protocol", "U2F_V2").add("command", "register").add("input", regres).add("reg_metadata", reg_metadata).build(); 
        w.sendmsg(hostURI, jo.toString());
        int j = 0;
        while (j < 15 && !w.getres()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WsFidoU2FRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
            j++;
        }

        w.interrupt();
        String resp = w.getresponse();
        System.out.println(" Response   : " + resp);
        
        System.out.println("\n Registration Complete.");
        System.out.println("*******************************");
        
        return resp;
    }
}
