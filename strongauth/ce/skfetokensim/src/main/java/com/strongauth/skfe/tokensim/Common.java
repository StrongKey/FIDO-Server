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
 * **********************************************
 *
 *  888b    888          888
 *  8888b   888          888
 *  88888b  888          888
 *  888Y88b 888  .d88b.  888888  .d88b.  .d8888b
 *  888 Y88b888 d88""88b 888    d8P  Y8b 88K
 *  888  Y88888 888  888 888    88888888 "Y8888b.
 *  888   Y8888 Y88..88P Y88b.  Y8b.          X88
 *  888    Y888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * **********************************************
 *
 * Common class with static helper functions
 */

package com.strongauth.skfe.tokensim;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

public final class Common 
{
    // Json factories for creating Json parsers/generators
    private static final JsonGeneratorFactory jgf;
    private static final JsonParserFactory jpf;
    
    /*******************************************
    d8b          d8b 888
    Y8P          Y8P 888
                     888
    888 88888b.  888 888888
    888 888 "88b 888 888
    888 888  888 888 888
    888 888  888 888 Y88b.
    888 888  888 888  "Y888
    ********************************************
    * 
    * A static initializer block to get stuff initialized
    */
    static {
        // Add BouncyCastle JCE provider
        if (Security.getProvider("BCFIPS") == null) {
            Security.addProvider(new BouncyCastleFipsProvider());
        }
        
        // Setup JSON factories
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        jgf = Json.createGeneratorFactory(properties);
        jpf = Json.createParserFactory(null);
    }
    
    /*******************************************
                                      .d888          
                                     d88P"           
                                     888             
    888d888  .d88b.   .d88b.         888888 88888b.  
    888P"   d8P  Y8b d88P"88b        888    888 "88b 
    888     88888888 888  888 888888 888    888  888 
    888     Y8b.     Y88b 888        888    888  888 
    888      "Y8888   "Y88888        888    888  888 
                          888                        
                     Y8b d88P                        
                      "Y88P"                         
    ********************************************
    /**
     * Decodes the returned value from a preregister webservice request
     * @param input String containing JSON object
     * @param type int value denoting the element we want from the JSON
     * @return String with the returned value from the JSON
     */
    public static String decodeRegistrationSignatureRequest(String input, int type) 
    {
        JsonObject jsonObject;
        try (JsonReader jsonReader = Json.createReader(new StringReader(input))) {
            jsonObject = jsonReader.readObject();
        }

        switch (type) {
            case Constants.JSON_KEY_SESSIONID:  return jsonObject.getString(Constants.JSON_KEY_SESSIONID_LABEL);
            case Constants.JSON_KEY_CHALLENGE:  return jsonObject.getString(Constants.JSON_KEY_CHALLENGE_LABEL);
            case Constants.JSON_KEY_VERSION:    return jsonObject.getString(Constants.JSON_KEY_VERSION_LABEL);
            case Constants.JSON_KEY_APPID:      return jsonObject.getString(Constants.JSON_KEY_APPID_LABEL);
            default: return null; // Shouldn't happen, but....
        }
    }
   
