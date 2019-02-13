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
 * Copyright (c) 2001-2016 StrongAuth, Inc.
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
 * A static class to validate X.509 digital certificatets using
 * the PKIX validation algorithm/standard
 */

package com.strongauth.pki2fido.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXRevocationChecker.Option;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CertificateValidation {
    
    // JCE related objects
    final static int BUFFER_SIZE = 8192;
    final static String SECURITY_PROVIDER = null;
    final static String AIA_EXTENSION = "1.3.6.1.5.5.7.1.1";
    final static String AIA_CA_ISSUER_OID = "1.3.6.1.5.5.7.48.2";
    final static String USE_OCSP = Common.getConfigurationProperty("pki2fido.cfg.property.pkix.useocsp");
    final static String TRUSTSTORE_LOCATION = Common.getConfigurationProperty("pki2fido.cfg.property.pkix.truststore.location");
    final static String TRUSTSTORE_PASSWORD = Common.getConfigurationProperty("pki2fido.cfg.property.pkix.truststore.password");
    
    // Enable choice of JCA/JCE Security Providers - Sun/SunJCE, BC or BCFIPS
    static {
        // Add JCA/JCE Provider - use Sun/SunJCE provider if unspecified
        if (SECURITY_PROVIDER == null)
            Common.log(Level.INFO, "PKI2FIDO-MSG-1001", "Using default Sun/SunJCE Security Provider");
        else {
            switch (SECURITY_PROVIDER) {
                case "BC":
                    Security.addProvider(new BouncyCastleProvider());
                    Common.log(Level.INFO, "PKI2FIDO-MSG-1001", "Added BC Security Provider");
                    break;
                case "BCFIPS":
                    Security.addProvider(new BouncyCastleFipsProvider());
                    Common.log(Level.INFO, "PKI2FIDO-MSG-1001", "Added BC-FIPS Security Provider");
                default:
                    Common.log(Level.SEVERE, "PKI2FIDO-ERR-1006", SECURITY_PROVIDER);
            }
        }
        // Enable/Disable OCSP - OCSP/CRL revocation checking are on by default
        java.security.Security.setProperty("ocsp.enable", USE_OCSP);
        Common.log(Level.INFO, "PKI2FIDO-MSG-1001", "OCSP is enabled:" + USE_OCSP);
    }
    
/********************************************************************************
8888888b.  888    d8P  8888888 Y88b   d88P 888     888          888 d8b      888
888   Y88b 888   d8P     888    Y88b d88P  888     888          888 Y8P      888
888    888 888  d8P      888     Y88o88P   888     888          888          888
888   d88P 888d88K       888      Y888P    Y88b   d88P  8888b.  888 888  .d88888
8888888P"  8888888b      888      d888b     Y88b d88P      "88b 888 888 d88" 888
888        888  Y88b     888     d88888b     Y88o88P   .d888888 888 888 888  888
888        888   Y88b    888    d88P Y88b     Y888P    888  888 888 888 Y88b 888
888        888    Y88b 8888888 d88P   Y88b     Y8P     "Y888888 888 888  "Y88888
********************************************************************************/
    
    @SuppressWarnings("UnusedAssignment")
    public static boolean pkixvalid(X509Certificate eecert) {
        try {
            // Load factory
            CertificateFactory certfact;
            if (SECURITY_PROVIDER == null)
                certfact = CertificateFactory.getInstance("X.509");
            else
                certfact = CertificateFactory.getInstance("X.509", SECURITY_PROVIDER);
            
            // Load cert
            List<Certificate> certs = new ArrayList<>();
            certs.add(eecert);
            CertPath cp = certfact.generateCertPath(certs);

            // Validate the end-entity certificate
            TrustAnchor anchor = new TrustAnchor(eecert, null);
            Set<TrustAnchor> anchors = new HashSet();
            anchors.add(anchor);
            PKIXParameters pkix = new PKIXParameters(anchors);

            // Set policy parameters for PKIX validation 
            pkix.setDate(null);  // Uses current time
            pkix.setRevocationEnabled(true);

            // Validate the single certificate 
            Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "Validating the specified certificate...");
            if (cpvalidate(cp, pkix)) {
                Common.log(Level.INFO, "PKI2FIDO-MSG-2005", eecert.getSubjectDN().getName());
                return true;
            }

            // Did not validate - load the truststore
            try {
                Common.log(Level.INFO, "PKI2FIDO-MSG-1001", "Using Truststore: " + TRUSTSTORE_LOCATION);
                KeyStore ks;
                if (SECURITY_PROVIDER == null || SECURITY_PROVIDER.equalsIgnoreCase("BC"))
                    ks = KeyStore.getInstance("JKS"); 
                else 
                    ks = KeyStore.getInstance("JKS", SECURITY_PROVIDER);
                ks.load(new FileInputStream(TRUSTSTORE_LOCATION), TRUSTSTORE_PASSWORD.toCharArray());
                pkix = new PKIXParameters(ks);

                // Set policy parameters for PKIX validation
                pkix.setDate(null);
                pkix.setRevocationEnabled(true);

                // Validate same certificate with truststore
                Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "Validating the EE certificate with truststore...");
                if (cpvalidate(cp, pkix)) {
                    Common.log(Level.INFO, "PKI2FIDO-MSG-2005", eecert.getSubjectDN().getName());
                    return true;
                }
            } catch (FileNotFoundException ex) {
                Common.log(Level.WARNING, "PKI2FIDO-ERR-4001", TRUSTSTORE_LOCATION);
            } catch (ArrayIndexOutOfBoundsException ex) {
                Common.log(Level.WARNING, "PKI2FIDO-ERR-4002", "Truststore is possibly empty: " + TRUSTSTORE_LOCATION);
            } catch (IOException ex) {
                Common.log(Level.WARNING, "PKI2FIDO-ERR-4003", "Not a JKS file or invalid password: " + TRUSTSTORE_LOCATION);
            }

            /**
             * Did not validate - start using AIA for validation; must start
             * with EndEntity certificate to build new CertPath.
             *
             * Note that the Federal Bridge CA (FBCA) PKI produces a PKCS7 (p7c)
             * file with many third-party CAs who have cross-certified with the
             * FBCA (C=US, O=U.S. Government, OU=FPKI, CN=Federal Bridge CA
             * 2013) with a SubjectKeyIdentifier of:
             * BB:CE:74:71:83:34:4E:59:32:45:15:5F:40:60:60:DC:2B:B0:B4:E4
             *
             */
            X509Certificate aiaanchor = null;
            URL caurl = getcacerturl(eecert);
            while (caurl != null) {
                byte[] cacertbytes = getcacert(caurl);
                if (cacertbytes == null) {
                    Common.log(Level.SEVERE, "PKI2FIDO-ERR-4005", caurl.toString());
                    return false;
                }
                
                // Parse certificate
                X509Certificate cacert = (X509Certificate) certfact.generateCertificate(new ByteArrayInputStream(cacertbytes));
                String caissuerdn = cacert.getIssuerX500Principal().getName();
                String casubjectdn = cacert.getSubjectX500Principal().getName();
                Common.log(Level.INFO, "PKI2FIDO-MSG-1000", "Downloaded P7C certificate(s) from AIA...\n"
                            + "Issuer:  " + caissuerdn + "\nSubject: " + casubjectdn);
                
                // Should not add TrustAnchor to list of certificates in cert-path
                if (caissuerdn.equalsIgnoreCase(casubjectdn)) {
                    aiaanchor = cacert;
                    Common.log(Level.INFO, "PKI2FIDO-MSG-1000", "Self-signed Root; will be added to TrustAnchor: " + casubjectdn);
                    break;
                } else {
                    // Add cert to collection
                    certs.add(cacert);
                    Common.log(Level.INFO, "PKI2FIDO-MSG-1000", "Added CA cert to cert-path: " + casubjectdn);
                }
                
                // Is there another certificate in the chain? If there is
                // it will execute this loop again
                caurl = getcacerturl(cacert);
            }
            // Get cert from AIA extension - need the searchfinished logic to
            // prevent circular searches for ORC ACES certificate            
//            boolean searchfinished = false;            
//            while (caurl != null) {
//                byte[] cacertbytes = getcacert(caurl);
//                if (cacertbytes == null) {
//                    Common.log(Level.WARNING, "PKI2FIDO-ERR-1000", "CA cert retreival from AIA failed");
//                    System.exit(-4);
//                }
//                
//                while (!searchfinished) {
//                    // Parse certificate
//                    cacert = (X509Certificate) certfact.generateCertificate(new ByteArrayInputStream(cacertbytes));
//                    String issuerdn = cacert.getIssuerX500Principal().getName();
//                    String subjectdn = cacert.getSubjectX500Principal().getName();
//                    Common.log(Level.INFO, "PKI2FIDO-MSG-1000", "Downloaded P7C certificate(s) from AIA...\n"
//                            + "Issuer:  " + issuerdn + "\nSubject: " + subjectdn);
//
//                    // If we get a self-signed cert, set anchor and add it to TrustAnchor 
//                    // but NOT to the list of certs in CertPath; break out of the loop
//                    // - we've reached the root
//                    if (issuerdn.equalsIgnoreCase(subjectdn)) {
//                        aiaanchor = cacert;
//                        caurl = null;
//                        searchfinished = true;
//                        // We got the FBCA 2013 p7c instead - what if we get other bridges?
//                    } else if (issuerdn.equalsIgnoreCase("C=US, O=U.S. Government, OU=FPKI, CN=Federal Bridge CA 2013")
//                            || issuerdn.equalsIgnoreCase("CN=Federal Bridge CA 2013,OU=FPKI,O=U.S. Government,C=US")) {
//                        aiaanchor = cacert;
//                        caurl = null;
//                        searchfinished = true;
//                    } else {
//                        // Add cert to collection
//                        certs.add(cacert);
//                        caurl = getcacerturl(cacert);
//                    }
//                }
//                // Is there another certificate in chain? If yes, execute loop again
//                caurl = getcacerturl(cacert);
//            }

            // Out of the loop with cert-chain - add it to CertPath
            cp = certfact.generateCertPath(certs);

            // Setup new Trust Anchor since we have a new chain
            anchor = new TrustAnchor(aiaanchor, null);
            anchors = new HashSet();
            anchors.add(anchor);
            pkix = new PKIXParameters(anchors);

            // Set policy parameters for PKIX validation
            pkix.setDate(null);
            pkix.setRevocationEnabled(true);

            // Validate the certificate chain
            Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "Validating certificate chain using AIA...");
            if (cpvalidate(cp, pkix)) {
                Common.log(Level.INFO, "PKI2FIDO-MSG-2005", eecert.getSubjectDN().getName());
                return true;
            }
            
        } catch (CertificateException | NoSuchProviderException | InvalidAlgorithmParameterException | 
                KeyStoreException | NoSuchAlgorithmException ex) {
            Common.log(Level.SEVERE, null, ex);
        }
        return false;
    }
        
