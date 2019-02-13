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
 * Constants used in this program.
 */

package com.strongauth.skfe.tokensim;

public class Constants 
{
    // Constants related to the Attestation Key
    public static final String ATTESTATION_KEYSTORE_FILE = "/resources/attestation.jceks";
    public static final String ATTESTATION_KEYSTORE_PASSWORD = "changeit";
    public static final String ATTESTATION_KEYSTORE_PRIVATEKEY_ALIAS = "mykey";
    
    // Constants related to JSON keys for decoding
    public static final int JSON_KEY_APPID = 0;
    public static final int JSON_KEY_CHALLENGE = 1;
    public static final int JSON_KEY_CHANNELID = 2;
    public static final int JSON_KEY_CLIENTDATA = 3;   
    public static final int JSON_KEY_KEYHANDLE = 4;
    public static final int JSON_KEY_REGISTRATIONDATA = 5;
    public static final int JSON_KEY_REQUEST_TYPE = 6;
    public static final int JSON_KEY_SERVER_CHALLENGE = 7;
    public static final int JSON_KEY_SERVER_ORIGIN = 8;
    public static final int JSON_KEY_SESSIONID = 9;
    public static final int JSON_KEY_SIGNATURE = 10;
    public static final int JSON_KEY_VERSION = 11;
    
    // Constants related to JSON keys
    public static final String JSON_KEY_APPID_LABEL = "appId";
    public static final String JSON_KEY_CHALLENGE_LABEL = "challenge";
    public static final String JSON_KEY_CHANNELID_LABEL = "cid_pubkey";
    public static final String JSON_KEY_CLIENTDATA_LABEL = "clientData";   
    public static final String JSON_KEY_KEYHANDLE_LABEL = "keyHandle";
    public static final String JSON_KEY_REGISTRATIONDATA_LABEL = "registrationData";
    public static final String JSON_KEY_REQUEST_TYPE_LABEL = "typ";
    public static final String JSON_KEY_SERVER_CHALLENGE_LABEL = "challenge";
    public static final String JSON_KEY_SERVER_ORIGIN_LABEL = "origin";
    public static final String JSON_KEY_SESSIONID_LABEL = "sessionId";
    public static final String JSON_KEY_SIGNATURE_LABEL = "signatureData";
    public static final String JSON_KEY_VERSION_LABEL = "version";
    
    // Constants related to FIDO Client (Chrome browser for now)
    public static final String REGISTER_CLIENT_BAD_APPID = "TESTBADSIGNATUREWITHINVALIDAPPID";
    public static final String REGISTER_CLIENT_DATA_CHANNELID = "NOT IMPLEMENTED YET";
    public static final String REGISTER_CLIENT_DATA_OPTYPE = "navigator.id.finishEnrollment";
    public static final String AUTHENTICATE_CLIENT_DATA_CHANNELID = "NOT IMPLEMENTED YET";
    public static final String AUTHENTICATE_CLIENT_DATA_OPTYPE = "navigator.id.getAssertion";

    // Constants related to cryptography
    public static final String EC_P256_CURVE = "secp256r1";
    public static final String FIXED_AES256_WRAPPING_KEY = "0123456789ABCDEF0123456789ABCDEF";
    
    // Constants related to Authenticator
    public static final byte AUTHENTICATOR_CONTROL_BYTE = 0x03;
    public static final byte AUTHENTICATOR_USERPRESENCE_BYTE = 0X01;
    
    // Constants related to sizes
    public static final int APPLICATION_PARAMETER_LENGTH = 32;
    public static final int AUTHENTICATOR_COUNTER_LENGTH = 4;
    public static final int AUTHENTICATOR_KEY_HANDLE_LENGTH = 1;
    public static final int CHALLENGE_PARAMETER_LENGTH = 32;
    public static final int ECDSA_P256_PUBLICKEY_LENGTH = 65;
    public static final int ENCRYPTION_MODE_CBC_IV_LENGTH = 16;
}