    /**
     * Function to create the packed FIDO U2F data-structure to sign when
     * registering a new public-key with a FIDO U2F server.  See the U2F Raw
     * Messages specification for details:
     *
     * https://fidoalliance.org/specs/fido-u2f-v1.0-nfc-bt-amendment-20150514/fido-u2f-raw-message-formats.html
     * 
     * @param ApplicationParam String The application parameter is the SHA-256 
     * hash of the application identity of the application requesting the 
     * registration; it is 32-bytes in length
     * @param ChallengeParam String The challenge parameter is the SHA-256 hash 
     * of the Client Data, a string JSON data structure the FIDO Client prepares. 
     * Among other things, the Client Data contains the challenge from the 
     * relying party (hence the name of the parameter)
     * @param kh String Base64-encoded, encrypted JSON data-structure of the
     * private-key, origin and the message-digest of the private-key
     * @param PublicKey String Base64-encoded public-key of the ECDSA key-pair
     * @return String Base64-encoded data-structure of the object being signed
     * as per the FIDO U2F protocol for a new-key registration
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException 
     */
    public static String createRegistrationObjectToSign(
                                String ApplicationParam, 
                                String ChallengeParam, 
                                String kh, 
                                String PublicKey) 
                            throws 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                InvalidKeySpecException, 
                                IOException 
    {
        // U2F Signed Registration constant
        final byte[] constant = {(byte) 0x00};
        int constantL = constant.length;
        
        // 32-byte challenge parameter
        byte[] Challenge = Base64.decodeBase64(ChallengeParam);
        int ChanllengeL = Challenge.length;
        
        // 32-byte application parameter
        byte[] Application = Base64.decodeBase64(ApplicationParam);
        int ApplicationL = Application.length;
        
        // Variable length encrypted key-handle JSON data-structure
        byte[] keyHandle = Base64.decodeBase64(kh);
        int keyHandleL = keyHandle.length;
        
        // Fixed-length ECDSA public key
        byte[] publicKey = Base64.decodeBase64(PublicKey);
        int pbkL = Constants.ECDSA_P256_PUBLICKEY_LENGTH;
        
        // Test the public key for sanity
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BCFIPS");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
        PublicKey pub = kf.generatePublic(pubKeySpec);
        ECPublicKey ecpub = (ECPublicKey) pub;

        ASN1InputStream bIn = new ASN1InputStream(new ByteArrayInputStream(pub.getEncoded()));
        ASN1Primitive obj = bIn.readObject();
        Enumeration e = ((ASN1Sequence) obj).getObjects();

        byte[] q = null;
        while (e.hasMoreElements()) {
            ASN1Primitive o = (ASN1Primitive) e.nextElement();
            if (o instanceof DERBitString) {
                DERBitString bt = (DERBitString) o;
                q = bt.getBytes();
            }
        }

        // Create byte[] for to-be-signed (TBS) object
        // Could have also used  ByteBuffer for this
        int currpos = 0;
        byte[] tbs = new byte[constantL + ChanllengeL + ApplicationL + keyHandleL + pbkL];
        
        // Copy the Signed Registration constant to TBS
        System.arraycopy(constant, 0, tbs, currpos, constantL);
        currpos += constantL;
        
        // Copy ApplicationParameters to TBS
        System.arraycopy(Application, 0, tbs, currpos, ApplicationL);
        currpos += ApplicationL;
        
        // Copy ChallengeParameters to TBS
        System.arraycopy(Challenge, 0, tbs, currpos, ChanllengeL);
        currpos += ChanllengeL;
        
        // Copy encrypted KeyHandle JSON to TBS
        System.arraycopy(keyHandle, 0, tbs, currpos, keyHandleL);
        currpos += keyHandleL;
        
        // Copy public-key to TBS
        System.arraycopy(q, 0, tbs, currpos, pbkL);
        
        // Return Base64-encoded TBS
        return Base64.encodeBase64String(tbs);
    }

    /**
     * Function to sign the to-be-signed (TBS) blob with the Attestation Key
     * 
     * @param tbs String containing the Base64-encoded TBS
     * @return String containing the Base64-encoded digital signature or NULL
     * if the signature does not compute
     * @throws FileNotFoundException
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws UnrecoverableKeyException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException 
     */
    public static String signWithAttestationKey(String tbs) 
                            throws 
                                FileNotFoundException, 
                                KeyStoreException, 
                                IOException, 
                                NoSuchAlgorithmException, 
                                CertificateException, 
                                UnrecoverableKeyException, 
                                NoSuchProviderException, 
                                InvalidKeyException, 
                                SignatureException 
    {
        // Base64-decode TBS input
        byte[] tbsbytes = Base64.decodeBase64(tbs);

        // Retrieve Attestation Certificate keystore and private-key
        KeyStore attks = KeyStore.getInstance("JCEKS");
        char[] kspassword = Constants.ATTESTATION_KEYSTORE_PASSWORD.toCharArray();
        attks.load((Common.class.getResourceAsStream(Constants.ATTESTATION_KEYSTORE_FILE)), kspassword);
        PrivateKey attpvk = (PrivateKey) attks.getKey(Constants.ATTESTATION_KEYSTORE_PRIVATEKEY_ALIAS, kspassword);

        // Sign the TBS bytes
        Signature sig = Signature.getInstance("SHA256withECDSA", "BCFIPS");
        sig.initSign(attpvk, new SecureRandom());
        sig.update(tbsbytes);
        byte[] signedBytes = sig.sign();

        // Verify before responding (to be sure its accurate); this will slow
        // down responses - comment it out if faster responses are desired
        Certificate cert = attks.getCertificate(Constants.ATTESTATION_KEYSTORE_PRIVATEKEY_ALIAS);
        PublicKey attpbk = cert.getPublicKey();
        sig.initVerify(attpbk);
        sig.update(tbsbytes);
        if (sig.verify(signedBytes))
            return Base64.encodeBase64String(signedBytes);
        else
            return null;
    }

