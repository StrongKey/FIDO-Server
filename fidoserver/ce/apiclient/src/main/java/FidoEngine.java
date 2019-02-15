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
 * Copyright (c) 2001-2019 StrongAuth, Inc.
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
 * FidoEngine class takes care of testing various web-service operations
 * that are part of skee (strongkey fido engine); which is a sub-component
 * of skce (strongkey cryptoengine)
 *
 * The operations that could be tested using this class are
 *
 * 1. fido-Register a user
 * 2. fido-authenticate a user
 *
 */

import com.strongauth.skceclient.common.Constants;
import com.strongauth.skceclient.common.common;
import com.strongauth.skfe.client.impl.RestFidoU2FActionsOnKey;
import com.strongauth.skfe.client.impl.RestFidoU2FAuthenticate;
import com.strongauth.skfe.client.impl.RestFidoU2FGetKeysInfo;
import com.strongauth.skfe.client.impl.RestFidoU2FRegister;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FidoEngine {

    public static void main(String[] args) throws ParseException, IOException {

        // Get the skceclient version info.  CANNOT use new FileInputStream
        // as this is loaded from a different classloader.  Properties file
        // must be under src/main/resources
        Properties vprops = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        vprops.load(loader.getResourceAsStream("skceclient.properties"));
        String version = vprops.getProperty("skceclient.property.version");

        System.out.println();
        System.out.println("skceclient.jar " + version);
        System.out.println("Copyright (c) 2001-2019 StrongAuth, Inc. All rights reserved.");
        System.out.println();

        String usage = "Usage: java -cp skceclient.jar FidoEngine <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> R  <origin> [-e <good/bad signature>]\n"
                     + "       java -cp skceclient.jar FidoEngine <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> A  <origin> <authcounter> [-e <good/bad signature>]\n"
                     + "       java -cp skceclient.jar FidoEngine <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> G \n"
                     + "       java -cp skceclient.jar FidoEngine <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> DA <random-id> \n"
                     + "       java -cp skceclient.jar FidoEngine <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> AC <random-id> \n"
                     + "       java -cp skceclient.jar FidoEngine <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> DR <random-id> \n\n"
                     + "Acceptable Values:\n"
                     + "         hostport            : host and port to access the fido \n"
                     + "                                 SOAP & REST format : http://<FQDN>:<non-ssl-portnumber> or \n"
                     + "                                                      https://<FQDN>:<ssl-portnumber>\n"
                     + "                                 example            : https://fidodemo.strongauth.com:8181\n"
                     + "         did                 : Unique domain identifier that belongs to SKCE\n"
                     + "         accesskey           : access key for use in identifying a secret key\n"
                     + "         secretkey           : secret key for HMACing a request\n"
                     + "         fidoprotocol        : fido protocol; example U2F_V2 or FIDO20\n"
                     + "         username            : username for registration or authentication\n"
                     + "         command             : R  (registration) | A  (authentication) | G  (getkeysinfo) |\n"
                     + "                               DA (deactivate)   | AC (activate)       | DR (deregister) \n"
                     + "         origin              : Origin to be used by the fido client simulator\n"
                     + "         authcounter         : Auth counter to be used by the fido client simulator\n"
                     + "         random-id           : random-id string associated to a specific fido key registered to a\n"
                     + "                                 specific user. This is needed to perform actions on the key like\n"
                     + "                                 de-activate, activate and deregister.\n"
                     + "                                 Random-id can be obtained by calling 'G' option.\n"
                     + "         good/bad signature  : Optional; boolean value that simulates emiting good/bad signatures\n"
                     + "                                 true for good signature | false for bad signature\n"
                     + "                                 default is true\n";

        String hostport;
        String username;
        String fidoprotocol;
        String skcedid;
        String accesskey, secretkey;
        String command;
        String randomid = "";
        String origin = null;
        int auth_counter = 0;
        String modifyloc = "Sunnyvale CA";
        boolean sig = true;

        try {
            if (args.length == 0) {
                System.out.println(usage);
                return;
            }

            Options opt = new Options();
            opt.addOption("h", false, "Print help for this application");
            opt.addOption("e", true, "Emit Sig");
            opt.addOption("o", true, "Origin");
            opt.addOption("c", true, "Auth Counter");

            BasicParser parser = new BasicParser();
            CommandLine cl = parser.parse(opt, args);

            if (cl.hasOption('h')) {
                System.out.println(usage);
                return;
            }

            if (cl.hasOption("e")) {
                String esig = cl.getOptionValue("e");
                if (esig.equalsIgnoreCase("True") || esig.equalsIgnoreCase("Yes")) {
                    sig = true;
                } else if (esig.equalsIgnoreCase("False") || esig.equalsIgnoreCase("No")) {
                    sig = false;
                } else {
                    System.out.println("Incorrect value : True | False for option -e");
                    System.out.println(usage);
                    return;
                }
            }
        } catch (ParseException e) {
            System.out.println("Invalid argument or value Passed... Value for any argument cannot be null\n");
            System.out.println(usage);
            return;
        }

        /*
         * Initialize parameters to be passed to the web service calls
         * Parsing Arguments
         */
        if (args.length >= 7) {
            hostport = args[0];
            skcedid = args[1];
            accesskey = args[2];
            secretkey = args[3];
            fidoprotocol = args[4];
            username = args[5];
            command = args[6];

            if (!command.equalsIgnoreCase(Constants.COMMANDS_REG)
                    && !command.equalsIgnoreCase(Constants.COMMANDS_AUTH)
                    && !command.equalsIgnoreCase(Constants.COMMANDS_GETKEYS)
                    && !command.equalsIgnoreCase(Constants.COMMANDS_DEACT)
                    && !command.equalsIgnoreCase(Constants.COMMANDS_ACT)
                    && !command.equalsIgnoreCase(Constants.COMMANDS_DEREG)) {
                System.out.println("Invalid command ...");
                System.out.println(usage);
                return;
            }

            if (command.equalsIgnoreCase("DA") || command.equalsIgnoreCase("AC") || command.equalsIgnoreCase("DR") || command.equalsIgnoreCase("R")) {
                if (args.length < 8) {
                    System.out.println("Missing arguments ...");
                    System.out.println(usage);
                    return;
                }
            }else if (command.equalsIgnoreCase("A") || command.equalsIgnoreCase("AZ")) {
                if (args.length < 9) {
                    System.out.println("Missing arguments ...");
                    System.out.println(usage);
                    return;
                }
            }
            if (args.length == 8) {
                if (command.equalsIgnoreCase("DA") || command.equalsIgnoreCase("AC") || command.equalsIgnoreCase("DR")) {
                    randomid = args[8];
                }
                if(command.equalsIgnoreCase("R") ){
                    origin = args[8];
                }
            }
            if(args.length == 10){
                origin = args[8];
                auth_counter = Integer.parseInt(args[9]);
            }
            hostport = hostport + Constants.REST_SUFFIX;
        } else {
            System.out.println("Missing arguments ...");
            System.out.println(usage);
            return;
        }

        if (origin!=null) {
            URL url = new URL(origin);
            String host = url.getHost();
            common.setHost(host);
        }

        if (command.equalsIgnoreCase(Constants.COMMANDS_REG)) {
            RestFidoU2FRegister restFCR = new RestFidoU2FRegister();

            String response = restFCR.u2fRegister(hostport, fidoprotocol, skcedid, accesskey, secretkey, username, origin, sig);
            if (response.startsWith(" Error") || response.startsWith(" Exception")) {
                System.out.println("\nRegistration failed!");
                System.out.println(" " + response);
            } else {
                System.out.println("\nDone with registration!");
                System.out.println("Registration response : " + common.parseresponse(response));
            }

            System.out.println();
        } else if (command.equalsIgnoreCase(Constants.COMMANDS_AUTH)) {
            RestFidoU2FAuthenticate restFCA = new RestFidoU2FAuthenticate();
            String response = restFCA.u2fAuthenticate(hostport, fidoprotocol, skcedid, accesskey, secretkey, username, origin, auth_counter, sig);
            if (response.startsWith(" Error") || response.startsWith(" Exception")) {
                System.out.println("\nAuthentication failed!");
                System.out.println(" " + response);
            } else {
                System.out.println("\nDone with authentication!");
                System.out.println("Authentication response : " + common.parseresponse(response));
            }

            System.out.println();
        } else if (command.equalsIgnoreCase(Constants.COMMANDS_GETKEYS)) {

            RestFidoU2FGetKeysInfo restFCA = new RestFidoU2FGetKeysInfo();
            restFCA.u2fGetKeysInfo(hostport, fidoprotocol, skcedid, accesskey, secretkey, username);

            System.out.println("\nDone with GetKeysInfo!");
            System.out.println();
        } else if (command.equalsIgnoreCase(Constants.COMMANDS_DEACT)) {

            RestFidoU2FActionsOnKey restFCA = new RestFidoU2FActionsOnKey();
            String response = restFCA.u2fDeactivate(hostport, fidoprotocol, skcedid, accesskey, secretkey, username, randomid, modifyloc);

            System.out.println("\nDone with Deactivate!");
            System.out.println();
        } else if (command.equalsIgnoreCase(Constants.COMMANDS_ACT)) {

            RestFidoU2FActionsOnKey restFCA = new RestFidoU2FActionsOnKey();
            String response = restFCA.u2fActivate(hostport, fidoprotocol, skcedid, accesskey, secretkey, username, randomid, modifyloc);

            System.out.println("\nDone with Activate!");
            System.out.println();
        } else if (command.equalsIgnoreCase(Constants.COMMANDS_DEREG)) {

            RestFidoU2FActionsOnKey restFCA = new RestFidoU2FActionsOnKey();
            String response = restFCA.u2fDeregister(hostport, fidoprotocol, skcedid, accesskey, secretkey, username, randomid);

            System.out.println("\nDone with Deregister!");
            System.out.println();
        } else {
            System.out.println("Invalid Command...");
            System.out.println(usage);
        }
    }
}
