package com.strongauth.skfe.fido2;

import com.webauthn4j.attestation.AttestationObject;
import com.webauthn4j.attestation.statement.AttestationType;
import com.webauthn4j.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.client.Origin;
import com.webauthn4j.client.challenge.Challenge;
import com.webauthn4j.client.challenge.DefaultChallenge;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.test.authenticator.model.WebAuthnModelAuthenticatorAdaptor;
import com.webauthn4j.test.client.AttestationConveyancePreference;
import com.webauthn4j.test.client.AuthenticatorAssertionResponse;
import com.webauthn4j.test.client.AuthenticatorAttachment;
import com.webauthn4j.test.client.AuthenticatorAttestationResponse;
import com.webauthn4j.test.client.AuthenticatorSelectionCriteria;
import com.webauthn4j.test.client.AuthenticatorTransport;
import com.webauthn4j.test.client.ClientPlatform;
import com.webauthn4j.test.client.PublicKeyCredential;
import com.webauthn4j.test.client.PublicKeyCredentialCreationOptions;
import com.webauthn4j.test.client.PublicKeyCredentialDescriptor;
import com.webauthn4j.test.client.PublicKeyCredentialParameters;
import com.webauthn4j.test.client.PublicKeyCredentialRequestOptions;
import com.webauthn4j.test.client.PublicKeyCredentialRpEntity;
import com.webauthn4j.test.client.PublicKeyCredentialType;
import com.webauthn4j.test.client.PublicKeyCredentialUserEntity;
import com.webauthn4j.test.client.UserVerificationRequirement;
import com.webauthn4j.util.Base64UrlUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

/**
 *
 * @author dpatterson
 */
public class Fido2TokenSim {

    private Origin origin = null;
    private String domain = null;
    private WebAuthnModelAuthenticatorAdaptor webAuthnModelAuthenticatorAdaptor = new WebAuthnModelAuthenticatorAdaptor();
    private ClientPlatform clientPlatform;

    static Map<String, Object> props = new HashMap<>();
    private List<PublicKeyCredentialDescriptor> list = new ArrayList<>();
    static String DATA_FILE = "authenticator-data.ser";
        
    public Fido2TokenSim(String origin) {
        this.origin = new Origin(origin);
        String host = this.origin.getHost();
        this.domain = host.substring(host.indexOf(".") + 1);
        clientPlatform = new ClientPlatform(this.origin, webAuthnModelAuthenticatorAdaptor);
    }
    
