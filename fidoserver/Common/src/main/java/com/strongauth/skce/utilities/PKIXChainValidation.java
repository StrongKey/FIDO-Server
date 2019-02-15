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
 * $Author$
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
package com.strongauth.skce.utilities;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mishimoto
 */
public class PKIXChainValidation {
    
    final static String USE_OCSP = "false";
    private static final String classname = "PKIXChainValidation";

    // Enable choice of JCA/JCE Security Providers - Sun/SunJCE, BC or BCFIPS
    static {
        // Enable/Disable OCSP - OCSP/CRL revocation checking are on by default
        java.security.Security.setProperty("ocsp.enable", USE_OCSP);
        System.out.println("OCSP is enabled:" + USE_OCSP);
    }

    public static boolean pkixvalidate(CertPath cp, Set<TrustAnchor> trustAnchorSet, 
            boolean isRevocationChecked, boolean isPolicyQualifiersRejected) {
        try {
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");  //TODO use BCFIPS when "Support for PKIXRevocationChecker 
                                                                            //in the CertPath implementation" is added
                                                                            
            PKIXParameters pkix = new PKIXParameters(trustAnchorSet);
            
            if(isRevocationChecked){
                PKIXRevocationChecker prc = (PKIXRevocationChecker) cpv.getRevocationChecker();
                prc.setOptions(EnumSet.of(PKIXRevocationChecker.Option.PREFER_CRLS, PKIXRevocationChecker.Option.NO_FALLBACK));
                pkix.addCertPathChecker(prc);
            }
            else{
                pkix.setRevocationEnabled(false);
            }
            
            pkix.setPolicyQualifiersRejected(isPolicyQualifiersRejected);
            pkix.setDate(null);
            CertPathValidatorResult cpvr = cpv.validate(cp, pkix);
            if (cpvr != null) {
                System.out.println("Certificate validated");
                return true;
            } else {
                System.out.println("Certificate not valid");
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertPathValidatorException ex) {
            Logger.getLogger(PKIXChainValidation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
