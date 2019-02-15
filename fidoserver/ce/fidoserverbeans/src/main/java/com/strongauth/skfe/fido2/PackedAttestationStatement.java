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
 * Copyright (c) 2001-2015 StrongAuth, Inc.
 *
 * $Date: 2018-06-18 14:47:15 -0400 (Mon, 18 Jun 2018) $
 * $Revision: 50 $
 * $Author: pmarathe $
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/fido2/PackedAttestationStatement.java $
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
package com.strongauth.skfe.fido2;

import com.google.common.primitives.Bytes;
import com.strongauth.crypto.utility.cryptoCommon;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.utilities.skfeLogger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

public class PackedAttestationStatement implements FIDO2AttestationStatement {
    private int alg;
    private byte[] signature;
    private byte[] ecdaaKeyId;
    private ArrayList x5c = null;
    private String attestationType = "self";
    String validataPkix = skfeCommon.getConfigurationProperty("skfe.cfg.property.pkix.validate");
    String validataPkixMethod = skfeCommon.getConfigurationProperty("skfe.cfg.property.pkix.validate.method");
    
    static {
        Security.addProvider(new BouncyCastleFipsProvider());
    }
    
    @Override
    public void decodeAttestationStatement(Object attestationStmt) {
        Map<String, Object> attStmtObjectMap = (Map<String, Object>) attestationStmt;
        for (String key : attStmtObjectMap.keySet()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "Key attstmt Packed: " + key);
            switch (key) {
                case "sig":
                    signature = (byte[]) attStmtObjectMap.get(key);
                    break;
                case "x5c":
                    x5c = (ArrayList) attStmtObjectMap.get(key);
                    attestationType = "basic";
                    break;
                case "ecdaaKeyId":
                    ecdaaKeyId = (byte[]) attStmtObjectMap.get(key);
                    attestationType = "ecdaa";
                    break;
                case "alg":
                    alg = (int) attStmtObjectMap.get(key);
                    break;
            }
        }
    }

    @Override
    public Boolean verifySignature(String browserDataBase64, FIDO2AuthenticatorData authData) {
        skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "ALG = " + alg);
        if (x5c != null) {
            try {
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    x5c.size());
                Iterator x5cItr = x5c.iterator();
                CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BCFIPS");
                byte[] certByte = (byte[]) x5cItr.next();
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "x5c base64 java: " + java.util.Base64.getEncoder().encodeToString(certByte));
                InputStream instr = new ByteArrayInputStream(certByte);
                X509Certificate attCert = (X509Certificate) certFactory.generateCertificate(instr);
                
                PublicKey certPublicKey = attCert.getPublicKey();
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "CERT ALGO = " + certPublicKey.getAlgorithm());

                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "Signed Bytes Input: " + browserDataBase64);
                
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "authData.getAuthDataDecoded(): " + java.util.Base64.getEncoder().encodeToString(authData.getAuthDataDecoded()));
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "signature: " + java.util.Base64.getEncoder().encodeToString(signature));
                
                //Verify that sig is a valid signature over the concatenation of authenticatorData and clientDataHash using the attestation public key in attestnCert with the algorithm specified in alg.
                byte[] signedBytes = Bytes.concat(authData.getAuthDataDecoded(), skfeCommon.getDigestBytes(java.util.Base64.getDecoder().decode(browserDataBase64), "SHA256"));
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                    "signedBytes: " + java.util.Base64.getEncoder().encodeToString(signedBytes));
                boolean isValidSignature = cryptoCommon.verifySignature(signature, certPublicKey, signedBytes, skfeCommon.getAlgFromIANACOSEAlg(alg));
                if(!isValidSignature){
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                        "browserDataBase64 = " + browserDataBase64);
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                        "authData = " + bytesToHexString(authData.getAuthDataDecoded(), authData.getAuthDataDecoded().length));
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                        "public key = " + certPublicKey);
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                        "Signature = " + bytesToHexString(signature, signature.length));
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                        "Failed to verify Packed signature");
                    return false;
                }
                
                //Verify that attestnCert meets the requirements in §8.2.1 Packed Attestation Statement Certificate Requirements.
                //  Version MUST be set to 3 (which is indicated by an ASN.1 INTEGER with value 2).
                if(attCert.getVersion() != 3){
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                        "Attestation Certificate (Packed) Failure: Version");
                    return false;
                }
                
                //  Subject field MUST be set to:
                String subjectDN = attCert.getSubjectDN().getName();
                String[] subjectFields = subjectDN.split(",");
                Map<String, String> subjectFieldMap = new HashMap<>();
                for(String field: subjectFields){
                    String[] entry = field.split("=");
                    if(entry.length != 2){
                        skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Unable to parse subjectDN");
                        return false;
                    }
                    subjectFieldMap.put(entry[0], entry[1]);
                }
                
                //      Subject-C ISO 3166 code specifying the country where the Authenticator vendor is incorporated (PrintableString)
                //TODO ensure string is an ISO 3166 country code
                if (!subjectFieldMap.containsKey("C")) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Attestation Certificate (Packed) Failure: Invalid Country " + subjectFields[3]);
                    return false;
                }
                
                //      Subject-O Legal name of the Authenticator vendor (UTF8String)
                if (!subjectFieldMap.containsKey("O")) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Attestation Certificate (Packed) Failure: Invalid Organization " + subjectFields[2]);
                    return false;
                }
                
                //      Subject-OU Literal string “Authenticator Attestation” (UTF8String)
                if (!subjectFieldMap.containsKey("OU") || !subjectFieldMap.get("OU").equals("Authenticator Attestation")) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Attestation Certificate (Packed) Failure: Invalid OU " + subjectFields[1]);
                    return false;
                }
                
                //      Subject-CN A UTF8String of the vendor’s choosing
                if (!subjectFieldMap.containsKey("CN")) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Attestation Certificate (Packed) Failure: Invalid CN " + subjectFields[0]);
                    return false;
                }
                
                //The Basic Constraints extension MUST have the CA component set to false.
                if (attCert.getBasicConstraints() != -1) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Packed attestation statement cetificate: Invalid Basic Constraints");
                    return false;
                }
                
                //If attestnCert contains an extension with OID 1.3.6.1.4.1.45724.1.1.4 (id-fido-gen-ce-aaguid) verify that the value of this extension matches the aaguid in authenticatorData.
                byte[] certAaguidExtension = attCert.getExtensionValue("1.3.6.1.4.1.45724.1.1.4");
                if (certAaguidExtension != null) {
                    //Note that an X.509 Extension encodes the DER-encoding of the value in an OCTET STRING. Thus, the AAGUID MUST be wrapped in two OCTET STRINGS to be valid.
                    //Remove 2 OCTET String wrappers
                    byte[] certAaguid = Arrays.copyOfRange(certAaguidExtension, 4, certAaguidExtension.length);
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                        "Certificate contains aaguid = " + bytesToHexString(certAaguid, certAaguid.length));
                    if (!Arrays.equals(certAaguid, authData.getAttCredData().getAaguid())) {
                        skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Packed x5c's aaguid does not match");
                        return false;
                    }
                }
                
                return true;
            } catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | UnsupportedEncodingException ex) {
                Logger.getLogger(U2FAttestationStatment.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Boolean.FALSE;
        } else if (ecdaaKeyId != null) {
            //not supported yet
            return false;
        } else {
            try {
                //Self attestation

                //Validate that alg matches the algorithm of the credentialPublicKey in authenticatorData.
                if (alg != authData.getAttCredData().getFko().getAlg()) {
                    skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015", 
                            "Attestation Statement algorithm does not match Authenticator Data algorithm");
                    return false;
                }
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, skfeCommon.getMessageProperty("FIDO-MSG-2001"), 
                        "CERT ALGO = " + authData.getAttCredData().getPublicKey().getAlgorithm());
                //Verify that sig is a valid signature over the concatenation of authenticatorData and clientDataHash using the credential public key with alg.
                byte[] signedBytes = Bytes.concat(authData.getAuthDataDecoded(), skfeCommon.getDigestBytes(java.util.Base64.getDecoder().decode(browserDataBase64), "SHA256"));
                Signature verifySignature = Signature.getInstance(skfeCommon.getAlgFromIANACOSEAlg(alg), "BCFIPS");
                verifySignature.initVerify(authData.getAttCredData().getPublicKey());
                verifySignature.update(signedBytes);

                //If successful, return attestation type Self and empty attestation trust path.
                return verifySignature.verify(signature);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | UnsupportedEncodingException | InvalidKeyException | SignatureException ex) {
                Logger.getLogger(PackedAttestationStatement.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    @Override
    public ArrayList getX5c() {
        return x5c;
    }

    @Override
    public String getAttestationType() {
        return attestationType;
    }
    
    private static String bytesToHexString(byte[] rawBytes, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            if (i % 16 == 0) {
                sb.append('\n');
            }
            sb.append(String.format("%02x ", rawBytes[i]));
        }
        return sb.toString();
    }
}