/**************************************************************************
8888888b.  8888888b.  8888888 888     888      d8888 88888888888 8888888888 
888   Y88b 888   Y88b   888   888     888     d88888     888     888        
888    888 888    888   888   888     888    d88P888     888     888        
888   d88P 888   d88P   888   Y88b   d88P   d88P 888     888     8888888    
8888888P"  8888888P"    888    Y88b d88P   d88P  888     888     888        
888        888 T88b     888     Y88o88P   d88P   888     888     888        
888        888  T88b    888      Y888P   d8888888888     888     888        
888        888   T88b 8888888     Y8P   d88P     888     888     8888888888
***************************************************************************/
    
    /**
     * Private method to validate a chain of certificates
     * @param cp CertPath object containing the chain of certificates
     * @param pkix PKIXParameters object containing validation parameters
     * @return boolean
     */
    private static boolean cpvalidate(CertPath cp, PKIXParameters pkix) throws NoSuchProviderException {
        try {
            List<? extends Certificate> certs = cp.getCertificates();
            Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "List of certificates to validate:");
            int n = 0;
            for (Certificate c : certs) {
                Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "  Certificate #" + n++ + ": " + ((X509Certificate) c).getSubjectX500Principal().getName());
            }
            
            Set<TrustAnchor> anchors = pkix.getTrustAnchors();
            Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "Anchor set has " + anchors.size() + " anchor(s)");
            for (TrustAnchor ta : anchors) {
                try {
                    Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "  Certificate #" + n++ + ": " + ta.getTrustedCert().getSubjectX500Principal().getName());
                } catch (NullPointerException npe) {}
            }
            
            /**
             * Must use Sun/SunJCE Provider to use PKIXRevocationChecker for now
             * until code for BC/BCFIPS is added to this class
             * 
            CertPathValidator cpv;
            if (SECURITY_PROVIDER == null)
                cpv = CertPathValidator.getInstance("PKIX");
            else
                cpv = CertPathValidator.getInstance("PKIX", SECURITY_PROVIDER);
             */
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            
            // Add a PKIXRevocationChecker to PKIXParameters
            PKIXRevocationChecker prc = (PKIXRevocationChecker) cpv.getRevocationChecker();
            prc.setOptions(EnumSet.of(Option.ONLY_END_ENTITY, Option.PREFER_CRLS, Option.NO_FALLBACK));
            pkix.addCertPathChecker(prc);
            
            // Get and return validation result
            CertPathValidatorResult cpvr = cpv.validate(cp, pkix);
            return cpvr != null;
       
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
            Common.log(Level.SEVERE, null, ex);
         } catch (CertPathValidatorException ex) {
            Common.log(Level.WARNING, "PKI2FIDO-ERR-4000", "  Certificate #" + (ex.getIndex()+1) + " could not be validated: " + ex.getLocalizedMessage() + '\n' + ex.getReason() + '\n');
        }
        return false;
    }
    
    /**
     * Private method to return the URL of the CA Issuer certificate
     * from the authorityInfoAccess extension in the certificate
     * @param cert X509Certificate object containing the AIA extension
     * @return java.net.URL
     */
    private static URL getcacerturl(X509Certificate cert) {
        try {
            byte[] aiabytes = cert.getExtensionValue(AIA_EXTENSION);
            if (aiabytes == null)
                return null;
            ASN1InputStream ain = new ASN1InputStream(aiabytes);
            ASN1OctetString extnvalue = (ASN1OctetString) ain.readObject();
            ain = new ASN1InputStream(extnvalue.getOctets());
            ASN1Primitive extn = ain.readObject();
            AuthorityInformationAccess aia = AuthorityInformationAccess.getInstance(extn);
            AccessDescription[] ads = aia.getAccessDescriptions();
            for (AccessDescription ad : ads) {
                if (ad.getAccessMethod().getId().equals(AIA_CA_ISSUER_OID)) {
                    return new URL(ad.getAccessLocation().getName().toString());
                }
            }
        } catch (IOException ex) {
            Common.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Private method to get a CA certificate from a URL (which was specified
     * in the AIA extension of the X509 digital certificate
     * @param caurl URL from the CA Issuer attribute of the AIA extension
     * @return byte[] containing the CA certificate, or null if there are errors
     */
    private static byte[] getcacert(URL caurl) 
    {    
        try {
            HttpURLConnection conn = (HttpURLConnection) caurl.openConnection();
            int resp = conn.getResponseCode();
            
            // Check HTTP response code 
            if (resp == HttpURLConnection.HTTP_OK) {
                String condisp = conn.getHeaderField("Content-Disposition");
                String contype = conn.getContentType();
                int conlen = conn.getContentLength();
                
                // Some useful output
                Common.log(Level.INFO, "PKI2FIDO-MSG-4000", "Loading CA certificate from AIA extension..." + '\n' +
                    "Content-URL = " + caurl  + '\n' +
                    "Content-Type = " + contype  + '\n' +
                    "Content-Disposition = " + condisp + '\n' +
                    "Content-Length = " + conlen + '\n');
                
                // Stream for HTTP connection and certificate
                byte[] certbytes;
                try (InputStream is = conn.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    int n;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((n = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, n);
                    }   certbytes = baos.toByteArray();
                }
                return certbytes;
            } else {
                Common.log(Level.WARNING, "PKI2FIDO-ERR-4004", resp);
            }
            conn.disconnect();
        } catch (IOException ex) {
            Common.log(Level.SEVERE, null, ex);
        }
        return null;
    }    
}
