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
 * $Date: 
 * $Revision:
 * $Author: mishimoto $
 * $URL: 
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
 *
 *
 */

package com.strongkey.fido2mds.jws;

import com.strongauth.appliance.objects.JWT;
import com.strongauth.crypto.utility.cryptoCommon;
import com.strongauth.skce.utilities.PKIXChainValidation;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchProviderException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.json.JsonArray;

public class MDSJwtVerifier {
    private final X509Certificate rootCert;
    
    public MDSJwtVerifier(X509Certificate rootCert){
        this.rootCert = rootCert;
    }
    
    public void verify(JWT jwt) throws CertificateException, NoSuchProviderException, UnsupportedEncodingException {
        Set<TrustAnchor> trustAnchor = new HashSet<>();
        trustAnchor.add(new TrustAnchor(rootCert, null));

        List<Certificate> certchain = getCertificatesFromJsonArray(jwt.getHeader().getJsonArray("x5c"));
        if(certchain == null){
            throw new IllegalArgumentException("MDS JWT returned null certificate chain");
        }

        CertPath certPath = CertificateFactory.getInstance("X.509", "BCFIPS").generateCertPath(certchain);
        
        if (certchain.isEmpty()) {
            throw new IllegalArgumentException("MDS JWT certificate chain missing");
        }

        if (!PKIXChainValidation.pkixvalidate(certPath, trustAnchor, true, true)) {
            throw new IllegalArgumentException("MDS JWT certificate could not be validated");
        }

        System.out.println("Certificate checked:" + certchain.get(0).toString());
        if (!jwt.verifySignature(certchain.get(0).getPublicKey())) {
            throw new IllegalArgumentException("MDS JWT signature cannot be verified");
        }
    }
    
    private List<Certificate> getCertificatesFromJsonArray(JsonArray x5c) throws CertificateException, NoSuchProviderException{
        List<Certificate> result = new ArrayList<>();
        if(x5c == null){
            return result;
        }
        Decoder decoder = Base64.getDecoder();
        for(int i = 0; i < x5c.size(); i++){
            byte[] certBytes = decoder.decode(x5c.getString(i));
            result.add(cryptoCommon.generateX509FromBytes(certBytes));
        }
        return result;
    }
}
