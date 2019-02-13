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
 * Common static methods used in the application
 */
package com.strongauth.pki2fido.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;


@SuppressWarnings({"CallToPrintStackTrace", "StaticNonFinalUsedInInitialization"})
public class Common {
    
    // Property files used by this application for configuration info
    private static final ResourceBundle DEFAULT_CONFIG = ResourceBundle.getBundle("resources.pki2fido-configuration");
    private static ResourceBundle pki2fidoconfig = null ;
    
    // Location where PKI2FIDO is installed on this machine
    private static String P2F_HOME;

    private static final String SKFE_HOST_PORT;
    private static final String SKFE_DID;
    private static final String SKFE_REST_SUFFIX; 
    private static final String SKFE_REST_URI;
    private static final String SKFE_SVCUSERNAME;
    private static final String SKFE_SVCPASSWORD;
    private static final String SKFE_SVCINFO;
    private static final String SKFE_FIDOPROTOCOL;
    
    private static HttpURLConnection SKFE_CONN_REGISTER;
    private static HttpURLConnection SKFE_CONN_DEREGISTER;
    private static HttpURLConnection SKFE_CONN_PREREGISTER;
    private static HttpURLConnection SKFE_CONN_AUTHENTICATE;
    private static HttpURLConnection SKFE_CONN_PREAUTHENTICATE;
    
    // Logger for the application
    private static final Logger LOGGER = Logger.getLogger("PKI2FIDO", "resources.pki2fido-messages_" + Locale.getDefault());
    