    /**
     * Function to create the Base64-encoded packed data-structure of the
     * Registration Data object for response to a FIDO U2F server.  See:
     *
     * https://fidoalliance.org/specs/fido-u2f-v1.0-nfc-bt-amendment-20150514/fido-u2f-raw-message-formats.html#response-message-framing
     * 
     * Note: Could have used ByteBuffer instead of copying arrays, but will 
     * plan on updating to ByteBuffer in the next release.
     * 
     * @param userPublicKey String with Base64-encoded public-key being registered
     * @param keyHandle String with Base64-encoded JSON structure with encrypted private key
     * @param AttestationCertificate String with Base64-encoded certificate of Attestation key
     * @param Signature String with Base64-encoded digital signature of response
     * @return String with Base64-encoded message of packed Registration Response
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException 
     */
    public static String createRegistrationData(
                                String userPublicKey, 
                                String keyHandle, 
                                String AttestationCertificate, 
                                String Signature) 
                            throws 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                InvalidKeySpecException, 
                                IOException 
    {
        // Required reserved legacy-byte for U2F response
        byte constant = 0x05;
        
        // User's Public key
        byte[] upk = Base64.decodeBase64(userPublicKey);
        int upklen = Constants.ECDSA_P256_PUBLICKEY_LENGTH;    //ECDSA secp256r1 publickey length
        
        // Key Handle and its length
        byte[] kh = Base64.decodeBase64(keyHandle);
        int khlen = kh.length;
        
        // Registration Response allows 1-byte for KH length; problem if more than 255
        if (khlen > 255) {
            System.err.println("Fatal error: Key-handle length greater than 255");
            return null;
        }
        
        // Attestation certificate and its length
        byte[] ac = Base64.decodeBase64(AttestationCertificate);
        int aclen = ac.length;
        
        // Attestation digital signature of response with length
        byte[] acsig = Base64.decodeBase64(Signature);
        int acsiglen = acsig.length;

        // Test the public key for sanity
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BCFIPS");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(upk);
        PublicKey pbk = kf.generatePublic(pubKeySpec);

        ASN1InputStream bIn = new ASN1InputStream(new ByteArrayInputStream(pbk.getEncoded()));
        ASN1Primitive obj = bIn.readObject();
        Enumeration e = ((ASN1Sequence) obj).getObjects();

        byte[] q = null;
        while (e.hasMoreElements()) {
            ASN1Primitive o = (ASN1Primitive) e.nextElement();
            if (o instanceof DERBitString) {
                DERBitString bt = (DERBitString) o;
                q = bt.getBytes();
            }
        }

        // Create byte array for Registration Response's raw-message
        byte[] regresp = new byte[1 + upklen + 1 + khlen + aclen + acsiglen];
        
        // Copy the reserved legacy-byte constant to regresp; set currpos to 1
        regresp[0] = constant;
        int currpos = 1;
        
        // Copy public-key to regresp; update currpos
        System.arraycopy(q, 0, regresp, currpos, upklen);
        currpos += upklen;

        // Copy key-handle length byte to regresp
        regresp[currpos] = (byte) khlen;
        currpos += 1;

        // Copy key-handle to regresp
        System.arraycopy(kh, 0, regresp, currpos, khlen);
        currpos += khlen;
        
        // Copy attestation certificate to regresp
        System.arraycopy(ac, 0, regresp, currpos, aclen);
        currpos += aclen;
        
        // Finally, copy signature to regresp
        System.arraycopy(acsig, 0, regresp, currpos, acsiglen);
        
        // Return URL-safe Base64-encoded response
        return Base64.encodeBase64URLSafeString(regresp);
    }
    
    /**
     * Function to create the RegistrationResponse's JSON object
     *
     * @param clientdata String containing the Base64-encoded JSON structure
     * of the i) Transaction-type; ii) Challenge from the server; iii) Origin
     * of the relying-party web-application; and (optional) iv) TLS Channel ID
     * public-key (currently not supported in this implementation yet)
     * @param sessionid String containing unique session-ID from RP web-app to
     * trace transactions with U2F server
     * @param regdata String containing Base64-encoded RegistrationData object
     * @return JsonObject
     */
    public static JsonObject createRegistrationResponse(
                                String clientdata, 
//                                String sessionid, 
                                String regdata) 
                            throws 
                                UnsupportedEncodingException 
    {
        JsonObject jo = Json.createObjectBuilder()
                .add(Constants.JSON_KEY_CLIENTDATA_LABEL, Base64.encodeBase64URLSafeString(clientdata.getBytes("UTF-8")))
//                .add(Constants.JSON_KEY_SESSIONID_LABEL, sessionid)
                .add(Constants.JSON_KEY_REGISTRATIONDATA_LABEL, regdata)
                .build();
        return jo;
    }
    
