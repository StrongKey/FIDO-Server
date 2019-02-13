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
 * Copyright (c) 2001-2016 StrongAuth, Inc.
 *
 * $Date$ 
 * $Revision$
 * $Author$ 
 * $URL$
 *
 * **********************************************
 *
 *  888b    888          888
 *  8888b   888          888
 *  88888b  888          888
 *  888Y88b 888  .d88b.  888888  .d88b.  .d8888b
 *  888 Y88b888 d88""88b 888    d8P  Y8b 88K
 *  888  Y88888 888  888 888    88888888 "Y8888b.
 *  888   Y8888 Y88..88P Y88b.  Y8b.          X88
 *  888    Y888  "Y88P"   "Y888  "Y8888   88888P'
 *
 * **********************************************
 *
 *
 */
package com.strongauth.pki2fido.utilities;


public class Constants {
    
    // TLS Client-Authentication with X.509 certificate/key
    public static final String CLIENTAUTH = "clientauth"; 
    
    public static final String FIDOREGISTER = "fidoregister"; 
    public static final String FIDOAUTHENTICATE = "fidoauthenticate"; 
    
    public static final String PREREGISTER = "preregister"; 
    public static final String REGISTER = "register"; 
    public static final String PREAUTHENTICATE = "preauthenticate"; 
    public static final String AUTHENTICATE = "authenticate";
    public static final String DEREGISTER = "deregister";
    public static final String GET_KEYS_INFO = "getkeysinfo";
    
    public static final String JSON_KEY_SERVLET_INPUT_USERNAME = "username"; 
    public static final String JSON_KEY_SERVLET_INPUT_RESPONSE = "response"; 
    public static final String JSON_KEY_SERVLET_INPUT_METADATA = "metadata"; 
    public static final String JSON_KEY_SERVLET_INPUT_RANDOMID = "randomid"; 
    public static final String JSON_KEY_SERVLET_INPUT_REQUEST = "request";
    
    public static final String REST_SERVER_ERROR = "serverErr"; 
    public static final String REST_SERVICE_ERROR = "serviceErr"; 
    public static final int TIMEOUT_VALUE = 30000; 
}