    static {
        /**
         * Print out the values of the central configuration properties built
         * into the application - sort it for readability
         */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Enumeration<String> enm = DEFAULT_CONFIG.getKeys();
        List<String> keys = new ArrayList<>();
        while (enm.hasMoreElements()) {
            keys.add(enm.nextElement());
        }

        Collections.sort((List<String>) keys);
        Iterator it = keys.iterator();
        try {
            while (it.hasNext()) {
                String key = (String) it.next();
                baos.write(("\n\t" + key + ": " + DEFAULT_CONFIG.getString(key)).getBytes());
            }
            baos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        log(Level.INFO, "PKI2FIDO-MSG-1001", baos.toString());

        /**
         * Check environment variable for installation location; if not found
         * get default location specified in the configuration properties file.
         */
        if ((P2F_HOME = System.getenv("PKI2FIDO_HOME")) == null) {
            P2F_HOME = DEFAULT_CONFIG.getString("pki2fido.cfg.property.p2fhome");
        }
        log(Level.INFO, "PKI2FIDO-MSG-1001", "PKI2FIDO_HOME is: " + P2F_HOME);

        // See if there is an over-riding properties file in pki2fido
        try {
            File f = new File(P2F_HOME + "/" + "etc" + "/" + "pki2fido-configuration.properties");
            /**
             * Using try-with-resources; which will take care of closing the
             * FileInputStream fis in any case (success or failure)
             */
            try (FileInputStream fis = new FileInputStream(f)) {
                pki2fidoconfig = new java.util.PropertyResourceBundle(fis);
            }

            log(Level.INFO, "PKI2FIDO-MSG-1001",
                    "Using pki2fido-configuration.properties from pki2fido directory: "
                    + P2F_HOME + "/etc/pki2fido-configuration.properties");

            // Sort properties for readability
            baos = new ByteArrayOutputStream();
            enm = pki2fidoconfig.getKeys();
            keys = new ArrayList<>();
            while (enm.hasMoreElements()) {
                keys.add(enm.nextElement());
            }

            Collections.sort((List<String>) keys);
            it = keys.iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                baos.write(("\n\t" + key + ": " + pki2fidoconfig.getString(key)).getBytes());
            }
            baos.close();

        } catch (java.io.FileNotFoundException ex) {
            log(Level.WARNING, "PKI2FIDO-MSG-1001", "There is no pki2fido-configuration.properties in the "
                    + "pki2fido directory; using system-wide pki2fido-configuration.properties");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Print out local configuration values from pki2fido
        log(Level.INFO, "PKI2FIDO-MSG-1002", baos.toString());

        // Get configured properties
        SKFE_DID = getConfigurationProperty("pki2fido.cfg.property.skfe.did");
        SKFE_SVCUSERNAME = getConfigurationProperty("pki2fido.cfg.property.svcusername");
        SKFE_SVCPASSWORD = getConfigurationProperty("pki2fido.cfg.property.svcpassword");
        SKFE_FIDOPROTOCOL = getConfigurationProperty("pki2fido.cfg.property.u2fversion");
        SKFE_HOST_PORT = getConfigurationProperty("pki2fido.cfg.property.skfe.hostport");
        SKFE_REST_SUFFIX = getConfigurationProperty("pki2fido.cfg.property.skferestsuffix");
        SKFE_REST_URI = SKFE_HOST_PORT + SKFE_REST_SUFFIX;
        SKFE_SVCINFO = Json.createObjectBuilder()
                            .add("did", SKFE_DID)
                            .add("svcusername", SKFE_SVCUSERNAME)
                            .add("svcpassword", SKFE_SVCPASSWORD)
                            .add("protocol", SKFE_FIDOPROTOCOL)
                            .build().toString();
    }
    
    public static String getConfigurationProperty(String key) {
        if (pki2fidoconfig != null) {
            try {
                String s = pki2fidoconfig.getString(key);
                if (s.startsWith("PKI2FIDO_HOME")) {
                    return s.replaceFirst("PKI2FIDO_HOME", P2F_HOME);
                } else {
                    return s;
                }
            } catch (java.util.MissingResourceException ex) {
                // Do nothing
            }
        }

        String s = DEFAULT_CONFIG.getString(key);
        if (s.startsWith("PKI2FIDO_HOME")) {
            return s.replaceFirst("PKI2FIDO_HOME", P2F_HOME);
        } else {
            return s;
        }
    }

    /**
     *  Prints the appropriate information to the application logger
     *  Databeans cannot have the java.util.logging.logger class as it
     *  is not serializable.
     *  @param level - Level at which the message should be logged
     *  @param key - Property key for this message
     *  @param param - Any parameters specified with this message
     */
    public static void log(java.util.logging.Level level, String key, Object param)
    {
        LOGGER.log(level, key, param);
    }
    
    public static String getSkfeHostPort() {
        return SKFE_HOST_PORT;
    }
    
    /********************************************************************************
     *     888888  .d8888b.   .d88888b.  888b    888       .d88888b.
     *       "88b d88P  Y88b d88P" "Y88b 8888b   888      d88P" "Y88b
     *        888 Y88b.      888     888 88888b  888      888     888
     *        888  "Y888b.   888     888 888Y88b 888      888     888 88888b.  .d8888b
     *        888     "Y88b. 888     888 888 Y88b888      888     888 888 "88b 88K
     *        888       "888 888     888 888  Y88888      888     888 888  888 "Y8888b.
     *        88P Y88b  d88P Y88b. .d88P 888   Y8888      Y88b. .d88P 888 d88P      X88
     *        888  "Y8888P"   "Y88888P"  888    Y888       "Y88888P"  88888P"   88888P'
     *      .d88P                                                     888
     *    .d88P"                                                      888
     *   888P"                                                        888
     *********************************************************************************/
        /**
     * Convert the JSON represented as a String into a JsonObject
     *
     * @param jsonstr String containing the JSON
     * @return JsonObject if successful, null otherwise
     */
    public static JsonObject stringToJSON(String jsonstr) 
    {
        if (jsonstr == null || jsonstr.isEmpty()) {
            return null;
        }

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonstr))) {
            return jsonReader.readObject();
        } catch (Exception ex) {
            log(Level.WARNING, "PKI2FIDO-ERR-1001", ex.getLocalizedMessage());
        }
        return null;
    }
    
    /**
     * Given a JSON string and a search-key, this method looks up the 'key'
     * in the JSON and if found, returns the associated value.  Returns NULL
     * in all error conditions.
     *
     * @param jsonstr String containing JSON
     * @param key String containing the search-key
     * @param datatype String containing the data-type of the value-object
     * @return Object containing the value for the specified key if valid;
     * null in all error cases.
     */
    public static Object getJsonValue(String jsonstr, String key, String datatype) {
        if (jsonstr == null || jsonstr.isEmpty()) {
            if (key == null || key.isEmpty()) {
                if (datatype == null || datatype.isEmpty()) {
                    return null;
                }
            }
        }

        try (JsonReader jsonreader = Json.createReader(new StringReader(jsonstr))) {
            JsonObject json = jsonreader.readObject();

            if (!json.containsKey(key)) {
                log(Level.WARNING, "PKI2FIDO-ERR-1003", "'" + key + "' does not exist in the json");
                return null;
            }

            switch (datatype) {
                case "Boolean": return json.getBoolean(key);
                case "Int": return json.getInt(key);
                case "JsonArray": return json.getJsonArray(key);
                case "JsonNumber": return json.getJsonNumber(key);
                case "JsonObject": return json.getJsonObject(key);
                case "JsonString": return json.getJsonString(key);
                case "String": return json.getString(key);
                default: return null;
            }
        } catch (Exception ex) {
            log(Level.WARNING, "PKI2FIDO-ERR-1001", ex.getLocalizedMessage());
            return null;
        }
    }
    
    /**
     * Converts InputStream to JSON format
     * @param is
     * @return JsonObject
     */
    public static JsonObject inputstreamToJSON(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(isr);
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
        } catch (Exception ex) {
            log(Level.WARNING, "PKI2FIDO-ERR-1001", ex.getLocalizedMessage());
        }
        String jsonString = sb.toString();
        log(Level.INFO, "PKI2FIDO-ERR-1001", "Input stream JSON: " + jsonString);
        
        return stringToJSON(jsonString);
    }
    
    /**
     * Decodes the returned value from the SKCERO
     *
     * @param input String containing returned JSON
     * @param type String value denoting the type of object
     * @return String with the returned value from the JSON
     */
    public static String decodeJson(String input, String type)
    {
        JsonObject jsonObject;
        try (JsonReader jsonReader = Json.createReader(new StringReader(input))) {
            jsonObject = jsonReader.readObject();
        }

        switch (type) {
            case "uid":  return jsonObject.getString("uid");
            case "FIDOKeysEnabled":  return jsonObject.getString("FIDOKeysEnabled");
            case "TwoStepVerification":  return jsonObject.getString("TwoStepVerification");
            case "did":    return jsonObject.getString("did");
            default: return null;   // Shouldn't happen, but...
        }
    }
    
