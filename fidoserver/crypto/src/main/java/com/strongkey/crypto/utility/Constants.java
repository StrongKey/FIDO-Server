/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */

package com.strongkey.crypto.utility;

public class Constants {
    /**
     * Constants for status field
     */
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String STATUS_RETIRED = "Retired";
    public static final String STATUS_SUSPENDED = "Suspended";
    public static final String STATUS_OTHER = "Other";

    public static final int RESPONSE_ENCODING_CBOR = 0;
    public static final int RESPONSE_ENCODING_JSON = 1;
    public static final int RESPONSE_ENCODING_XML  = 2;
}