    /*******************************************
                      888    888              .d888          
                      888    888             d88P"           
                      888    888             888             
     8888b.  888  888 888888 88888b.         888888 88888b.  
        "88b 888  888 888    888 "88b        888    888 "88b 
    .d888888 888  888 888    888  888 888888 888    888  888 
    888  888 Y88b 888 Y88b.  888  888        888    888  888 
    "Y888888  "Y88888  "Y888 888  888        888    888  888 
    ********************************************

    /**
     * Decodes the returned value from a preauthenticate webservice request
     *
     * @param input String containing returned JSON
     * @param type int value denoting the type of object
     * @return String with the returned value from the JSON
     */
    public static String decodeAuthRequestJsonKeys(String input, int type) 
    {
        JsonObject jsonObject;
        try (JsonReader jsonReader = Json.createReader(new StringReader(input))) {
            jsonObject = jsonReader.readObject();
        }

        switch (type) {
            case Constants.JSON_KEY_KEYHANDLE:  return jsonObject.getString(Constants.JSON_KEY_KEYHANDLE_LABEL);
            case Constants.JSON_KEY_SESSIONID:  return jsonObject.getString(Constants.JSON_KEY_SESSIONID_LABEL);
            case Constants.JSON_KEY_CHALLENGE:  return jsonObject.getString(Constants.JSON_KEY_CHALLENGE_LABEL);
            case Constants.JSON_KEY_VERSION:    return jsonObject.getString(Constants.JSON_KEY_VERSION_LABEL);
            case Constants.JSON_KEY_APPID:      return jsonObject.getString(Constants.JSON_KEY_APPID_LABEL);
            default: return null;   // Shouldn't happen, but...
        }
    }
    
    /**
     * Function to create the U2F Authentication response in the software
     * authenticator.
     * 
     * @param chalparam String containing the Base64-encoded hash of the 
     * challenge nonce sent by SKCE (U2F server) from the preregister call
     * @param appparam String containing the Base64-encoded hash of the
     * facet-id (application parameter)
     * @param keyhandle String containing the Base64-encoded encrypted KeyHandle
     * @return String containing the base64-encoded signed authentication response
     */
    public static String createAuthenticationSignatureResponse(String chalparam, String appparam, String keyhandle, int counter) throws InvalidParameterSpecException 
    {
        // Recover challenge parameter
        byte[] cpbytes = Base64.decodeBase64(chalparam);
        int cplen = cpbytes.length;
        
        // Recover application parameter
        byte[] apbytes = Base64.decodeBase64(appparam);
        int aplen = apbytes.length;
        
        // Create a new byte-array to-be-signed.  The 1 is for user-presence-byte
        // while the 4 is for the byte-array of the (authenticator) counter value
        byte[] tbs = new byte[aplen + 1 + Constants.AUTHENTICATOR_COUNTER_LENGTH + cplen];

        // Initialize current position
        int currpos = 0;
        
        // Copy application parameter into TBS
        System.arraycopy(apbytes, 0, tbs, currpos, aplen);
        currpos += aplen;
        
        // Copy user-presence-byte into TBBS
        tbs[currpos] = Constants.AUTHENTICATOR_USERPRESENCE_BYTE;
        currpos += 1;
        
        // Copy counter value into TBS - verify if less than Integer.MAX_VALUE
        if (counter > 2147483647) {
            System.err.println("Authenticator Counter MAX value reached; wrapping around...");
            counter = 1;
        }
        byte[] counterbytes = ByteBuffer.allocate(Constants.AUTHENTICATOR_COUNTER_LENGTH).putInt(counter).array();
        System.arraycopy(counterbytes, 0, tbs, currpos, Constants.AUTHENTICATOR_COUNTER_LENGTH);
        currpos += Constants.AUTHENTICATOR_COUNTER_LENGTH;
        
        // Copy challenge parameter into TBS; done with curpos here 
        System.arraycopy(cpbytes, 0, tbs, currpos, cplen);
        
        // Decrypt KeyHandle
        @SuppressWarnings("UnusedAssignment")
        String khjson = null;
        byte[] signedbytes;
        try {
            khjson = decryptKeyHandle(keyhandle);
            System.out.println("PlaintextKeyHandle:   " + khjson);

            // Extract user's private-key
            PrivateKey pvk = getUserPrivateKey(decodeKeyHandle(khjson, 0));

            // Sign TBS with private-key
            Signature sig = Signature.getInstance("SHA256withECDSA", "BCFIPS");
            sig.initSign(pvk, new SecureRandom());
            sig.update(tbs);
            signedbytes = sig.sign();

        } catch (DecoderException | NoSuchAlgorithmException | 
                        NoSuchProviderException | NoSuchPaddingException | 
                        InvalidKeyException | InvalidAlgorithmParameterException | 
                        ShortBufferException | IllegalBlockSizeException | 
                        BadPaddingException | UnsupportedEncodingException | 
                        InvalidKeySpecException | SignatureException ex) 
        {
            System.err.println("Fatal Error: KeyHandle exception: " + ex.getLocalizedMessage());
            return null;
        }
        
        // Create Signature Data byte-array and reset current position
        // The 1 byte in signdata is for the user-presence-byte
        byte[] signdata = new byte[1 + Constants.AUTHENTICATOR_COUNTER_LENGTH + signedbytes.length];
        currpos = 0;
        
        // Copy user-presence byte into first position of signdata
        signdata[currpos] = Constants.AUTHENTICATOR_USERPRESENCE_BYTE;
        currpos += 1;
        
        // Copy counter bytes into signdata
        System.arraycopy(counterbytes, 0, signdata, currpos, Constants.AUTHENTICATOR_COUNTER_LENGTH);
        currpos += Constants.AUTHENTICATOR_COUNTER_LENGTH;
        
        // Copy signed-bytes into signdata
        System.arraycopy(signedbytes, 0, signdata, currpos, signedbytes.length);
        
        // Return Base64-encoded signature response
        return Base64.encodeBase64URLSafeString(signdata);
    }

