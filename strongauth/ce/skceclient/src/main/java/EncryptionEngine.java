/*
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
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$ $Revision$
 * $Author$ $URL:
 * https://svn.strongauth.com/repos/jade/trunk/skce/skceclient/src/main/java/EncryptionEngine.java
 * $
 *
 * *********************************************
 *                   888                      
 *                   888                      
 *                   888                      
 * 88888b.   .d88b.  888888  .d88b.  .d8888b  
 * 888 "88b d88""88b 888    d8P  Y8b 88K      
 * 888  888 888  888 888    88888888 "Y8888b. 
 * 888  888 Y88..88P Y88b.  Y8b.          X88 
 * 888  888  "Y88P"   "Y888  "Y8888   88888P' 
 *
 * *********************************************
 *
 * EncryptionEngine class takes care of testing various web-service operations
 * that are part of skee (strongkey encryption engine); which is a sub-component
 * of skce (strongkey cryptoengine)
 *
 * The operations that could be tested using this class are
 *
 * 1. Encrypt a plain-text file 2. Decrypt an encrypted file 3. Encrypt a
 * plain-text file and store it in a cloud 4. Decrypt an encrypted file
 * available in cloud 5. Ping the encryption engine
 *
 */
import com.strongauth.skceclient.common.Constants;
import com.strongauth.skceclient.common.common;
import com.strongauth.skee.soapstubs.*;
import com.strongauth.skfe.client.impl.RestFidoU2FAuthorize;
import com.strongauth.skfe.tokensim.FIDOU2FTokenSimulator;
import com.sun.xml.ws.developer.JAXWSProperties;
import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import static javax.json.stream.JsonParser.Event.KEY_NAME;
import static javax.json.stream.JsonParser.Event.VALUE_STRING;
import javax.json.stream.JsonParserFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.DecoderException;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.jvnet.staxex.StreamingDataHandler;

public class EncryptionEngine {

