/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.skce.utilities;

import com.strongkey.skce.hashmaps.SAConcurrentHashMapImpl;
import com.strongkey.skce.hashmaps.SAHashmap;
import com.strongkey.skce.pojos.FIDOSecretKeyInfo;
import com.strongkey.skce.pojos.FidoKeysInfo;
import com.strongkey.skce.pojos.FidoPolicyMDS;
import com.strongkey.skce.pojos.UserSessionInfo;
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
