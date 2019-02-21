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
 * $Date: $
 * $Revision: $
 * $Author: $
 * $URL: $
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
 */
package com.strongkey.appliance.utilities;

import java.util.logging.Level;

public class applianceInputChecks {

    private static final String classname = "applianceInputChecks";

    static {
    }

    public applianceInputChecks() {

    }

    public static boolean checkDid(Long did) {

        if (did == null) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1003", "did");
            throw new NullPointerException(applianceCommon.getMessageProperty("APPL-ERR-1003").replace("{0}", "") + "did");
        } else if (did < 1 || did > Long.MAX_VALUE) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1002", "did");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1002").replace("{0}", "") + "did");
        } else if (!applianceMaps.domainActive(did)) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1011", "did");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1011").replace("{0}", "") + did);
        }
        return true;
    }

    public static boolean checkServiceCredentails(String username, String password) {

        if (username == null) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1003", "username");
            throw new NullPointerException(applianceCommon.getMessageProperty("APPL-ERR-1003").replace("{0}", "") + "username");
        } else if (username.trim().length() == 0 || username.trim().equalsIgnoreCase("")) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1002", "username");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1002").replace("{0}", "") + "username");
        } else if (username.trim().length() > applianceCommon.getMaxLenProperty("appliance.cfg.maxlen.128charstring")) {
            strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER, Level.WARNING, classname, "setUsername", "APPL-ERR-1005", "username");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1005").replace("{0}", "") + "username");
        }

        if (password == null) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1003", "password");
            throw new NullPointerException(applianceCommon.getMessageProperty("APPL-ERR-1003").replace("{0}", "") + "password");
        } else if (password.trim().length() == 0 || password.trim().equalsIgnoreCase("")) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1002", "password");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1002").replace("{0}", "") + "password");
        } else if (password.trim().length() > applianceCommon.getMaxLenProperty("appliance.cfg.maxlen.64charstring")) {
            strongkeyLogger.logp(applianceConstants.APPLIANCE_LOGGER, Level.WARNING, classname, "setUsername", "APPL-ERR-1005", "password");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1005").replace("{0}", "") + "password");
        }

        return true;
    }

    public static boolean checkOperation(String operation) {
        if (operation == null) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1003", "operation");
            throw new NullPointerException(applianceCommon.getMessageProperty("APPL-ERR-1003").replace("{0}", "") + "operation");
        } else if (operation.trim().length() == 0 || operation.trim().equalsIgnoreCase("")) {
            strongkeyLogger.log(applianceConstants.APPLIANCE_LOGGER, Level.SEVERE, "APPL-ERR-1002", "operation");
            throw new IllegalArgumentException(applianceCommon.getMessageProperty("APPL-ERR-1002").replace("{0}", "") + "operation");
        }
        return true;
    }
}
