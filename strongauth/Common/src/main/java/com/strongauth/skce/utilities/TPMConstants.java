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

public class TPMConstants {

    public static final int TPM_GENERATED_VALUE = 0xff544347;
    public static final short TPM_ST_ATTEST_CERTIFY = (short) 0x8017;

    public static final short TPM_ALG_ERROR = (short) 0x0000;
    public static final short TPM_ALG_RSA = (short) 0x0001;
    public static final short TPM_ALG_NULL = (short) 0x0010;
    public static final short TPM_ALG_ECC = (short) 0x0023;

    public static final short TPM_ECC_NIST_P256 = (short) 0x0003;
    public static final short TPM_ECC_NIST_P384 = (short) 0x0004;
    public static final short TPM_ECC_NIST_P521 = (short) 0x0005;

    public static final short TPM_ALG_SHA1 = (short) 0x0004;
    public static final short TPM_ALG_SHA256 = (short) 0x000B;
    public static final short TPM_ALG_SHA384 = (short) 0x000C;
    public static final short TPM_ALG_SHA512 = (short) 0x000D;

    public static final int SIZEOFLONG = 8;
    public static final int SIZEOFINT = 4;
    public static final int SIZEOFSHORT = 2;
    public static final int SIZEOFBYTE = 1;
    public static final int SIZEOFTAG = 2;
}
