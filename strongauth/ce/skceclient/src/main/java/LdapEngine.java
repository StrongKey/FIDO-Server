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
 * LdapEngine class takes care of testing various web-service operations
 * that are part of ldape (Ldap engine); which is a sub-component
 * of skce (strongkey cryptoengine)
 * 
 * The operations that could be tested using this class are
 * 
 * 1. Authenticate an ldap user
 * 2. Authorize an ldap user
 * 3. Get information (attributes key-value pairs)
 * 4. Update/Delete an attribute
 * 
 */

import com.strongauth.ldape.soapstubs.LDAPEServlet;
import com.strongauth.ldape.soapstubs.LDAPEServlet_Service;
import com.strongauth.ldape.soapstubs.SKCEException_Exception;
import com.strongauth.ldape.soapstubs.SKCEServiceInfoType;
import com.strongauth.ldape.soapstubs.SkceReturnObject;
import com.strongauth.skceclient.common.Constants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.xml.ws.WebServiceException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class LdapEngine {
    
    public static void main(String[] args) throws SKCEException_Exception, IOException {

        // Get the skceclient version info.  CANNOT use new FileInputStream
        // as this is loaded from a different classloader.  Properties file
        // must be under src/main/resources
        Properties vprops = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        vprops.load(loader.getResourceAsStream("skceclient.properties"));
        String version = vprops.getProperty("skceclient.property.version");
        
        System.out.println();
        System.out.println("skceclient.jar " + version);
        System.out.println("Copyright (c) 2001-2018 StrongAuth, Inc. All rights reserved.");
        System.out.println();
        
        // Declare Variables
        String hostport;
        String operation;
        int did;
        String svcusername = null;
        String svcpassword = null;
        String username = null;
        String password = null;
        String skceoperation = "";
        String key = "";
        String value = "";
        String searchkey = "", searchvalue = "", searchdn = null;
        boolean deletion = false;

        String Usage ="Usage: java -cp skceclient.jar LdapEngine https://<host:port> <did> AT <username> <password>  \n"
                    + "       java -cp skceclient.jar LdapEngine https://<host:port> <did> AZ <username> <password> <operation> \n"
//                    + "       java -cp skceclient.jar LdapEngine https://<host:port> <did> GU <svcusername> <svcpassword> <username> \n"
                    + "       java -cp skceclient.jar LdapEngine https://<host:port> <did> GM <svcusername> <svcpassword> <basedn> <searchkey> <searchvalue> \n"
                    + "       java -cp skceclient.jar LdapEngine https://<host:port> <did> AU <svcusername> <svcpassword> <username> <password>\n"
                    + "       java -cp skceclient.jar LdapEngine https://<host:port> <did> UU <svcusername> <svcpassword> <username> <password> <key> <value> [deletion]\n\n"
                    + "Acceptable Values: \n"
                    + "         hostport    : host url where skce is listening;\n"
                    + "                         format  : http://<FQDN>:<non-ssl-portnumber> or \n"
                    + "                                   https://<FQDN>:<ssl-portnumber>\n"
                    + "                         example : https://fidodemo.strongauth.com:8181\n\n"
                    + "         did         : Unique domain identifier that belongs to SKCE\n"
                    + "         command     : AT (authenticate) | AZ (authorize) | GU (getuserinfo) | AU (adduser) | UU (updateuser)\n"
                    + "         svcusername : service credential for SKFE\n"
                    + "         svcpassword : password for the service credential for SKFE\n"
                    + "         searchkey   : Filter to be applied during LDAPSearch\n"
                    + "         searchvalue : Value of the filter applied during LDAPSearch\n"
                    + "         username    : LDAP user to be authenticated\n"
                    + "         password    : LDAP user's password\n"
                    + "         operation   : ADM | CMV | DEC | ENC | LDKY | RMKY | SIGN\n"
                    + "         key         : LDAP attribute to be modified\n"
                    + "         value       : New value, when being updated; old value for delete operation\n"
                    + "         deletion    : Indicator to delete the attribute (Y/N/T/F - default is to NOT delete)\n";

        // Initialize a Command Line Parser
        try {
            Options opt = new Options();

            opt.addOption("h", false, "Print help for this application");

            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(opt, args);

            if (cl.hasOption('h')) {
                System.out.println(Usage);
                return;
            }
        } catch (ParseException e) {
            System.out.println("Invalid argument or value Passed... Value for any argument cannot be null\n");
            System.out.println(Usage);
            return;
        }

        /*
         * Initialize parameters to be passed to the web service calls
         * Parsing Arguments
         */
        if (args.length > 3) {
            hostport = args[0];
            String domainid = args[1];
            did = Integer.parseInt(domainid);
            operation = args[2];
            
            if ( !operation.equalsIgnoreCase("AT") &&
                !operation.equalsIgnoreCase("AZ") &&
//                !operation.equalsIgnoreCase("GU") &&
                !operation.equalsIgnoreCase("GM") &&
                !operation.equalsIgnoreCase("AU") &&
                !operation.equalsIgnoreCase("UU") ) {
                
                System.err.println("Invalid Operation Specified...\n");
                System.out.println(Usage);
                return;
            }

            if (operation.equalsIgnoreCase("AT")) {
                if ( args.length != 5 ) {
                    System.out.println(Usage);
                    return;
                }
                
                username = args[3];
                password = args[4];
            } else if (operation.equalsIgnoreCase("AZ")) {
                if ( args.length != 6 ) {
                    System.out.println(Usage);
                    return;
                }
                
                username = args[3];
                password = args[4];
                skceoperation = args[5];
//            } else if (operation.equalsIgnoreCase("GU")) {
//                if ( args.length != 6 ) {
//                    System.out.println(Usage);
//                    return;
//                }
//                
//                svcusername = args[3];
//                svcpassword = args[4];
//                username = args[5];
            } else if (operation.equalsIgnoreCase("GM")) {
                if ( args.length < 7 || args.length > 8) {
                    System.out.println(Usage);
                    return;
                }
                
                svcusername = args[3];
                svcpassword = args[4];
                if(args.length == 7){
                    searchkey = args[5];
                    searchvalue = args[6];
                }else{
                    searchdn = args[5];
                    searchkey = args[6];
                    searchvalue = args[7];
                }
                
            } else if (operation.equalsIgnoreCase("AU")) {
                if ( args.length != 7 ) {
                    System.out.println(Usage);
                    return;
                }
                
                svcusername = args[3];
                svcpassword = args[4];
                username = args[5];
                password = args[6];
            } else if (operation.equalsIgnoreCase("UU")) {
                if ( args.length < 9 || args.length > 10) {
                    System.out.println(Usage);
                    return;
                }
                
                svcusername = args[3];
                svcpassword = args[4];
                username = args[5];
                password = args[6];
                key = args[7];
                value = args[8];
                
                if ( args.length == 10 ) {
                    String delflag = args[9];
                    if ( !delflag.equalsIgnoreCase("Y") && !delflag.equalsIgnoreCase("N")
                         && !delflag.equalsIgnoreCase("T") && !delflag.equalsIgnoreCase("F")) {
                        System.err.println("Invalid value for deletion flag...\n");
                        System.out.println(Usage);
                        return; 
                    }
                    
                    if ( delflag.equalsIgnoreCase("Y") || delflag.equalsIgnoreCase("T") ) {
                        deletion = true;
                    }
                }
            }
        } else {
            System.out.println(Usage);
            return;
        }
    
        LDAPEServlet port;
        try {
            // Set up the URL and webService variables
            String hosturl = hostport + Constants.LDAPE_WSDL_SUFFIX;
            URL url = new URL(hosturl);

            LDAPEServlet_Service ldapeser = new LDAPEServlet_Service(url);
            port = ldapeser.getLDAPEServletPort();
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
            return;
        }
        
        //  Build serviceinfo object
        SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
        svcinfo.setDid(did);
        if (svcusername!=null && svcpassword!=null) {
            svcinfo.setSvcusername(svcusername);
            svcinfo.setSvcpassword(svcpassword);
        }

        // Call authenticate
        if (operation.equalsIgnoreCase("AT")) {
            System.out.println("Calling authenticate at " + hostport + " ... " );
            SkceReturnObject skcero = port.authenticate(svcinfo, username, password);
            if ( skcero != null ) {              
                System.out.println("Response : " + skcero.getResponse());
            }
        } 
        // Call authorize
        else if (operation.equalsIgnoreCase("AZ")) {
            System.out.println("Calling authorize at " + hostport + " ... ");
            SkceReturnObject skcero = port.authorize(svcinfo, username, password, skceoperation);
            if ( skcero != null ) {
                System.out.println("Response : " + skcero.getResponse());
            }
        }
        // Call adduser
        else if (operation.equalsIgnoreCase("AU")) {
            System.out.println("Calling adduser at " + hostport + " ... ");
            SkceReturnObject skcero = port.adduser(svcinfo, username, password);
            if ( skcero != null ) {
                System.out.println("Response : " + skcero.getResponse());
            }
        } 
//        // Call getuserinfo
//        else if (operation.equalsIgnoreCase("GU")) {
//            System.out.println("Calling getuserinfo at " + hostport + " ... ");
//            SkceReturnObject skcero = port.getuserinfo(svcinfo, username);
//            if ( skcero != null ) {
//                System.out.println("Response : " + skcero.getResponse());
//            }
//        } 
        // Call getuserinfo
        else if (operation.equalsIgnoreCase("GM")) {
            System.out.println("Calling getuserinfo at " + hostport + " ... ");
            SkceReturnObject skcero = port.getuserinfo(svcinfo, searchdn, searchkey, searchvalue);
            if ( skcero != null ) {
                System.out.println("Response : " + skcero.getResponse());
            }
        } 
        // Call updateuser
        else if (operation.equalsIgnoreCase("UU")) {
            System.out.println("Calling updateuser at " + hostport + " ... ");
            SkceReturnObject skcero = port.updateuser(svcinfo, username, key, value, deletion);
            if ( skcero != null ) {
                System.out.println("Response : " + skcero.getResponse());
            }
        }
        
        System.out.println();
        System.out.println("Done!");
        System.out.println();
    }
}