    /**
     * Function to create a JSON object for the Authentication response to
     * the SKCE's FIDOEngine.  Need to re-encode clientdata into URL-safe
     * string just in case its not already so.
     * 
     * @param clientdata
     * @param keyhandle
     * @param sessionid
     * @param signresponse
     * @return 
     */
    public static JsonObject encodeAuthenticationSignatureResponse(
                                        String clientdata, 
                                        String keyhandle, 
//                                        String sessionid, 
                                        String signresponse) 
                                throws 
                                        UnsupportedEncodingException 
    {
        return Json.createObjectBuilder()
                .add(Constants.JSON_KEY_CLIENTDATA_LABEL, Base64.encodeBase64URLSafeString(clientdata.getBytes("UTF-8")))
//                .add(Constants.JSON_KEY_SESSIONID_LABEL, sessionid)
                .add(Constants.JSON_KEY_KEYHANDLE_LABEL, keyhandle)
                .add(Constants.JSON_KEY_SIGNATURE_LABEL, signresponse)
                .build();
    }
    
    /*******************************************
                  d8b                   
                  Y8P                   

    88888b.d88b.  888 .d8888b   .d8888b 
    888 "888 "88b 888 88K      d88P"    
    888  888  888 888 "Y8888b. 888      
    888  888  888 888      X88 Y88b.    
    888  888  888 888  88888P'  "Y8888P 
    ********************************************

    /**
     * Returns message digest of a byte-array of the specified algorithm
     *
     * @param input byte[] containing content that must be digested (hashed)
     * @param algorithm String indicating digest algorithm
     * @return String Base64-encoded digest of specified input
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws UnsupportedEncodingException 
     */
    public static String getDigest(byte[] input, String algorithm) 
                            throws 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                UnsupportedEncodingException 
    {
        MessageDigest digest = MessageDigest.getInstance(algorithm, "BCFIPS");
        byte[] digestbytes = digest.digest(input);
        return Base64.encodeBase64String(digestbytes);
    }
    
    /**
     * Returns the message digest of the specified input string - calls the
     * getDigest function
     * 
     * @param input String containing content that must be digested (hashed)
     * @param algorithm String indicating digest algorithm
     * @return String Base64-encoded message digest of specified input
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws UnsupportedEncodingException 
     */
    public static String getDigest(String input, String algorithm) 
                            throws 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                UnsupportedEncodingException 
    {
        return getDigest(input.getBytes("UTF-8"), algorithm);
    }

    /**
     * Function to return a JSON object from inputs
     *
     * @param optype String indicating the typ of operation 
     * @param challenge String containing the U2F server challenge
     * @param facetID String containing the facetid
     * @param cid String containing channelid information (not currently supported)
     * @return String with the JSON data-structure
     */
    public static String clientDataEncoder(String optype, String challenge, String facetID, String cid) 
    {
        JsonObjectBuilder job = Json.createObjectBuilder();
        if (cid == null) {
            job.add(Constants.JSON_KEY_REQUEST_TYPE_LABEL, optype)
               .add(Constants.JSON_KEY_SERVER_CHALLENGE_LABEL, challenge)
               .add(Constants.JSON_KEY_SERVER_ORIGIN_LABEL, facetID);
        } else {
            job.add(Constants.JSON_KEY_REQUEST_TYPE_LABEL, optype)
               .add(Constants.JSON_KEY_SERVER_CHALLENGE_LABEL, challenge)
               .add(Constants.JSON_KEY_SERVER_ORIGIN_LABEL, facetID)
               .add(Constants.JSON_KEY_CHANNELID_LABEL, cid);
        }
        return job.build().toString();
    }

