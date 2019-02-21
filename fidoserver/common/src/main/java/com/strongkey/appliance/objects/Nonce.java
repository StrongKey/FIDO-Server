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

package com.strongkey.appliance.objects;

import java.util.Date;

public class Nonce {

    private final byte[] nonce;
    private final long timestamp;

    public Nonce(byte[] nonce) {
        this.nonce = new byte[nonce.length];
        System.arraycopy(nonce, 0, this.nonce, 0, nonce.length);
        this.timestamp = new Date().getTime();
    }
    
    public Nonce(byte[] nonce, long timestamp) {
        this.nonce = new byte[nonce.length];
        System.arraycopy(nonce, 0, this.nonce, 0, nonce.length);
        this.timestamp = new Date().getTime();
    }
    
    public byte[] getNonce() {
        byte[] b = new byte[this.nonce.length];
        System.arraycopy(this.nonce, 0, b, 0, this.nonce.length);
        return b;
    }

    protected long getTimestamp() {
        return this.timestamp;
    }
}