    public void saveList() {
        FileOutputStream fos=null;
        ObjectOutputStream oos=null;
        try {
            try {
                fos = new FileOutputStream(DATA_FILE);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(list);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Error saving authenticator data.");
            } finally {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
        } catch (Exception ex) {
            // do nothing
        }
    }
    
    public void loadList() {
        FileInputStream fis=null;
        ObjectInputStream ois=null;
        try {
            try {
                fis = new FileInputStream(DATA_FILE);
                ois = new ObjectInputStream(fis);
                list = (List<PublicKeyCredentialDescriptor>)ois.readObject();
            } catch (FileNotFoundException ex) {
                //ex.printStackTrace();
                System.out.println("No saved authenticator data.  Using empty list.");
                list = new ArrayList<>();
            } catch (Exception ex2) {
                ex2.printStackTrace();
                System.out.println("Error reading authenticator data.  Using empty list.");
                list = new ArrayList<>();
            } finally {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            }
        } catch (Exception ex) {
            // do nothing
        }
    }

    public JsonObject create(JsonObject in) {

        AuthenticatorSelectionCriteria authenticatorSelectionCriteria = new AuthenticatorSelectionCriteria();
        authenticatorSelectionCriteria.setAuthenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM);
        //DBP
        authenticatorSelectionCriteria.setRequireResidentKey(false);
        authenticatorSelectionCriteria.setUserVerificationRequirement(UserVerificationRequirement.REQUIRED);

        JsonArray pubKeyCredParamsArray = in.getJsonArray(WebAuthn.PUBKEYCREDPARAMS);
        JsonObject pubKeyCredParams = pubKeyCredParamsArray.getJsonObject(0);
        PublicKeyCredentialParameters publicKeyCredentialParameters = new PublicKeyCredentialParameters();
        publicKeyCredentialParameters.setAlg(COSEAlgorithmIdentifier.create(pubKeyCredParams.getInt(WebAuthn.PUBKEYCREDPARAMS_ALG)));
        publicKeyCredentialParameters.setType(PublicKeyCredentialType.PublicKey);

        JsonObject user = in.getJsonObject(WebAuthn.USER);
        String userName = user.getString(WebAuthn.USER_NAME);
        String userId = user.getString(WebAuthn.USER_ID);
        String userDisplayName = user.getString(WebAuthn.USER_DISPLAY_NAME, userName);
        PublicKeyCredentialUserEntity publicKeyCredentialUserEntity = new PublicKeyCredentialUserEntity(userId.getBytes(), userName, userDisplayName);

        JsonObject rp = in.getJsonObject(WebAuthn.RELYING_PARTY);
        String name = rp.getString(WebAuthn.RELYING_PARTY_NAME);
        String rpid = rp.getString(WebAuthn.RELYING_PARTY_RPID, domain);
        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpid, name));
        String challengeB64 = in.getString(WebAuthn.CHALLENGE);
        Challenge challenge = new DefaultChallenge(challengeB64);
        credentialCreationOptions.setChallenge(challenge);

        credentialCreationOptions.setAttestation(AttestationConveyancePreference.valueOf(in.getString(WebAuthn.ATTESTATION_PREFERENCE, AttestationType.NONE.toString()).toUpperCase()));
        credentialCreationOptions.setAuthenticatorSelection(authenticatorSelectionCriteria);
        credentialCreationOptions.setPubKeyCredParams(Collections.singletonList(publicKeyCredentialParameters));
        credentialCreationOptions.setUser(publicKeyCredentialUserEntity);

        PublicKeyCredential<AuthenticatorAttestationResponse> pkc = clientPlatform.create(credentialCreationOptions);
        AuthenticatorAttestationResponse registrationResp = pkc.getAuthenticatorResponse();

        JsonObjectBuilder resp = Json.createObjectBuilder();
        resp.add(WebAuthn.ATTESTATION_OJBECT, Base64UrlUtil.encodeToString(registrationResp.getAttestationObject()));
        resp.add(WebAuthn.CLIENT_DATA_JSON, Base64UrlUtil.encodeToString(registrationResp.getClientDataJSON()));
        JsonParser jp = Json.createParser(new StringReader(new String(registrationResp.getClientDataJSON())));
        jp.next();
        System.out.print("ClientData ");
        printObject(jp.getObject());

        AttestationObjectConverter conv = new AttestationObjectConverter();
        AttestationObject aobj = conv.convert(registrationResp.getAttestationObject());

        System.out.println("AttObj " + aobj.getAuthenticatorData().hashCode() + " " + conv.convertToString(aobj));

        loadList();        
        list.add(new PublicKeyCredentialDescriptor(
                PublicKeyCredentialType.PublicKey,
                pkc.getRawId(),
                Arrays.asList(AuthenticatorTransport.USB, AuthenticatorTransport.NFC, AuthenticatorTransport.BLE)
        ));
        saveList();

        JsonObjectBuilder ret = Json.createObjectBuilder();
        ret.add(WebAuthn.ID, pkc.getId());
        //ret.add(WebAuthn.RAW_ID, new String(pkc.getRawId()));
        ret.add(WebAuthn.TYPE, PublicKeyCredentialType.PublicKey.getValue());
        ret.add(WebAuthn.RESPONSE, resp);

        JsonObject jo = ret.build();

        printObject(jo);

        return jo;
    }

    private AttestationObject createAttestationObject(String rpId, Challenge challenge) {
        AuthenticatorSelectionCriteria authenticatorSelectionCriteria = new AuthenticatorSelectionCriteria();
        authenticatorSelectionCriteria.setAuthenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM);
        //DBP
        authenticatorSelectionCriteria.setRequireResidentKey(false);
        authenticatorSelectionCriteria.setUserVerificationRequirement(UserVerificationRequirement.REQUIRED);

        PublicKeyCredentialParameters publicKeyCredentialParameters = new PublicKeyCredentialParameters();
        publicKeyCredentialParameters.setAlg(COSEAlgorithmIdentifier.ES256);
        publicKeyCredentialParameters.setType(PublicKeyCredentialType.PublicKey);

        PublicKeyCredentialUserEntity publicKeyCredentialUserEntity = new PublicKeyCredentialUserEntity();
        publicKeyCredentialParameters.setAlg(COSEAlgorithmIdentifier.ES256);
        publicKeyCredentialParameters.setType(PublicKeyCredentialType.PublicKey);

        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions();
        credentialCreationOptions.setRp(new PublicKeyCredentialRpEntity(rpId, domain));
        credentialCreationOptions.setChallenge(challenge);
        credentialCreationOptions.setAttestation(AttestationConveyancePreference.NONE);
        credentialCreationOptions.setAuthenticatorSelection(authenticatorSelectionCriteria);
        credentialCreationOptions.setPubKeyCredParams(Collections.singletonList(publicKeyCredentialParameters));
        credentialCreationOptions.setUser(publicKeyCredentialUserEntity);

        AuthenticatorAttestationResponse registrationRequest = clientPlatform.create(credentialCreationOptions).getAuthenticatorResponse();
        AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter();
        return attestationObjectConverter.convert(registrationRequest.getAttestationObject());
    }

    public JsonObject get(JsonObject in) {

        String rpId = in.getString(WebAuthn.RP_ID, domain);
        long timeout = in.getInt(WebAuthn.TIMEOUT, 0);
        Challenge challenge = new DefaultChallenge(in.getString(WebAuthn.CHALLENGE));

        loadList();
        // get
        PublicKeyCredentialRequestOptions credentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                timeout,
                rpId,
                list,
                UserVerificationRequirement.REQUIRED,
                null
        );
        PublicKeyCredential<AuthenticatorAssertionResponse> publicKeyCredential = clientPlatform.get(credentialRequestOptions);
        AuthenticatorAssertionResponse authenticationResp = publicKeyCredential.getAuthenticatorResponse();

        JsonObjectBuilder resp = Json.createObjectBuilder();
        resp.add(WebAuthn.CLIENT_DATA_JSON, Base64UrlUtil.encodeToString(authenticationResp.getClientDataJSON()));
        resp.add(WebAuthn.AUTHENTICATOR_DATA, Base64UrlUtil.encodeToString(authenticationResp.getAuthenticatorData()));
        resp.add(WebAuthn.SIGNATURE, Base64UrlUtil.encodeToString(authenticationResp.getSignature()));
        if (authenticationResp.getUserHandle() == null) {
            resp.add(WebAuthn.USER_HANDLE, "null");
        } else {
            resp.add(WebAuthn.USER_HANDLE, Base64UrlUtil.encodeToString(authenticationResp.getUserHandle()));
        }

        JsonObjectBuilder ret = Json.createObjectBuilder();
        ret.add(WebAuthn.ID, publicKeyCredential.getId());
        //ret.add(WebAuthn.RAW_ID, new String(publicKeyCredential.getRawId()));
        ret.add(WebAuthn.TYPE, PublicKeyCredentialType.PublicKey.getValue());
        ret.add(WebAuthn.RESPONSE, resp);

        JsonObject jo = ret.build();

        printObject(jo);

        return jo;
    }

    public static void main(String[] args) throws IOException {
        Fido2TokenSim sim = new Fido2TokenSim("http://tellaro1.rdu.strongkey.com");

        props.put(JsonGenerator.PRETTY_PRINTING, true);

        JsonObject regReq = getFile("register.txt");
        JsonObject authReq = getFile("auth.txt");

        JsonObject jo;
        jo = sim.create(regReq);

        jo = sim.get(authReq);
    }

    static private JsonObject getFile(String filename) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        JsonParser jp = Json.createParser(is);
        jp.next();
        return jp.getObject();
    }

    private static void printObject(JsonObject jo) {
        JsonGenerator jg = Json.createGeneratorFactory(props).createGenerator(System.out);
        jg.write(jo);
        jg.flush();
        System.out.println();
    }

}

