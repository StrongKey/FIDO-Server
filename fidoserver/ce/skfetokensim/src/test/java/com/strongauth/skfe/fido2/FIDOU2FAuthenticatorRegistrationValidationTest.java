/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strongauth.skfe.fido2;

import com.webauthn4j.WebAuthnRegistrationContext;
import com.webauthn4j.anchor.TrustAnchorProvider;
import com.webauthn4j.anchor.WebAuthnTrustAnchorService;
import com.webauthn4j.anchor.WebAuthnTrustAnchorServiceImpl;
import com.webauthn4j.client.ClientDataType;
import com.webauthn4j.client.CollectedClientData;
import com.webauthn4j.client.Origin;
import com.webauthn4j.client.challenge.Challenge;
import com.webauthn4j.client.challenge.DefaultChallenge;
import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.test.TestUtil;
import com.webauthn4j.test.authenticator.fido.u2f.FIDOU2FAuthenticatorAdaptor;
import com.webauthn4j.test.client.*;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import com.webauthn4j.validator.attestation.fido.FIDOU2FAttestationStatementValidator;
import com.webauthn4j.validator.attestation.none.NoneAttestationStatementValidator;
import com.webauthn4j.validator.attestation.trustworthiness.certpath.TrustAnchorCertPathTrustworthinessValidator;
import com.webauthn4j.validator.attestation.trustworthiness.ecdaa.DefaultECDAATrustworthinessValidator;
import com.webauthn4j.validator.attestation.trustworthiness.self.DefaultSelfAttestationTrustworthinessValidator;
import com.webauthn4j.validator.exception.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;

public class FIDOU2FAuthenticatorRegistrationValidationTest {

    private Origin origin = new Origin("http://localhost");
    private ClientPlatform clientPlatform = new ClientPlatform(origin, new FIDOU2FAuthenticatorAdaptor());
    private NoneAttestationStatementValidator noneAttestationStatementValidator = new NoneAttestationStatementValidator();
    private FIDOU2FAttestationStatementValidator fidoU2FAttestationStatementValidator = new FIDOU2FAttestationStatementValidator();
    private TrustAnchorProvider trustAnchorProvider = TestUtil.createTrustAnchorProviderWith2tierTestRootCACertificate();
    private WebAuthnTrustAnchorService webAuthnTrustAnchorService = new WebAuthnTrustAnchorServiceImpl(trustAnchorProvider);
    private WebAuthnRegistrationContextValidator target = new WebAuthnRegistrationContextValidator(
            Arrays.asList(noneAttestationStatementValidator, fidoU2FAttestationStatementValidator),
            new TrustAnchorCertPathTrustworthinessValidator(webAuthnTrustAnchorService),
            new DefaultECDAATrustworthinessValidator(),
            new DefaultSelfAttestationTrustworthinessValidator()
    );

    @Test
    public void validate_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test
    public void validate_with_direct_attestation_conveyance_preference_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.DIRECT);
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test(expected = MaliciousDataException.class)
    public void validate_with_bad_clientData_type_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);

        CollectedClientData collectedClientData = clientPlatform.createCollectedClientData(ClientDataType.GET, challenge);
        RegistrationEmulationOption registrationEmulationOption = new RegistrationEmulationOption();
        registrationEmulationOption.setCollectedClientData(collectedClientData);
        registrationEmulationOption.setCollectedClientDataOverrideEnabled(true);
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions, registrationEmulationOption).getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test(expected = BadChallengeException.class)
    public void validate_with_bad_challenge_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        Challenge badChallenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(badChallenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);

        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test(expected = BadOriginException.class)
    public void validate_with_bad_origin_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        Origin badOrigin = new Origin("http://bad.origin.example.net");
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);

        clientPlatform.setOrigin(badOrigin); //bad origin
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test(expected = BadRpIdException.class)
    public void validate_with_bad_rpId_test() {
        String rpId = "example.com";
        String badRpId = "example.net";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(badRpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test(expected = BadAttestationStatementException.class)
    public void validate_with_bad_attestationStatement_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);

        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        WebAuthnRegistrationContextValidator target = new WebAuthnRegistrationContextValidator(
                Collections.singletonList(fidoU2FAttestationStatementValidator),
                new TrustAnchorCertPathTrustworthinessValidator(mock(WebAuthnTrustAnchorService.class)),
                new DefaultECDAATrustworthinessValidator(),
                new DefaultSelfAttestationTrustworthinessValidator()
        );
        target.validate(registrationContext);
    }

    @Test(expected = BadSignatureException.class)
    public void validate_invalid_format_attestation_signature_test() {
        String rpId = "example.com";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.DIRECT);
        RegistrationEmulationOption registrationEmulationOption = new RegistrationEmulationOption();
        registrationEmulationOption.setSignatureOverrideEnabled(true);
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions, registrationEmulationOption).getAuthenticatorResponse();

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(registrationRequest.getClientDataJSON(), registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }

    @Test(expected = BadSignatureException.class)
    public void validate_malicious_client_data_test() {
        Origin phishingSiteOrigin = new Origin("http://phishing.site.example.com");
        Origin validSiteOrigin = new Origin("http://valid.site.example.com");
        Origin phishingSiteClaimingOrigin = new Origin("http://valid.site.example.com");

        ClientPlatform clientPlatform = new ClientPlatform(phishingSiteOrigin, new FIDOU2FAuthenticatorAdaptor()); // client platform loads phishing site
        String rpId = "valid.site.example.com";
        Challenge challenge = new DefaultChallenge();
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, "valid.site.example.com"));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.DIRECT);
        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();

        CollectedClientData maliciousClientData = new CollectedClientData(ClientDataType.CREATE, challenge, phishingSiteClaimingOrigin, null);
        byte[] maliciousClientDataBytes = new CollectedClientDataConverter().convertToBytes(maliciousClientData);
        ServerProperty serverProperty = new ServerProperty(validSiteOrigin, rpId, challenge, null);
        WebAuthnRegistrationContext registrationContext = new WebAuthnRegistrationContext(maliciousClientDataBytes, registrationRequest.getAttestationObject(), serverProperty, false);
        target.validate(registrationContext);
    }


}
