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
 * $Author: mishimoto $
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

package com.strongkey.skfs.entitybeans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class AttestationCertificatesPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "sid")
    private short sid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "did")
    private short did;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attcid")
    private int attcid;

    public AttestationCertificatesPK() {
    }

    public AttestationCertificatesPK(short sid, short did, int attcid) {
        this.sid = sid;
        this.did = did;
        this.attcid = attcid;
    }

    public short getSid() {
        return sid;
    }

    public void setSid(short sid) {
        this.sid = sid;
    }

    public short getDid() {
        return did;
    }

    public void setDid(short did) {
        this.did = did;
    }

    public int getAttcid() {
        return attcid;
    }

    public void setAttcid(int attcid) {
        this.attcid = attcid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) sid;
        hash += (int) did;
        hash += (int) attcid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AttestationCertificatesPK)) {
            return false;
        }
        AttestationCertificatesPK other = (AttestationCertificatesPK) object;
        if (this.sid != other.sid) {
            return false;
        }
        if (this.did != other.did) {
            return false;
        }
        if (this.attcid != other.attcid) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.strongauth.skce.entitybeans.AttestationCertificatesPK[ sid=" + sid + ", did=" + did + ", attcid=" + attcid + " ]";
    }

}
