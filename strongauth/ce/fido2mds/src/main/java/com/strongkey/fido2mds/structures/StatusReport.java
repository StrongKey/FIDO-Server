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

package com.strongkey.fido2mds.structures;

public class StatusReport {
    private AuthenticatorStatus status;
    private String effectiveDate;
    private String certificate;
    private String url;
    private String certificationDescriptor;
    private String certificateNumber;
    private String certificationPolicyVersion;
    private String certificationRequirementsVersion;

    public AuthenticatorStatus getStatus() {
        return status;
    }

    public void setStatus(AuthenticatorStatus status) {
        this.status = status;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCertificationDescriptor() {
        return certificationDescriptor;
    }

    public void setCertificationDescriptor(String certificationDescriptor) {
        this.certificationDescriptor = certificationDescriptor;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getCertificationPolicyVersion() {
        return certificationPolicyVersion;
    }

    public void setCertificationPolicyVersion(String certificationPolicyVersion) {
        this.certificationPolicyVersion = certificationPolicyVersion;
    }

    public String getCertificationRequirementsVersion() {
        return certificationRequirementsVersion;
    }

    public void setCertificationRequirementsVersion(String certificationRequirementsVersion) {
        this.certificationRequirementsVersion = certificationRequirementsVersion;
    }
    
    
}
