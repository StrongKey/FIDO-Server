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

package com.strongauth.skfe.fido.policyobjects;

import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.skfe.utilities.SKFEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import java.util.Base64;
import java.util.Date;
import javax.json.JsonObject;

public class FidoPolicyObject {
    private final Long did;
    private final Long sid;
    private final Long pid;
    private final Integer version;
    private final Date startDate;
    private final Date endDate;
    private final CryptographyPolicyOptions cryptographyOptions;
    private final RpPolicyOptions rpOptions;
    private final Integer timeout;
    private final MdsPolicyOptions mdsOptions;
    private final String tokenBindingOption;
    private final CounterPolicyOptions counterOptions;
    private final Boolean isUserSettingsRequired;
    private final Boolean isStoreSignaturesRequired;
    private final RegistrationPolicyOptions registrationOptions;
    private final AuthenticationPolicyOptions authenticationOptions;
    private final ExtensionsPolicyOptions extensionsOptions;
    
    private FidoPolicyObject(
            Long did,
            Long sid,
            Long pid,
            Integer version,
            Date startDate,
            Date endDate,
            CryptographyPolicyOptions cryptographyOptions,
            RpPolicyOptions rpOptions,
            Integer timeout,
            MdsPolicyOptions mdsOptions,
            String tokenBindingOption,
            CounterPolicyOptions counterOptions,
            Boolean isUserSettingsRequired,
            Boolean isStoreSignaturesRequired,
            RegistrationPolicyOptions registrationOptions,
            AuthenticationPolicyOptions authenticationOptions,
            ExtensionsPolicyOptions extensionsOptions){
        this.did = did;
        this.sid = sid;
        this.pid = pid;
        this.version = version;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cryptographyOptions = cryptographyOptions;
        this.rpOptions = rpOptions;
        this.timeout = timeout;
        this.mdsOptions = mdsOptions;
        this.tokenBindingOption = tokenBindingOption;
        this.counterOptions = counterOptions;
        this.isUserSettingsRequired = isUserSettingsRequired;
        this.isStoreSignaturesRequired = isStoreSignaturesRequired;
        this.registrationOptions = registrationOptions;
        this.authenticationOptions = authenticationOptions;
        this.extensionsOptions = extensionsOptions;
    }

    public Long getDid() {
        return did;
    }

    public Long getSid() {
        return sid;
    }

    public Long getPid() {
        return pid;
    }
    
    public String getPolicyMapKey(){
        return sid+"-"+did+"-"+pid;
    }
    
    public Integer getVersion(){
        return version;
    }
    
    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
    
    public CryptographyPolicyOptions getCryptographyOptions() {
        return cryptographyOptions;
    }

