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

package com.strongauth.skfe.entitybeans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "fido_users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FidoUsers.findAll", query = "SELECT f FROM FidoUsers f"),
    @NamedQuery(name = "FidoUsers.findBySid", query = "SELECT f FROM FidoUsers f WHERE f.fidoUsersPK.sid = :sid"),
    @NamedQuery(name = "FidoUsers.findByDid", query = "SELECT f FROM FidoUsers f WHERE f.fidoUsersPK.did = :did"),
    @NamedQuery(name = "FidoUsers.findByUsername", query = "SELECT f FROM FidoUsers f WHERE f.fidoUsersPK.username = :username"),
    @NamedQuery(name = "FidoUsers.findByDidUsername", query = "SELECT f FROM FidoUsers f WHERE f.fidoUsersPK.did = :did and f.fidoUsersPK.username = :username"),
    @NamedQuery(name = "FidoUsers.findByUserdn", query = "SELECT f FROM FidoUsers f WHERE f.userdn = :userdn"),
    @NamedQuery(name = "FidoUsers.findByFidoKeysEnabled", query = "SELECT f FROM FidoUsers f WHERE f.fidoKeysEnabled = :fidoKeysEnabled"),
    @NamedQuery(name = "FidoUsers.findByTwoStepVerification", query = "SELECT f FROM FidoUsers f WHERE f.twoStepVerification = :twoStepVerification"),
    @NamedQuery(name = "FidoUsers.findByPrimaryEmail", query = "SELECT f FROM FidoUsers f WHERE f.primaryEmail = :primaryEmail"),
    @NamedQuery(name = "FidoUsers.findByRegisteredEmails", query = "SELECT f FROM FidoUsers f WHERE f.registeredEmails = :registeredEmails"),
    @NamedQuery(name = "FidoUsers.findByPrimaryPhoneNumber", query = "SELECT f FROM FidoUsers f WHERE f.primaryPhoneNumber = :primaryPhoneNumber"),
    @NamedQuery(name = "FidoUsers.findByRegisteredPhoneNumbers", query = "SELECT f FROM FidoUsers f WHERE f.registeredPhoneNumbers = :registeredPhoneNumbers"),
    @NamedQuery(name = "FidoUsers.findByTwoStepTarget", query = "SELECT f FROM FidoUsers f WHERE f.twoStepTarget = :twoStepTarget"),
    @NamedQuery(name = "FidoUsers.findByStatus", query = "SELECT f FROM FidoUsers f WHERE f.status = :status"),
    @NamedQuery(name = "FidoUsers.findBySignature", query = "SELECT f FROM FidoUsers f WHERE f.signature = :signature")})
public class FidoUsers implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FidoUsersPK fidoUsersPK;
    @Size(max = 2048)
    @Column(name = "userdn")
    private String userdn;
    @Size(max = 5)
    @Column(name = "fido_keys_enabled")
    private String fidoKeysEnabled;
    @Size(max = 5)
    @Column(name = "two_step_verification")
    private String twoStepVerification;
    @Size(max = 256)
    @Column(name = "primary_email")
    private String primaryEmail;
    @Size(max = 2048)
    @Column(name = "registered_emails")
    private String registeredEmails;
    @Size(max = 32)
    @Column(name = "primary_phone_number")
    private String primaryPhoneNumber;
    @Size(max = 2048)
    @Column(name = "registered_phone_numbers")
    private String registeredPhoneNumbers;
    @Size(max = 6)
    @Column(name = "two_step_target")
    private String twoStepTarget;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "status")
    private String status;
    @Size(max = 2048)
    @Column(name = "signature")
    private String signature;

    @Transient
    private String id;
    
    public FidoUsers() {
    }

    public FidoUsers(FidoUsersPK fidoUsersPK) {
        this.fidoUsersPK = fidoUsersPK;
    }

    public FidoUsers(FidoUsersPK fidoUsersPK, String status) {
        this.fidoUsersPK = fidoUsersPK;
        this.status = status;
    }

    public FidoUsers(short sid, short did, String username) {
        this.fidoUsersPK = new FidoUsersPK(sid, did, username);
    }

    public FidoUsersPK getFidoUsersPK() {
        return fidoUsersPK;
    }

    public void setFidoUsersPK(FidoUsersPK fidoUsersPK) {
        this.fidoUsersPK = fidoUsersPK;
    }

    public String getUserdn() {
        return userdn;
    }

    public void setUserdn(String userdn) {
        this.userdn = userdn;
    }

    public String getFidoKeysEnabled() {
        return fidoKeysEnabled;
    }

    public void setFidoKeysEnabled(String fidoKeysEnabled) {
        this.fidoKeysEnabled = fidoKeysEnabled;
    }

    public String getTwoStepVerification() {
        return twoStepVerification;
    }

    public void setTwoStepVerification(String twoStepVerification) {
        this.twoStepVerification = twoStepVerification;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getRegisteredEmails() {
        return registeredEmails;
    }

    public void setRegisteredEmails(String registeredEmails) {
        this.registeredEmails = registeredEmails;
    }

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public String getRegisteredPhoneNumbers() {
        return registeredPhoneNumbers;
    }

    public void setRegisteredPhoneNumbers(String registeredPhoneNumbers) {
        this.registeredPhoneNumbers = registeredPhoneNumbers;
    }

    public String getTwoStepTarget() {
        return twoStepTarget;
    }

    public void setTwoStepTarget(String twoStepTarget) {
        this.twoStepTarget = twoStepTarget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlTransient
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

        public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fidoUsersPK != null ? fidoUsersPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FidoUsers)) {
            return false;
        }
        FidoUsers other = (FidoUsers) object;
        if ((this.fidoUsersPK == null && other.fidoUsersPK != null) || (this.fidoUsersPK != null && !this.fidoUsersPK.equals(other.fidoUsersPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.strongauth.skce.entitybeans.FidoUsers[ fidoUsersPK=" + fidoUsersPK + " ]";
    }

}