    /**
     * Function to encode a keyHandle as a JSON object
     *
     * @param pvk String containing the Base64-encoded private-key
     * @param origin String containing the origin with which the key is associated
     * @param sha1hash String containing the SHA1 digest of the key
     * @return String containing the JSON of the keyHandle
     */
    public static String encodeKeyHandle(String pvk, String origin, String sha1hash) 
    {
        return Json.createObjectBuilder()
            .add("key", pvk)
            .add("sha1", sha1hash)
            .add("origin_hash", origin)
            .build().toString();
    }
    
    /**
     * Function to decode the return-values of a keyHandle
     * @param input
     * @param type
     * @return 
     */
    public static String decodeKeyHandle(String input, int type) 
    {    
        JsonObject jsonObject = Json.createReader(new StringReader(input)).readObject();
        switch (type) {
            case 0: return jsonObject.getString("key");
            case 1: return jsonObject.getString("sha1");
            default:return jsonObject.getString("origin_hash");
        }
    }

    /**
     * Function to make a key-handle for transporting to the FIDO U2F server
     * 
     * @param pvk PrivateKey of the ECDSA key-pair
     * @param originHash String Message digest of the origin for which this
     * private-key is valid
     * @return String Base64-encoded key-handle
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws FileNotFoundException
     * @throws DecoderException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws ShortBufferException
     * @throws InvalidKeySpecException
     * @throws SignatureException 
     */
    public static String makeKeyHandle(PrivateKey pvk, String originHash) 
                            throws 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                NoSuchPaddingException, 
                                FileNotFoundException, 
                                DecoderException, 
                                InvalidKeyException, 
                                IllegalBlockSizeException, 
                                BadPaddingException, 
                                UnsupportedEncodingException, 
                                InvalidAlgorithmParameterException, 
                                ShortBufferException, 
                                InvalidKeySpecException, 
                                SignatureException, 
                                InvalidParameterSpecException 
    {
        // Get wrapping key
        byte[] Seckeybytes = Hex.decodeHex(Constants.FIXED_AES256_WRAPPING_KEY.toCharArray());
        SecretKeySpec sks = new SecretKeySpec(Seckeybytes, "AES");
        ECPrivateKey ecpk = (ECPrivateKey) pvk;
        byte[] s = org.bouncycastle.util.encoders.Hex.decode(String.format("%064x", ecpk.getS()));

        // Encode plaintext key-handle into JSON structure
        String ptkh = encodeKeyHandle(Base64.encodeBase64String(s), originHash, getDigest(pvk.getEncoded(), "SHA1"));
        System.out.println("PlaintextKeyHandle:     " + ptkh);
                           
        // Encrypt key handle to create ciphertext
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BCFIPS");
        cipher.init(Cipher.ENCRYPT_MODE, sks, new SecureRandom());
        byte[] ctkh = cipher.doFinal(ptkh.getBytes("UTF-8"));

        // Recover IV from cipher and prepend to encrypted keyhandle in new array
        byte[] iv = cipher.getIV();
        byte[] ctkhiv = new byte[ctkh.length + Constants.ENCRYPTION_MODE_CBC_IV_LENGTH];
        System.arraycopy(iv, 0, ctkhiv, 0, Constants.ENCRYPTION_MODE_CBC_IV_LENGTH);              // Copy IV to new array
        System.arraycopy(ctkh, 0, ctkhiv, Constants.ENCRYPTION_MODE_CBC_IV_LENGTH, ctkh.length);  // Append ciphertext KH to IV

        // Base64-encode ciphertext keyhandle + IV
        String ctkhivb64 = Base64.encodeBase64String(ctkhiv);

        // Test recovery of plaintext key-handle before returning
        String ptkh2 = decryptKeyHandle(ctkhivb64);
        if (!ptkh2.trim().equalsIgnoreCase(ptkh.trim())) {
            System.err.println("Decryption of keyhandle failed during test");
            return null;
        }
        
        // Decryption succeeded - return Base64-encoded, encrypted keyhandle + IV
        return ctkhivb64;
    }

