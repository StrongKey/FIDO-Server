# FIDO REST Calls

StrongKey has designed a FIDO web application programmed using Angular2 Framework, written in TypeScript and Java, and using REST web service calls for client-server communication.

 ## 1&mdash;How FIDO Works

This section explains how FIDO works and how a developer can build FIDO for the relying party (RP) web application. The server REST web service calls have four functions: _preregister_, _register_, _preauthenticate_, and _authenticate_. The client calls the four server functions and uses the responses.

The following steps explain the high-end structure of how the client browser talks to the U2F library and back-end server:

### 1.1&mdash;Registration

1.	The RP web application calls the server-side _preregister_ function. The server generates a challenge and sends it to the client as the _preregister_ response.

2. The RP web application extracts the challenge from the _preregister_ response and uses <code>u2f.register</code> from the <code>U2F.api.js</code> library to register using a U2F device. The U2F _register_ function returns the device response.

3. The device response is sent to the server-side _register_ function.

4. The server-side _register_ function verifies the response and returns a response code if the registration is a success or an error code if it is a failure.

### 1.2&mdash;Authentication

1. The RP web application calls the server-side _preauthenticate_ function. The server generates a challenge and sends it to the client as the _preauthenticate_ response.

2. The RP web application extracts the challenge from the _preauthenticate_ response and uses the <code>u2f.sign</code> from the <code>U2F.api.js</code> library to register using a U2F device. The U2F sign function returns the device response.

3. The RP web application parses the _preauthenticate_ response and passes the challenge as a parameter to the <code>u2f.sign</code> from the <code>U2F.api.js</code> library to authenticate using the <code>U2F.api.js</code> library to authenticate using a U2F device. The U2F sign function returns the device response.

4. The server side _authenticate_ function verifies the response and returns a response code if the authentication is a success or an error code if it is a failure.

**NOTE**: U2F only works on HTTPS web pages. For a list of compatible browsers, visit https://caniuse.com/#search=webauthn

## 2&mdash;Registration

The above registration flow is programmed in Angular2. The following steps demonstrate _preregister_ and _register_ client-side code in TypeScript and servlet code in Java.

Based on the user action, the application determines the REST service call to be made, i.e., the registration call talks to the back-end server using the _preregister_ REST services and returns the challenge to the user. This challenge is used to call the browser U2F registration.

### 2.1&mdash;Preregister

The RP web application calls an HTTP POST request to preregister the REST service call. The following code can be accessed by navigating to the file <code>fido.component.ts</code>, located here:

CODE HERE

````~/location-to-download-file/angular/app/fido directory````

CODE HERE

The above code is written in TypeScript language. The corresponding JavaScript code is below.

### 2.2&mdash;_Pregister()_ Servlet Code

