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
 * $URL: https://svn.strongkey.com/repos/topaz4/branches/preFIDO2/strongauth/ce/skcebeans/src/main/java/com/strongauth/skce/fido2/U2FAttestationStatment.java $
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
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class U2FAttestationStatment implements FIDO2AttestationStatement {
    byte[] signature;
    ArrayList x5c = null;
    private final String attestationType = "basic";     //TODO support attca
    String validataPkix = skfeCommon.getConfigurationProperty("skfe.cfg.property.pkix.validate");
    String validataPkixMethod = skfeCommon.getConfigurationProperty("skfe.cfg.property.pkix.validate.method");

    @Override
    public void decodeAttestationStatement(Object attestationStmt) {
        Map<String, Object> attStmtObjectMap = (Map<String, Object>) attestationStmt;
        for (String key : attStmtObjectMap.keySet()) {
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    "Key attstmt U2f: " + key);
            switch (key) {
                case "sig":
                    signature = (byte[]) attStmtObjectMap.get(key);
                    break;
                case "x5c":
                    x5c = (ArrayList) attStmtObjectMap.get(key);
                    break;
            }
        }
    }

    @Override
    public Boolean verifySignature(String browserDataBase64, FIDO2AuthenticatorData authData) {
        ECKeyObject ecKeyObj = null;

        List<X509Certificate> certchain = new ArrayList<>();

        try {
            if(!Arrays.equals(authData.getAttCredData().getAaguid(), new byte[16])){
                skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.SEVERE, "FIDO-ERR-0015",
                        "u2f AAGUID is not zero");
                return false;
            }
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    x5c.size());
            Iterator x5cItr = x5c.iterator();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            byte[] certByte = (byte[]) x5cItr.next();
            InputStream instr = new ByteArrayInputStream(certByte);
            X509Certificate attCert = (X509Certificate) certFactory.generateCertificate(instr);
            
            PublicKey certPublicKey = attCert.getPublicKey();
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    certPublicKey.getAlgorithm());
            skfeLogger.log(skfeConstants.SKFE_LOGGER, Level.FINE, "FIDO-MSG-2001",
                    "Signed Bytes Input: " + browserDataBase64);
            if (authData.getAttCredData().getFko() instanceof ECKeyObject) {
                ecKeyObj = (ECKeyObject) authData.getAttCredData().getFko();
            }
            byte[] signedBytes = Bytes.concat(new byte[]{0}, authData.getRpIdHash(), skfeCommon.getDigestBytes(Base64.getDecoder().decode(browserDataBase64), "SHA256"), authData.getAttCredData().getCredentialId(),
                    new byte[]{0x04}, ecKeyObj.getX(), ecKeyObj.getY());

            Signature ecdsaSignature = Signature.getInstance("SHA256withECDSA", "BCFIPS");
            ecdsaSignature.initVerify(certPublicKey);
            ecdsaSignature.update(signedBytes);
            return ecdsaSignature.verify(signature);
//        return Boolean.FALSE;
        } catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | UnsupportedEncodingException | InvalidKeyException | SignatureException ex) {
            Logger.getLogger(U2FAttestationStatment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Boolean.FALSE;
    }
    
    @Override
    public ArrayList getX5c() {
        return x5c;
    }

    @Override
    public String getAttestationType() {
        return attestationType;
    }
}
