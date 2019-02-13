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
 * $Date: 
 * $Revision:
 * $Author$
 * $URL: 
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
 *
 *
 */
package com.strongauth.skfe.fido2.tpm;

import com.strongauth.skce.utilities.TPMConstants;

/**
 *
 * @author mishimoto
 */
public class TPMScheme implements TPMMarshallable {
    private final short scheme;
    private final short schemeDetails;

    public TPMScheme(short scheme) {
        this.scheme = scheme;
        this.schemeDetails = TPMConstants.TPM_ALG_ERROR;
    }

    public TPMScheme(short scheme, short schemeDetails) {
        this.scheme = scheme;
        this.schemeDetails = schemeDetails;
    }

    @Override
    public byte[] marshalData() {
        if (scheme == TPMConstants.TPM_ALG_NULL) {       //TODO check for other schemes that do not have schemeDetails
            return Marshal.marshalObjects(
                    scheme);
        } else {
            return Marshal.marshalObjects(
                    scheme,
                    schemeDetails);
        }
    }
}
