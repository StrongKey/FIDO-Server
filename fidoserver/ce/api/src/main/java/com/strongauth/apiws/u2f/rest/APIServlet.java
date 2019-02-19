/**
 * Copyright StrongAuth, Inc. All Rights Reserved.
 *
 * Use of this source code is governed by the Gnu Lesser General Public License 2.3.
 * The license can be found at https://github.com/StrongKey/FIDO-Server/LICENSE
 */
package com.strongauth.apiws.u2f.rest;

import com.strongauth.apiws.utility.PATCH;
import com.strongauth.appliance.utilities.applianceCommon;
import com.strongauth.appliance.utilities.strongkeyLogger;
import com.strongauth.skce.jaxb.SKCEServiceInfoType;
import com.strongauth.skce.utilities.SKCEException;
import com.strongauth.skfe.utilities.skfeCommon;
import com.strongauth.skfe.utilities.skfeConstants;
import com.strongauth.skfe.txbeans.u2fServletHelperBeanLocal;
import com.strongkey.auth.txbeans.authenticateRestRequestBeanLocal;
import com.strongkey.auth.txbeans.authorizeLdapUserBeanLocal;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * REST based web services that serve FIDO U2F protocol based functionality.
 *
 */
@Stateless
@Path("/domains/{did}/fidokeys")
public class APIServlet {

    @javax.ws.rs.core.Context private HttpServletRequest request;
    @EJB u2fServletHelperBeanLocal u2fHelperBean;
    @EJB authenticateRestRequestBeanLocal authRest;

    public APIServlet() {
    }
    
