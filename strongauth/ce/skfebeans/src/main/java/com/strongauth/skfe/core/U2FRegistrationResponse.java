/**
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
 * Derived class POJO to hold U2F Registration response that comes back to the 
 * FIDO server from the RP application. This class is a derivation of U2FResponse
 * class.
 * 
 * This class object once constructed successfully is capable of processing the
 * registration response.
 *
 */
package com.strongauth.skfe.core;

import com.strongauth.skfe.utilities.skfeLogger;
import com.strongauth.crypto.utility.cryptoCommon;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;

/**
 * Derived class for U2F Registration response that comes back to the FIDO server 
 * from the RP application.
 */
public class U2FRegistrationResponse extends U2FResponse implements Serializable {

    /**
     * This class' name - used for logging
     */
    private String classname = this.getClass().getName();
    
    private String registrationdata;
    private String keyhandle = null;
    private String userpublickey = null;

    /**
     * The constructor of this class takes the U2F registration response parameters
     * in the form of stringified Json. The method parses the Json to extract needed
     * fileds compliant with the u2fversion specified. 
     * @param u2fversion        - Version of the U2F protocol being communicated in; 
     *                            example : "U2F_V2"     
     * @param regresponseJson   - U2F Reg Response params in stringified Json form
     * @throws SKFEException 
     *                          - In case of any error
     */
    public U2FRegistrationResponse(String u2fversion, String regresponseJson) throws SKFEException {
        
        //  Input checks
        if ( u2fversion==null || u2fversion.trim().isEmpty() ) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FRegistrationResponse", skfeCommon.getMessageProperty("FIDO-ERR-5001"), " u2f version");
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5001") + " username");
        }
        