    public RpPolicyOptions getRpOptions() {
        return rpOptions;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public MdsPolicyOptions getMdsOptions() {
        return mdsOptions;
    }

    public String getTokenBindingOption() {
        return tokenBindingOption;
    }

    public CounterPolicyOptions getCounterOptions() {
        return counterOptions;
    }

    public Boolean isUserSettingsRequired() {
        return isUserSettingsRequired;
    }

    public Boolean isStoreSignatures() {
        return isStoreSignaturesRequired;
    }

    public RegistrationPolicyOptions getRegistrationOptions() {
        return registrationOptions;
    }

    public AuthenticationPolicyOptions getAuthenticationOptions() {
        return authenticationOptions;
    }

    public ExtensionsPolicyOptions getExtensionsOptions() {
        return extensionsOptions;
    }
    
    public static FidoPolicyObject parse(String base64Policy, Integer version,
            Long did, Long sid, Long pid, Date startDate, Date endDate) throws SKFEException {
        try {
            String policyString = new String(Base64.getUrlDecoder().decode(base64Policy));
            JsonObject policyJson = applianceCommon.stringToJSON(policyString);

            CryptographyPolicyOptions crypto = CryptographyPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_CRYPTOGRAPHY));

            RpPolicyOptions rp = RpPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_RP));

            int timeoutInt = policyJson.getInt(skfeConstants.POLICY_ATTR_TIMEOUT, -1);
            Integer timeout = (timeoutInt == -1) ? null : timeoutInt;

            MdsPolicyOptions mds = MdsPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_MDS));

            String tokenBinding = policyJson.getString(skfeConstants.POLICY_ATTR_TOKENBINDING, null);

            CounterPolicyOptions counter = CounterPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_COUNTER));

            Boolean userSettings = skfeCommon.handleNonExistantJsonBoolean(policyJson, skfeConstants.POLICY_ATTR_USERSETTINGS);

            Boolean storeSignatures = skfeCommon.handleNonExistantJsonBoolean(policyJson, skfeConstants.POLICY_ATTR_STORESIGNATURES);

            RegistrationPolicyOptions registration = RegistrationPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_REGISTRATION));

            AuthenticationPolicyOptions authentication = AuthenticationPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_AUTHENTICATION));
            
            ExtensionsPolicyOptions extensions = ExtensionsPolicyOptions.parse(
                    policyJson.getJsonObject(skfeConstants.POLICY_ATTR_EXTENSIONS));

            return new FidoPolicyObject.FidoPolicyObjectBuilder(did, sid, pid, version,
                    startDate, endDate, crypto, rp, mds, counter, registration, authentication)
                    .setTimeout(timeout)
                    .setTokenBindingOption(tokenBinding)
                    .setIsUserSettingsRequired(userSettings)
                    .setIsStoreSignatureRequired(storeSignatures)
                    .setBuilderExtensionsOptions(extensions)
                    .build();
        } catch (ClassCastException | NullPointerException ex) {
            ex.printStackTrace();
            throw new SKFEException(ex.getLocalizedMessage());      //TODO replace with standard parsing error message
        }
    }
    
    public static class FidoPolicyObjectBuilder{
        private final Long builderDid;
        private final Long builderSid;
        private final Long builderPid;
        private final Integer builderVersion;
        private final Date builderStartDate;
        private final Date builderEndDate;
        private final CryptographyPolicyOptions builderCryptographyOptions;
        private final RpPolicyOptions builderRpOptions;
        private Integer builderTimeout;
        private final MdsPolicyOptions builderMdsOptions;
        private String builderTokenBindingOption;
        private final CounterPolicyOptions builderCounterOptions;
        private Boolean builderIsUserSettingsRequired;
        private Boolean builderIsStoreSignaturesRequired;
        private final RegistrationPolicyOptions builderRegistrationOptions;
        private final AuthenticationPolicyOptions builderAuthenticationOptions;
        private ExtensionsPolicyOptions builderExtensionsOptions;
        
        public FidoPolicyObjectBuilder(
                Long did, Long sid, Long pid, Integer version, Date startDate, 
                Date endDate, CryptographyPolicyOptions cryptographyOptions,
                RpPolicyOptions rpOptions, MdsPolicyOptions mdsOptions,
                CounterPolicyOptions counterOptions,
                RegistrationPolicyOptions registrationOptions,
                AuthenticationPolicyOptions authenticationOptions){
            this.builderDid = did;
            this.builderSid = sid;
            this.builderPid = pid;
            this.builderVersion = version;
            this.builderStartDate = startDate;
            this.builderEndDate = endDate;
            this.builderCryptographyOptions = cryptographyOptions;
            this.builderRpOptions = rpOptions;
            this.builderMdsOptions = mdsOptions;
            this.builderCounterOptions = counterOptions;
            this.builderRegistrationOptions = registrationOptions;
            this.builderAuthenticationOptions = authenticationOptions;
        }
        
        public FidoPolicyObjectBuilder setTimeout(Integer timeout) {
            this.builderTimeout = timeout;
            return this;
        }
        
        public FidoPolicyObjectBuilder setTokenBindingOption(String tokenBindingOption) {
            this.builderTokenBindingOption = tokenBindingOption;
            return this;
        }
        
        public FidoPolicyObjectBuilder setIsUserSettingsRequired(Boolean isUserSettingsRequired) {
            this.builderIsUserSettingsRequired = isUserSettingsRequired;
            return this;
        }
        
        public FidoPolicyObjectBuilder setIsStoreSignatureRequired(Boolean isStoreSignaturesRequired) {
            this.builderIsStoreSignaturesRequired = isStoreSignaturesRequired;
            return this;
        }

        public FidoPolicyObjectBuilder setBuilderExtensionsOptions(ExtensionsPolicyOptions builderExtensionsOptions) {
            this.builderExtensionsOptions = builderExtensionsOptions;
            return this;
        }
        
        public FidoPolicyObject build(){
            return new FidoPolicyObject(
                    builderDid, builderSid, builderPid, builderVersion, builderStartDate, 
                    builderEndDate, builderCryptographyOptions, builderRpOptions,
                    builderTimeout, builderMdsOptions, builderTokenBindingOption,
                    builderCounterOptions, builderIsUserSettingsRequired,
                    builderIsStoreSignaturesRequired, builderRegistrationOptions,
                    builderAuthenticationOptions, builderExtensionsOptions);
        }
    }
}