/***************************************************************************
                  888    8888888888 d8b      888           .d8888b.  888               888 888                                     
                  888    888        Y8P      888          d88P  Y88b 888               888 888                                     
                  888    888                 888          888    888 888               888 888                                     
 .d88b.   .d88b.  888888 8888888    888  .d88888  .d88b.  888        88888b.   8888b.  888 888  .d88b.  88888b.   .d88b.   .d88b.  
d88P"88b d8P  Y8b 888    888        888 d88" 888 d88""88b 888        888 "88b     "88b 888 888 d8P  Y8b 888 "88b d88P"88b d8P  Y8b 
888  888 88888888 888    888        888 888  888 888  888 888    888 888  888 .d888888 888 888 88888888 888  888 888  888 88888888 
Y88b 888 Y8b.     Y88b.  888        888 Y88b 888 Y88..88P Y88b  d88P 888  888 888  888 888 888 Y8b.     888  888 Y88b 888 Y8b.     
 "Y88888  "Y8888   "Y888 888        888  "Y88888  "Y88P"   "Y8888P"  888  888 "Y888888 888 888  "Y8888  888  888  "Y88888  "Y8888  
     888                                                                                                              888          
Y8b d88P                                                                                                         Y8b d88P          
 "Y88P"                                                                                                           "Y88P"           
 ***************************************************************************/
    
    /**
     * Makes a web-service call 'preregister' or 'preauthenticate' based on
     * the methodendpoint provided. Both of these web-services will return
     * a fido registration/authentication challenge.
     *
     * @param username String; username of the account holder trying to register
     * or authenticate
     * @param methodendpoint String; "preregister" or "preauthenticate" based
     * on the operation being performed
     * @return String; SKFE response
     * @throws URISyntaxException
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    public static String getFidoChallenge(final String username, final String methodendpoint) 
            throws URISyntaxException, MalformedURLException, ProtocolException, IOException 
    {
        // Input checks
        if (username == null || username.isEmpty() || 
            methodendpoint == null || methodendpoint.isEmpty() || 
            (!methodendpoint.equalsIgnoreCase(Constants.PREREGISTER) && 
            !methodendpoint.equalsIgnoreCase(Constants.PREAUTHENTICATE))) 
        {
            log(Level.WARNING, "PKI2FIDO-ERR-1001", "Null username or methodendpoint");
            return null;
        }

        // Create a SKFE compliant payload object to pass in the username.
        String payload = Json.createObjectBuilder()
                .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, username)
                .build().toString();

        // Make the webservice request 
        String response = null;
        try {
            response = callFido(methodendpoint, payload);
            if (response==null || response.isEmpty())
                log(Level.WARNING, "PKI2FIDO-ERR-3000", "Server error: Check application logs or contact support");
        } catch (Exception ex) {
            log(Level.WARNING, "PKI2FIDO-ERR-3000", "Server error : " + ex.getLocalizedMessage() + ". Check application logs or contact support");
        }
        return response;
    }
    
/********************************************************************************
                  888                    d8b 888    8888888888 d8b      888          8888888b.                                                                 
                  888                    Y8P 888    888        Y8P      888          888   Y88b                                                                
                  888                        888    888                 888          888    888                                                                
.d8888b  888  888 88888b.  88888b.d88b.  888 888888 8888888    888  .d88888  .d88b.  888   d88P  .d88b.  .d8888b  88888b.   .d88b.  88888b.  .d8888b   .d88b.  
88K      888  888 888 "88b 888 "888 "88b 888 888    888        888 d88" 888 d88""88b 8888888P"  d8P  Y8b 88K      888 "88b d88""88b 888 "88b 88K      d8P  Y8b 
"Y8888b. 888  888 888  888 888  888  888 888 888    888        888 888  888 888  888 888 T88b   88888888 "Y8888b. 888  888 888  888 888  888 "Y8888b. 88888888 
     X88 Y88b 888 888 d88P 888  888  888 888 Y88b.  888        888 Y88b 888 Y88..88P 888  T88b  Y8b.          X88 888 d88P Y88..88P 888  888      X88 Y8b.     
 88888P'  "Y88888 88888P"  888  888  888 888  "Y888 888        888  "Y88888  "Y88P"  888   T88b  "Y8888   88888P' 88888P"   "Y88P"  888  888  88888P'  "Y8888  
                                                                                                                  888                                          
                                                                                                                  888                                          
                                                                                                                  888               
********************************************************************************/

    /**
     * Makes a web-service call to 'register' or 'authenticate' based on the
     * supplied methodendpoint. 
     *
     * @param location String; URL location of the webservice
     * @param adata JsonObject, FIDO U2F authenticator's signed challenge
     * @param methodendpoint String; "register" or "authenticate"
     * @return String containing the JSON response from SKFE
     * @throws URISyntaxException MalformedURLException ProtocolException IOException
     */
    public static String submitFidoResponse(final String location, 
                                            final JsonObject tokendata, 
                                            final String methodendpoint) 
            throws URISyntaxException, MalformedURLException, ProtocolException, IOException 
    {
        // Check parameters
        if (location == null || location.isEmpty() || tokendata == null || 
                methodendpoint == null || methodendpoint.isEmpty() || 
                (!methodendpoint.equalsIgnoreCase(Constants.REGISTER) && 
                !methodendpoint.equalsIgnoreCase(Constants.AUTHENTICATE))) {
            log(Level.WARNING, "PKI2FIDO-ERR-1001", "Null location, tokendata or methodendpoint");
            return null;
        }
        String username = tokendata.getString("username");
        tokendata.remove("username");
        
        // Build metadata object with location information
        String locationkey = (methodendpoint.equalsIgnoreCase(Constants.REGISTER)) ? "create_location" : "last_used_location";
        JsonObject metadata = javax.json.Json.createObjectBuilder()
                .add("version", "1.0") // only supported version currently
                .add(locationkey, location)
                .add("username", username)
                .build();

        // Create SKFE payload object with data from FIDO Token
        String payload = Json.createObjectBuilder()
                .add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, metadata)
                .add(Constants.JSON_KEY_SERVLET_INPUT_RESPONSE, tokendata)
                .build().toString();
        if (payload == null) {
            return null;
        }
        
        // Make the webservice request
        String response = null;
        try {
            response = callFido(methodendpoint, payload);
            if (response==null || response.isEmpty()) 
                log(Level.WARNING, "HC-SECURE-ERR-3000", "Server error: Check application logs or contact support");
        } catch (Exception ex) {
            log(Level.WARNING, "HC-SECURE-ERR-3000", "Server error : " + ex.getLocalizedMessage() + ". Check application logs or contact support");
        }
        return response;
    }
    
