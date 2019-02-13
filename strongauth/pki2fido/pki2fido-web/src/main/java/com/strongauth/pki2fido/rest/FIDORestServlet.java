/**
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, as published by the Free
 * Software Foundation and available at
 * http://www.fsf.org/licensing/licenses/lgpl.html, version 2.1 or above.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Copyright (c) 2001-2016 StrongAuth, Inc.
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
 * This servlet receives REST webservices from the PKI2FIDO web-application
 * and relays them to the appropriate EJB for servicing.
 */
package com.strongauth.pki2fido.rest;

import com.strongauth.pki2fido.ejb.authenticateLocal;
import com.strongauth.pki2fido.ejb.deregisterLocal;
import com.strongauth.pki2fido.ejb.getKeyInfoLocal;
import com.strongauth.pki2fido.ejb.preauthenticateLocal;
import com.strongauth.pki2fido.ejb.preregisterLocal;
import com.strongauth.pki2fido.ejb.registerLocal;
import com.strongauth.pki2fido.utilities.CertificateValidation;
import com.strongauth.pki2fido.utilities.Common;
import com.strongauth.pki2fido.utilities.Constants;
import com.strongauth.skce.ldap.LDAPEServlet;
import com.strongauth.skce.ldap.LDAPEServlet_Service;
import com.strongauth.skce.ldap.SKCEException_Exception;
import com.strongauth.skce.ldap.SKCEServiceInfoType;
import com.strongauth.skce.ldap.SkceReturnObject;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("")
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class FIDORestServlet {
    
    // Necessary to get digital certificates from request
    @Context private HttpServletRequest request;
    
    // LDAP Constants
    private static final String HOSTPORT = Common.getSkfeHostPort();
    private static final String LDAP_SERVICE_WSDL_SUFFIX = "/ldape/LDAPEServlet?wsdl";
    private static final String LDAP_SEARCH_BASEDN = Common.getConfigurationProperty("pki2fido.cfg.property.search.basedn");
    private static final String SEARCHKEY = "cn";
    private static final String SEARCHVALUE = "*";
    
    // SKFE Constants
    private static final Integer SKFE_DID = Integer.valueOf(Common.getConfigurationProperty("pki2fido.cfg.property.skfe.did"));
    private static final String SKFE_SERVICE_USERNAME = Common.getConfigurationProperty("pki2fido.cfg.property.svcusername");
    private static final String SKFE_SERVICE_PASSWORD = Common.getConfigurationProperty("pki2fido.cfg.property.svcpassword");
    
    // EJBs
    @EJB preregisterLocal preregister;
    @EJB preauthenticateLocal preauthenticate;
    @EJB registerLocal register;
    @EJB authenticateLocal authenticate;
    @EJB deregisterLocal deregister;
    @EJB getKeyInfoLocal getKeyInfo;
            
    // SKCE webservice object
    SKCEServiceInfoType svcinfo;
    
    // Session Map
    Map<String, Object> p2fmap = null;
    
    /**
     * Creates a new instance of FIDORestServlet
     */
    public FIDORestServlet() 
    {
        // Setup service credential to call SKCE LDAP service
        svcinfo = new SKCEServiceInfoType();
        svcinfo.setDid(SKFE_DID);
        svcinfo.setSvcusername(SKFE_SERVICE_USERNAME);
        svcinfo.setSvcpassword(SKFE_SERVICE_PASSWORD);
        
        // Setup map
        p2fmap = new ConcurrentHashMap<>();
        Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "Created FIDORestServlet");
    }
    
