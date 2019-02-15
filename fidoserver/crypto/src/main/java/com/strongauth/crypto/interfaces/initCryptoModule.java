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
 * This class initializes the hardware cryptographic module for a number
 * of EJBs in this package.  To ensure that it isn't called from anywhere
 * else, the constructor is set to "protected" access control.
 */

package com.strongauth.crypto.interfaces;

import com.strongauth.crypto.bcfips.GenericCryptoModule;
import com.strongauth.crypto.utility.cryptoCommon;
import java.util.logging.Level;

public class initCryptoModule
{
    /**
     ** This class's name - used for logging and file-separator
     **/
    private static final String classname  = "initCryptoModule";

    private static GenericCryptoModule gcm = null;

    public static GenericCryptoModule getCryptoModule()
    {
        cryptoCommon.entering(classname, "initCryptoModule");

        String moduletype = cryptoCommon.getConfigurationProperty("crypto.cfg.property.cryptomodule.type");
        String modulevendor = cryptoCommon.getConfigurationProperty("crypto.cfg.property.cryptomodule.vendor");
        cryptoCommon.logp(Level.FINE, classname, "getCryptoModule", "CRYPTO-MSG-1019", moduletype + " [vendor=" + modulevendor + "]");
        
        if (gcm == null) {
                gcm = new GenericCryptoModule(null);
        } 
        return gcm;
    }

    public static void setCryptoModule(GenericCryptoModule newgcm) {
        gcm = newgcm;
    }
}
