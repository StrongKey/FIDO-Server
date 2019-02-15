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
 * This is a generic class that holds common parameters between U2F registration
 * and authentication response objects.
 *
 */
package com.strongauth.skfe.core;

import java.io.Serializable;

/**
 * Super class for U2F responses that comes back to the FIDO server from the
 * RP application.
 */
public class U2FResponse implements Serializable {
    
    /**
     * This class' name - used for logging
     */
    private final String classname = this.getClass().getName();
    
    /**
     * Supported versions for U2F protocol 
     */
    final String U2F_VERSION_V2 = "U2F_V2";
    
    /**
     * Generic attributes
     */
    String browserdata;
    
    /**
     * Class internal use only
     */
    BrowserData bd;

    /**
     * Constructor - Does not need to do anything at this point. Verification is
     * mostly process (registration or authentication) specific. 
     * Additionally,
     * an empty constructor since this class implements java.io.Serializable
     */
    protected U2FResponse() {
    }
}