    /**
     * Step-1 for fido authenticator registration. This methods generates a
     * challenge and returns the same to the caller, which typically is a
     * Relying Party (RP) application.
     *
     * @param requestbody - String The full body for auth purposes
     * @param did - Long value of the domain to service this request
     * @param protocol - String value of the protocol to use
     * @param username - String user for which to create the pre-registration
     * request
     * @param displayname - String user for which to create the pre-registration
     * request
     * @param options - String json value of options to use for FIDO2
     * @param extensions - String json value of extensions to use for FIDO2
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Challenge' : 'U2F Reg Challenge parameters; a json again' 2.
     * 'Message' : String, with a list of messages that explain the process. 3.
     * 'Error' : String, with error message incase something went wrong. Will be
     * empty if successful.
     */
    @POST
    @Path("/challenge")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response preregister(String requestbody,
                                @PathParam("did") Long did,
                                @FormParam("protocol") String protocol,
                                @FormParam("username") String username,
                                @FormParam("displayname") String displayname,
                                @FormParam("options") String options,
                                @FormParam("extensions") String extensions) {
        
        if (!authRest.execute(did, request, requestbody)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return u2fHelperBean.preregister(did, protocol, username, displayname, options, extensions);
    }

    /**
     * Step-2 or last step of fido authenticator registration process. This
     * method receives the u2f registration response parameters which is
     * processed and the registration result is notified back to the caller.
     *
     * Both preregister and register methods are time linked. Meaning, register
     * should happen with in a certain time limit after the preregister is
     * finished; otherwise, the user session would be invalidated.
     *
     * @param requestbody - String The full body for auth purposes
     * @param did - Long value of the domain to service this request
     * @param protocol - String value of the protocol to use
     * @param response - String 
     * @param metadata - String
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Response' : String, with a simple message telling if the process was
     * successful or not. 2. 'Message' : String, with a list of messages that
     * explain the process. 3. 'Error' : String, with error message incase
     * something went wrong. Will be empty if successful.
     */
    @POST
    @Path("")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response register(String requestbody,
                             @PathParam("did") Long did,
                             @FormParam("protocol") String protocol,
                             @FormParam("response") String response,
                             @FormParam("metadata") String metadata) {

        if (!authRest.execute(did, request, requestbody)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        return u2fHelperBean.register(did, protocol, response, metadata);
    }

    /**
     * Step-1 for fido authenticator authentication. This methods generates a
     * challenge and returns the same to the caller.
     *
     * @param requestbody - String The full body for auth purposes
     * @param did - Long value of the domain to service this request
     * @param protocol - String value of the protocol to use
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Challenge' : 'U2F Auth Challenge parameters; a json again' 2.
     * 'Message' : String, with a list of messages that explain the process. 3.
     * 'Error' : String, with error message incase something went wrong. Will be
     * empty if successful.
     */
    @POST
    @Path("/authenticate/challenge")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response preauthenticate(String requestbody,
                                    @PathParam("did") Long did,
                                    @FormParam("protocol") String protocol,
                                    @FormParam("username") String username,
                                    @FormParam("options") String options,
                                    @FormParam("extensions") String extensions) {

        if (!authRest.execute(did, request, requestbody)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return u2fHelperBean.preauthenticate(did, protocol, username, options, extensions);
    }

    /**
     * Step-2 or last step of fido authenticator authentication process. This
     * method receives the u2f authentication response parameters which is
     * processed and the authentication result is notified back to the caller.
     *
     * Both preauthenticate and authenticate methods are time linked. Meaning,
     * authenticate should happen with in a certain time limit after the
     * preauthenticate is finished; otherwise, the user session would be
     * invalidated.
     *
     * @param requestbody - String The full body for auth purposes
     * @param did - Long value of the domain to service this request
     * @param protocol - String value of the protocol to use
     * 
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Response' : String, with a simple message telling if the process was
     * successful or not. 2. 'Message' : String, with a list of messages that
     * explain the process. 3. 'Error' : String, with error message incase
     * something went wrong. Will be empty if successful.
     */
    @POST
    @Path("/authenticate")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response authenticate(String requestbody,
                                 @PathParam("did") Long did,
                                 @FormParam("protocol") String protocol,
                                 @FormParam("response") String response,
                                 @FormParam("metadata") String metadata) {

        if (!authRest.execute(did, request, requestbody)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return u2fHelperBean.authenticate(did, protocol, response, metadata);
    }

    /**
     * Step-1 for fido based transaction confirmation using u2f authenticator.
     * This methods generates a challenge and returns the same to the caller.
     *
     * @param svcinfo - Object that carries SKCE service information.
     * Information bundled is :
     * 
     * (1) did - Unique identifier for a SKCE encryption domain (2) svcusername
     * - SKCE service credentials : username requesting the service. The service
     * credentials are looked up in the 'service' setup of authentication system
     * based on LDAP / AD. The user should be authorized to encrypt. (3)
     * svcpassword - SKCE service credentials : password of the service username
     * specified above (4) protocol - U2F protocol version to comply with.
     * 
     * @param payload - String indicating transaction reference.
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Challenge' : 'U2F Auth Challenge parameters; a json again' 2.
     * 'Message' : String, with a list of messages that explain the process. 3.
     * 'Error' : String, with error message incase something went wrong. Will be
     * empty if successful.
     */
    @POST
    @Path("/authorize/challenge")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response preauthorize(String requestbody,
                                 @PathParam("did") Long did,
                                 @FormParam("protocol") String protocol,
                                 @FormParam("username") String username,
                                 @FormParam("options") String options,
                                 @FormParam("extensions") String extensions) {

        if (!authRest.execute(did, request, requestbody)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return u2fHelperBean.preauthorize(did, protocol, username, options, extensions);
    }

    /**
     * Step-2 or last step for fido based transaction confirmation using a u2f
     * authenticator. This method receives the u2f authentication response
     * parameters which is processed and the authorization result is notified
     * back to the caller.
     *
     * Both preauthorize and authorize methods are time linked. Meaning,
     * authorize should happen with in a certain time limit after the
     * preauthorize is finished; otherwise, the user session would be
     * invalidated.
     *
     * @param svcinfo - Object that carries SKCE service information.
     * Information bundled is :
     * 
     * (1) did - Unique identifier for a SKCE encryption domain (2) svcusername
     * - SKCE service credentials : username requesting the service. The service
     * credentials are looked up in thaf9dd4a68c94ab8383980042ebd479113dc23b21e 'service' setup of authentication system
     * based on LDAP / AD. The user should be authorized to encrypt. (3)
     * svcpassword - SKCE service credentials : password of the service username
     * specified above
     *
     * @param payload * @return - A Json in String format. The Json will have 3
     * key-value pairs; 1. 'Response' : String, with a simple message telling if
     * the process was successful or not. 2. 'Message' : String, with a list of
     * messages that explain the process. 3. 'Error' : String, with error
     * message incase something went wrong. Will be empty if successful.
     * @return 
     */
    @POST
    @Path("/authorize")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response authorize(String requestbody,
                              @PathParam("did") Long did,
                              @FormParam("protocol") String protocol,
                              @FormParam("response") String response,
                              @FormParam("metadata") String metadata) {

        if (!authRest.execute(did, request, requestbody)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return u2fHelperBean.authorize(did, protocol, response, metadata);
    }

    /**
     * The process of deleting or de-registering an already registered fido
     * authenticator. The inputs needed are the name of the user and the random
     * id to point to a unique registered key for that user. This random id can
     * be obtained by calling getkeysinfo method.
     *
     * @param svcinfo - Object that carries SKCE service information.
     * Information bundled is :
     * 
     * (1) did - Unique identifier for a SKCE encryption domain (2) svcusername
     * - SKCE service credentials : username requesting the service. The service
     * credentials are looked up in the 'service' setup of authentication system
     * based on LDAP / AD. The user should be authorized to encrypt. (3)
     * svcpassword - SKCE service credentials : password of the service username
     * specified above (4) protocol - U2F protocol version to comply with.
     * 
     * @param payload - U2F de-registration parameters in Json form. Should
     * contain username and randomid.
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Response' : String, with a simple message telling if the process was
     * successful or not. 2. 'Message' : Empty string since there is no
     * cryptographic work involved in de-registration 3. 'Error' : String, with
     * error message incase something went wrong. Will be empty if successful.
     */
//    @DELETE
//    @Path("/{id}")
//    @Consumes({"application/x-www-form-urlencoded"})
//    @Produces({"application/json"})
//    public Response deregister(@FormParam("svcinfo") String svcinfo,
//            @FormParam("payload") String payload) {
//        //  Local variables       
//        //  Service credentials
//        String did;
//        String svcusername;
//        String svcpassword;
//        String protocol;
//        
//        //  SKCE domain id validation
//        try {
//            SKCEServiceInfoType si = basicInputChecks("deregister", svcinfo);
//            did = Integer.toString(si.getDid());
//            svcusername = si.getSvcusername();
//            svcpassword = si.getSvcpassword();
//            protocol = si.getProtocol();
//
////            skfeCommon.inputValidateSKCEDid(did);
//        } catch (SKCEException ex) {
//            return skfeCommon.buildDeregisterResponse(null, "", ex.getLocalizedMessage());
//        }
//        
//        //  2. Input checks
//        if (svcusername == null || svcusername.isEmpty()) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0002", " svcusername");
//            return skfeCommon.buildDeregisterResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0002") + " svcusername");
//        }
//        if (svcpassword == null) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0002", " svcpassword");
//            return skfeCommon.buildDeregisterResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0002") + " svcpassword");
//        }
//        //authenticate
//        boolean isAuthorized;
//        try {
//            isAuthorized = authorizebean.execute(Long.parseLong(did), svcusername, svcpassword, skfeConstants.LDAP_ROLE_FIDO);
//        } catch (SKCEException ex) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, skfeCommon.getMessageProperty("FIDO-ERR-0003"), ex.getMessage());
//            return skfeCommon.buildDeregisterResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0003") + ex.getMessage());
//        }
//        if (!isAuthorized) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0033", "");
//            return skfeCommon.buildDeregisterResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0033"));
//        }
//        return u2fHelperBean.deregister(did, protocol, payload);
//    }

    /*
     ************************************************************************
     *                        888    d8b                   888             
     *                        888    Y8P                   888             
     *                        888                          888             
     *       8888b.   .d8888b 888888 888 888  888  8888b.  888888  .d88b.  
     *          "88b d88P"    888    888 888  888     "88b 888    d8P  Y8b 
     *      .d888888 888      888    888 Y88  88P .d888888 888    88888888 
     *      888  888 Y88b.    Y88b.  888  Y8bd8P  888  888 Y88b.  Y8b.     
     *      "Y888888  "Y8888P  "Y888 888   Y88P   "Y888888  "Y888  "Y8888 
     ************************************************************************
     */
    /**
     * The process of activating an already registerd but de-activated fido
     * authenticator. This process will turn the status of the key in the
     * database back to ACTIVE. The inputs needed are the name of the user and
     * the random id to point to a unique registered key for that user. This
     * random id can be obtained by calling getkeysinfo method.
     *
     * @param svcinfo - Object that carries SKCE service information.
     * Information bundled is :
     * 
     * (1) did - Unique identifier for a SKCE encryption domain (2) svcusername
     * - SKCE service credentials : username requesting the service. The service
     * credentials are looked up in the 'service' setup of authentication system
     * based on LDAP / AD. The user should be authorized to encrypt. (3)
     * svcpassword - SKCE service credentials : password of the service username
     * specified above (4) protocol - U2F protocol version to comply with.
     * 
     * @param payload - U2F activation parameters in Json form. Should contain
     * username and randomid.
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Response' : String, with a simple message telling if the process was
     * successful or not. 2. 'Message' : Empty string since there is no
     * cryptographic work involved in activation 3. 'Error' : String, with error
     * message incase something went wrong. Will be empty if successful.
     */
//    @PATCH
//    @Path("/{id}")
//    @Consumes({"application/x-www-form-urlencoded"})
//    @Produces({"application/json"})
//    public Response status(@FormParam("svcinfo") String svcinfo,
//            @FormParam("payload") String payload) {
//        //  Local variables       
//        //  Service credentials
//        String did;
//        String svcusername;
//        String svcpassword;
//        String protocol;
//        
//        //  SKCE domain id validation
//        try {
//            SKCEServiceInfoType si = basicInputChecks("activate", svcinfo);
//            did = Integer.toString(si.getDid());
//            svcusername = si.getSvcusername();
//            svcpassword = si.getSvcpassword();
//            protocol = si.getProtocol();
//
////            skfeCommon.inputValidateSKCEDid(did);
//        } catch (SKCEException ex) {
//            return skfeCommon.buildDeactivateResponse(null, "", ex.getLocalizedMessage());
//        }
//        
//        //  2. Input checks
//        if (svcusername == null || svcusername.isEmpty()) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0002", " svcusername");
//            return skfeCommon.buildActivateResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0002") + " svcusername");
//        }
//        if (svcpassword == null) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0002", " svcpassword");
//            return skfeCommon.buildActivateResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0002") + " svcpassword");
//        }
//        //authenticate
//        boolean isAuthorized;
//        try {
//            isAuthorized = authorizebean.execute(Long.parseLong(did), svcusername, svcpassword, skfeConstants.LDAP_ROLE_FIDO);
//        } catch (SKCEException ex) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, skfeCommon.getMessageProperty("FIDO-ERR-0003"), ex.getMessage());
//            return skfeCommon.buildActivateResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0003") + ex.getMessage());
//        }
//        if (!isAuthorized) {
//            strongkeyLogger.log(skfeConstants.SKFE_LOGGER,Level.SEVERE, "FIDO-ERR-0033", "");
//            return skfeCommon.buildActivateResponse(null, "", skfeCommon.getMessageProperty("FIDO-ERR-0033"));
//        }
//        return u2fHelperBean.activate(did, protocol, payload);
//    }

    /**
     * Method to return a list of user registered fido authenticator
     * information; In short, registered keys information. Information includes
     * the meta data of the key like the place and time it was registered and
     * used (last modified) from, a random id (which has a time-to-live) that
     * has to be sent back as a token during de-registration.
     *
     * @param did
     * @param username - The username we are finding keys for
     * @return - A Json in String format. The Json will have 3 key-value pairs;
     * 1. 'Response' : A Json array, each entry signifying metadata of a key
     * registered; Metadata includes randomid and its time-to-live, creation and
     * modify location and time info etc., 2. 'Message' : Empty string since
     * there is no cryptographic work involved in this process. 3. 'Error' :
     * String, with error message incase something went wrong. Will be empty if
     * successful.
     */
    @GET
    @Path("")
    @Consumes({"application/x-www-form-urlencoded"})
    @Produces({"application/json"})
    public Response getkeysinfo(@PathParam("did") Long did,
                                @QueryParam("username") String username) {

        if (!authRest.execute(did, request, null)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        
        try {
            return u2fHelperBean.getkeysinfo(did, username);
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SKCE-ERR-1031: Request failed: " + ex.getLocalizedMessage()).build();
        }
    }
}