    public static void main(String[] args) throws SKCEException_Exception, Exception {

        // Get the skceclient version info.  CANNOT use new FileInputStream
        // as this is loaded from a different classloader.  Properties file
        // must be under src/main/resources
        Properties vprops = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        vprops.load(loader.getResourceAsStream("skceclient.properties"));
        String version = vprops.getProperty("skceclient.property.version");
        System.out.println("\nskceclient.jar " + version);
        System.out.println("Copyright (c) 2001-2018 StrongAuth, Inc. All rights reserved.");

        BasicParser parser;
        CommandLine cl;
        // Declare Variables
        Boolean err = false;
        String destpath;
        String note;
        String hostport;
        String did ;
        String svcusername;
        String svcpassword;
        String operation;
        String sourcepath = "";
        String uniquekey;
        String algorithm;
        String keysize;
        String propfile;
        String ldapuser = null;
        String userdn = null;
        String wsprotocol = "";
        List ldapgroup = null;
        String storetype = null;
        String filedigest =null, filedigestalgo=null;
        String ldapgroupinput = null;
        int requiredauth = 0;
        String origin = null;
        int auth_counter = 0;
        String Usage = "Usage: java -cp skceclient.jar EncryptionEngine https://<host:port> <did> <svcusername> <svcpassword> E  <sourcefile> [-dn <ldapdn>] -lu <ldapuser> -lg <ldapgroups> -wp <ws-protocol> [-ra <requiredauth>] [-u <uniquekey>] [-a <algorithm>] [-s <keysize>] [-t <targetlocation>] [-n <notes>]\n"
                + "       java -cp skceclient.jar EncryptionEngine https://<host:port> <did> <svcusername> <svcpassword> CE <sourcefile> -wp <ws-protocol> [-dn <ldapdn>] -lu <ldapuser> -lg <ldapgroups> [-ra <requiredauth>] [-u <uniquekey>] [-a <algorithm>] [-s <keysize>] -f <propertiesfile> [-n <notes>]\n"
                + "       java -cp skceclient.jar EncryptionEngine https://<host:port> <did> <svcusername> <svcpassword> D  <sourcefile> -wp <ws-protocol> [-dn <ldapdn>] -lu <ldapuser> [-t <targetlocation>]\n"
                + "       java -cp skceclient.jar EncryptionEngine https://<host:port> <did> <svcusername> <svcpassword> AD <sourcefile> -wp <ws-protocol> [-dn <ldapdn>] -lu <ldapuser> -o <origin> -ac <auth counter> [-t <targetlocation>]\n"
                + "       java -cp skceclient.jar EncryptionEngine https://<host:port> <did> <svcusername> <svcpassword> CD <sourcefile> -wp <ws-protocol> [-dn <ldapdn>] -lu <ldapuser> -f <propertiesfile> [-t <targetlocation>]\n"
                + "       java -cp skceclient.jar EncryptionEngine https://<host:port> <did> <svcusername> <svcpassword> P -wp <ws-protocol> \n\n"
                + "Acceptable values are as follows:\n"
                + "  hostport       : host url where skce service is listening;\n"
                + "                     format  : https://<FQDN>:<ssl-portnumber>\n"
                + "                     example : https://fidodemo.strongauth.com:8181\n\n"
                + "  did            : Unique domain identifier that belongs to SKCE\n"
                + "  svcusername    : Service credentials; username\n"
                + "  svcpassword    : Service credentials; password\n"
                + "  command        : E (encrypt) | CL (cloudencrypt) | D (decrypt) | AD (authorizeddecrypt) | CD (clouddecrypt) | P (ping)\n"
                + "  sourcefile     : Full pathname of file to be encrypted or decrypted\n"
                + "  ldapdn         : The full LDAP DN of the user (optional)\n"
                + "  ldapuser       : LDAP user to determine authorization for operation\n"
                + "  ws-protocol    : Web SErvice protocol to be used for this operation. Allowed values are SOAP | REST\n"
                + "  ldapgroups     : List of LDAP groups to determine authorization for decryption (hyphen (-) separated list)\n"
                + "  origin         : Origin to be used by the FIDO client simulator\n"
                + "  authcounter    : Auth Counter to be used by the FIDO client simulator\n"
                + "  requiredauth   : (Optional) Integer number ranging from 0 to 3; to indicate the level of authorization\n"
                + "                     needed to be able to decrypt the file. Default value is 0; meaning no additional\n"
                + "                     authorization is needed\n"
                + "  payload        : (Optional) Data related to transaction authorization. In most of the cases, it is the\n"
                + "                     response from a FIDO Authenticator. This parameter is optional.\n"
                + "  targetlocation : (Optional) Target destination path to store output file\n"
                + "  notes          : (Optional) notes for the operation\n"
                + "  uniquekey      : (Optional) Generate a unique encryption key for encrypt operation?  [true | false]\n"
                + "  algorithm      : (Optional) Encryption-key algorithm AES | TDEA\n"
                + "  keysize        : (Optional) Encryption-key size (if a new key is to be generated)\n"
                + "                     AES  : 128 | 192 | 256\n"
                + "                     TDEA : 112 | 168\n"
                + "  propertiesfile : full directory path to properties file with following cloud configuration\n"
                + "                     skceclient.property.cloudtype=\n"
                + "                     skceclient.property.cloudcontainer=\n"
                + "                     skceclient.property.cloudname=\n"
                + "                     skceclient.property.accesskey=\n"
                + "                     skceclient.property.secretkey=\n"
                + "                     skceclient.property.cloudcredentialid=\n";

        Boolean uniqueKey = null;
        int keySize = 0;

        // Initialize a Command Line Parser
        try {
            Options opt = new Options();

            opt.addOption("h", false, "Print help for this application");
            opt.addOption("f", true, "The properties file to use");
            opt.addOption("n", true, "Note");
            opt.addOption("t", true, "Target Location");
            opt.addOption("u", true, "Unique Key");
            opt.addOption("a", true, "Algorithm");
            opt.addOption("dn", true, "LDAP DN");
            opt.addOption("lu", true, "LDAP username");
            opt.addOption("lg", true, "LDAP groups");
            opt.addOption("s", true, "Key Size");
            opt.addOption("o", true, "Origin");
            opt.addOption("wp", true, "WS Protocol");
            opt.addOption("ac", true, "Auth Counter");
            opt.addOption("ra", true, "requiredauth");

            parser = new BasicParser();
            cl = parser.parse(opt, args);

            if (cl.hasOption('h')) {
                System.out.println(Usage);
                return;
            } else {
                propfile = cl.getOptionValue("f");
                destpath = cl.getOptionValue("t");
                note = cl.getOptionValue("n");
                algorithm = cl.getOptionValue("a");
                uniquekey = cl.getOptionValue("u");
                keysize = cl.getOptionValue("s");
                String ra = cl.getOptionValue("ra");
            }
        } catch (ParseException e) {
            System.out.println("\nInvalid argument or value Passed... Value for any argument cannot be null\n");
            System.out.println(Usage);
            return;
        }

        /*
         * Initialize parameters to be passed to the web service calls
         * Parsing Arguments
         */
        if (args.length >= 5) {
            hostport = args[0];
            did = args[1];
            svcusername = args[2];
            svcpassword = args[3];
            operation = args[4];

            /**
             * ********************************************************
             * 888 888 888 .d88b. 88888b. .d8888b 888d888 888 888 88888b. 888888
             * d8P Y8b 888 "88b d88P" 888P" 888 888 888 "88b 888 88888888 888
             * 888 888 888 888 888 888 888 888 Y8b. 888 888 Y88b. 888 Y88b 888
             * 888 d88P Y88b. "Y8888 888 888 "Y8888P 888 "Y88888 88888P" "Y888
             * 888 888 Y8b d88P 888 "Y88P" 888
             * ********************************************************
             */
            if (operation.equalsIgnoreCase("E")) {
                if (args.length < 9) {
                    System.out.println("\nMissing arguments..\n");
                    System.out.println(Usage);
                    return;
                }
                sourcepath = args[5];

                // Check for full LDAP DN
                if (cl.hasOption("dn")) {
                    userdn = cl.getOptionValue("dn");
                }
                if (cl.hasOption("lu")) {
                    ldapuser = cl.getOptionValue("lu");
                } else {
                    System.out.println("\nMissing ldapuser name..\n");
                    System.out.println(Usage);
                    return;
                }
                
                if(cl.hasOption("wp")){
                    wsprotocol = cl.getOptionValue("wp");
                    if(!wsprotocol.equalsIgnoreCase("REST") && !wsprotocol.equalsIgnoreCase("SOAP")){
                        System.out.println("Invalid WS Protocol..\n");
                        System.out.println(Usage);
                        return;
                    }
                }else{
                    System.out.println("\nMissing WS Protocol..\n");
                    System.out.println(Usage);
                    return;
                }

                if (cl.hasOption("lg")) {
                    ldapgroupinput = cl.getOptionValue("lg");
                    ldapgroup = common.getInputNames(ldapgroupinput);
                } else {
                    System.out.println("\nMissing ldapgroup name..\n");
                    System.out.println(Usage);
                    return;
                }
                
                if (cl.hasOption("ra")) {
                    String ra = cl.getOptionValue("ra");
                    try {
                        requiredauth = Integer.parseInt(ra);
                        if ( requiredauth<0 || requiredauth>3 ) {
                            System.out.println("\nRequiredauth should be a number ranging from 0 to 3 (inclusive)..\n");
                            System.out.println(Usage);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("\nRequiredauth should be a number ranging from 0 to 3 (inclusive)..\n");
                        System.out.println(Usage);
                        return;
                    }
                }
                
                // Check UniqueKey Parameter
                if (uniquekey == null) {
                    uniqueKey = true;
                } else {
                    if (uniquekey.equalsIgnoreCase("true")) {
                        uniqueKey = Boolean.parseBoolean(uniquekey);
                    } else if (uniquekey.equalsIgnoreCase("false")) {
                        uniqueKey = Boolean.parseBoolean(uniquekey);
                    } else {
                        System.err.println("\nInvalid value for UNIQUEKEY... Acceptable values are true | false\n");
                        err = true;
                    }
                }

                // Check Algorithm Parameter and the corresponding KeySize
                if (algorithm == null) {
                    algorithm = "AES";
                    if (keysize == null) {
                        keysize = "256";
                        keySize = Integer.parseInt(keysize);
                    } else {
                        if (keysize.equalsIgnoreCase("128")) {
                            keySize = Integer.parseInt(keysize);
                        } else if (keysize.equalsIgnoreCase("192")) {
                            keySize = Integer.parseInt(keysize);
                        } else if (keysize.equalsIgnoreCase("256")) {
                            keySize = Integer.parseInt(keysize);
                        } else {
                            System.err.println("\nInvalid KEYSIZE for Default Algorithm (AES)... Acceptable values are 128 | 192 | 256\n");
                            err = true;
                        }
                    }
                } else {
                    if (algorithm.equalsIgnoreCase("AES")) {
                        algorithm = "AES";
                        if (keysize == null) {
                            keysize = "256";
                            keySize = Integer.parseInt(keysize);
                        } else {
                            if (keysize.equalsIgnoreCase("128")) {
                                keySize = Integer.parseInt(keysize);
                            } else if (keysize.equalsIgnoreCase("192")) {
                                keySize = Integer.parseInt(keysize);
                            } else if (keysize.equalsIgnoreCase("256")) {
                                keySize = Integer.parseInt(keysize);
                            } else {
                                System.err.println("\nInvalid KEYSIZE for AES... Acceptable values are 128 | 192 | 256\n");
                                err = true;
                            }
                        }
                    } else if (algorithm.equalsIgnoreCase("TDEA")) {
                        algorithm = "DESEDE";
                        if (keysize == null) {
                            keysize = "112";
                            keySize = Integer.parseInt(keysize);
                        } else {
                            if (keysize.equalsIgnoreCase("112")) {
                                keySize = Integer.parseInt(keysize);
                            } else if (keysize.equalsIgnoreCase("168")) {
                                keySize = Integer.parseInt(keysize);
                            } else {
                                System.err.println("\nInvalid KEYSIZE for TDEA... Acceptable values are 112 | 168\n");
                                err = true;
                            }
                        }
                    } else {
                        System.err.println("\nInvalid value for ALGORITHM... Acceptable values are AES | TDEA\n");
                    }
                }

                // Check for target location
                if (destpath == null) {
                    destpath = "";
                }


                // Check if there were errors in passing arguments
                if (err == true) {
                    System.out.println(Usage);
                    return;
                }

                /**
                 * ********************************************************
                 * 888 888 888 888 888 888 .d88888 .d88b. .d8888b 888d888 888
                 * 888 88888b. 888888 d88" 888 d8P Y8b d88P" 888P" 888 888 888
                 * "88b 888 888 888 88888888 888 888 888 888 888 888 888 Y88b
                 * 888 Y8b. Y88b. 888 Y88b 888 888 d88P Y88b. "Y88888 "Y8888
                 * "Y8888P 888 "Y88888 88888P" "Y888 888 888 Y8b d88P 888 "Y88P"
                 * 888 ********************************************************
                 */
            } 
            else if (operation.equalsIgnoreCase("AD")){
                if (args.length < 10) {
                    System.out.println("\nMissing arguments..\n");
                    System.out.println(Usage);
                    return;
                }
                if(cl.hasOption("o")){
                    origin = cl.getOptionValue("o");
                }else{
                    System.out.println("\nMissing argument origin\n");
                    System.out.println(Usage);
                    return;
                }
                
                if(cl.hasOption("ac")){
                    auth_counter = Integer.parseInt(cl.getOptionValue("ac"));
                }else{
                    System.out.println("\nMissing argument auth counter\n");
                    System.out.println(Usage);
                    return;
                }
                sourcepath = args[5];
                if (sourcepath == null) {
                    System.out.println("\nFile to decrypt cannot be found: " + sourcepath + "\n");
                    System.out.println(Usage);
                    return;
                } else {
                    File f = new File(sourcepath);
                    if (!f.exists()) {
                        System.out.println("\nFile to decrypt cannot be found: " + sourcepath + "\n");
                        System.out.println(Usage);
                        return;
                    }
                }
                
                if(cl.hasOption("wp")){
                    wsprotocol = cl.getOptionValue("wp");
                    if(!wsprotocol.equalsIgnoreCase("REST") && !wsprotocol.equalsIgnoreCase("SOAP")){
                        System.out.println("Invalid WS Protocol..\n");
                        System.out.println(Usage);
                        return;
                    }
                }else{
                    System.out.println("\nMissing WS Protocol..\n");
                    System.out.println(Usage);
                    return;
                }

                if (cl.hasOption("dn")) {
                    userdn = cl.getOptionValue("dn");
                }
                if (cl.hasOption("lu")) {
                    ldapuser = cl.getOptionValue("lu");
                } else {
                    System.out.println("\nMissing lookup username..\n");
                    System.out.println(Usage);
                    return;
                }

                if (destpath == null) {
                    destpath = "";
                }
            }
            else if (operation.equalsIgnoreCase("D")) {
                if (args.length < 8) {
                    System.out.println("\nMissing arguments..\n");
                    System.out.println(Usage);
                    return;
                }
                sourcepath = args[5];
                if (sourcepath == null) {
                    System.out.println("\nFile to decrypt cannot be found: " + sourcepath + "\n");
                    System.out.println(Usage);
                    return;
                } else {
                    File f = new File(sourcepath);
                    if (!f.exists()) {
                        System.out.println("\nFile to decrypt cannot be found: " + sourcepath + "\n");
                        System.out.println(Usage);
                        return;
                    }
                }

                if (cl.hasOption("dn")) {
                    userdn = cl.getOptionValue("dn");
                }
                if (cl.hasOption("lu")) {
                    ldapuser = cl.getOptionValue("lu");
                } else {
                    System.out.println("\nMissing lookup username..\n");
                    System.out.println(Usage);
                    return;
                }

                if(cl.hasOption("wp")){
                    wsprotocol = cl.getOptionValue("wp");
                    if(!wsprotocol.equalsIgnoreCase("REST") && !wsprotocol.equalsIgnoreCase("SOAP")){
                        System.out.println("Invalid WS Protocol..\n");
                        System.out.println(Usage);
                        return;
                    }
                }else{
                    System.out.println("\nMissing WS Protocol..\n");
                    System.out.println(Usage);
                    return;
                }
                
                if (destpath == null) {
                    destpath = "";
                }
                /**
                 * ********************************************************
                 * 888 888 888 888 888 888 888 888 888 .d8888b 888 .d88b. 888
                 * 888 .d88888 .d88b. 88888b. .d8888b 888d888 888 888 88888b.
                 * 888888 d88P" 888 d88""88b 888 888 d88" 888 d8P Y8b 888 "88b
                 * d88P" 888P" 888 888 888 "88b 888 888 888 888 888 888 888 888
                 * 888 88888888 888 888 888 888 888 888 888 888 888 Y88b. 888
                 * Y88..88P Y88b 888 Y88b 888 Y8b. 888 888 Y88b. 888 Y88b 888
                 * 888 d88P Y88b. "Y8888P 888 "Y88P" "Y88888 "Y88888 "Y8888 888
                 * 888 "Y8888P 888 "Y88888 88888P" "Y888 888 888 Y8b d88P 888
                 * "Y88P" 888
                 * ********************************************************
                 */
            } else if (operation.equalsIgnoreCase("CE")) {
                if (args.length < 9) {
                    System.out.println("\nMissing arguments..\n");
                    System.out.println(Usage);
                    return;
                }
                sourcepath = args[5];
                if (sourcepath == null) {
                    System.out.println("\nFile to encrypt cannot be found: " + sourcepath + "\n");
                    System.out.println(Usage);
                    return;
                } else {
                    File f = new File(sourcepath);
                    if (!f.exists()) {
                        System.out.println("\nFile to encrypt cannot be found: " + sourcepath + "\n");
                        System.out.println(Usage);
                        return;
                    }
                }

                if(cl.hasOption("wp")){
                    wsprotocol = cl.getOptionValue("wp");
                    if(!wsprotocol.equalsIgnoreCase("REST") && !wsprotocol.equalsIgnoreCase("SOAP")){
                        System.out.println("Invalid WS Protocol..\n");
                        System.out.println(Usage);
                        return;
                    }
                }else{
                    System.out.println("\nMissing WS Protocol..\n");
                    System.out.println(Usage);
                    return;
                }

                if (cl.hasOption("dn")) {
                    userdn = cl.getOptionValue("dn");
                }
                if (cl.hasOption("lu")) {
                    ldapuser = cl.getOptionValue("lu");
                } else {
                    System.out.println("\nMissing lookup username..\n");
                    System.out.println(Usage);
                    return;
                }

                if (cl.hasOption("lg")) {
                    ldapgroupinput = cl.getOptionValue("lg");
                    ldapgroup = common.getInputNames(ldapgroupinput);
                } else {
                    System.out.println("\nMissing ldapgroup..\n");
                    System.out.println(Usage);
                    return;
                }
                
                if (cl.hasOption("ra")) {
                    String ra = cl.getOptionValue("ra");
                    try {
                        requiredauth = Integer.parseInt(ra);
                        if ( requiredauth<0 || requiredauth>3 ) {
                            System.out.println("\nRequiredauth should be a number ranging from 0 to 3 (inclusive)..\n");
                            System.out.println(Usage);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("\nRequiredauth should be a number ranging from 0 to 3 (inclusive)..\n");
                        System.out.println(Usage);
                        return;
                    }
                }

                // Check UniqueKey Parameter
                if (uniquekey == null) {
                    uniqueKey = true;
                } else {
                    if (uniquekey.equalsIgnoreCase("true")) {
                        uniqueKey = Boolean.parseBoolean(uniquekey);
                    } else if (uniquekey.equalsIgnoreCase("false")) {
                        uniqueKey = Boolean.parseBoolean(uniquekey);

                    } else {
                        System.err.println("\nInvalid value for UNIQUEKEY... Acceptable values are true | false\n");
                        err = true;
                    }
                }

                // Check Algorithm Parameter and the corresponding KeySize
                if (algorithm == null) {
                    algorithm = "AES";
                    if (keysize == null) {
                        keysize = "128";
                        keySize = Integer.parseInt(keysize);
                    } else {
                        if (keysize.equalsIgnoreCase("128")) {
                            keySize = Integer.parseInt(keysize);
                        } else if (keysize.equalsIgnoreCase("192")) {
                            keySize = Integer.parseInt(keysize);
                        } else if (keysize.equalsIgnoreCase("256")) {
                            keySize = Integer.parseInt(keysize);
                        } else {
                            System.err.println("\nInvalid KEYSIZE for Default Algorithm (AES)... Acceptable values are 128 | 192 | 256\n");
                            err = true;
                        }
                    }
                } else {
                    if (algorithm.equalsIgnoreCase("AES")) {
                        algorithm = "AES";
                        if (keysize == null) {
                            keysize = "128";
                            keySize = Integer.parseInt(keysize);
                        } else {
                            if (keysize.equalsIgnoreCase("128")) {
                                keySize = Integer.parseInt(keysize);
                            } else if (keysize.equalsIgnoreCase("192")) {
                                keySize = Integer.parseInt(keysize);
                            } else if (keysize.equalsIgnoreCase("256")) {
                                keySize = Integer.parseInt(keysize);
                            } else {
                                System.err.println("\nInvalid KEYSIZE for AES... Acceptable values are 128 | 192 | 256\n");
                                err = true;
                            }
                        }
                    } else if (algorithm.equalsIgnoreCase("TDEA")) {
                        algorithm = "DESEDE";
                        if (keysize == null) {
                            keysize = "112";
                            keySize = Integer.parseInt(keysize);
                        } else {
                            if (keysize.equalsIgnoreCase("112")) {
                                keySize = Integer.parseInt(keysize);
                            } else if (keysize.equalsIgnoreCase("168")) {
                                keySize = Integer.parseInt(keysize);
                            } else {
                                System.err.println("\nInvalid KEYSIZE for TDEA... Acceptable values are 112 | 168\n");
                                err = true;
                            }
                        }
                    } else {
                        System.err.println("\nInvalid value for ALGORITHM... Acceptable values are AES | TDEA\n");
                        err = true;
                    }
                }

                // Check for target location
                if (propfile == null) {
                    System.err.println("\nNo Properties File Specified for Cloud Encrypt operation...\n");
                    err = true;
                }

                // Check for errors in arguments
                if (err == true) {
                    System.out.println(Usage);
                    return;
                }

                /*
                 888                        888      888                                             888
                 888                        888      888                                             888
                 888                        888      888                                             888
                 .d8888b 888  .d88b.  888  888  .d88888  .d88888  .d88b.   .d8888b 888d888 888  888 88888b.  888888
                 d88P"    888 d88""88b 888  888 d88" 888 d88" 888 d8P  Y8b d88P"    888P"   888  888 888 "88b 888
                 888      888 888  888 888  888 888  888 888  888 88888888 888      888     888  888 888  888 888
                 Y88b.    888 Y88..88P Y88b 888 Y88b 888 Y88b 888 Y8b.     Y88b.    888     Y88b 888 888 d88P Y88b.
                 "Y8888P 888  "Y88P"   "Y88888  "Y88888  "Y88888  "Y8888   "Y8888P 888      "Y88888 88888P"   "Y888
                 888 888
                 Y8b d88P 888
                 "Y88P"  888
                 */
            } else if (operation.equalsIgnoreCase("CD")) {
                if (args.length < 7) {
                    System.out.println("\nMissing arguments..\n");
                    System.out.println(Usage);
                    return;
                }

                sourcepath = args[5];
                if (sourcepath == null) {
                    System.out.println("\nFile name cannot be null\n");
                    System.out.println(Usage);
                    return;
                }
                if (destpath == null) {
                    destpath = "";
                }
                
                if(cl.hasOption("wp")){
                    wsprotocol = cl.getOptionValue("wp");
                    if(!wsprotocol.equalsIgnoreCase("REST") && !wsprotocol.equalsIgnoreCase("SOAP")){
                        System.out.println("Invalid WS Protocol..\n");
                        System.out.println(Usage);
                        return;
                    }
                }else{
                    System.out.println("\nMissing WS Protocol..\n");
                    System.out.println(Usage);
                    return;
                }
                
                if (cl.hasOption("dn")) {
                    userdn = cl.getOptionValue("dn");
                }
                if (cl.hasOption("lu")) {
                    ldapuser = cl.getOptionValue("lu");
                } else {
                    System.out.println("\nMissing lookup username..\n");
                    System.out.println(Usage);
                    return;
                }

                if (propfile == null) {
                    System.err.println("\nNo Properties File Specified for Cloud Decrypt operation...\n");
                    System.out.println(Usage);
                    return;
                }

                /*
                 d8b
                 Y8P

                 88888b.  888 88888b.   .d88b.
                 888 "88b 888 888 "88b d88P"88b
                 888  888 888 888  888 888  888
                 888 d88P 888 888  888 Y88b 888
                 88888P"  888 888  888  "Y88888
                 888                        888
                 888                   Y8b d88P
                 888                    "Y88P"
                 */
            } else if (operation.equalsIgnoreCase("P")) {
                if (args.length < 6) {
                    System.out.println("\nMissing arguments..\n");
                    System.out.println(Usage);
                    return;
                }
                if(cl.hasOption("wp")){
                    wsprotocol = cl.getOptionValue("wp");
                    if(!wsprotocol.equalsIgnoreCase("REST") && !wsprotocol.equalsIgnoreCase("SOAP")){
                        System.out.println("Invalid WS Protocol..\n");
                        System.out.println(Usage);
                        return;
                    }
                }else{
                    System.out.println("\nMissing WS Protocol..\n");
                    System.out.println(Usage);
                    return;
                }
            } else {
                System.err.println("\nInvalid Operation Specified...\n");
                System.out.println(Usage);
                return;
            }
        } else {
            System.out.println("\nMissing arguments..\n");
            System.out.println(Usage);
            return;
        }

        SKEEServlet port;
        try {
            // Set up the URL and webService variables
            String hosturl = hostport + Constants.SKEE_WSDL_SUFFIX;
            URL baseUrl = SKEEServlet_Service.class.getResource(".");
            URL url = new URL(baseUrl, hosturl);

            SKEEServlet_Service skceser = new SKEEServlet_Service(url);
            StreamingAttachmentFeature stf = new StreamingAttachmentFeature(null, true, 4000000L);
            port = skceser.getSKEEServletPort(new MTOMFeature(), stf);

            Map<String, Object> ctxt = ((BindingProvider) port).getRequestContext();
            // Enable HTTP chunking mode, otherwise HttpURLConnection buffers
            ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 20000);
        } catch (MalformedURLException ex) {
            System.out.println("Malformed hostport - " + hostport);
            return;
        } catch (WebServiceException ex) {
            System.err.println("\nIt appears that the site " + hostport + " is\n\n"
                    + "\t(1) either down;\n\t(2) is not accessible over the specified port; or\n\t"
                    + "(3) has a digital certificate that is not in your JVM's truststore.\n\n"
                    + "In case of (3), please include it in your JAVA_HOME/jre/lib/security/cacerts\n"
                    + "file with the [keytool -import] command before attempting this operation again.\n"
                    + "Please refer to the documentation on skceclient.jar at the above-mentioned URL\n"
                    + "on how to accomplish this.\n");
            java.util.logging.Logger.getLogger(EncryptionEngine.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        File fileobj = new File(sourcepath);
        DataHandler in = new DataHandler(new FileDataSource(fileobj));
        String inputfilename = fileobj.getName();
        System.out.println();
        
        // Call Encrypt
        if (operation.equalsIgnoreCase("E")) {
            if (wsprotocol.equalsIgnoreCase("SOAP")) {
                SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
                svcinfo.setDid(Integer.parseInt(did));
                svcinfo.setSvcusername(svcusername);
                svcinfo.setSvcpassword(svcpassword);

                SKCEInputFileInfoType fileinfo = new SKCEInputFileInfoType();
                fileinfo.setFilename(inputfilename);
                fileinfo.setFiledigest(null);
                fileinfo.setFiledigestalgo(null);

                SKCEEncryptionKeyInfoType encinfo = new SKCEEncryptionKeyInfoType();
                encinfo.setAlgorithm(algorithm);
                encinfo.setKeysize(keySize);
                encinfo.setUniquekey(uniqueKey);

                SKCEAuthorizationInfoType authzinfo = new SKCEAuthorizationInfoType();
                authzinfo.setUserdn(userdn);
                authzinfo.setUsername(ldapuser);
                authzinfo.setAuthgroups(ldapgroupinput);
                authzinfo.setRequiredauthorization(requiredauth);
                authzinfo.setPayload(null);

                System.out.println("Calling Encrypt at " + hostport + " ...");
                SkceReturnObject skcero = port.encrypt(svcinfo, fileinfo, in, encinfo, authzinfo);
                if (skcero != null) {
                    DataHandler out = skcero.getOutDataHandler();

                    // Compute target location
                    String targetlocation = destpath + inputfilename + ENCRYPT_EXT;

                    // Write the result file to target location
                    writeToTargetSDH(out, targetlocation);
                    StreamingDataHandler sdh = (StreamingDataHandler) out;
                    sdh.close();
                    // Check data integrity
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + skcero.getHash());
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (skcero.getHash().equals(hash)) {
                        System.out.println("SHA sum verified! Encryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                }
            } else {
                String svcinfo = Json.createObjectBuilder()
                        .add("did", did)
                        .add("svcusername", svcusername)
                        .add("svcpassword", svcpassword)
                        .build().toString();

                JsonObjectBuilder filejob = Json.createObjectBuilder();
                filejob.add("filename", inputfilename);
                if(filedigest == null){
                    filejob.addNull("filedigest");
                }else{
                    filejob.add("filedigest", filedigest);
                }
                if(filedigestalgo == null){
                    filejob.addNull("filedigestalgo");
                }else{
                    filejob.add("filedigestalgo", filedigestalgo);
                }
                String fileinfo = filejob
                        .build().toString();

                String encinfo = Json.createObjectBuilder()
                        .add("algorithm", algorithm)
                        .add("keysize", keySize)
                        .add("uniquekey", uniqueKey)
                        .build().toString();

                JsonObjectBuilder authzjob = Json.createObjectBuilder();
                authzjob.add("username", ldapuser);
                if(userdn == null){
                    authzjob.addNull("userdn");
                }else{
                    authzjob.add("userdn", userdn);
                }
                authzjob.add("authgroups", ldapgroupinput);
                authzjob.add("requiredauthorization", requiredauth);
                String authzinfo = authzjob
                        .build().toString();

                Logger logger = Logger.getLogger("skceclient");

                Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(feature).build();
//                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
                client.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
                
                final FileDataBodyPart filePart = new FileDataBodyPart("filedata", fileobj, MediaType.APPLICATION_OCTET_STREAM_TYPE);
                FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("svcinfo", svcinfo).field("fileinfo", fileinfo).field("encinfo", encinfo).field("authzinfo", authzinfo).bodyPart(filePart);

                System.out.println("Calling Encrypt at " + hostport + " ...");
                final WebTarget target = client.target(hostport+"/skee/rest/encrypt");
                final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));

                formDataMultiPart.close();
                multipart.close();
                if (response.getStatus() == 200) {

                    String headerJson = response.getHeaderString("skcero");
                    JsonReader jreader = Json.createReader(new StringReader(headerJson));
                    JsonObject jo = jreader.readObject();
                    String receivedHash = jo.getString("hash");
                    InputStream inputStream = response.readEntity(InputStream.class);
                    
                    // Compute target location
                    String targetlocation = destpath + inputfilename + ENCRYPT_EXT;
                    // Write the result file to target location
                    FileOutputStream fos = new FileOutputStream(targetlocation);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + receivedHash);
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (receivedHash.equals(hash)) {
                        System.out.println("SHA sum verified! Encryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                } else {
                    System.out.println("Response : " + response);
                    String result = response.readEntity(String.class);
                    System.out.println(result);
                }

//        filePart.cleanup();
                client.close();

            }
        } // Call Decrypt
        else if (operation.equalsIgnoreCase("D")) {
            if (wsprotocol.equalsIgnoreCase("SOAP")) {
                SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
                svcinfo.setDid(Integer.parseInt(did));
                svcinfo.setSvcusername(svcusername);
                svcinfo.setSvcpassword(svcpassword);

                SKCEInputFileInfoType fileinfo = new SKCEInputFileInfoType();
                fileinfo.setFilename(inputfilename);
                fileinfo.setFiledigest(null);
                fileinfo.setFiledigestalgo(null);

                SKCEAuthorizationInfoType authzinfo = new SKCEAuthorizationInfoType();
                authzinfo.setUserdn(userdn);
                authzinfo.setUsername(ldapuser);
                authzinfo.setPayload(null);

                System.out.println("Calling Decrypt at " + hostport + " ... \n");
                SkceReturnObject skcero = port.decrypt(svcinfo, fileinfo, in, authzinfo);
                if (skcero != null) {
                    DataHandler out = skcero.getOutDataHandler();

                    // Compute target location
                    String outputfilename = removeExt(inputfilename, "\\.");
                    String targetlocation = destpath + outputfilename;

                    // Write the result file to target location
                    writeToTargetSDH(out, targetlocation);
                    StreamingDataHandler sdh = (StreamingDataHandler) out;
                    sdh.close();
                    // Check data integrity
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + skcero.getHash());
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (skcero.getHash().equals(hash)) {
                        System.out.println("SHA sum verified! Decryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                }
            } else {
                String svcinfo = Json.createObjectBuilder()
                        .add("did", did)
                        .add("svcusername", svcusername)
                        .add("svcpassword", svcpassword)
                        .build().toString();

                JsonObjectBuilder filejob = Json.createObjectBuilder();
                filejob.add("filename", inputfilename);
                if (filedigest == null) {
                    filejob.addNull("filedigest");
                } else {
                    filejob.add("filedigest", filedigest);
                }
                if (filedigestalgo == null) {
                    filejob.addNull("filedigestalgo");
                } else {
                    filejob.add("filedigestalgo", filedigestalgo);
                }
                String fileinfo = filejob
                        .build().toString();

                JsonObjectBuilder authzjob = Json.createObjectBuilder();
                authzjob.add("username", ldapuser);
                if (userdn == null) {
                    authzjob.addNull("userdn");
                } else {
                    authzjob.add("userdn", userdn);
                }
                if(ldapgroupinput == null){
                    authzjob.addNull("authgroups");
                }else{
                    authzjob.add("authgroups", ldapgroupinput);
                }
                authzjob.add("requiredauthorization", requiredauth);
                String authzinfo = authzjob
                        .build().toString();
                
                Logger logger = Logger.getLogger("skceclient");

                Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(feature).build();
//                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
                client.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
                
                final FileDataBodyPart filePart = new FileDataBodyPart("filedata", fileobj, MediaType.APPLICATION_OCTET_STREAM_TYPE);
                FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("svcinfo", svcinfo).field("fileinfo", fileinfo).field("authzinfo", authzinfo).bodyPart(filePart);

                System.out.println("Calling Decrypt at " + hostport + " ... \n");
                final WebTarget target = client.target(hostport+"/skee/rest/decrypt");
                final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));

                formDataMultiPart.close();
                multipart.close();
                if (response.getStatus() == 200) {

                    String headerJson = response.getHeaderString("skcero");
                    JsonReader jreader = Json.createReader(new StringReader(headerJson));
                    JsonObject jo = jreader.readObject();
                    String receivedHash = jo.getString("hash");
                    InputStream inputStream = response.readEntity(InputStream.class);
                    
                    // Compute target location
                    // Compute target location
                    String outputfilename = removeExt(inputfilename, "\\.");
                    String targetlocation = destpath + outputfilename;
                    // Write the result file to target location
                    FileOutputStream fos = new FileOutputStream(targetlocation);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + receivedHash);
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (receivedHash.equals(hash)) {
                        System.out.println("SHA sum verified! Encryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                } else {
                    System.out.println("Response : " + response);
                    String result = response.readEntity(String.class);
                    System.out.println(result);
                }

//        filePart.cleanup();
                client.close();

            }
        } // Call Authorized Decrypt
        else if (operation.equalsIgnoreCase("AD")) {
            if (wsprotocol.equalsIgnoreCase("SOAP")) {
                SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
                svcinfo.setDid(Integer.parseInt(did));
                svcinfo.setSvcusername(svcusername);
                svcinfo.setSvcpassword(svcpassword);

                SKCEInputFileInfoType fileinfo = new SKCEInputFileInfoType();
                fileinfo.setFilename(inputfilename);
                fileinfo.setFiledigest(null);
                fileinfo.setFiledigestalgo(null);

                SKCEAuthorizationInfoType authzinfo = new SKCEAuthorizationInfoType();
                authzinfo.setUserdn(userdn);
                authzinfo.setUsername(ldapuser);

                //  Generate fido response (payload)
                String payload = getFidoResponse(did, hostport, "svcfidouser", "Abcd1234!", "U2F_V2", ldapuser, origin, auth_counter);
                if (payload == null || payload.trim().isEmpty()) {
                    System.out.println("Error generating fido response for authorization");
                    return;
                }

                authzinfo.setPayload(payload);

                System.out.println("Calling (Authorized) Decrypt at " + hostport + " ... \n");
                SkceReturnObject skcero = port.decrypt(svcinfo, fileinfo, in, authzinfo);
                if (skcero != null) {
                    DataHandler out = skcero.getOutDataHandler();

                    // Compute target location
                    String outputfilename = removeExt(inputfilename, "\\.");
                    String targetlocation = destpath + outputfilename;

                    // Write the result file to target location
                    writeToTargetSDH(out, targetlocation);
                    StreamingDataHandler sdh = (StreamingDataHandler) out;
                    sdh.close();
                    // Check data integrity
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + skcero.getHash());
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (skcero.getHash().equals(hash)) {
                        System.out.println("SHA sum verified! Decryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                }
            } else {
                String svcinfo = Json.createObjectBuilder()
                        .add("did", did)
                        .add("svcusername", svcusername)
                        .add("svcpassword", svcpassword)
                        .build().toString();

                JsonObjectBuilder filejob = Json.createObjectBuilder();
                filejob.add("filename", inputfilename);
                if (filedigest == null) {
                    filejob.addNull("filedigest");
                } else {
                    filejob.add("filedigest", filedigest);
                }
                if (filedigestalgo == null) {
                    filejob.addNull("filedigestalgo");
                } else {
                    filejob.add("filedigestalgo", filedigestalgo);
                }
                String fileinfo = filejob
                        .build().toString();

                JsonObjectBuilder authzjob = Json.createObjectBuilder();
                authzjob.add("username", ldapuser);
                if (userdn == null) {
                    authzjob.addNull("userdn");
                } else {
                    authzjob.add("userdn", userdn);
                }
                if(ldapgroupinput == null){
                    authzjob.addNull("authgroups");
                }else{
                    authzjob.add("authgroups", ldapgroupinput);
                }
                authzjob.add("requiredauthorization", requiredauth);
                
                //  Generate fido response (payload)
                String payload = getFidoResponse(did, hostport, "svcfidouser", "Abcd1234!", "U2F_V2", ldapuser, origin, auth_counter);
                if (payload == null || payload.trim().isEmpty()) {
                    System.out.println("Error generating fido response for authorization");
                    return;
                }

                authzjob.add("payload",payload);
                String authzinfo = authzjob
                        .build().toString();
                
                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
                
                final FileDataBodyPart filePart = new FileDataBodyPart("filedata", fileobj, MediaType.APPLICATION_OCTET_STREAM_TYPE);
                FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("svcinfo", svcinfo).field("fileinfo", fileinfo).field("authzinfo", authzinfo).bodyPart(filePart);

                System.out.println("Calling (Authorized) Decrypt at " + hostport + " ... \n");
                final WebTarget target = client.target(hostport+"/skee/rest/decrypt");
                final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));

                formDataMultiPart.close();
                multipart.close();
                if (response.getStatus() == 200) {

                    String headerJson = response.getHeaderString("skcero");
                    JsonReader jreader = Json.createReader(new StringReader(headerJson));
                    JsonObject jo = jreader.readObject();
                    String receivedHash = jo.getString("hash");
                    InputStream inputStream = response.readEntity(InputStream.class);
                    
                    // Compute target location
                    // Compute target location
                    String outputfilename = removeExt(inputfilename, "\\.");
                    String targetlocation = destpath + outputfilename;
                    // Write the result file to target location
                    FileOutputStream fos = new FileOutputStream(targetlocation);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + receivedHash);
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (receivedHash.equals(hash)) {
                        System.out.println("SHA sum verified! Encryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                } else {
                    System.out.println("Response : " + response);
                    String result = response.readEntity(String.class);
                    System.out.println(result);
                }

//        filePart.cleanup();
                client.close();

            }
        } // Call Enrypt to Cloud
        else if (operation.equalsIgnoreCase("CE")) {
                Properties props = new Properties();
                props.load(new FileInputStream(propfile));
                String cloudtype = props.getProperty("skceclient.property.cloudtype");
                String cloudcontainer = props.getProperty("skceclient.property.cloudcontainer");
                String cloudname = props.getProperty("skceclient.property.cloudname");
                String accesskey = props.getProperty("skceclient.property.accesskey");
                String secretkey = props.getProperty("skceclient.property.secretkey");
                String cloudcredentialid = props.getProperty("skceclient.property.cloudcredentialid");
            if (wsprotocol.equalsIgnoreCase("SOAP")) {
                SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
                svcinfo.setDid(Integer.parseInt(did));
                svcinfo.setSvcusername(svcusername);
                svcinfo.setSvcpassword(svcpassword);

                SKCEInputFileInfoType fileinfo = new SKCEInputFileInfoType();
                fileinfo.setFilename(inputfilename);
                fileinfo.setFiledigest(null);
                fileinfo.setFiledigestalgo(null);

                SKCEEncryptionKeyInfoType encinfo = new SKCEEncryptionKeyInfoType();
                encinfo.setAlgorithm(algorithm);
                encinfo.setKeysize(keySize);
                encinfo.setUniquekey(uniqueKey);

                SKCEAuthorizationInfoType authzinfo = new SKCEAuthorizationInfoType();
                authzinfo.setUserdn(userdn);
                authzinfo.setUsername(ldapuser);
                authzinfo.setAuthgroups(ldapgroupinput);
                authzinfo.setRequiredauthorization(requiredauth);
                authzinfo.setPayload(null);

                SKCEStorageInfoType storageinfo = new SKCEStorageInfoType();
                storageinfo.setStoretype(null);
                storageinfo.setCloudtype(cloudtype);
                storageinfo.setCloudcontainer(cloudcontainer);
                storageinfo.setCloudcredentialid(cloudcredentialid);
                storageinfo.setCloudname(cloudname);
                storageinfo.setAccesskey(accesskey);
                storageinfo.setSecretkey(secretkey);

                System.out.println("Calling Encrypt to Cloud at " + hostport + " ... \n");
                SkceReturnObject skcero = port.encryptToCloud(svcinfo, fileinfo, in, encinfo, authzinfo, storageinfo);

                System.out.println("Response              : " + skcero.getResponse());
                System.out.println("SHA256 message digest : " + skcero.getHash());

            } else {

                String svcinfo = Json.createObjectBuilder()
                        .add("did", did)
                        .add("svcusername", svcusername)
                        .add("svcpassword", svcpassword)
                        .build().toString();

                JsonObjectBuilder filejob = Json.createObjectBuilder();
                filejob.add("filename", inputfilename);
                if(filedigest == null){
                    filejob.addNull("filedigest");
                }else{
                    filejob.add("filedigest", filedigest);
                }
                if(filedigestalgo == null){
                    filejob.addNull("filedigestalgo");
                }else{
                    filejob.add("filedigestalgo", filedigestalgo);
                }
                String fileinfo = filejob
                        .build().toString();

                String encinfo = Json.createObjectBuilder()
                        .add("algorithm", algorithm)
                        .add("keysize", keySize)
                        .add("uniquekey", uniqueKey)
                        .build().toString();

                JsonObjectBuilder authzjob = Json.createObjectBuilder();
                authzjob.add("username", ldapuser);
                if(userdn == null){
                    authzjob.addNull("userdn");
                }else{
                    authzjob.add("userdn", userdn);
                }
                authzjob.add("authgroups", ldapgroupinput);
                authzjob.add("requiredauthorization", requiredauth);
                String authzinfo = authzjob
                        .build().toString();
                
                JsonObjectBuilder storeagejob = Json.createObjectBuilder();
                storeagejob.add("cloudtype", cloudtype);
                storeagejob.add("cloudname", cloudname);
                storeagejob.add("cloudcontainer", cloudcontainer);
                storeagejob.add("accesskey", accesskey);
                storeagejob.add("secretkey", secretkey);
                if(storetype == null){
                    storeagejob.addNull("storetype");
                }else{
                    storeagejob.add("storetype", storetype);
                }
                
                if(cloudcredentialid == null){
                    storeagejob.addNull("cloudcredentialid");
                }else{
                    storeagejob.add("cloudcredentialid", cloudcredentialid);
                }
                
                String storeageinfo = storeagejob.build().toString();

                Logger logger = Logger.getLogger("skceclient");

                Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(feature).build();
//                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
                client.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
                
                final FileDataBodyPart filePart = new FileDataBodyPart("filedata", fileobj, MediaType.APPLICATION_OCTET_STREAM_TYPE);
                FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("svcinfo", svcinfo).field("fileinfo", fileinfo)
                        .field("encinfo", encinfo).field("authzinfo", authzinfo).field("storageinfo", storeageinfo).bodyPart(filePart);

                 System.out.println("Calling Encrypt to Cloud at " + hostport + " ... \n");  
                 final WebTarget target = client.target(hostport+"/skee/rest/encrypttocloud");
                final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));

                formDataMultiPart.close();
                multipart.close();
                if (response.getStatus() == 200) {

                    String headerJson = response.getHeaderString("skcero");
                    JsonReader jreader = Json.createReader(new StringReader(headerJson));
                    JsonObject jo = jreader.readObject();
                    String receivedHash = jo.getString("hash");
                    String receivedResponse = jo.getString("response");
                    
                    
                    System.out.println("Response              : " + receivedResponse);
                System.out.println("SHA256 message digest : " + receivedHash);
                    
                } else {
                    System.out.println("Response : " + response);
                    String result = response.readEntity(String.class);
                    System.out.println(result);
                }

                client.close();

            }
        } //Call Decrypt From Cloud
        else if (operation.equalsIgnoreCase("CD")) {
            Properties props = new Properties();
                props.load(new FileInputStream(propfile));
                String cloudtype = props.getProperty("skceclient.property.cloudtype");
                String cloudcontainer = props.getProperty("skceclient.property.cloudcontainer");
                String cloudname = props.getProperty("skceclient.property.cloudname");
                String accesskey = props.getProperty("skceclient.property.accesskey");
                String secretkey = props.getProperty("skceclient.property.secretkey");
                String cloudcredentialid = props.getProperty("skceclient.property.cloudcredentialid");
            if (wsprotocol.equalsIgnoreCase("SOAP")) {

                SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
                svcinfo.setDid(Integer.parseInt(did));
                svcinfo.setSvcusername(svcusername);
                svcinfo.setSvcpassword(svcpassword);

                SKCEInputFileInfoType fileinfo = new SKCEInputFileInfoType();
                fileinfo.setFilename(inputfilename);
                fileinfo.setFiledigest(null);
                fileinfo.setFiledigestalgo(null);

                SKCEAuthorizationInfoType authzinfo = new SKCEAuthorizationInfoType();
                authzinfo.setUserdn(userdn);
                authzinfo.setUsername(ldapuser);
                authzinfo.setAuthgroups(ldapgroupinput);
                authzinfo.setRequiredauthorization(requiredauth);
                authzinfo.setPayload(null);

                SKCEStorageInfoType storageinfo = new SKCEStorageInfoType();
                storageinfo.setStoretype(null);
                storageinfo.setCloudtype(cloudtype);
                storageinfo.setCloudcontainer(cloudcontainer);
                storageinfo.setCloudcredentialid(cloudcredentialid);
                storageinfo.setCloudname(cloudname);
                storageinfo.setAccesskey(accesskey);
                storageinfo.setSecretkey(secretkey);

                // Call webservice to download/decrypt the file from Cloud
                System.out.println("Calling Decrypt from Cloud at " + hostport + " ... \n");
                SkceReturnObject skcero = port.decryptFromCloud(svcinfo, fileinfo, authzinfo, storageinfo);
                if (skcero != null) {
                    DataHandler out = skcero.getOutDataHandler();

                    // Compute target location
                    String outputFileName = removeExt(inputfilename, "\\.");
                    String targetLocation = destpath + outputFileName;

                    // Write the result file to target location
                    writeToTargetSDH(out, targetLocation);
                    StreamingDataHandler sdh = (StreamingDataHandler) out;
                    sdh.close();
                    // Check data integrity
                    String hash = getSHA256MDForFileContent(targetLocation);
                    System.out.println("SHA256 message digest received   : " + skcero.getHash());
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (skcero.getHash().equals(hash)) {
                        System.out.println("SHA sum verified! Encryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                }
            } else {
                String svcinfo = Json.createObjectBuilder()
                        .add("did", did)
                        .add("svcusername", svcusername)
                        .add("svcpassword", svcpassword)
                        .build().toString();

                JsonObjectBuilder filejob = Json.createObjectBuilder();
                filejob.add("filename", inputfilename);
                if (filedigest == null) {
                    filejob.addNull("filedigest");
                } else {
                    filejob.add("filedigest", filedigest);
                }
                if (filedigestalgo == null) {
                    filejob.addNull("filedigestalgo");
                } else {
                    filejob.add("filedigestalgo", filedigestalgo);
                }
                String fileinfo = filejob
                        .build().toString();

                JsonObjectBuilder authzjob = Json.createObjectBuilder();
                authzjob.add("username", ldapuser);
                if (userdn == null) {
                    authzjob.addNull("userdn");
                } else {
                    authzjob.add("userdn", userdn);
                }
                if (ldapgroupinput == null) {
                    authzjob.addNull("authgroups");
                } else {
                    authzjob.add("authgroups", ldapgroupinput);
                }
                authzjob.add("requiredauthorization", requiredauth);
                String authzinfo = authzjob
                        .build().toString();
                
                JsonObjectBuilder storeagejob = Json.createObjectBuilder();
                storeagejob.add("cloudtype", cloudtype);
                storeagejob.add("cloudname", cloudname);
                storeagejob.add("cloudcontainer", cloudcontainer);
                storeagejob.add("accesskey", accesskey);
                storeagejob.add("secretkey", secretkey);
                if(storetype == null){
                    storeagejob.addNull("storetype");
                }else{
                    storeagejob.add("storetype", storetype);
                }
                
                if(cloudcredentialid == null){
                    storeagejob.addNull("cloudcredentialid");
                }else{
                    storeagejob.add("cloudcredentialid", cloudcredentialid);
                }
                
                String storeageinfo = storeagejob.build().toString();

                Logger logger = Logger.getLogger("skceclient");

                Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(feature).build();
//                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
                client.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);

                final FileDataBodyPart filePart = new FileDataBodyPart("filedata", fileobj, MediaType.APPLICATION_OCTET_STREAM_TYPE);
                FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("svcinfo", svcinfo).field("fileinfo", fileinfo).field("authzinfo", authzinfo).field("storageinfo", storeageinfo).bodyPart(filePart);
                final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("svcinfo", svcinfo).field("fileinfo", fileinfo).field("authzinfo", authzinfo).field("storageinfo", storeageinfo);

                System.out.println("Calling Decrypt from Cloud at " + hostport + " ... \n");
                final WebTarget target = client.target(hostport + "/skee/rest/decryptfromcloud");
                final Response response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));

                formDataMultiPart.close();
                multipart.close();
                if (response.getStatus() == 200) {

                    String headerJson = response.getHeaderString("skcero");
                    JsonReader jreader = Json.createReader(new StringReader(headerJson));
                    JsonObject jo = jreader.readObject();
                    String receivedHash = jo.getString("hash");
                    InputStream inputStream = response.readEntity(InputStream.class);

                    // Compute target location
                    // Compute target location
                    String outputfilename = removeExt(inputfilename, "\\.");
                    String targetlocation = destpath + outputfilename;
                    // Write the result file to target location
                    FileOutputStream fos = new FileOutputStream(targetlocation);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    String hash = getSHA256MDForFileContent(targetlocation);
                    System.out.println("SHA256 message digest received   : " + receivedHash);
                    System.out.println("SHA256 message digest calculated : " + hash);
                    if (receivedHash.equals(hash)) {
                        System.out.println("SHA sum verified! Encryption successful");
                    } else {
                        System.out.println("SHA sum is NOT verified!");
                    }
                } else {
                    System.out.println("Response : " + response);
                    String result = response.readEntity(String.class);
                    System.out.println(result);
                }

//        filePart.cleanup();
                client.close();

            }
        } // Call Ping
        else if (operation.equalsIgnoreCase("P")) {
            if (wsprotocol.equalsIgnoreCase("SOAP")) {
                SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
                svcinfo.setDid(Integer.parseInt(did));
                svcinfo.setSvcusername(svcusername);
                svcinfo.setSvcpassword(svcpassword);

                System.out.println("Calling Ping at " + hostport + " ... \n");
                SkceReturnObject skcero = port.ping(svcinfo);
                if (skcero != null) {
                    // Print the result
                    System.out.println();
                    System.out.println("Response received   : \n\n" + skcero.getResponse());
                }
            } else {
                String svcinfo = Json.createObjectBuilder()
                        .add("did", did)
                        .add("svcusername", svcusername)
                        .add("svcpassword", svcpassword)
                        .build().toString();
                
                Logger logger = Logger.getLogger("skceclient");

                Feature feature = new LoggingFeature(logger, Level.INFO, null, null);

                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).register(feature).build();
                
//                final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                client.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
                client.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);

                final FormDataMultiPart multipart;
                final Response response;
                try (FormDataMultiPart formDataMultiPart = new FormDataMultiPart()) {
                    multipart = formDataMultiPart.field("svcinfo", svcinfo);
                    System.out.println("Calling Ping at " + hostport + " ... \n");
                    final WebTarget target = client.target(hostport + "/skee/rest/ping");
                    response = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
                }
                multipart.close();
                if (response.getStatus() == 200) {

                    String headerJson = response.getHeaderString("skcero");
                    JsonReader jreader = Json.createReader(new StringReader(headerJson));
                    JsonObject jo = jreader.readObject();
                    System.out.println();
                    System.out.println("Response received   : \n\n" + jo.getString("response"));

                } else {
                    System.out.println("Response : " + response);
                    String result = response.readEntity(String.class);
                    System.out.println(result);
                }

                client.close();

            }
        }else {
            System.err.println("Invalid Operation Specified...\n");
            System.out.println(Usage);
            return;
        }
        System.out.println("Done!");
    }
    
    /**
     * 
     * @param hostport
     * @param svcuser
     * @param svcpass
     * @param fidoprotocol
     * @param username
     * @param origin
     * @param auth_counter
     * @return 
     */
    public static String getFidoResponse(String did, String hostport, String svcuser, String svcpass, String fidoprotocol, String username, String origin,
                                int auth_counter) {
        
//  Instance an SKFE service client.
        HttpURLConnection conn = null;
        String response = "" ;

        try {
            System.out.println("Authorization test");
            System.out.println("*******************************");

            //  Build svcinfo
            String svcinfo = Json.createObjectBuilder()
                    .add("did", did)
                    .add("svcusername", svcuser)
                    .add("svcpassword", svcpass)
                    .add("protocol", fidoprotocol)
                    .build().toString();
            
            //  Build payload
            String payload = Json.createObjectBuilder()
                    .add(Constants.JSON_KEY_SERVLET_INPUT_USERNAME, username)
                    .build().toString();
            //  create a client object
            //  Build a URI with all needed query parameters
            URIBuilder uribuilder = new URIBuilder(hostport + Constants.REST_SUFFIX + Constants.PRE_AUTHZ_ENDPOINT);
            String urlstring = uribuilder.toString();

            // Create HTTP URL connection with parameters; accept JSON output
            System.out.println("Calling preauthorize: " + urlstring);
            URL url = new URL(urlstring);
            conn = (HttpURLConnection) url.openConnection();
            // Set connection properties
            conn.setReadTimeout(Constants.TIMEOUT_VALUE);
            conn.setConnectTimeout(Constants.TIMEOUT_VALUE);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // Write out form parameters
            String formparams = "svcinfo=" + svcinfo + "&payload=" + payload;
            conn.setFixedLengthStreamingMode(formparams.getBytes().length);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(formparams);
            out.close();
            
            // Error from SKCE server
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
            }

            // Read FSUT response
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                response = response + output;
            }

            System.out.println(" Response : " + response);

            //  Build a json object out of response
            StringReader s = new StringReader(response);
            JsonReader jsonReader = Json.createReader(s);
            JsonObject responseJSON = jsonReader.readObject();
            jsonReader.close();

            //  Check to see if there is any
            try {
                String error = responseJSON.getString("Error");
                if (error != null && !error.equalsIgnoreCase("")) {
                    System.out.println("*******************************");
                    return " Error during preauth : " + error;
                }
            } catch (Exception ex) {
                //  continue since there is no error
            }

            System.out.println("\n Authorization Parameters:\n");
            String challenge = responseJSON.getJsonObject("Challenge").toString();
            s = new StringReader(challenge);

            jsonReader = Json.createReader(s);
            JsonObject resJsonObj = jsonReader.readObject();
            jsonReader.close();

            String appid ;
            try{
                appid = resJsonObj.getString("appId");
            } catch (NullPointerException ex) {
                appid = null;
            }
            String nonce = resJsonObj.getString("challenge");
            JsonArray jarray = resJsonObj.getJsonArray("registeredKeys");
            String s2 = jarray.getJsonObject(0).toString();
            s = new StringReader(s2);
            JsonParserFactory factory = Json.createParserFactory(null);
            JsonParser parser = factory.createParser(s);
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

            System.out.println("\n Pre-Authorization Complete.");
            System.out.println("\n Generating Authorization response...\n");
            JsonObject input = null;
            try {
                input = FIDOU2FTokenSimulator.generateAuthenticationResponse(appid, nonce, s2, origin, auth_counter,true);
            } catch (NoSuchAlgorithmException |
                    NoSuchProviderException |
                    UnsupportedEncodingException |
                    InvalidParameterSpecException |
                    DecoderException |
                    NoSuchPaddingException |
                    InvalidKeyException |
                    InvalidAlgorithmParameterException |
                    ShortBufferException |
                    IllegalBlockSizeException |
                    BadPaddingException |
                    InvalidKeySpecException |
                    SignatureException ex) {
                System.out.println("\n Exception : " + ex.getLocalizedMessage());
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

            System.out.println("\n Finished Generating Authorization Response.");
            System.out.println("\n Authorization ...");

            //  test register
            JsonObject auth_metadata = javax.json.Json.createObjectBuilder()
                    .add("version", "1.0") // ALWAYS since this is just the first revision of the code
                    .add("last_used_location", "Bangalore, India").
                    build();
            payload = Json.createObjectBuilder().add(Constants.JSON_KEY_SERVLET_INPUT_METADATA, auth_metadata).add(Constants.JSON_KEY_SERVLET_INPUT_RESPONSE, input).build().toString();
            
            return payload;
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(RestFidoU2FAuthorize.class.getName()).log(Level.SEVERE, null, ex);
            //  continue since there is no error
        } catch (MalformedURLException ex) {
            Logger.getLogger(RestFidoU2FAuthorize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestFidoU2FAuthorize.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        
        return null;
    }

    public static String getSHA256MDForFileContent(String filelocation) {
        if (filelocation == null) {
            return "";
        }

        String hash = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            FileInputStream fis = new FileInputStream(filelocation);
            byte[] dataBytes = new byte[1024];
            int nread;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            // Generate digest
            byte[] digestbytes = md.digest();

            // Base64-encode digest and use it
            hash = bytesToHex(digestbytes);

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("NoSuchAlgorithmException : " + ex.getLocalizedMessage());
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException : " + ex.getLocalizedMessage());
        } catch (IOException ex) {
            System.out.println("IOException : " + ex.getLocalizedMessage());
        }

        return hash;
    }

    public static String bytesToHex(byte[] bytes) {
        // For converting byte array to hex string
        String digits = "0123456789abcdef";

        int length = bytes.length;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i != length; i++) {
            int v = bytes[i] & 0xff;

            sb.append(digits.charAt(v >> 4));
            sb.append(digits.charAt(v & 0xf));
        }
        return sb.toString();
    }

    // This function writes the output to the desired location
    private static void writeToTargetSDH(DataHandler data, String targetLocation)
            throws Exception {
        // Local variables

        if (data == null) {
            System.out.println("Datahandler received from SKCE is null" + data);
        } else {
            FileOutputStream fos = new FileOutputStream(targetLocation);
            data.writeTo(fos);
            fos.close();
        }
    }

    // This Function removes extension from a fileName specified
    private static String removeExt(String inputFileName, String regex) {
        String[] result = inputFileName.split(regex);
        int tokens = result.length;
        String outputFileName = "";
        if (tokens > 0) {
            outputFileName = result[0];
            for (int i = 1; i < tokens - 1; i++) {
                outputFileName = outputFileName + "." + result[i];
            }
        }
        return outputFileName;
    }
    public final static String ENCRYPT_EXT = ".zenc";
    public static final String FS = System.getProperty("file.separator");
}