/**********************************************************************************************************
 .d8888b.  888      8888888 8888888888 888b    888 88888888888     d8888 888     888 88888888888 888    888 
d88P  Y88b 888        888   888        8888b   888     888        d88888 888     888     888     888    888 
888    888 888        888   888        88888b  888     888       d88P888 888     888     888     888    888 
888        888        888   8888888    888Y88b 888     888      d88P 888 888     888     888     8888888888 
888        888        888   888        888 Y88b888     888     d88P  888 888     888     888     888    888 
888    888 888        888   888        888  Y88888     888    d88P   888 888     888     888     888    888 
Y88b  d88P 888        888   888        888   Y8888     888   d8888888888 Y88b. .d88P     888     888    888 
 "Y8888P"  88888888 8888888 8888888888 888    Y888     888  d88P     888  "Y88888P"      888     888    888 
***********************************************************************************************************/
    
    /**
     * Retrieves X.509 digital certificate from a ClientAuth session,
     * looks up the user in LDAP and returns the UID as the username
     * needed for the next step - the FIDO Registration
     * 
     * @return String with the username (uid) from LDAP directory
     * @throws IOException
     * @throws URISyntaxException 
     */
    @GET
    @Path("/" + Constants.CLIENTAUTH)
    @Produces({"application/json"})
    public String clientauth() throws IOException, URISyntaxException 
    {
        String response = "";
        boolean error = false;
        String message = "ok";
        JsonObject returnObject;
        
        X509Certificate[] certs = (java.security.cert.X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        X509Certificate clientCert = certs[0];
        String userdn = clientCert.getSubjectDN().getName();
        Date startdate = clientCert.getNotBefore();
        Date enddate = clientCert.getNotAfter();
        BigInteger serialno = clientCert.getSerialNumber();
        Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "User Certificate:\n" + 
                "   SubjectDN: " + userdn + '\n' +
                "   Serial#: " + serialno + '\n' +
                "   StartDate: " + startdate + '\n' +
                "   EndDate: " + enddate);
        
        if (!CertificateValidation.pkixvalid(clientCert)) {
            return Json.createObjectBuilder()
                        .add("Response", response)
                        .add("Error", true)
                        .add("Message", "PKIX Validation failed for: " + userdn)
                        .build().
                    toString();
        }
        
        String username = null;
        if (userdn != null) 
        {
            String[] dnParts1 = userdn.split(",");
            String[] dnParts2 = dnParts1[0].split("=");
            
            String ldapUN = dnParts2[1];
            userdn = "cn=" + ldapUN + LDAP_SEARCH_BASEDN;
            Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "userdn: " + userdn);
            
            // Call webservice 
            String skceroReturnVal = null;
            try {
                SkceReturnObject skcero = getuserinfo(svcinfo, userdn, SEARCHKEY, SEARCHVALUE);
                if (skcero.getErrorkey() == null) {
                    username = Common.decodeJson(skcero.getResponse(), "uid");
                    p2fmap.put("username", username);
                    skceroReturnVal = skcero.getReturnval().toString();
                    Common.log(Level.INFO, "PKI2FIDO-MSG-3001", skcero.getReturnval());
                    Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "UID: " + username + '\n' +
                        "FIDO Enabled? " + Common.decodeJson(skcero.getResponse(), "FIDOKeysEnabled"));
                }
            } catch (SKCEException_Exception ex) {
                error = true;
                message = "Did not find user with: " + SEARCHKEY + " of " + SEARCHVALUE;
                Common.log(Level.WARNING, "PKI2FIDO-ERR-3000", "Did not find user with: " + SEARCHKEY + " of " + SEARCHVALUE);
            }
        }
        
        // Create response
        if (username != null) {
            response = Json.createObjectBuilder()
                        .add("username", username)
                        .add("userdn", userdn)
                        .add("serialno", serialno)
                        .add("startdate", startdate.toString())
                        .add("enddate", enddate.toString())
                        .build().toString();
        } else {
            response = Json.createObjectBuilder()
                        .add("username", "")
                        .add("userdn", userdn)
                        .add("serialno", serialno)
                        .add("startdate", startdate.toString())
                        .add("enddate", enddate.toString())
                        .build().toString();
        }
        
        // Create return-object
        returnObject = Json.createObjectBuilder()
                        .add("Response", response)
                        .add("Error", error)
                        .add("Message", message)
                        .build();
        
        Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "ClientAuth JSON ReturnValue: " + returnObject.toString());
        return returnObject.toString();
    }
    
/*****************************************************************
 8888888b.  8888888b.  8888888888 8888888b.  8888888888  .d8888b.  
888   Y88b 888   Y88b 888        888   Y88b 888        d88P  Y88b 
888    888 888    888 888        888    888 888        888    888 
888   d88P 888   d88P 8888888    888   d88P 8888888    888        
8888888P"  8888888P"  888        8888888P"  888        888  88888 
888        888 T88b   888        888 T88b   888        888    888 
888        888  T88b  888        888  T88b  888        Y88b  d88P 
888        888   T88b 8888888888 888   T88b 8888888888  "Y8888P88 
******************************************************************/
/**
 * 
 * @param incomingData
 * @return
 * @throws URISyntaxException
 * @throws IOException 
 */ 
    @POST
    @Path("/" + Constants.PREREGISTER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public String preregister(InputStream incomingData) throws URISyntaxException, IOException {
        JsonObject jo = Common.inputstreamToJSON(incomingData);
        JsonObject returnObject = preregister.execute(jo.getString("username"));
        Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "Preregister response:" + returnObject.toString());
        return returnObject.toString();
    }