        if ( regresponseJson==null || regresponseJson.trim().isEmpty() ) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FRegistrationResponse", skfeCommon.getMessageProperty("FIDO-ERR-5001"), " regresponseJson");
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5001") + " regresponseJson");
        }
        
        //  u2f protocol version specific processing.
        if ( u2fversion.equalsIgnoreCase(U2F_VERSION_V2) ) {
            
            //  Parse the reg response json string
            try (JsonReader jsonReader = Json.createReader(new StringReader(regresponseJson))) {
                JsonObject jsonObject = jsonReader.readObject();
                browserdata = jsonObject.getString(skfeConstants.JSON_KEY_CLIENTDATA);
                registrationdata = jsonObject.getString(skfeConstants.JSON_KEY_REGSITRATIONDATA);
            } catch (Exception ex) {
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FAuthenticationResponse", skfeCommon.getMessageProperty("FIDO-ERR-5011"), ex.getLocalizedMessage());
                throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5011") + ex.getLocalizedMessage());
            }

            //  Generate new browser data
            bd = new BrowserData(this.browserdata, BrowserData.REGISTRATION_RESPONSE);
            
        } else {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "U2FRegistrationResponse", skfeCommon.getMessageProperty("FIDO-ERR-5002"), " version passed=" + u2fversion);
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5002") + " version passed=" + u2fversion);
        }
    }

    /**
     * Get methods to access the response parameters
     * @return 
     */
    public String getBrowserdata() {
        return browserdata;
    }

    public String getRegistrationdata() {
        return registrationdata;
    }

    public String getKeyhandle() {
        return keyhandle;
    }

    public String getUserpublickey() {
        return userpublickey;
    }
    
    /**
     * Converts this POJO into a JsonObject and returns the same.
     * @return JsonObject
     */
    public final JsonObject toJsonObject() {

        JsonObject jsonObj = Json.createObjectBuilder().add(skfeConstants.JSON_USER_KEY_HANDLE_SERVLET, this.keyhandle)
                        .add(skfeConstants.JSON_USER_PUBLIC_KEY_SERVLET, this.userpublickey)
//                        .add(skfeConstants.JSON_KEY_SESSIONID, this.sessionid)
                        .build();
        return jsonObj;
    }
    
    /**
     * Converts this POJO into a JsonObject and returns the String form of it.
     * @return String containing the Json representation of this POJO.
     */
    public final String toJsonString() {
        return toJsonObject().toString();
    }

    /**
     * Once this class object is successfully constructed, calling verify method
     * will actually process the registration response params.
     * 
     * The first step in verification is sessionid validation, which if found
     * valid goes ahead and processes the registration data.
     * @param appid
     * @return
     * @throws SKFEException - In case of any error
     */
    public final boolean verify(String appid) throws SKFEException {

        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "verify", skfeCommon.getMessageProperty("FIDO-MSG-5009"), "");
        
        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "verify", skfeCommon.getMessageProperty("FIDO-MSG-5011"), "");

        try {
            return processRegistrationData(this.registrationdata, this.browserdata, appid);
        } catch (SignatureException ex) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "verify", skfeCommon.getMessageProperty("FIDO-ERR-5004"), "");
            throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5004"));
        }
    }

    /**
     * Processes the registration data
     * @param registrationdata
     * @param browserdata
     * @param appid
     * @return
     * @throws SKFEException  - In case of any error
     * @throws SignatureException   - In case of any signature related error
     */
    private boolean processRegistrationData(String registrationdata, String browserdata, String appid) throws SKFEException, SignatureException {

        skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5012"), "");
        
        try {
            byte[] regDataReceived = Base64.decodeBase64(registrationdata);

            //  get First byte
            byte reservedByte = regDataReceived[0];
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5014"), reservedByte);

            int total = 1;

            //  get UserPublickey
            byte[] publicKeyBytes = new byte[skfeConstants.P256_PUBLIC_KEY_SIZE];
            System.arraycopy(regDataReceived, total, publicKeyBytes, 0, skfeConstants.P256_PUBLIC_KEY_SIZE);
            total += skfeConstants.P256_PUBLIC_KEY_SIZE;
            PublicKey puk = cryptoCommon.getUserECPublicKey(publicKeyBytes);
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5015"), puk);
            this.userpublickey = Base64.encodeBase64String(puk.getEncoded());

            //  get KeyHandle length
            byte keyHandleLength = (byte) regDataReceived[total];
            int KeyHandleLengthInt = (keyHandleLength & 0xff);
            total += 1;
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5016"), KeyHandleLengthInt);

            //  get KeyHandle
            byte[] keyHandleBytes = new byte[KeyHandleLengthInt];
            System.arraycopy(regDataReceived, total, keyHandleBytes, 0, KeyHandleLengthInt);
            total += KeyHandleLengthInt;
            this.keyhandle = Base64.encodeBase64URLSafeString(keyHandleBytes);
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5017"), this.keyhandle);

            //  getattestationCertificate
            //  send in just the remaining bytes FIXME //certvalidity check, verify check,path validation checks
            byte[] acertBytes = new byte[regDataReceived.length - total];
            System.arraycopy(regDataReceived, total, acertBytes, 0, regDataReceived.length - total);
            X509Certificate attestationcertificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(acertBytes));
            int certificateLength = attestationcertificate.getEncoded().length;
            total += certificateLength;
            
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5018"), attestationcertificate);
            if (!cryptoCommon.verifyU2FAttestationCertificate(attestationcertificate)) {
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-ERR-5010"), "");
                throw new SKFEException(skfeCommon.getMessageProperty("FIDO-ERR-5010"));
            }           

            //  getSignatureBytes
            int signedBytesLength = regDataReceived.length - total;
            byte[] signedBytes = new byte[signedBytesLength];
            System.arraycopy(regDataReceived, total, signedBytes, 0, signedBytesLength);
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5019"), Hex.encodeHexString(signedBytes));

            //  verify signature
            //  send all parameters required to build the plaintext message
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5020"), "");
            String appIDHash = skfeCommon.getDigest(appid, "SHA-256");
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5021"), 
                    Hex.encodeHexString(Base64.decodeBase64(appIDHash)));

            //  get browserdata Hash
            //  Base64decode browser data to obtain a string
            String brData = new String(Base64.decodeBase64(browserdata), "UTF-8");

            String bdHash = skfeCommon.getDigest(brData, "SHA-256");
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5022"), 
                    Hex.encodeHexString(Base64.decodeBase64(bdHash)));

            //  get object signed        
            String objectSigned = objectTBS(appIDHash, bdHash, this.keyhandle, this.userpublickey);
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5023"), 
                    Hex.encodeHexString(Base64.decodeBase64(objectSigned)));

            if (cryptoCommon.verifySignature(signedBytes, attestationcertificate.getPublicKey(), objectSigned)) {
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.FINE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-MSG-5024"), "");
                //return toJsonString(Base64.encodeBase64URLSafeString(keyHandleBytes), Base64.encodeBase64String(puk.getEncoded()), sessionid);
                return true;

            } else {
                skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-ERR-5005"), "");
                return false;
            }
        } catch (SKFEException | DecoderException | InvalidParameterSpecException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException | UnsupportedEncodingException | ArrayIndexOutOfBoundsException | InvalidKeySpecException ex) {
            skfeLogger.logp(skfeConstants.SKFE_LOGGER,Level.SEVERE, classname, "processRegistrationData", skfeCommon.getMessageProperty("FIDO-ERR-5006"), ex.getLocalizedMessage());
            ex.printStackTrace();
            throw new SKFEException(ex);
        }
    }

    /**
     * 
     * @param ApplicationParam
     * @param ChallengeParam
     * @param kh
     * @param PublicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException 
     */
    private String objectTBS(String ApplicationParam, String ChallengeParam, String kh, String PublicKey) 
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, DecoderException {
        
        byte constant = (byte) 0x00;
        byte[] Challenge = Base64.decodeBase64(ChallengeParam);
        int ChanllengeL = Challenge.length;
        byte[] Application = Base64.decodeBase64(ApplicationParam);
        int ApplicationL = Application.length;
        byte[] keyHandle = Base64.decodeBase64(kh);
        int keyHandleL = keyHandle.length;
        byte[] publicKey = Base64.decodeBase64(PublicKey);
        int publicKeyL = publicKey.length;

        //  Convert back to publicKey
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BCFIPS");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
        PublicKey pub = kf.generatePublic(pubKeySpec);

        ASN1InputStream bIn = new ASN1InputStream(new ByteArrayInputStream(pub.getEncoded()));
        ASN1Primitive obj = null;
        try {
            obj = bIn.readObject();
        } catch (IOException ex) {}
        Enumeration e = ((ASN1Sequence) obj).getObjects();

        byte[] q = null;
        while (e.hasMoreElements()) {
            ASN1Primitive o = (ASN1Primitive) e.nextElement();
            if (o instanceof DERBitString) {
                DERBitString bt = (DERBitString) o;
                q = bt.getBytes();
            }
        }

        int pukL = skfeConstants.P256_PUBLIC_KEY_SIZE; //ECDSA secp256r1 publickey length        
        byte[] ob2Sign = new byte[1 + ChanllengeL + ApplicationL + keyHandleL + pukL];

        int tot = 1;
        ob2Sign[0] = constant;
        System.arraycopy(Application, 0, ob2Sign, tot, ApplicationL);
        tot += ApplicationL;
        System.arraycopy(Challenge, 0, ob2Sign, tot, ChanllengeL);
        tot += ChanllengeL;
        System.arraycopy(keyHandle, 0, ob2Sign, tot, keyHandleL);
        tot += keyHandleL;
        System.arraycopy(q, 0, ob2Sign, tot, pukL);
        tot += pukL;
        
        return Base64.encodeBase64String(ob2Sign);
    }
}
