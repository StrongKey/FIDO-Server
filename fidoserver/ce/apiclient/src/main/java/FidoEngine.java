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
import com.strongauth.skfe.client.impl.RestFidoActionsOnKey;
import com.strongauth.skfe.client.impl.RestFidoAuthenticate;
import com.strongauth.skfe.client.impl.RestFidoGetKeysInfo;
import com.strongauth.skfe.client.impl.RestFidoRegister;
import java.util.Calendar;

public class FidoEngine {

    public static void main(String[] args) throws Exception {

        System.out.println();
        System.out.println("Copyright (c) 2001-"+Calendar.getInstance().get(Calendar.YEAR)+" StrongAuth, Inc. All rights reserved.");
        System.out.println();

        String usage = "Usage: java -jar apiclient.jar R <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> <origin>\n"
                     + "       java -jar apiclient.jar A <hostport> <did> <accesskey> <secretkey> <fidoprotocol> <username> <origin> <authcounter>\n"
                     + "       java -jar apiclient.jar G <hostport> <did> <accesskey> <secretkey> <username> \n"
                     + "       java -jar apiclient.jar D <hostport> <did> <accesskey> <secretkey> <random-id> \n"
                     + "       java -jar apiclient.jar U <hostport> <did> <accesskey> <secretkey> <random-id> <Active/Inactive>\n\n"
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
                     + "         Active/Inactive     : status to set the fido-key to.\n"
                     + "         good/bad signature  : Optional; boolean value that simulates emiting good/bad signatures\n"
                     + "                                 true for good signature | false for bad signature\n"
                     + "                                 default is true\n";

        try {
            switch (args[0]) {

                case Constants.COMMANDS_REG:
                    if (args.length != 8)
                        System.out.println("Missing arguments...\n" + usage);

                    RestFidoRegister.register(args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
                    System.out.println("\nDone with Register!\n");
                    break;

                case Constants.COMMANDS_AUTH:
                    if (args.length != 9)
                        System.out.println("Missing arguments...\n" + usage);

                    RestFidoAuthenticate.authenticate(args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
                    System.out.println("\nDone with Authorize!\n");
                    break;

                case Constants.COMMANDS_GETKEYS:
                    if (args.length != 6)
                        System.out.println("Missing arguments...\n" + usage);

                    RestFidoGetKeysInfo.getKeysInfo(args[1], args[2], args[3], args[4], args[5]);
                    System.out.println("\nDone with GetKeysInfo!\n");
                    break;

                case Constants.COMMANDS_DEACT:
                    if (args.length != 6)
                        System.out.println("Missing arguments...\n" + usage);

                    RestFidoActionsOnKey.deregister(args[1], args[2], args[3], args[4], args[5]);
                    System.out.println("\nDone with Deactivate!\n");
                    break;

                case Constants.COMMANDS_UP:
                    if (args.length != 7) 
                        System.out.println("Missing arguments...\n" + usage);

                    RestFidoActionsOnKey.patch(args[1], args[2], args[3], args[4], args[5], args[6]);
                    System.out.println("\nDone with Update!\n");
                    break;

                default:
                    System.out.println("Invalid Command...\n" + usage);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Missing arguments...\n" + usage);
        }
    }
}