/********************************
8888888b.  8888888888  .d8888b.  
888   Y88b 888        d88P  Y88b 
888    888 888        888    888 
888   d88P 8888888    888        
8888888P"  888        888  88888 
888 T88b   888        888    888 
888  T88b  888        Y88b  d88P 
888   T88b 8888888888  "Y8888P88 
*********************************/
/**
 * 
 * @param incomingData
 * @return
 * @throws IOException
 * @throws MalformedURLException
 * @throws URISyntaxException 
 */    
    @POST
    @Path("/" + Constants.REGISTER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public String register(InputStream incomingData) throws IOException, MalformedURLException, URISyntaxException {
        JsonObject jo = Common.inputstreamToJSON(incomingData);
        return register.execute("unknown", jo);
    }
    
    
/********************************************************************************
8888888b.  8888888b.  8888888888        d8888 888     888 88888888888 888    888 
888   Y88b 888   Y88b 888              d88888 888     888     888     888    888 
888    888 888    888 888             d88P888 888     888     888     888    888 
888   d88P 888   d88P 8888888        d88P 888 888     888     888     8888888888 
8888888P"  8888888P"  888           d88P  888 888     888     888     888    888 
888        888 T88b   888          d88P   888 888     888     888     888    888 
888        888  T88b  888         d8888888888 Y88b. .d88P     888     888    888 
888        888   T88b 8888888888 d88P     888  "Y88888P"      888     888    888 
*********************************************************************************/
/**
 * 
 * @param incomingData
 * @return
 * @throws IOException
 * @throws URISyntaxException 
 */ 
    @POST
    @Path("/" + Constants.PREAUTHENTICATE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public String preauthenticate(InputStream incomingData) throws IOException, URISyntaxException {
        JsonObject jo = Common.inputstreamToJSON(incomingData);
        JsonObject returnObject = preauthenticate.execute(jo.getString("username"));
        Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "Preauthenticate response:" + returnObject.toString());
        return returnObject.toString();
    }

/***********************************************
       d8888 888     888 88888888888 888    888 
      d88888 888     888     888     888    888 
     d88P888 888     888     888     888    888 
    d88P 888 888     888     888     8888888888 
   d88P  888 888     888     888     888    888 
  d88P   888 888     888     888     888    888 
 d8888888888 Y88b. .d88P     888     888    888 
d88P     888  "Y88888P"      888     888    888 
************************************************/
/**
 * 
 * @param incomingData
 * @return
 * @throws IOException
 * @throws URISyntaxException 
 */    
    @POST
    @Path("/" + Constants.AUTHENTICATE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public String authenticate(InputStream incomingData) throws IOException, URISyntaxException {
        JsonObject jo = Common.inputstreamToJSON(incomingData);
        return authenticate.execute("unknown", jo);
    }
    
    
/******************************************************
8888888b.  8888888888 8888888b.  8888888888  .d8888b.  
888  "Y88b 888        888   Y88b 888        d88P  Y88b 
888    888 888        888    888 888        888    888 
888    888 8888888    888   d88P 8888888    888        
888    888 888        8888888P"  888        888  88888 
888    888 888        888 T88b   888        888    888 
888  .d88P 888        888  T88b  888        Y88b  d88P 
8888888P"  8888888888 888   T88b 8888888888  "Y8888P88 
*******************************************************/
/**
 * 
 * @param incomingData
 * @return
 * @throws URISyntaxException
 * @throws IOException 
 */        
    @POST
    @Path("/" + Constants.DEREGISTER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public String deregister(InputStream incomingData) throws URISyntaxException, IOException {
        JsonObject jo = Common.inputstreamToJSON(incomingData);
        return deregister.execute(jo.getString("username"), jo.getJsonArray("randomIDs"));
    }

/****************************************************************************
888    d8P  8888888888 Y88b   d88P 8888888 888b    888 8888888888  .d88888b.  
888   d8P   888         Y88b d88P    888   8888b   888 888        d88P" "Y88b 
888  d8P    888          Y88o88P     888   88888b  888 888        888     888 
888d88K     8888888       Y888P      888   888Y88b 888 8888888    888     888 
8888888b    888            888       888   888 Y88b888 888        888     888 
888  Y88b   888            888       888   888  Y88888 888        888     888 
888   Y88b  888            888       888   888   Y8888 888        Y88b. .d88P 
888    Y88b 8888888888     888     8888888 888    Y888 888         "Y88888P"  
*****************************************************************************/    
/**
 * 
 * @param incomingData
 * @return
 * @throws URISyntaxException
 * @throws IOException 
 */    
    @POST
    @Path("/" + Constants.GET_KEYS_INFO)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"application/json"})
    public String getKeysInfo(InputStream incomingData) throws URISyntaxException, IOException {
        JsonObject jo = Common.inputstreamToJSON(incomingData);
        System.out.println("username: " + jo.getString("username"));
        return getKeyInfo.execute(jo.getString("username"));
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
     * Calls a webservice operation on the SKCE to search for a user in LDAP
     * @param svcinfo A JSON structure with containing service credentials
     * @param basedn Optional string with BaseDN to search in LDAP server
     * @param searchkey String containing LDAP attribute to search on
     * @param searchvalue String containing LDAP attribute value to search for
     * 
     * @return SkceReturnObject A JSON data-structure
     * @throws SKCEException_Exception
     * @throws MalformedURLException 
     */
    private static SkceReturnObject getuserinfo(SKCEServiceInfoType svcinfo, String basedn, String searchkey, String searchvalue) 
            throws SKCEException_Exception, MalformedURLException 
    {
        String hosturl = HOSTPORT + LDAP_SERVICE_WSDL_SUFFIX;
        URL baseUrl = LDAPEServlet_Service.class.getResource(".");
        URL url = new URL(baseUrl, hosturl);
        Common.log(Level.INFO, "PKI2FIDO-MSG-3001", "Calling LDAPE at " + url.toString());
        LDAPEServlet_Service service = new LDAPEServlet_Service(url);
        LDAPEServlet port = service.getLDAPEServletPort();
        return port.getuserinfo(svcinfo, basedn, searchkey, searchvalue);
    }
}