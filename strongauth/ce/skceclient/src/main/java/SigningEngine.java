
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
 * Copyright (c) 2001-2018 StrongAuth, Inc.
 *
 * $Date$ $Revision$
 * $Author$ $URL:
 * https://svn.strongauth.com/repos/jade/trunk/skce/skceclient/src/main/java/SigningEngine.java
 * $
 *
 * *********************************************
 * 888 888 888 88888b. .d88b. 888888 .d88b. .d8888b 888 "88b d88""88b 888 d8P
 * Y8b 88K 888 888 888 888 888 88888888 "Y8888b. 888 888 Y88..88P Y88b. Y8b. X88
 * 888 888 "Y88P" "Y888 "Y8888 88888P'
 *
 * *********************************************
 *
 * SigningEngine class takes care of testing various web-service operations that
 * are part of skse (strongkey signing engine); which is a sub-component of skce
 * (strongkey cryptoengine)
 *
 * The operations that could be tested using this class are
 *
 * 1. Load a key into a key store (JCEKS | PKCS12 | TRSM) 2. Sign a code file
 * (manifest file in a jar) using the key loaded into a key store 3. Remove a
 * key from a key store (JCEKS | PKCS12 | TRSM)
 *
 */

import com.strongauth.skceclient.common.Constants;
import com.strongauth.skceclient.common.GetManifestFile;
import com.strongauth.skceclient.common.createSignJar;
import com.strongauth.skceclient.common.testcreatexml;
import com.strongauth.skse.jaxb.SKCEInput;
import com.strongauth.skse.soapstubs.SKCEException_Exception;
import com.strongauth.skse.soapstubs.SKCEServiceInfoType;
import com.strongauth.skse.soapstubs.SKSEServlet;
import com.strongauth.skse.soapstubs.SKSEServlet_Service;
import com.strongauth.skse.soapstubs.SkceReturnObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.WebServiceException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SigningEngine {

    public static void main(String[] args) throws SKCEException_Exception, Exception {

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
        String username;
        String password;
        String operation;
        String sourcepath = "";
        String source = "", documentID = "", signtype = "", signature="";
        String keystoretype;
        String propfile;
        String did;
        String Usage = "Usage: java -cp skceclient.jar SigningEngine https://<host:port> <did> <username> <password> <keystoretype> <propertiesfile> L \n"
                + "       java -cp skceclient.jar SigningEngine https://<host:port> <did> <username> <password> <keystoretype> <propertiesfile> R \n"
                + "       java -cp skceclient.jar SigningEngine https://<host:port> <did> <username> <password> <keystoretype> <propertiesfile> S <sourcefile>\n"
                + "       java -cp skceclient.jar SigningEngine https://<host:port> <did> <username> <password> <documentID> <signtype> XSV <source>\n\n"
                + "Acceptable Values: \n"
                + "         hostport       : host url where skce is listening;\n"
                + "                          format  : http://<FQDN>:<non-ssl-portnumber> or \n"
                + "                                     https://<FQDN>:<ssl-portnumber>\n"
                + "                          example : https://fidodemo.strongauth.com:8181\n\n"
                + "         did            : Unique domain identifier that belongs to SKCE\n"
                + "         username       : skce user authorized to perform specific operation\n"
                + "         password       : skce user password\n"
                + "         keystoretype   : JCEKS | P12 | TRSM\n"
                + "         propertiesfile : full directory path to the properties file for signing data\n"
                + "                           please provide these properties with values in the file\n"
                + "                             skceclient.property.saka.hosturl=\n"
                + "                             skceclient.property.saka.did=\n"
                + "                             skceclient.property.saka.username=\n"
                + "                             skceclient.property.saka.password=\n"
                + "                             skceclient.property.keystore.token=\n"
                + "                             skceclient.property.keytore.password.token=\n"
                + "                             skceclient.property.keytore.keyalias=\n"
                + "         command        : L (loadkey) | R (removekey) | S (sign) \n"
                + "         documentID     : id for the document to be signed\n"
                + "         signType       : type of signatue requested [ 0 | 1 | 2]\n"
                + "                          0 : Enveloping\n"
                + "                          1 : Enveloped ( has not been implemented yet)\n"
                + "                          2 : Detached\n"
                + "         source         : source XML to be signed\n"
//                + "         signature      : XML signature to be verified\n"
                + "         sourcefile     : source file to be signed\n";

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
        if (args.length >= 7) {
            hostport = args[0];
            did = args[1];
            username = args[2];
            password = args[3];
            keystoretype = args[4];
            propfile = args[5];
            operation = args[6];
            if (operation.equalsIgnoreCase("S")) {
                if (args.length != 8) {
                    System.out.println(Usage);
                    return;
                }
                sourcepath = args[7];
            }
            if (operation.equalsIgnoreCase("XSV")) {
                if (args.length != 8) {
                    System.out.println(Usage);
                    return;
                }
                documentID = args[4];
                signtype = args[5];
                source = args[7];
//                signature = args[8];
            }
        } else {
            System.out.println(Usage);
            return;
        }

        SKSEServlet port;
        try {
            // Set up the URL and webService variables
            String hosturl = hostport + Constants.SKSE_WSDL_SUFFIX;
            URL url = new URL(hosturl);
            SKSEServlet_Service skseser = new SKSEServlet_Service(url);
            port = skseser.getSKSEServletPort();
        } catch (MalformedURLException ex) {
            System.out.println("Malformed hostport - " + hostport);
            return;
        } catch (WebServiceException ex) {
            ex.printStackTrace();
            System.out.println(hostport);
            
            System.err.println("\nIt appears that the site " + hostport + " is\n\n"
                    + "\t(1) either down;\n\t(2) is not accessible over the specified port; or\n\t"
                    + "(3) has a digital certificate that is not in your JVM's truststore.\n\n"
                    + "In case of (3), please include it in your JAVA_HOME/jre/lib/security/cacerts\n"
                    + "file with the [keytool -import] command before attempting this operation again.\n"
                    + "Please refer to the documentation on skceclient.jar at the above-mentioned URL\n"
                    + "on how to accomplish this.\n");
            return;
        }

        // Call Load key
        if (operation.equalsIgnoreCase("L")) {

            //  Build csainput
            String csainput = buildSigningInput(propfile);
            if (csainput.equals("")) {
                System.out.println("\nError reading properties file @ " + propfile);
                return;
            }
            
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(did));
            svcinfo.setSvcusername(username);
            svcinfo.setSvcpassword(password);

            //  call load key method
            System.out.println("Calling loadkey at " + hostport + " ... ");
            SkceReturnObject skcero = port.loadKey(svcinfo, keystoretype, csainput);
            if (skcero != null) {
                System.out.println("Response received : " + skcero.getResponse());
            }
        } // Call removekey
        else if (operation.equalsIgnoreCase("R")) {

            //  Build csainput
            String csainput = buildSigningInput(propfile);
            if (csainput.equals("")) {
                System.out.println("\nError reading properties file @ " + propfile);
                return;
            }

            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(did));
            svcinfo.setSvcusername(username);
            svcinfo.setSvcpassword(password);
            //  call remove key method
            System.out.println("Calling removekey at " + hostport + " ... ");
            SkceReturnObject skcero = port.removeKey(svcinfo, keystoretype, csainput);
            if (skcero != null) {
                System.out.println("Response received : " + skcero.getResponse());
            }
        } // Call sign
        else if (operation.equalsIgnoreCase("S")) {

            //  Build csainput
            String csainput = buildSigningInput(propfile);
            if (csainput.equals("")) {
                System.out.println("\nError reading properties file @ " + propfile);
                return;
            }

            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(did));
            svcinfo.setSvcusername(username);
            svcinfo.setSvcpassword(password);
            
            //  Get the manifest file from the source jar file
            System.out.println("\nExtracting manifest.sf file from input jar file ...");
            GetManifestFile getmfile = new GetManifestFile();
            String mffilepath;
            try {
                mffilepath = getmfile.signJar(sourcepath);
                if (mffilepath == null || mffilepath.equals("")) {
                    System.out.println("\nError retrieving manifest file from source jar file " + sourcepath);
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("\nError retrieving manifest file from source jar file " + sourcepath);
                System.out.println(ex.getLocalizedMessage());
                return;
            }

            //  convert the manifest file into a datahandler
            File fileobj = new File(mffilepath);
            DataHandler data = new DataHandler(new FileDataSource(fileobj));

            //  call sign method
            System.out.println("\nCalling sign at " + hostport + " ... ");
            SkceReturnObject skcero = port.sign(svcinfo, keystoretype, data, "dummy_signaturetype", "dummy_digesttype", csainput);
            System.out.println("Response received  : " + skcero.getResponse());
            System.out.println("Hash received      : " + skcero.getHash());

            //  If successfully signed, re-pack the jar with signed manifest file
            if (skcero.getResponse().contains("Successfully")) {
                System.out.println("\nRe-packing the signed jar with new manifest file ... ");
                createSignJar csj = new createSignJar();
                try {
                    String outname = csj.createsignjar(sourcepath, skcero.getHash());
                    System.out.println("\nSigned jar file saved at " + outname);
                } catch (Exception ex) {
                    System.out.println("\nError re-packing signed manifest file back into source jar file " + sourcepath);
                    System.out.println(ex.getLocalizedMessage());
                    return;
                }
            }

            //  Delete the temporary manifest file.
            File ff = new File(mffilepath);
            ff.delete();
        } else if (operation.equalsIgnoreCase("XSV")) {
            SKCEServiceInfoType svcinfo = new SKCEServiceInfoType();
            svcinfo.setDid(Integer.parseInt(did));
            svcinfo.setSvcusername(username);
            svcinfo.setSvcpassword(password);
            
            source = new testcreatexml().xmldoc();
            //  call sign method
            System.out.println("\nCalling sign XML at " + hostport + " ... ");
            SkceReturnObject skcero = port.signxml(svcinfo, null, null, documentID, Integer.parseInt(signtype), source);
            signature = skcero.getResponse();
            System.out.println("Signature : ");
            System.out.println(signature);
            
            System.out.println("\nCalling verify XML at " + hostport + " ... ");
            String verifysource;
            if(Integer.parseInt(signtype) == 0){
                verifysource = signature;
            }else{
                verifysource = source;
            }
            System.out.println("Input: " + source);
            skcero = port.verifyxml(svcinfo, Integer.parseInt(signtype), verifysource, signature);
            System.out.println(skcero.getResponse());
            
        }
