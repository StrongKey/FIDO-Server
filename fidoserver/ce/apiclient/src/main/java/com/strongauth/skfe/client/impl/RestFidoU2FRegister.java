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
import static com.strongauth.skceclient.common.common.calculateHMAC;
import static com.strongauth.skceclient.common.common.calculateMD5;
import com.strongauth.skfe.client.interfaces.FIDOClientRegister;
import com.strongauth.skfe.fido2.Fido2TokenSim;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RestFidoU2FRegister implements FIDOClientRegister {

    @Override
    public String u2fRegister(String REST_URI, 
                            String fidoprotocol, 
                            String skcedid, 
                            String accesskey, 
                            String secretkey, 
                            String accountname, 
                            String origin,
                            boolean goodsig) 
    {
        // Local variables
        HttpURLConnection conn = null;
        String regresponse="";
        
        try {
            System.out.println("Registration test");
            System.out.println("*******************************");

            // Build payload
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("username", accountname));
            nvps.add(new BasicNameValuePair("protocol", fidoprotocol));

            if ("FIDO20".compareTo(fidoprotocol) == 0) {
                nvps.add(new BasicNameValuePair("displayName", accountname));
            }
//            System.out.println(EntityUtils.toString(new UrlEncodedFormEntity(nvps)));

            HttpEntity body = new UrlEncodedFormEntity(nvps);

            String contentType = "application/x-www-form-urlencoded";
            String currentDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date());
            String contentMD5 = calculateMD5(EntityUtils.toString(new UrlEncodedFormEntity(nvps)));

            String resourceLoc = REST_URI + "/domains/" + skcedid + Constants.PRE_REGISTER_ENDPOINT;
            
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(resourceLoc);
            httpPost.setEntity(body);
            String requestToHmac = httpPost.getMethod() + "\n"
                    + contentMD5 + "\n"
                    + contentType + "\n"
                    + currentDate + "\n"
                    + httpPost.getURI().getPath();

            String hmac = calculateHMAC(secretkey, requestToHmac);
            httpPost.addHeader("Authorization", "HMAC " + accesskey + ":" + hmac);
            httpPost.addHeader("Content-MD5", contentMD5);
            httpPost.addHeader("content-type", contentType);
            httpPost.addHeader("Date", currentDate);

            //  Make SKFE rest call and get response from the server
            System.out.println("\nCalling preregister @ " + resourceLoc);
            CloseableHttpResponse response = httpclient.execute(httpPost);
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
                        System.out.println("Error during pre-register : 401 HMAC Authentication Failed");
                        return null;
                    case 404:
                        System.out.println("Error during pre-register : 404 Resource not found");
                        return null;
                    case 400:
                    case 500:
                    default:
                        System.out.println("Error during pre-register : " + responseStatusLine.getStatusCode() + " " + result);
                        return null;
                }
            } finally {
                response.close();
            }

            System.out.println(" Response : " + result);
            
            //  Build a json object out of response
            StringReader s = new StringReader(result);
            JsonReader jsonReader = Json.createReader(s);
            JsonObject responseJSON = jsonReader.readObject();
            jsonReader.close();
            
            JsonObject resJsonObj = responseJSON.getJsonObject("Challenge");
            
            System.out.println("\n Pre-Registration Complete.");
            if (true)
                return null;
            System.out.println("\n Generating Registration response...\n");
            
            JsonObject input = null;
            JsonParserFactory factory = Json.createParserFactory(null);
            JsonParser parser;
            
            
            if ("U2F_V2".compareTo(fidoprotocol) == 0) {
                
                System.out.println("\n Registration Parameters:\n");
            JsonArray jarray = resJsonObj.getJsonArray("registerRequests");
            String s2 = jarray.getJsonObject(0).toString();
            s = new StringReader(s2);
                parser = factory.createParser(s);
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
            
                String appidfromserver = resJsonObj.getString("appId");
            
            try {
                    input = FIDOU2FTokenSimulator.generateRegistrationResponse(appidfromserver, s2, origin, goodsig);
                } catch (NoSuchAlgorithmException
                        | NoSuchProviderException
                        | KeyStoreException
                        | InvalidParameterSpecException
                        | DecoderException
                        | IOException
                        | CertificateException
                        | InvalidAlgorithmParameterException
                        | InvalidKeyException
                        | SignatureException
                        | NoSuchPaddingException
                        | IllegalBlockSizeException
                        | BadPaddingException
                        | ShortBufferException
                        | UnrecoverableKeyException
                        | InvalidKeySpecException ex) {
                System.out.println("\n Exception : " + ex.getLocalizedMessage());
            }
            
            } else if ("FIDO20".compareTo(fidoprotocol) == 0) {
                
                JsonObject rpInfo = resJsonObj.getJsonObject("rp");
                String rpName = rpInfo.getString("name");
                String challenge = resJsonObj.getString("challenge");
                
                Fido2TokenSim sim = new Fido2TokenSim(origin);
                JsonObjectBuilder in = Json.createObjectBuilder();
                
                JsonObjectBuilder rp = Json.createObjectBuilder();
                rp.add(Constants.WebAuthn.RELYING_PARTY_NAME, rpName);
                in.add(Constants.WebAuthn.RELYING_PARTY, rp);
                
                JsonObjectBuilder user = Json.createObjectBuilder();
                user.add(Constants.WebAuthn.USER_NAME,accountname);
                user.add(Constants.WebAuthn.USER_ID,accountname);
                in.add(Constants.WebAuthn.USER,user);
                
                JsonArrayBuilder pubKeyParams = Json.createArrayBuilder();
                JsonObjectBuilder alg1 = Json.createObjectBuilder();
                alg1.add(Constants.WebAuthn.PUBKEYCREDPARAMS_ALG, -7);
                alg1.add(Constants.WebAuthn.TYPE, "public-key");
                pubKeyParams.add(alg1);

                JsonObjectBuilder alg2 = Json.createObjectBuilder();
                alg2.add(Constants.WebAuthn.PUBKEYCREDPARAMS_ALG, -257);
                alg2.add(Constants.WebAuthn.TYPE, "public-key");
                pubKeyParams.add(alg2);

                in.add(Constants.WebAuthn.PUBKEYCREDPARAMS, pubKeyParams);
                
                in.add(Constants.WebAuthn.CHALLENGE,challenge);
                in.add(Constants.WebAuthn.ATTESTATION_PREFERENCE,"direct");
                
                /*
                {
                    "rp": {
                        "name": "example.com"
                    },
                    "user": {
                        "name": "fidouser06110896",
                        "displayName": "fidouser06110896",
                        "id": "B16lQ8O1ZDTNX0NP0EP8dNRV6ShLlS4cbcWa72r2GyDfleYgFoJe7xZBIvST9PtZB_Jjx8als_XqggjeTQJyFw"
                    },
                    "challenge": "VciUZwhfiPCdElS0RygNEHAxxKtqUBkFN472KakrjsgqfFLKNm8wOkGYQFaqklFYrtNST1QLSOuaOO9r-428GH7LZ6qJ9NYkdH79jonCDptr5Pt4BfFmQDg0bTJXpc1dLAPRYsyrezVDtTWNw2FX3mibjvst9ThxfNe8deWdVsE",
                    "attestation": "direct",
                    "pubKeyCredParams": [{
                        "type": "public-key",
                        "alg": -7
                    }, {
                        "type": "public-key",
                        "alg": -257
                    }]
                }
                */
                input = sim.create(in.build());
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
            System.out.println("\n Registering ...");
            
            //  Build payload
            JsonObject reg_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is just the first revision of the code
                    .add("create_location", "Sunnyvale, CA")
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, accountname)
                    .build();
//            payload = Json.createObjectBuilder()
//                    .add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, reg_metadata)
//                    .add(Constants.JSON_KEY_SERVLET_INPUT_RESPONSE, input)
//                    .build().toString();
//
//            //  Make SKFE rest call and get response from the server
//            System.out.println("\nCalling register @ " 
//                    + REST_URI + Constants.REGISTER_ENDPOINT);
//            regresponse = common.callSKFERestApi(REST_URI, Constants.REGISTER_ENDPOINT, 
//                                                    svcinfo, payload);
//            System.out.println(" Response   : " + regresponse);
            
            System.out.println("\n Registration Complete.");
            System.out.println("*******************************");
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FRegister.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FRegister.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RestFidoU2FRegister.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if ( conn != null ) {
                conn.disconnect();
            }
        }
        return regresponse;
    }
}
