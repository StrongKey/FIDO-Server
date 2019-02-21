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
 *************************************************
 *                     888
 *                     888
 *                     888
 *   88888b.   .d88b.  888888  .d88b.  .d8888b
 *   888 "88b d88""88b 888    d8P  Y8b 88K
 *   888  888 888  888 888    88888888 "Y8888b.
 *   888  888 Y88..88P Y88b.  Y8b.          X88
 *   888  888  "Y88P"   "Y888  "Y8888   88888P'
 *
 *************************************************
 *
 * Defines constants used by modules of the SKLES.
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