//  Invalid operation
        else {
            System.err.println("Invalid Operation Specified...\n");
            System.out.println(Usage);
            return;
        }

        System.out.println();
        System.out.println("Done!");
        System.out.println();
    }

    //  Reads the input properties file and builds CSAInput object
    private static String buildSigningInput(String propsfilepath) throws JAXBException, FileNotFoundException, IOException {
        if (propsfilepath == null || propsfilepath.trim().equalsIgnoreCase("")) {
            return "";
        }

        String input;

        //  Load the properties file
        Properties props = new Properties();
        props.load(new FileInputStream(propsfilepath));

        //  Read the property values
        String sakahosturl = props.getProperty("skceclient.property.saka.hosturl");
        Long sakadid = Long.parseLong(props.getProperty("skceclient.property.saka.did"));
        String sakausername = props.getProperty("skceclient.property.saka.username");
        String sakapassword = props.getProperty("skceclient.property.saka.password");

        SKCEInput.SAKA saka = new SKCEInput.SAKA();
        saka.setUrl(sakahosturl);
        saka.setUsername(sakausername);
        saka.setPassword(sakapassword);

        // TRSM is not implemented yet
        SKCEInput.TRSM trsm = new SKCEInput.TRSM();
        trsm.setTrsmdevice(null);
        trsm.setTrsmslotid(null);     //Temporary
        trsm.setTrsmslotdid(null);
        trsm.setTrsmslotpasswordtoken(null);

        String keystoretoken = props.getProperty("skceclient.property.keystore.token");
        String keystorepwdtoken = props.getProperty("skceclient.property.keytore.password.token");
        String keyalias = props.getProperty("skceclient.property.keytore.keyalias");

        SKCEInput.P12 p12 = new SKCEInput.P12();
        p12.setP12Did(sakadid);
        p12.setP12Token(keystoretoken);
        p12.setP12Passworddid(sakadid);
        p12.setP12Passwordtoken(keystorepwdtoken);
        p12.setP12Alias(keyalias);

        SKCEInput in = new SKCEInput();
        in.setP12(p12);
        in.setSAKA(saka);
        in.setTRSM(trsm);

        StringWriter writer = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(SKCEInput.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(in, writer);
        input = writer.toString();

        return input;
    }
}