This method makes a _preregister()_ REST web service call (denoted by _Constants.PREREGISTER_ENDPOINT_) to SKFE, which returns a _preregister_ response with a challenge. The challenge is generated using the _Common.getFidoChallenge_ function. This uses <code>Common.java</code>, (see _[Appendix A](#2.Appendix A|outline)_). The response from the SKFE is a JSON string. The code in the following example can be accessed here:

````~[location-to-downloaded-file]/pki2fido-ejb/src/main/java/com/strongauth/pki2fido/ejb/preregister.java````

CODE HERE

### 2.3&mdash;U2f Register

The following code can be accessed by navigating to <code>fido.component.ts</code>, located here:

````~/location-to-download-file/angular/app/fido directory````

The _preregister_ REST service HTTP call returns a JSON response with a challenge passed as a parameter to the U2F API _register_ function; the _register_ REST service call uses the result of the U2F _register_.

CODE HERE

Dispatches _register_ requests to available U2F tokens. The following code can be accessed by navigating to the file <code>u2f-api.js</code>, located here:

````~/location-to-download-file/angular/js/````

CODE HERE

### 2.4&mdash;Registration

This method makes a _register_ REST web service call with the result from the U2F _register_ call. The following code can be accessed by navigating to <code>fido.component.ts</code>, located here:

````~/location-to-download-file/angular/app/fido directory````

CODE HERE

The browser calls an HTTP POST request to register a REST service call. The following code can be accessed by navigating to the file <code>rest.service.ts</code>, located here:

````~/location-to-download-file/ angular/app/shared directory````

CODE HERE

The above code is written in TypeScript language. The corresponding JavaScript code is below.

CODE HERE

### 2.5&mdash;_Register()_ Servlet Code

This methods makes a _register()_ REST web service call (denoted by _Constants.REGISTER_) to SKFE with the signed challenge from the earlier _preregister()_ call. The registration response is returned from the function _submitFidoResponse_. This uses <code>Common.java</code>, (see _[Appendix A](#2.Appendix A|outline)_). The response from the servlet code determines if the registration is a success or failure. The code in the following example can be accessed here:

````~[location-to-downloaded-file]/pki2fido-ejb/src/main/java/com/strongauth/pki2fido/ejb/register.java````

CODE HERE

### 2.6&mdash;Response

The response from the _register_ REST service determines if the registration is successful or if it failed with an error code. The following code can be accessed by navigating to <code>fido.component.ts</code>, located here:

````~/location-to-download-file/angular/app/fido````

CODE HERE

## 3&mdash;Authentication

The above authentication flow was programmed in Angular2. The following steps are all the _preauthenticate_ and _authenticate_ client side code in TypeScript and servlet code in Java. The similar functionality can be adapted in any programming language.

Based on the user action application determines the REST service call to be made, i.e., the action to be authentication and talks to the back-end server using the _preauthenticate_ REST services and returns the challenge to the user. This challenge is used to call the browser U2F sign function (authentication).

### 3.1&mdash;*Preauthenticate*

The RP web application calls a HTTP POST request to _preauthenticate_ REST service call. The following code can be accessed by navigating to the file <code>fido.component.ts</code> located here:

````~/location-to-download-file/angular/app/fido````

CODE HERE

The above code is written in TypeScript language. The corresponding JavaScript code is below:

CODE HERE

### 3.2&mdash;_Preauthenticate()_ Servlet Code

This method makes a _preauthenticate()_ REST web service call (denoted by _Constants.PREAUTHENTICATE_ENDPOINT_) to SKFE, which returns a _preauthenticate_ response with a challenge. The challenge is generated using the function _Common.getFidoChallenge_. This uses <code>Common.java</code> (see _[Appendix A](#Appendix A|outline)_). The response from the SKFE is a JSON string. The code in the following example can be accessed here:

````~[location-to-downloaded-file]/pki2fido-ejb/src/main/java/com/strongauth/pki2fido/ejb/preauthenticate.java````

CODE HERE

### 3.3&mdash;U2f Sign

The following code can be accessed by navigating to the file <code>fido.component.ts</code> located here:

````~/location-to-download-file/angular/app/fido````

The _preauthenticate_ REST service HTTP call returns a JSON response with a challenge, which is then passed as a parameter to U2F API sign function; the _authenticate_ REST service call uses the result of the U2F sign.

CODE HERE

Dispatches sign requests to available U2F tokens. The following code can be accessed by navigating to the file <code>u2f-api.js</code>, located here:

````~/location-to-download-file/angular/js/````

CODE HERE

### 3.4&mdash;Authentication

This method makes an _authenticate_ REST web service call with the result from the U2F sign call. The following code can be accessed by navigating to the file <code>fido.component.ts</code>:

````~/location-to-download-file/angular/app/fido````

CODE HERE

Browser calls a HTTP post request to authenticate REST service call. The following code can be accessed by navigating to the file <code>rest.service.ts</code> located here:

````~/location-to-download-file/ angular/app/shared````

CODE HERE

The above code is written in TypeScript language. The corresponding JavaScript code is below:

CODE HERE

### 3.5&mdash;*Authenticate()* Servlet Code

This methods makes an _authenticate()_ REST web service call (denoted by _Constants.AUTHENTICATE_) to SKFE with the signed challenge from the _preauthenticate()_ call earlier and the authenticate response is returned from the function _submitFidoResponse._ This uses <code>Common.java</code> (see _Appendix A_). The response from the servlet code determines if the authentication is success or failure. The code in the following example can be accessed here:

````~[location-to-downloaded-file]/pki2fido-ejb/src/main/java/com/strongauth/pki2fido/ejb/authenticate.java````

CODE HERE

 ### 3.6&mdash;Response

The response from the _authenticate_ REST service tells if the authentication is successful or if it failed with an error code. The following code can be accessed by navigating to the file fido.component.ts, located here:

````~/location-to-download-file/angular/app/fido````

CODE HERE

# Appendix A

This method makes an HTTP call with method name _preregister_, _register_, _preauthenticate_, _authenticate_, etc., and payload as input. It parses the response back from the HTTP request and returns it as a string. The code in the following example can be accessed here:

````~[location-to-downloaded-file]/pki2fido-ejb/src/main/java/com/strongauth/pki2fido/utilities/Common.java````

CODE HERE
