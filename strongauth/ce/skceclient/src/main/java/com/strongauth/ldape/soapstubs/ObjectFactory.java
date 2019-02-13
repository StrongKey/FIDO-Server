
package com.strongauth.ldape.soapstubs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.strongauth.ldape.soapstubs package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AuthenticateResponse_QNAME = new QName("http://ldapews.strongauth.com/", "authenticateResponse");
    private final static QName _Adduser_QNAME = new QName("http://ldapews.strongauth.com/", "adduser");
    private final static QName _Authenticate_QNAME = new QName("http://ldapews.strongauth.com/", "authenticate");
    private final static QName _Updateuser_QNAME = new QName("http://ldapews.strongauth.com/", "updateuser");
    private final static QName _Authorize_QNAME = new QName("http://ldapews.strongauth.com/", "authorize");
    private final static QName _AuthorizeResponse_QNAME = new QName("http://ldapews.strongauth.com/", "authorizeResponse");
    private final static QName _SKCEException_QNAME = new QName("http://ldapews.strongauth.com/", "SKCEException");
    private final static QName _GetuserinfoResponse_QNAME = new QName("http://ldapews.strongauth.com/", "getuserinfoResponse");
    private final static QName _AdduserResponse_QNAME = new QName("http://ldapews.strongauth.com/", "adduserResponse");
    private final static QName _Getuserinfo_QNAME = new QName("http://ldapews.strongauth.com/", "getuserinfo");
    private final static QName _UpdateuserResponse_QNAME = new QName("http://ldapews.strongauth.com/", "updateuserResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.strongauth.ldape.soapstubs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SKCEServiceInfoType }
     * 
     */
    public SKCEServiceInfoType createSKCEServiceInfoType() {
        return new SKCEServiceInfoType();
    }

    /**
     * Create an instance of {@link Authenticate }
     * 
     */
    public Authenticate createAuthenticate() {
        return new Authenticate();
    }

    /**
     * Create an instance of {@link Adduser }
     * 
     */
    public Adduser createAdduser() {
        return new Adduser();
    }

    /**
     * Create an instance of {@link AuthenticateResponse }
     * 
     */
    public AuthenticateResponse createAuthenticateResponse() {
        return new AuthenticateResponse();
    }

    /**
     * Create an instance of {@link AdduserResponse }
     * 
     */
    public AdduserResponse createAdduserResponse() {
        return new AdduserResponse();
    }

    /**
     * Create an instance of {@link Getuserinfo }
     * 
     */
    public Getuserinfo createGetuserinfo() {
        return new Getuserinfo();
    }

    /**
     * Create an instance of {@link UpdateuserResponse }
     * 
     */
    public UpdateuserResponse createUpdateuserResponse() {
        return new UpdateuserResponse();
    }

    /**
     * Create an instance of {@link SKCEException }
     * 
     */
    public SKCEException createSKCEException() {
        return new SKCEException();
    }

    /**
     * Create an instance of {@link GetuserinfoResponse }
     * 
     */
    public GetuserinfoResponse createGetuserinfoResponse() {
        return new GetuserinfoResponse();
    }

    /**
     * Create an instance of {@link Authorize }
     * 
     */
    public Authorize createAuthorize() {
        return new Authorize();
    }

    /**
     * Create an instance of {@link AuthorizeResponse }
     * 
     */
    public AuthorizeResponse createAuthorizeResponse() {
        return new AuthorizeResponse();
    }

    /**
     * Create an instance of {@link Updateuser }
     * 
     */
    public Updateuser createUpdateuser() {
        return new Updateuser();
    }

    /**
     * Create an instance of {@link SkceReturnObject }
     * 
     */
    public SkceReturnObject createSkceReturnObject() {
        return new SkceReturnObject();
    }

    /**
     * Create an instance of {@link Base64Binary }
     * 
     */
    public Base64Binary createBase64Binary() {
        return new Base64Binary();
    }

    /**
     * Create an instance of {@link HexBinary }
     * 
     */
    public HexBinary createHexBinary() {
        return new HexBinary();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthenticateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "authenticateResponse")
    public JAXBElement<AuthenticateResponse> createAuthenticateResponse(AuthenticateResponse value) {
        return new JAXBElement<AuthenticateResponse>(_AuthenticateResponse_QNAME, AuthenticateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Adduser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "adduser")
    public JAXBElement<Adduser> createAdduser(Adduser value) {
        return new JAXBElement<Adduser>(_Adduser_QNAME, Adduser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Authenticate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "authenticate")
    public JAXBElement<Authenticate> createAuthenticate(Authenticate value) {
        return new JAXBElement<Authenticate>(_Authenticate_QNAME, Authenticate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Updateuser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "updateuser")
    public JAXBElement<Updateuser> createUpdateuser(Updateuser value) {
        return new JAXBElement<Updateuser>(_Updateuser_QNAME, Updateuser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Authorize }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "authorize")
    public JAXBElement<Authorize> createAuthorize(Authorize value) {
        return new JAXBElement<Authorize>(_Authorize_QNAME, Authorize.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorizeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "authorizeResponse")
    public JAXBElement<AuthorizeResponse> createAuthorizeResponse(AuthorizeResponse value) {
        return new JAXBElement<AuthorizeResponse>(_AuthorizeResponse_QNAME, AuthorizeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SKCEException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "SKCEException")
    public JAXBElement<SKCEException> createSKCEException(SKCEException value) {
        return new JAXBElement<SKCEException>(_SKCEException_QNAME, SKCEException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetuserinfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "getuserinfoResponse")
    public JAXBElement<GetuserinfoResponse> createGetuserinfoResponse(GetuserinfoResponse value) {
        return new JAXBElement<GetuserinfoResponse>(_GetuserinfoResponse_QNAME, GetuserinfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdduserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "adduserResponse")
    public JAXBElement<AdduserResponse> createAdduserResponse(AdduserResponse value) {
        return new JAXBElement<AdduserResponse>(_AdduserResponse_QNAME, AdduserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Getuserinfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "getuserinfo")
    public JAXBElement<Getuserinfo> createGetuserinfo(Getuserinfo value) {
        return new JAXBElement<Getuserinfo>(_Getuserinfo_QNAME, Getuserinfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateuserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ldapews.strongauth.com/", name = "updateuserResponse")
    public JAXBElement<UpdateuserResponse> createUpdateuserResponse(UpdateuserResponse value) {
        return new JAXBElement<UpdateuserResponse>(_UpdateuserResponse_QNAME, UpdateuserResponse.class, null, value);
    }

}