/*
    "none" attestation statement. (8.7)
    "authenticatorGetAssertion". (Modified 6.2.3 since input will come from "RP" instead of "client")
    "u2f" attestation statement. (8.6)
    "packed" attestation statement. (8.2)
    u2f extension(?)
    Everything else



Unfortunately, my Glassfish server is acting up so I can't give you an actual input/output but here are some of my notes for what an input/output looks like for registration.

Input:

{
        "rp": {
            "name": Human readable string (eg company name),
            "icon": image associated with the RP [optional],
            "rpid": effective domain of site [optional]
        },
        "user": {
            "name": username,
            "icon": image associated with the user [optional],
            "id": Random bytes stored by the server that uniquely identifies the user,
            "displayName": displayName
        },
        "challenge": BASE64 challenge [needs to be converted to a buffer before use],
        "pubKeyCredParams":{
            "type": "public-key",
            "alg": -7 or -257 (ES256 or RS256 respectively)
        },
        "timeout":  "hint" for the amount of time the browser should wait before timing out
                    (it can choose to completely ignore this value in its implementation),
        "excludeCredentials": [
            {
                "type": "public-key",
                "id": Credential ID,
                "transports": "usb", "nfc", "ble" [optional]
            },
            ...
        ] [optional],
        "authenticatorSelection": {
            "authenticatorAttachment": "platform" or "cross-platform" [optional],
            "requireResidentKey": false (default) or true [optional],
            "userVerification": "required" or "preferred" or "discouraged" [optional]
        }
        "attestation": "none" or "indirect" or "direct" [optional],
        "extensions": {
            "txAuthSimple": prompt string [optional],
            "txAuthGeneric": prompt image to be displayed on the authenticator [optional],
            "authnSel": [
                List of "AAGUID" in BASE64 that need to be converted to buffers before use [optional]
            ] [optional],
            "exts": true [optional],
            "uvi": true [optional],
            "loc": true [optional],
            "uvm": true [optional],
            "biometricPerfBounds": {
                "FAR": maximum false acceptance rate for a biometric authenticator allowed [optional]
                "FRR":maximum false rejection rate for a biometric authenticator  allowed [optional]
            }[optional]
        }[optional]
    }

Output:

{

    id: Hex String

    rawId: Hex String

    response: {

        attestationObject: Hex String

        clientDataJSON: Hex String

    }

    type: "public-key"

}

"attestationObject" is a structure itself (CBOR):

{

    authData: Hex String

    fmt: String

    attStmt: String

}

authData is a structure (fixed format):

{

    RP ID hash: 32 bytes

    Flags: 1 byte

    Counter: 4 bytes

    Attested Cred Data: variable length

    Extensions: variable length

}

Attested Cred Data is a structure (fixed format):

{

    AAGUID: 16 bytes

    L: 2 bytes

    Credential ID: L bytes

    Credential Public Key: COSE_Key

}

Extensions is a structure (CBOR):

{

    "extension name": extension value,

    etc

}

attStmt is a structure (CBOR):

{

    Depends of attStmt Type

}

clientDataJSON is a structure (JSON):

{

    type: String

    challenge: Hex String

    origin: String

    tokenBinding (optional): {

        status: "present", "supported", or "not-supported"

        id: String

    }

}


Authentication Input:

{
    "challenge": BASE64 challenge [needs to be converted to a buffer before use],
    "timeout":  "hint" for the amount of time the browser should wait before timing out
                    (it can choose to completely ignore this value in its implementation) [optional],
    "rpId": effective domain of site [optional]
     "allowCredentials": [
            {
                "type": "public-key",
                "id": Credential ID,
                "transports": "usb", "nfc", "ble" [optional]
            },
            ...
     ] [optional],
     "userVerification": "required" or "preferred" or "discouraged" [optional default:"preferred"]
     "extensions": {
         "appid" : an appid
         "txAuthSimple": prompt string [optional],
         "txAuthGeneric": prompt image to be displayed on the authenticator [optional],
         "uvi": true [optional],
         "loc": true [optional],
         "uvm": true [optional]
     }[optional]

}

Output:
{
    id: Hex String
    rawId: Hex String
    response: {
        authenticatorData: Hex String
        signature: Hex String
        userHandle: Hex String
        clientDataJSON: Hex String
    }
    type: "public-key"
}

authenticatorData is the same as the "authData" structure defined above. 




I forgot the additional meta data SKCE adds to the FIDO communication. Here are some example JSONs from my FIDO2 key:

Preregistration Input:

{
    "Challenge": {
        "rp": {
            "name": "StrongAuth, Inc."
        },
        "user": {
            "name": "fidouser06110896",
            "displayName": "fidouser06110896",
            "id": "B16lQ8O1ZDTNX0NP0EP8dNRV6ShLlS4cbcWa72r2GyDfleYgFoJe7xZBIvST9PtZB_Jjx8als_XqggjeTQJyFw"
        },
        "challenge": "VciUZwhfiPCdElS0RygNEHAxxKtqUBkFN472KakrjsgqfFLKNm8wOkGYQFaqklFYrtNST1QLSOuaOO9r-428GH7LZ6qJ9NYkdH79jonCDptr5Pt4BfFmQDg0bTJXpc1dLAPRYsyrezVDtTWNw2FX3mibjvst9ThxfNe8deWdVsE",
        "attestation": "direct",
        "pubKeyCredParams": [{
            "type": "public-key",
            "alg": -7
        }, {
            "type": "public-key",
            "alg": -257
        }]
    },
    "Message": "",
    "Error": ""
}


Output:

{
    "id": "vK8kVP6A_B6twIdXTLYc6SaiInlqydUQuRvDfgCMZ5mTyw-ZyTJLg4I4d0r1Q6i9VkrOOfF-44pYoFQxEiQZOQ2BQZ5CtgHozJvaG24WX10gdiR8Fo4WJg1SottpIfFl",
    "rawId": "vK8kVP6A_B6twIdXTLYc6SaiInlqydUQuRvDfgCMZ5mTyw-ZyTJLg4I4d0r1Q6i9VkrOOfF-44pYoFQxEiQZOQ2BQZ5CtgHozJvaG24WX10gdiR8Fo4WJg1SottpIfFl",
    "response": {
        "attestationObject": "o2NmbXRmcGFja2VkZ2F0dFN0bXSjY2FsZyZjc2lnWEYwRAIfXLNaUETSTAJjKd5sOsGezHbnLqni0DOA2ZWYSfQ6ogIhAOv5wAEG_XLqzeeKKWStw7AG_fYIJIvVzQvUgO-829txY3g1Y4NZAkUwggJBMIIB6KADAgECAhAVn3vCzYkY8Shrk0j6nzPiMAoGCCqGSM49BAMCMEkxCzAJBgNVBAYTAkNOMR0wGwYDVQQKDBRGZWl0aWFuIFRlY2hub2xvZ2llczEbMBkGA1UEAwwSRmVpdGlhbiBGSURPMiBDQS0xMCAXDTE4MDQxMTAwMDAwMFoYDzIwMzMwNDEwMjM1OTU5WjBvMQswCQYDVQQGEwJDTjEdMBsGA1UECgwURmVpdGlhbiBUZWNobm9sb2dpZXMxIjAgBgNVBAsMGUF1dGhlbnRpY2F0b3IgQXR0ZXN0YXRpb24xHTAbBgNVBAMMFEZUIEJpb1Bhc3MgRklETzIgVVNCMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEgAZ1XFn7yUmwFajSCpJYl76DCrLv6Cz4j-2gkJZj5UjHHxEnBTO0JEZ4nUz-4QFDipTpgz3iACwvKh3Xb03bXaOBiTCBhjAdBgNVHQ4EFgQUelSCQoBi2Irnr4SYJcSvkak0mPIwHwYDVR0jBBgwFoAUTTvYxGcVG7sT6POE2DBPnWkVwIMwDAYDVR0TAQH_BAIwADATBgsrBgEEAYLlHAIBAQQEAwIFIDAhBgsrBgEEAYLlHAEBBAQSBBBCODJFRDczQzhGQjRFNUEyMAoGCCqGSM49BAMCA0cAMEQCICRLRaO-iNy34CWixqMSz_uG7bwnSiLBBS4xSFHw6LCHAiA0Gr9OHCTyCxpz1T2swqn5FbQbsjprAW8f7_jg5_iQwFkB_zCCAfswggGgoAMCAQICEBWfe8LNiRjxKGuTSPqfM-EwCgYIKoZIzj0EAwIwSzELMAkGA1UEBhMCQ04xHTAbBgNVBAoMFEZlaXRpYW4gVGVjaG5vbG9naWVzMR0wGwYDVQQDDBRGZWl0aWFuIEZJRE8gUm9vdCBDQTAgFw0xODA0MTAwMDAwMDBaGA8yMDM4MDQwOTIzNTk1OVowSTELMAkGA1UEBhMCQ04xHTAbBgNVBAoMFEZlaXRpYW4gVGVjaG5vbG9naWVzMRswGQYDVQQDDBJGZWl0aWFuIEZJRE8yIENBLTEwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASOfmAJ7MEWZcyg-sPpb-UIO5VtVyUR61sy9NZnOVfdZ9i2FzUd_0u5gOYLqbkzuZo0MPMX6iETB1a9agd03nWPo2YwZDAdBgNVHQ4EFgQUTTvYxGcVG7sT6POE2DBPnWkVwIMwHwYDVR0jBBgwFoAU0aGYTYF_w7lr9gdnvVAS_pBF8VQwEgYDVR0TAQH_BAgwBgEB_wIBADAOBgNVHQ8BAf8EBAMCAQYwCgYIKoZIzj0EAwIDSQAwRgIhAPt_o9JAR6ERUMJ4Vm0hzJAWmOyhf087SDRTecpg5MJlAiEA6wpDwYjB172IPpEkYFbCsLlbWKJ0bwufPKkcKS0rWexZAdwwggHYMIIBfqADAgECAhAVn3vCzYkY8Shrk0j6nzPWMAoGCCqGSM49BAMCMEsxCzAJBgNVBAYTAkNOMR0wGwYDVQQKDBRGZWl0aWFuIFRlY2hub2xvZ2llczEdMBsGA1UEAwwURmVpdGlhbiBGSURPIFJvb3QgQ0EwIBcNMTgwNDAxMDAwMDAwWhgPMjA0ODAzMzEyMzU5NTlaMEsxCzAJBgNVBAYTAkNOMR0wGwYDVQQKDBRGZWl0aWFuIFRlY2hub2xvZ2llczEdMBsGA1UEAwwURmVpdGlhbiBGSURPIFJvb3QgQ0EwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASd8ApuO8xfUTLVvqT5ZBB01Uy30mAZbInc-8zgFIrlepN-j77SgCP_i2fDIgvQcUFH1K36S2OpJcN-OJcC6uzzo0IwQDAdBgNVHQ4EFgQU0aGYTYF_w7lr9gdnvVAS_pBF8VQwDwYDVR0TAQH_BAUwAwEB_zAOBgNVHQ8BAf8EBAMCAQYwCgYIKoZIzj0EAwIDSAAwRQIhALexPWUGMZ4X7EpOnNXUphTZyRqFN3iYsnLNg6Foe_iKAiAPYliR_IflDgGmjyuug7Qi3uhiMXaSDL95JndT0aVqrGhhdXRoRGF0YVjkVbRbbET9fGAVrBusj2M_iai2OO3Js0LcQvVeeyQpYAdBAAAAA0I4MkVENzNDOEZCNEU1QTIAYLyvJFT-gPwercCHV0y2HOkmoiJ5asnVELkbw34AjGeZk8sPmckyS4OCOHdK9UOovVZKzjnxfuOKWKBUMRIkGTkNgUGeQrYB6Myb2htuFl9dIHYkfBaOFiYNUqLbaSHxZaUBAgMmIAEhWCADEQ5Y4B4kRsfBquGcqpylM8qtOM5yNr9-2eEZJWVMPSJYIF4fv523510NRtDDkpc6g7GbQp4clfuFfwk1RAbatEO1",
        "clientDataJSON": "eyJjaGFsbGVuZ2UiOiJWY2lVWndoZmlQQ2RFbFMwUnlnTkVIQXh4S3RxVUJrRk40NzJLYWtyanNncWZGTEtObTh3T2tHWVFGYXFrbEZZcnROU1QxUUxTT3VhT085ci00MjhHSDdMWjZxSjlOWWtkSDc5am9uQ0RwdHI1UHQ0QmZGbVFEZzBiVEpYcGMxZExBUFJZc3lyZXpWRHRUV053MkZYM21pYmp2c3Q5VGh4Zk5lOGRlV2RWc0UiLCJvcmlnaW4iOiJodHRwczovL21pbmVydmEuc3Ryb25nYXV0aC5jb206ODE4MSIsInR5cGUiOiJ3ZWJhdXRobi5jcmVhdGUifQ"
    },
    "type": "public-key"
}


Authentication Input:

{
    "Challenge": {
        "challenge": "kZfgt3zqC906IUTwLN7IWyqHc8dS_WeWosHbRiXWzHCozh5dCuckqHC1rOT2IciMV4Qt-VsPAbjAV7Yzfb0gP6hvxxfv5F80A7K6xoUtQC89fi6BXH5wA8AWayzWRxCvjcZw4hXpkxhMSG1kXHqAf5RsZdDUsDeCXXh1aKuknmA",
        "allowCredentials": [{
            "type": "public-key",
            "id": "vK8kVP6A/B6twIdXTLYc6SaiInlqydUQuRvDfgCMZ5mTyw+ZyTJLg4I4d0r1Q6i9VkrOOfF+44pYoFQxEiQZOQ2BQZ5CtgHozJvaG24WX10gdiR8Fo4WJg1SottpIfFl",
            "transports": ["usb"]
        }]
    },
    "Message": "",
    "Error": ""
}


Output:

{
    "id": "vK8kVP6A_B6twIdXTLYc6SaiInlqydUQuRvDfgCMZ5mTyw-ZyTJLg4I4d0r1Q6i9VkrOOfF-44pYoFQxEiQZOQ2BQZ5CtgHozJvaG24WX10gdiR8Fo4WJg1SottpIfFl",
    "rawId": "vK8kVP6A_B6twIdXTLYc6SaiInlqydUQuRvDfgCMZ5mTyw-ZyTJLg4I4d0r1Q6i9VkrOOfF-44pYoFQxEiQZOQ2BQZ5CtgHozJvaG24WX10gdiR8Fo4WJg1SottpIfFl",
    "response": {
        "authenticatorData": "VbRbbET9fGAVrBusj2M_iai2OO3Js0LcQvVeeyQpYAcFAAAAAw",
        "signature": "MEQCIGWE5fwhOmkTHPszS8R9SawQxo_E0guuhJE67ESJYeBjAiBgEOsxkQOicFNsKZgidwUMJmH3VGUJVxgPHErXirL9VA",
        "userHandle": "",
        "clientDataJSON": "eyJjaGFsbGVuZ2UiOiJrWmZndDN6cUM5MDZJVVR3TE43SVd5cUhjOGRTX1dlV29zSGJSaVhXekhDb3poNWRDdWNrcUhDMXJPVDJJY2lNVjRRdC1Wc1BBYmpBVjdZemZiMGdQNmh2eHhmdjVGODBBN0s2eG9VdFFDODlmaTZCWEg1d0E4QVdheXpXUnhDdmpjWnc0aFhwa3hoTVNHMWtYSHFBZjVSc1pkRFVzRGVDWFhoMWFLdWtubUEiLCJvcmlnaW4iOiJodHRwczovL21pbmVydmEuc3Ryb25nYXV0aC5jb206ODE4MSIsInR5cGUiOiJ3ZWJhdXRobi5nZXQifQ"
    },
    "type": "public-key"
}

*/
