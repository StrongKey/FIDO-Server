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
 */
package com.strongauth.skceclient.common;

public class Constants {

    //Operation names - for the web service
    public static final String REST_SUFFIX = "/api/domains/";
    public static final String PRE_REGISTER_ENDPOINT = "/fidokeys/challenge";
    public static final String REGISTER_ENDPOINT = "/fidokeys";
    public static final String PRE_AUTH_ENDPOINT = "/fidokeys/authorize/challenge";
    public static final String AUTHORIZE_ENDPOINT = "/fidokeys/authorize";
    public static final String GETKEYSINFO_ENDPOINT = "/fidokeys";
    public static final String UPDATE_ENDPOINT = "/fidokeys";
    public static final String DEACTIVATE_ENDPOINT = "/fidokeys";

    public static final String COMMANDS_REG = "R";
    public static final String COMMANDS_AUTH = "A";
    public static final String COMMANDS_GETKEYS = "G";
    public static final String COMMANDS_DEACT = "D";
    public static final String COMMANDS_UP = "U";

    public static final String JSON_KEY_SERVLET_INPUT_USERNAME = "username";
    public static final String JSON_KEY_SERVLET_INPUT_REQUEST = "request";
    public static final String JSON_KEY_SERVLET_INPUT_RESPONSE = "response";
    public static final String JSON_KEY_SERVLET_INPUT_METADATA = "metadata";

    public static class WebAuthn {

        public static String RELYING_PARTY = "rp";
        public static String RELYING_PARTY_NAME = "name";
        public static String CHALLENGE = "challenge";
        public static String RELYING_PARTY_RPID = "rpid";
        public static String USER = "user";
        public static String USER_NAME = "name";
        public static String USER_ID = "id";
        public static String USER_DISPLAY_NAME = "displayName";
        public static String ATTESTATION_PREFERENCE = "attestation";
        public static String PUBKEYCREDPARAMS = "pubKeyCredParams";
        public static String PUBKEYCREDPARAMS_ALG = "alg";
        public static String ATTESTATION_OJBECT = "attestationObject";
        public static String CLIENT_DATA_JSON = "clientDataJSON";
        public static String RAW_ID = "rawId";
        public static String ID = "id";
        public static String TYPE = "type";
        public static String RESPONSE = "response";
        public static String RP_ID = "rpId";
        public static String TIMEOUT = "timeout";
        public static String USER_HANDLE = "userHandle";
        public static String AUTHENTICATOR_DATA = "authenticatorData";
        public static String SIGNATURE = "signature";
    }
}