    /**
     * Function to decrypt a private-key and return it from a Base64-encoded
     * key-handle (which has a 16-byte IV prepended to it)
     * 
     * @param s String containing a 16-byte IV plus the encrypted keyhandle
     * @return String containing the Base64-encoded plaintext JSON structure
     * of the key-handle
     * @throws DecoderException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws ShortBufferException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeySpecException
     * @throws SignatureException 
     */
    public static String decryptKeyHandle(String s) 
                            throws 
                                DecoderException, 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                NoSuchPaddingException, 
                                InvalidKeyException, 
                                InvalidAlgorithmParameterException, 
                                ShortBufferException, 
                                IllegalBlockSizeException, 
                                BadPaddingException, 
                                UnsupportedEncodingException, 
                                InvalidKeySpecException, 
                                SignatureException, 
                                InvalidParameterSpecException 
    {
        // Get wrapping key
        byte[] Seckeybytes = Hex.decodeHex(Constants.FIXED_AES256_WRAPPING_KEY.toCharArray());
        SecretKeySpec sks = new SecretKeySpec(Seckeybytes, "AES");

        // Decode IV + ciphertext and extract components into new arrays
        byte[] ctkhiv = Base64.decodeBase64(s);
        byte[] iv = new byte[16];
        byte[] ctkh = new byte[ctkhiv.length - iv.length];
        System.arraycopy(ctkhiv, 0, iv, 0, Constants.ENCRYPTION_MODE_CBC_IV_LENGTH);
        System.arraycopy(ctkhiv, Constants.ENCRYPTION_MODE_CBC_IV_LENGTH, ctkh, 0, ctkh.length);

        // Decrypt keyhandle using IV in input string
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BCFIPS");
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, sks, ivspec);
        byte[] ptkh = new byte[cipher.getOutputSize(ctkh.length)];
        int p = cipher.update(ctkh, 0, ctkh.length, ptkh, 0);
        cipher.doFinal(ptkh, p);

        // Exctract ECDSA private-key from plaintext JSON keyhandle
        String pvks = decodeKeyHandle(new String(ptkh, "UTF-8"), 0); // 0 for key
        byte[] pvkb = Base64.decodeBase64(pvks);

