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
package com.strongauth.skce.utilities;

import com.strongauth.skce.hashmaps.SAConcurrentHashMapImpl;
import com.strongauth.skce.hashmaps.SAHashmap;
import com.strongauth.skce.pojos.FIDOSecretKeyInfo;
import com.strongauth.skce.pojos.FidoKeysInfo;
import com.strongauth.skce.pojos.FidoPolicyMDS;
import com.strongauth.skce.pojos.UserSessionInfo;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class skceMaps {

    private static final String classname = "skceMaps";
    /**
     * Map that stores the sessionid to a simple pojo (username and challenge)
     */
    public static Map<String, UserSessionInfo> sessionMap = new ConcurrentSkipListMap<>();
    public static SortedMap<String, FidoKeysInfo> FIDOkeysmap = new ConcurrentSkipListMap<>();
    /**
     * Map that stores the FIDO secret key
     */
    public static Map<String, FIDOSecretKeyInfo> FSKMap = new ConcurrentHashMap<>();
    /**
     * Map that stores FIDO policies
     */
    public static Map<String, FidoPolicyMDS> FPMap = new ConcurrentHashMap<>();

    static {
    }

    public skceMaps() {

    }
    
    public static SAHashmap getMapObj() {
        return SAConcurrentHashMapImpl.getInstance();
    }
}