/*********************************************************
                  888 888 8888888888 d8b      888          
                  888 888 888        Y8P      888          
                  888 888 888                 888          
 .d8888b  8888b.  888 888 8888888    888  .d88888  .d88b.  
d88P"        "88b 888 888 888        888 d88" 888 d88""88b 
888      .d888888 888 888 888        888 888  888 888  888 
Y88b.    888  888 888 888 888        888 Y88b 888 Y88..88P 
 "Y8888P "Y888888 888 888 888        888  "Y88888  "Y88P" 
 *********************************************************/
    /**
     * Makes HTTP call with methodname - REGISTER, AUTHENTICATE, etc. and 
     * payload as input. Parses the response back from the HTTP request and 
     * returns it as a string.
     * 
     * @param methodname    String; SKFE RESTful interface method end point.
     * @param payload   String; payload input to be provided to the SKCE service.
     * @return String
     * @throws java.io.IOException 
     */
    public static String callFido(final String methodname, String payload) throws IOException
    {
        if (payload == null || payload.trim().isEmpty()) 
            payload="";
        log(Level.INFO, "PKI2FIDO-MSG-3000", "Calling FIDO webservice: " + SKFE_REST_URI + methodname);
        
        if (methodname == null) {
            log(Level.WARNING, "PKI2FIDO-ERR-1001", "Null methodname");
            return null;
        }

        try {
            HttpURLConnection conn = null;
            switch (methodname) {
                case Constants.REGISTER: conn = (HttpURLConnection) new URL(SKFE_REST_URI + Constants.REGISTER).openConnection(); break;
                case Constants.DEREGISTER: conn = (HttpURLConnection) new URL(SKFE_REST_URI + Constants.DEREGISTER).openConnection(); break;
                case Constants.PREREGISTER: conn = (HttpURLConnection) new URL(SKFE_REST_URI + Constants.PREREGISTER).openConnection(); break;
                case Constants.AUTHENTICATE: conn = (HttpURLConnection) new URL(SKFE_REST_URI + Constants.AUTHENTICATE).openConnection(); break;
                case Constants.PREAUTHENTICATE: conn = (HttpURLConnection) new URL(SKFE_REST_URI + Constants.PREAUTHENTICATE).openConnection(); break;
                case Constants.GET_KEYS_INFO: conn = (HttpURLConnection) new URL(SKFE_REST_URI + Constants.GET_KEYS_INFO).openConnection(); break;
                default:
                    log(Level.INFO, "PKI2FIDO-ERR-1001", "Invalid methodname for HTTP connection");
            }

            // Set connection properties
            conn.setReadTimeout(Constants.TIMEOUT_VALUE);
            conn.setConnectTimeout(Constants.TIMEOUT_VALUE);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // Write out form parameters
            String formparams = "svcinfo=" + SKFE_SVCINFO + "&payload=" + payload;
            conn.setFixedLengthStreamingMode(formparams.getBytes().length);
            try (PrintWriter out = new PrintWriter(conn.getOutputStream())) {
                out.print(formparams);
            }
            
            // Error from SKCE server
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
            }

            // Read SKCE server response
            String output, response="";
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                response = response + output;
            }
            return response;
        } catch (IOException ex) {
            log(Level.WARNING, "PKI2FIDO-ERR-3000", "Exception : " + ex.getLocalizedMessage());
            return null;
        }
    }

    /***************************************************************************
         888                        888      8888888888                  8888888888                                  
         888                        888      888                         888                                         
         888                        888      888                         888                                         
 .d8888b 88888b.   .d88b.   .d8888b 888  888 8888888     .d88b.  888d888 8888888    888d888 888d888  .d88b.  888d888 
d88P"    888 "88b d8P  Y8b d88P"    888 .88P 888        d88""88b 888P"   888        888P"   888P"   d88""88b 888P"   
888      888  888 88888888 888      888888K  888        888  888 888     888        888     888     888  888 888     
Y88b.    888  888 Y8b.     Y88b.    888 "88b 888        Y88..88P 888     888        888     888     Y88..88P 888     
 "Y8888P 888  888  "Y8888   "Y8888P 888  888 888         "Y88P"  888     8888888888 888     888      "Y88P"  888     
     ***************************************************************************/
    
    /**
     * Checks if response from SKFE has a non-empty error element in it
     * @param skferesponse String with JSON content
     * @return JsonObject if anything erroneous is found, null otherwise.
     */
    public static JsonObject checkForError(String skferesponse) 
    {
        String error;
        if (skferesponse == null || skferesponse.isEmpty()) {
            error = "Empty response from SKCE server";
        } else {
            //  Read the "Error", a String element in the response
            error = (String) getJsonValue(skferesponse, "Error", "String");
        }
        
        if (error != null && !error.trim().isEmpty())
            return Json.createObjectBuilder().add(Constants.REST_SERVICE_ERROR, error).build();
        else
            return null;
    }
}