        // Create private key for sanity-check
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
        parameters.init(new ECGenParameterSpec("secp256r1"));

        ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(1, pvkb), ecParameterSpec);

        ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(ecPrivateKeySpec);

        // If we don't thrown an exception at this point, we can return JSON
        return new String(ptkh, "UTF-8");
    }

    /**
     * Function to generate a new ECDSA key-pair for a specific origin.  The
     * challenge parameter and application parameter are passed in by the
     * FIDO client and apply to the specific origin or the RP application.
     * 
     * @param ChallengeParam String The challenge parameter is the SHA-256 hash 
     * of the Client Data, a string JSON data structure the FIDO Client prepares. 
     * Among other things, the Client Data contains the challenge from the 
     * relying party (hence the name of the parameter)
     * @param ApplicationParam String The application parameter is the SHA-256 
     * hash of the application identity of the application requesting the 
     * registration; it is 32-bytes in length
     * @param origin String The URI of the RP application for which this keypair
     * is being generated.  This URIL must be a specific facet-id of the appid
     * configured for the RP application's U2F server.
     * @return String containing the Base64-encoded byte array with the 
     * registration response for the U2F server
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws NoSuchPaddingException
     * @throws FileNotFoundException
     * @throws DecoderException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws ShortBufferException
     * @throws UnrecoverableKeyException
     * @throws InvalidKeySpecException 
     */
    public static String generateFIDOKeyPair(
                                String ChallengeParam, 
                                String ApplicationParam, 
                                String origin) 
                        throws 
                                KeyStoreException, 
                                NoSuchProviderException, 
                                IOException, 
                                NoSuchAlgorithmException, 
                                CertificateException, 
                                InvalidAlgorithmParameterException, 
                                InvalidKeyException, 
                                SignatureException, 
                                NoSuchPaddingException, 
                                FileNotFoundException, 
                                DecoderException, 
                                IllegalBlockSizeException, 
                                BadPaddingException, 
                                UnsupportedEncodingException, 
                                ShortBufferException, 
                                UnrecoverableKeyException, 
                                InvalidKeySpecException, 
                                InvalidParameterSpecException 
    {
        // Generate key-pair
        KeyPair keypair = generateKeypair();
        PublicKey pbk = keypair.getPublic();
        PrivateKey pvk = keypair.getPrivate();
        System.out.println(pbk);
        System.out.println(pvk);

        // Create encrypted, Base64-encoded key-handle
        String originHash = getDigest(origin, "SHA-256");
        String keyhandle = makeKeyHandle(pvk, originHash);
        if (keyhandle != null)
            System.out.println("WrappedKeyHandle:       " + keyhandle);
                               
        // Create to-be-signed (TBS) object (Registration Data)
        String pbkstr = Base64.encodeBase64String(pbk.getEncoded());
        String tbs = createRegistrationObjectToSign(ApplicationParam, ChallengeParam, keyhandle, pbkstr);
        System.out.println("To-Be-Signed (TBS):     " + tbs);
                           
        // Sign Registration Data object
        String sig = signWithAttestationKey(tbs);
        System.out.println("AttestationSignature:   " + sig);
                           
        // Get Attestation Key's digital certificate
        String certstr;
        try {
            certstr = Base64.encodeBase64String(getAttestationCert().getEncoded());
            System.out.println("AttestationCertificate: " + certstr);
        } catch (NullPointerException npe) {
            System.err.println("Fatal Error: Creating RegistrationResponse - Attestation Certificate not found");
            return null;
        }
        
        // Create Registration Response and return it
        return createRegistrationData(pbkstr, keyhandle, certstr, sig);
    }
    
    /**
     * Generates a new ECDSA key-pair using the fixed curve secp256r1
     *
     * @return KeyPair containing a new ECDSA 256-bit key-pair
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws SignatureException 
     */
    public static KeyPair generateKeypair() 
                            throws 
                                KeyStoreException, 
                                NoSuchProviderException, 
                                IOException, 
                                NoSuchAlgorithmException, 
                                CertificateException, 
                                InvalidAlgorithmParameterException, 
                                InvalidKeyException, 
                                SignatureException 
    {
        ECGenParameterSpec paramSpec = new ECGenParameterSpec((Constants.EC_P256_CURVE));
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BCFIPS");
        kpg.initialize(paramSpec, new SecureRandom());
        return kpg.generateKeyPair();
    }
    
    /**
     * Function to generate a PrivateKey object from a byte-array containing
     * the ECDSA private-key
     *
     * @param pvkbytes byte[] containing the user's private-key
     * @return PrivateKey
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException 
     */
    private static PrivateKey getUserPrivateKey(String pvk) 
                            throws 
                                NoSuchAlgorithmException, 
                                NoSuchProviderException, 
                                InvalidKeySpecException, 
                                InvalidParameterSpecException 
    {
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
        parameters.init(new ECGenParameterSpec("secp256r1"));

        ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(1, Base64.decodeBase64(pvk)), ecParameterSpec);

        return KeyFactory.getInstance("EC").generatePrivate(ecPrivateKeySpec);
    }

    /**
     * Function to retrieve the Attestation Key's digital certificate from
     * the software-based JCEKS keystore.  Don't need password for keystore
     * to read the certificate
     * 
     * @return Certificate
     * @throws FileNotFoundException
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException 
     */
    public static Certificate getAttestationCert() 
                            throws  
                                FileNotFoundException, 
                                KeyStoreException, 
                                IOException, 
                                NoSuchAlgorithmException, 
                                CertificateException 
    {
        KeyStore attks = KeyStore.getInstance("JCEKS");
        attks.load(Common.class.getResourceAsStream(Constants.ATTESTATION_KEYSTORE_FILE), null);
        return attks.getCertificate(Constants.ATTESTATION_KEYSTORE_PRIVATEKEY_ALIAS);
    }
    
    /**
     * Function to print the contents of a JSON object - doesn't seem to work
     * right now; will fix in future build.
     *
     * @param jsonstring String input containing the JSON object
     * @param label String containing the label for the JSON object
     */
    public static void printJson(String jsonstring, String label) 
    {        
        String key = null;
        System.out.print(label);
        
        // Setup generator for pretty-printing JSON
        try (JsonGenerator jg = jgf.createGenerator(System.out)) 
        {
            try (JsonParser parser = jpf.createParser(new StringReader(jsonstring))) {
                JsonParser.Event event;
                while (parser.hasNext())
                {
                    event = parser.next();
                    switch (event) {
                        case START_OBJECT: try {jg.writeStartObject();} catch (Exception e) {jg.writeStartObject(key);} break;
                        case END_OBJECT: jg.writeEnd(); break;
                        case KEY_NAME: key = parser.getString(); break;
                        case VALUE_STRING: try {jg.write(parser.getString());} catch (Exception e) {jg.write(key, parser.getString());} break;
                        default: System.err.println("Unexpected parser-event in JSON: " + event.name());
                    }
                }
            }
        }
    }
    
    /**
     * Function to copy a source array to a destination array.  It does
     * duplicate System.arraycopy but returns a moving-total useful for
     * copying the next array in this module.  (Update to ByteBuffer in
     * next release....)
     * 
     */
    private static int copyarray(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
        return destPos + length;
    }
}
