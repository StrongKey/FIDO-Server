
# StrongKey FIDO2 Server User Guide
## Contents

* [Overview](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#overview)
* [API Calls](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#api-calls)
  * [Registration](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#registration)
  * [Authentication](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#authentication)
  * [Administration](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#administration)
* [Alternate Configurations](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#alternate-configurations)
  * [Options with StrongKey FIDO2 Server](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#options-with-strongkey-fido2-server)
    * [Policies and Their Use](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#policies-and-their-use)
    * [Application HOME Folders and the Path](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#application-home-folders-and-the-path)
  * [Options for the Database Server](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#options-for-the-database-server)
    * [Database Schema for StrongKey FIDO2 Server](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#database-schema-for-strongkey-fido2-server)
  * [Options for the Java Web Server](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#options-for-the-java-web-server)
    * [Create JDBC Resources](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#create-jdbc-resources)
* [Deploy StrongKey FIDO2 Server](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#deploy-strongkey-fido-server)
* [WebAuthn Client Files](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#webauthn-client-files)
* [Removing StrongKey FIDO2 Server and Its Components](https://github.com/khedrond/FIDO-Server/blob/master/docs/User_Guide.md#removing-strongkey-fido2-server-and-its-components)

## Overview
So, you've installed StrongKey's FIDO2 Server and decided to delve depper, into its internal workings. This User Guide will shepherd you through the fine points of configuring and permutating the necessary components to make our FIDO Server more customized to suit your needs.

StrongKey's FIDO2 Server has only been tested against a very specific application stack, but should be adaptable to other applications of similar function. This document briefly touches on the necessary API calls, then goes through a vendor-inspecific installation using generic settings and commands.

## API Calls
The following _Application Programmer Interface (API)_ calls are the underpinning pieces of the StrongKey FIDO2 Server. They pass the designated FIDO2 responses necessary to strongly authenticate a user with an appropriate Authenticator (see this list of [FIDO2-certified Authenticator vendors](https://fidoalliance.org/certification/fido-certified-products/)).

### Registration
These calls uniquely register a user, and are required before any other calls can be used. Though it is not required to include a Relying Party (RP) web application in the chain of events, it provides a number of [security benefits](https://www.w3.org/TR/webauthn/#sctn-rp-benefits). Using these calls a user, a Relying Party web applicaton, and the user’s client (containing at least one Authenticator) work in concert to generate a public key credential and associate it with the user’s RP web application account. This requires a test of user presence or user verification. We strongly recommend including a Relying Party web application in your architecture.
- **/fidokeys/challenge**:  This is always the first call made for any user, as it initiates the registration process by obtaining a single-use, cryptographically strong random number (nonce) from the FIDO2 Server via the RP web application. From the FIDO2 Server the nonce is then sent to the Authenticator for signing.
- **/fidokeys**: This call submits a signed challenge (nonce) from the Authenticator to the FIDO2 Server via an RP web application, after which registration is complete and the user may log in.

### Authentication
Authenticate a user using FIDO2 protocols. These calls mirror the registration calls in function. A user must be registered with at least one key before authentication calls can be made. In these calls the user and their client (containing at least one Authenticator) work together to cryptographically prove to an RP web application that the user controls the credential private key associated with a previously-registered public key credential (see Registration, above). This requires a test of user presence or user verification, and will occur with every login attempt.
- **/fidokeys/authenticate/challenge**: Obtains a single-use, cryptographically strong random number (nonce) from the FIDO2 Server via the RP web application. From the FIDO2 Server the nonce is then sent to the Authenticator for signing.
- **/fidokeys/authenticate**: This call submits a signed challenge (nonce) from the Authenticator to the FIDO2 Server via RP web application, after which authentication is complete and the user is logged in.

### Administration
Admin calls are designed for managing registered Authenticators. **{kid}** is the unique ID of the Authenticator being manipulated. These calls require a user to be registered with at least one Authenticator, but not necessarily logged in (authenticated).
- **/fidokeys (GET)**: Gets (via HTTP GET) all Authenticators associated with a registered user. Use this to generate lists and reports.
- **/fidokeys/{kid} (PATCH)**: Updates a registered Authenticator's status (_Active_ or _Inactive_).
- **/fidokeys/{kid} (DELETE)**: Deletes a registered Authenticator. Note that deleting all Authenticators from a user (including yourself) will prevent further logins for that user. If this occurs, either the orphaned user will need to be deleted and re-registered or, if you have built it into your application, a means must be made available for re-registering an Authenticator to the user without logging the user out.

## Alternate Configurations
StrongKey FIDO2 Server has only been tested using MariaDB (+JDBC), Payara, and Open JDK, but may work with other dependency applications. Following is a list of the component parts needed for StrongKey FIDO2 Server to function. All of the components may be installed on the same server, whether physical or virtual.

-   [StrongKey FIDO2 Server, Community Edition](../FIDOServer-v0.9-dist.tgz)
-   A [relational database](https://en.wikipedia.org/wiki/List_of_relational_database_management_systems)
-   A [Java Development Kit](https://en.wikipedia.org/wiki/Java_Development_Kit#External_links)
-   A [Java Enterprise Edition web server](https://en.wikipedia.org/wiki/Java_Platform,_Enterprise_Edition#Certified_referencing_runtimes)
-   A [JDBC version of your choice](https://www.soapui.org/jdbc/reference/jdbc-drivers.html)
-   [Jemalloc 3.6.0-1](https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/Packages/j/jemalloc-3.6.0-1.el7.x86_64.rpm)

For the adventurous who want to explore alternate configurations of the FIDO2 Server, the next sections detail the necessary attributes for installation of each component listed above. If you successfully create a working StrongKey FIDO2 Server using alternative configurations, please inform us as soon as possible at support@strongkey.com.

### Options with StrongKey FIDO2 Server 

#### Policies and Their Use

StrongKey installs a default FIDO2 policy with the StrongKey FIDO2 Server in JSON format, encoded using base64urlsafe in the _install-skfs.sh_ script in _/usr/local/strongkey_. The default policy is configured to approve all signature types, but may use any subset of the available attributes. The options provided in the following table allow the methods used to be tailored to your FIDO2 server's needs. Where appropriate, links have been provided to the various specifications governing each item's use:

Policy Attribute(s) | Accepted Value(s) &mdash; [...] indicates multiples can be chosen  |  More Information 
  :---  |  :---  |  :--
| "cryptography":  |  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"attestation_formats":  |  ["fido-u2f", "packed", "tpm", "android-key", "android-safetynet", "none"] |  [WebAuthn Attestation Statement Formats](https://w3c.github.io/webauthn/#defined-attestation-formats)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"elliptic_curves":  |  ["secp256r1", "secp384r1", "secp521r1", "curve25519"] | SEC 2: Recommended Elliptic Curve Domain Parameters [pages 9-11](http://www.secg.org/sec2-v2.pdf), and [This article](https://en.wikipedia.org/wiki/Curve25519) on curve25519
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"allowed_rsa_signatures":  |  ["rsassa-pkcs1-v1_5-sha1", "rsassa-pkcs1-v1_5-sha256", "rsassa-pkcs1-v1_5-sha384", "rsassa-pkcs1-v1_5-sha512", "rsassa-pss-sha256", "rsassa-pss-sha384", "rsassa-pss-sha512"]  |  Internet Engineering Task Force (IETF) # PKCS #1: RSA Cryptography Specifications Version 2.2 [pages 32-39](https://tools.ietf.org/html/rfc8017#page-32) and [pages 60-62](https://tools.ietf.org/html/rfc8017#page-60))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"allowed_ec_signatures":  |  ["ecdsa-p256-sha256", "ecdsa-p384-sha384", "ecdsa-p521-sha512", "eddsa", "ecdsa-p256k-sha256"]  |  [Elliptic Curve Digital Signature Algorithm (ECDSA)](https://en.wikipedia.org/wiki/Elliptic_Curve_Digital_Signature_Algorithm) and [Edwards-curve Digital Signature Algorithm (EdDSA)](https://en.wikipedia.org/wiki/EdDSA)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"attestation_types":  |  ["basic", "self", "attca", "ecdaa", "none"]  |  [Attestation Types](https://w3c.github.io/webauthn/#sctn-attestation-types) and [WebAuthn Considerations for Self and None Attestation Types and Ignoring Attestation](https://w3c.github.io/webauthn/#sctn-no-attestation-security-attestation)
"registration":  |
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"attestation":  |  ["none", "indirect", "direct"]  |  [Direct Anonymous Attestation](https://en.wikipedia.org/wiki/Direct_Anonymous_Attestation)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"displayName":  |  ["required", "preferred"]  |  Because everyone needs a display name...
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"authenticatorSelection":  |  |  [WebAuthn Authenticator Selection Criteria](https://w3c.github.io/webauthn/#dictdef-authenticatorselectioncriteria)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"authenticatorAttachment":  |  ["platform", "cross-platform"]  |  [WebAuthn Authenticator Taxonomy](https://w3c.github.io/webauthn/#sctn-authenticator-taxonomy)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"userVerification":  |  ["required", "preferred", "discouraged"]  |  [WebAuthn Authenticator Selection Criteria](https://w3c.github.io/webauthn/#dictdef-authenticatorselectioncriteria)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"requireResidentKey":  |  [true, false] (**can be both**)  |  [WebAuthn Authenticator Selection Criteria](https://w3c.github.io/webauthn/#dictdef-authenticatorselectioncriteria)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"excludeCredentials":  |  "enabled" or "disabled" | [WC3 Definition](https://w3c.github.io/webauthn/#dom-publickeycredentialcreationoptions-excludecredentials)
"authentication":  |
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"userVerification":  |  ["required", "preferred", "discouraged"]  |  [Authenticator Selection Criteria](https://w3c.github.io/webauthn/#dictdef-authenticatorselectioncriteria)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"allowCredentials":  |  "enabled" or "disabled"  |  [W3C Definition](https://w3c.github.io/webauthn/#dom-publickeycredentialrequestoptions-allowcredentials)
"rp":  |
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name":  |  "demo.strongauth.com:8181"
"counter": |  |  [WebAuthn Signature Counter Considerations](https://w3c.github.io/webauthn/#signature-counter)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"requireIncrease":  |  true or false
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"requireCounter":  |  true or false
"storeSignatures": | true or false |
"extensions": |  | [WebAuthn Extensions](https://w3c.github.io/webauthn/#extensions)
| "mds":  |  | Used to add a [MetaData Service (MDS)](https://fidoalliance.org/metadata/) endpoint
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"endpoints":  | 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"url":    |  ["https://mds2.fidoalliance.org"]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"token":  |  <Get from [https://mds2.fidoalliance.org/tokens/](https://mds2.fidoalliance.org/tokens/)> 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"certification":  |  ["FIDO_CERTIFIED", "NOT_FIDO_CERTIFIED", "USER_VERIFICATION_BYPASS", "ATTESTATION_KEY_COMPROMISE", "USER_KEY_REMOTE_COMPROMISE", "USER_KEY_PHYSICAL_COMPROMISE", "UPDATE_AVAILABLE", "REVOKED", "SELF_ASSERTION_SUBMITTED", "FIDO_CERTIFIED_L1", "FIDO_CERTIFIED_L1plus", "FIDO_CERTIFIED_L2", "FIDO_CERTIFIED_L2plus", "FIDO_CERTIFIED_L3", "FIDO_CERTIFIED_L3plus"]

The included default policy enables all supported configuration choices for StrongKey FIDO2 Server; use any or all of them as needed. **Prior to installation**, search the _install-skfs.sh_ file contents for "Default Policy" and decode the encoded text entry from there, or copy and paste from the example JSON below. When changes have been made, save it, re-encode it using base64urlsafe, then replace it in the script.
~~~~
{
	"storeSignatures": false,
	"extensions": {
		"example.extension": true
	},
	"userSettings": true,
	"cryptography": {
		"attestation_formats": ["fido-u2f", "packed", "tpm", "android-key", "android-safetynet", "none"],
		"elliptic_curves": ["secp256r1", "secp384r1", "secp521r1", "curve25519"],
		"allowed_rsa_signatures": ["rsassa-pkcs1-v1_5-sha1", "rsassa-pkcs1-v1_5-sha256", "rsassa-pkcs1-v1_5-sha384", "rsassa-pkcs1-v1_5-sha512", "rsassa-pss-sha256", "rsassa-pss-sha384", "rsassa-pss-sha512"],
		"allowed_ec_signatures": ["ecdsa-p256-sha256", "ecdsa-p384-sha384", "ecdsa-p521-sha512", "eddsa", "ecdsa-p256k-sha256"],
		"attestation_types": ["basic", "self", "attca", "ecdaa", "none"]
	},
	"registration": {
		"attestation": ["none", "indirect", "direct"],
		"displayName": "required",
		"authenticatorSelection": {
			"authenticatorAttachment": ["platform", "cross-platform"],
			"userVerification": ["required", "preferred", "discouraged"],
			"requireResidentKey": [true, false]
		},
		"excludeCredentials": "enabled"
	},
	"counter": {
		"requireIncrease": true,
		"requireCounter": false
	},
	"rp": {
		"name": "demo.strongauth.com:8181"
	},
	"authentication": {
		"userVerification": ["required", "preferred", "discouraged"],
		"allowCredentials": "enabled"
	}
}
~~~~

#### Application HOME Folders and the Path

The StrongKey FIDO2 Server is completely configurable to suit a specific enterprise environment. Application HOME folders and environment variables must be in place before the software is deployed and run. 

1.  Open a  **terminal window**.
    
2.  Edit the  _/etc/bashrc_  file and export the variables using the command below:
    
    `sudo vi /etc/bashrc`
    
3.  **Add these lines**  at the end of the file:
    
     `export FIDOSERVER_HOME=/usr/local/strongkey/skfs` 
     
     `export <WEB_SERVER>_HOME=/usr/local/strongkey/<path-to-web-server-home>/`
     
     `PATH=$<WEB_SERVER>_HOME/bin:$PATH`
    
4.  **Save**  and  **close**  the file and  **exit**  out of  _root_.
    
    `:wq`  `exec bash`
    
5.  Veirfy the paths have been included in the environment variables:
    
    `printenv`

### Options for the Database Server
The instructions for database installation assume strong administrative experience with _structured query language (SQL)_ and methods for your database of choice. StrongKey FIDO2 Server has been tested using MariaDB 10.2.13, but other databases may work. Where possible, _American National Standards Institute (ANSI)_ SQL commands have been used, but due to the variable nature of proprietary SQL, we leave it to the database administrator to choose the appropriate commands for the particular flavor of SQL being used. Beyond the initial creation of users and tables, and the inserting of data therein, there is no reason for direct manipulation of the database once StrongKey FIDO2 Server is installed.

The StrongKey FIDO2 Server code references table and column names in all lowercase. Despite the fact that the create and insert statements used include uppercase names, please check to make sure all the table and column names in the _skfs_ database are lowercase before proceeding.

The database may be on the same or a different machine (virtual or physical) than the StrongKey FIDO2 Server.

#### Database Schema for StrongKey FIDO2 Server

1.  **Login** to the database server via terminal using sudo and the default/owner database. This will open database server access.

    `sudo mysql --user=<dbo username> --password=<dbo password>`
    
2.  **Create a database** called  _skfs_  and a database user called _skfsdbuser_ for the StrongKey FIDO2 Server application, then grant privileges for the user on the new database. Create a strong password for _skfsdbuser_.
    
    `create database skfs;`
    `grant all on skfs.* to skfsdbuser@localhost identified by '<PASSWORD>';`
    
3.  **Log out**  of the database admin role.
    
4.  **Change Directory** to the database home folder (if unknown, use `printenv` to find MYSQL_HOME). **Login**  to the database server as _skfsdbuser_ using the _skfs_ database.
    
5.  **Execute the commands** in _fidoserver/fidoserverInstall/src/fidoserverSQL/mysql/create.txt_ to create the tables. We recommend generating a list of tables to verify all were created (methods vary by database). 
    
6.  Add the default entries to the the SERVERS, DOMAINS, and FIDO_POLICIES tables. The large hash strings contained between "BEGIN CERTIFICATE" and "END CERTIFICATE" are certificates.
    
    `insert into SERVERS values (1, '$(hostname)', 'Active', 'Both', 'Active', null, null);`
    
    `insert into DOMAINS values (1,'SKFS','Active','Active','-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'-----BEGIN CERTIFICATE-----\nMIIDizCCAnOgAwIBAgIENIYcAzANBgkqhkiG9w0BAQsFADBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwHhcNMTkwMTMwMjI1NDAwWhcNMTkwNDMwMjI1NDAwWjBuMRcwFQYDVQQKEw5T\ndHJvbmdBdXRoIEluYzEjMCEGA1UECxMaU0tDRSBTaWduaW5nIENlcnRpZmljYXRl\nIDExEzARBgNVBAsTClNBS0EgRElEIDExGTAXBgNVBAMTEFNLQ0UgU2lnbmluZyBL\nZXkwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH/W7ERX0U3a+2VLBY\nyjpCRTCdRtiuiLv+C1j64gLAyseF5sMH+tLNcqU0WgdZ3uQxb2+nl2y8Cp0B8Cs9\nvQi9V9CIC7zvMvgveQ711JqX8RMsaGBrn+pWx61E4B1kLCYCPSI48Crm/xkMydGM\nTKXHpfb+t9uo/uat/ykRrel5f6F764oo0o1KJkY6DjFEMh9TKMbJIeF127S2pFxl\nNNBhawTDGDaA1ag9GoWHGCWZ/bbCMMiwcH6q71AqRg8qby1EsBKA7E4DD8f+5X6b\nU3zcY3kudKlYxP4rix42PHCY3B4ZnpWS3A6lZRBot7NklsLvlxvDbKIiTcyDvSA0\nunfpAgMBAAGjMTAvMA4GA1UdDwEB/wQEAwIHgDAdBgNVHQ4EFgQUlSKnwxvmv8Bh\nlkFSMeEtAM7AyakwDQYJKoZIhvcNAQELBQADggEBAG2nosn6cTsZTdwRGws61fhP\n+tvSZXpE5mYk93x9FTnApbbsHJk1grWbC2psYxzuY1nYTqE48ORPngr3cHcNX0qZ\npi9JQ/eh7AaCLQcb1pxl+fJAjnnHKCKpicyTvmupv6c97IE4wa2KoYCJ4BdnJPnY\nnmnePPqDvjnAhuCTaxSRz59m7aW4Tyt9VPsoBShrCSBYzK5cH3FNIGffqB7zI3Jh\nXo0WpVD/YBE/OsWRbthZ0OquJIfxcpdXS4srCFocQlqNMhlQ7ZVOs73WrRx+uGIr\nhUYvIJrqgAc7+F0I7v2nAQLmxMBYheZDhN9DA9LuJRV93A8ELIX338DKxBKBPPU=\n-----END CERTIFICATE-----',NULL,'CN=SKFS Signing Key,OU=SAKA DID 1,OU=SKFS Signing Certificate 1,O=StrongKey Inc','https://localhost:8181',NULL);`
    
    `insert into FIDO_POLICIES values (1,1,1,NOW(),NULL,'Default Policy','eyJzdG9yZVNpZ25hdHVyZXMiOmZhbHNlLCJleHRlbnNpb25zIjp7ImV4YW1wbGUuZXh0ZW5zaW9uIjp0cnVlfSwidXNlclNldHRpbmdzIjp0cnVlLCJjcnlwdG9ncmFwaHkiOnsiYXR0ZXN0YXRpb25fZm9ybWF0cyI6WyJmaWRvLXUyZiIsInBhY2tlZCIsInRwbSIsImFuZHJvaWQta2V5IiwiYW5kcm9pZC1zYWZldHluZXQiLCJub25lIl0sImVsbGlwdGljX2N1cnZlcyI6WyJzZWNwMjU2cjEiLCJzZWNwMzg0cjEiLCJzZWNwNTIxcjEiLCJjdXJ2ZTI1NTE5Il0sImFsbG93ZWRfcnNhX3NpZ25hdHVyZXMiOlsicnNhc3NhLXBrY3MxLXYxXzUtc2hhMSIsInJzYXNzYS1wa2NzMS12MV81LXNoYTI1NiIsInJzYXNzYS1wa2NzMS12MV81LXNoYTM4NCIsInJzYXNzYS1wa2NzMS12MV81LXNoYTUxMiIsInJzYXNzYS1wc3Mtc2hhMjU2IiwicnNhc3NhLXBzcy1zaGEzODQiLCJyc2Fzc2EtcHNzLXNoYTUxMiJdLCJhbGxvd2VkX2VjX3NpZ25hdHVyZXMiOlsiZWNkc2EtcDI1Ni1zaGEyNTYiLCJlY2RzYS1wMzg0LXNoYTM4NCIsImVjZHNhLXA1MjEtc2hhNTEyIiwiZWRkc2EiLCJlY2RzYS1wMjU2ay1zaGEyNTYiXSwiYXR0ZXN0YXRpb25fdHlwZXMiOlsiYmFzaWMiLCJzZWxmIiwiYXR0Y2EiLCJlY2RhYSIsIm5vbmUiXX0sInJlZ2lzdHJhdGlvbiI6eyJhdHRlc3RhdGlvbiI6WyJub25lIiwiaW5kaXJlY3QiLCJkaXJlY3QiXSwiZGlzcGxheU5hbWUiOiJyZXF1aXJlZCIsImF1dGhlbnRpY2F0b3JTZWxlY3Rpb24iOnsiYXV0aGVudGljYXRvckF0dGFjaG1lbnQiOlsicGxhdGZvcm0iLCJjcm9zcy1wbGF0Zm9ybSJdLCJ1c2VyVmVyaWZpY2F0aW9uIjpbInJlcXVpcmVkIiwicHJlZmVycmVkIiwiZGlzY291cmFnZWQiXSwicmVxdWlyZVJlc2lkZW50S2V5IjpbdHJ1ZSxmYWxzZV19LCJleGNsdWRlQ3JlZGVudGlhbHMiOiJlbmFibGVkIn0sImNvdW50ZXIiOnsicmVxdWlyZUluY3JlYXNlIjp0cnVlLCJyZXF1aXJlQ291bnRlciI6ZmFsc2V9LCJycCI6eyJuYW1lIjoiZGVtby5zdHJvbmdhdXRoLmNvbTo4MTgxIn0sImF1dGhlbnRpY2F0aW9uIjp7InVzZXJWZXJpZmljYXRpb24iOlsicmVxdWlyZWQiLCJwcmVmZXJyZWQiLCJkaXNjb3VyYWdlZCJdLCJhbGxvd0NyZWRlbnRpYWxzIjoiZW5hYmxlZCJ9fQ',1,'Active','',NOW(),NULL,NULL);`
    
7.  List the affected tables to verify the rows have been inserted.
    
8.  **Exit** the database.

Your database is now installed and configured for StrongKey FIDO2 Server.

### Java Web Server

The StrongKey FIDO2 Server is fully tested using Payara 4.1 web application server, but other Java web application servers will suffice in the same role. A list of possible choices can be found [here](https://en.wikipedia.org/wiki/List_of_application_servers#Java).

#### Download, Install, Configure

1.  **Download** the installation files and **save** them locally.
    
2.  If not already using one, open a  **terminal window**  and extract the download into _usr/local/strongkey_.
    
3.  **Download and copy** the JDBC package into the web server's _/lib_ directory.
    
    `cp <saved-file-location>/ /usr/local/strongkey/<web-application-folder>/lib`
    
4.  **Start the web server** and ensure that it has started successfully.

Default ports differ by web server. Use this list of [common default web server ports](https://geekflare.com/default-port-numbers/) or consult the appropriate manuals. Open a web browser and type  **localhost:&lt;port-number&gt;** where &lt;port-number&gt; is the default port for your web server. If your web server must use another port, use that port instead. This opens the FIDO2 Server launch page.
    
#### Create JDBC Resources

1. Make sure you copy the JDBC driver _.JAR_ file into the web server's _/lib_ directory.

2. Set the JDBC connection pool information as shown here:

	  Field  |  Value
	---:  |  :---
	  _Pool Name_  |  **SKFSPool**
	  _Resource Type_  |  **javax.sql.ConnectionPoolDataSource**
	  _Database Driver Vendor_  |  **&lt;Database Vendor&gt;**
    
3. You will need to specify the _database name_, _hostname_, _port_, and _user credentials_ for access. Delete any existing values and add the new values as shown here:
    
	  Field  |  Value
	  ---:  |  :---
	  _User_  |  **skfsdbuser**
	  _Port_  |  **3306**
	  _Password_  |  **&lt;skfsdbuser password&gt;**
	  _Server Name_  |  **localhost**
	  _Database Name_  |  **skfs**
    
	Test the connection before proceeding (methods vary by database).
    
   **NOTE:**  If the connection test fails, please verify the JDBC driver  _.JAR_ file  is in the _/lib_ directory.
    
4.  To create a JDBC resource that uses the connection pool we just created above, we must set the resources. Enter the  **JDBC resource information**  as shown here.
    
    **Field**  |  **Value**
    ---:  |  :---
    _JNDI Name_  |  **jdbc/skfs**
    _Pool Name_  |  **SKFSPool**

**NOTE: To effect all the configuration changes, we recommend restarting the web server**. 

Your web application server is now installed, configured, and started.

## Deploy StrongKey FIDO2 Server

The StrongKey FIDO2 Server is ready to be deployed.

1.  Open a  **terminal window**.
    
2.  **Deploy**  _fidoserver.ear_ using the  _asadmin_  deploy command:
    
    `asadmin deploy /usr/local/strongkey/fidoserver.ear`
    
    **NOTE:**  If the deployment fails, verify the HOME folder is configured (using `printenv`) and check the web application server logs for errors.
    
3.  **Open a browser**  and type the URL:
    
    _[https://localhost:8181/api/application.wadl](https://localhost:8181/api/application.wadl)_
    
    The StrongKey FIDO2 Server application _Web Application Definition Language (WADL)_ displays.

## WebAuthn Client Files
StrongKey WebAuthn client uses the following files, contained in the _WebAuthn.tgz_ download, to operate with the StrongKey FIDO2 Server, Community Edition and the sample Relying Party web application code:
- index.html
- css/fonts.css
- css/fido2demo.css
- js/jquery-3.3.1.min.js
- js/browserCheck.js
- js/base64js.min.js
- js/buffer-5.2.1.js
- js/base64url.js
- js/cbor.js
- js/fido2demo.js

## Removing the StrongKey FIDO2 Server and Its Components

To uninstall StrongKey FIDO2 Server, run the following command from the */usr/local/strongkey* folder:

```sh
sudo ./cleanup.sh
```
This removes all StrongKey files plus the installed dependency packages, including the sample Relying Party web application and the StrongKey WebAuthn client.
