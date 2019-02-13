
package com.strongauth.skse.soapstubs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.strongauth.skse.soapstubs package. 
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

    private final static QName _LoadKey_QNAME = new QName("http://sksews.strongauth.com/", "loadKey");
    private final static QName _Sign_QNAME = new QName("http://sksews.strongauth.com/", "sign");
    private final static QName _SKCEException_QNAME = new QName("http://sksews.strongauth.com/", "SKCEException");
    private final static QName _RemoveKeyResponse_QNAME = new QName("http://sksews.strongauth.com/", "removeKeyResponse");
    private final static QName _SignResponse_QNAME = new QName("http://sksews.strongauth.com/", "signResponse");
    private final static QName _LoadKeyResponse_QNAME = new QName("http://sksews.strongauth.com/", "loadKeyResponse");
    private final static QName _Signxml_QNAME = new QName("http://sksews.strongauth.com/", "signxml");
    private final static QName _SignxmlResponse_QNAME = new QName("http://sksews.strongauth.com/", "signxmlResponse");
    private final static QName _Verifyxml_QNAME = new QName("http://sksews.strongauth.com/", "verifyxml");
    private final static QName _VerifyxmlResponse_QNAME = new QName("http://sksews.strongauth.com/", "verifyxmlResponse");
    private final static QName _RemoveKey_QNAME = new QName("http://sksews.strongauth.com/", "removeKey");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.strongauth.skse.soapstubs
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
     * Create an instance of {@link Signxml }
     * 
     */
    public Signxml createSignxml() {
        return new Signxml();
    }

    /**
     * Create an instance of {@link LoadKeyResponse }
     * 
     */
    public LoadKeyResponse createLoadKeyResponse() {
        return new LoadKeyResponse();
    }

    /**
     * Create an instance of {@link RemoveKey }
     * 
     */
    public RemoveKey createRemoveKey() {
        return new RemoveKey();
    }

    /**
     * Create an instance of {@link SignxmlResponse }
     * 
     */
    public SignxmlResponse createSignxmlResponse() {
        return new SignxmlResponse();
    }

    /**
     * Create an instance of {@link Verifyxml }
     * 
     */
    public Verifyxml createVerifyxml() {
        return new Verifyxml();
    }

    /**
     * Create an instance of {@link VerifyxmlResponse }
     * 
     */
    public VerifyxmlResponse createVerifyxmlResponse() {
        return new VerifyxmlResponse();
    }

    /**
     * Create an instance of {@link Sign }
     * 
     */
    public Sign createSign() {
        return new Sign();
    }

    /**
     * Create an instance of {@link LoadKey }
     * 
     */
    public LoadKey createLoadKey() {
        return new LoadKey();
    }

    /**
     * Create an instance of {@link SKCEException }
     * 
     */
    public SKCEException createSKCEException() {
        return new SKCEException();
    }

    /**
     * Create an instance of {@link RemoveKeyResponse }
     * 
     */
    public RemoveKeyResponse createRemoveKeyResponse() {
        return new RemoveKeyResponse();
    }

    /**
     * Create an instance of {@link SignResponse }
     * 
     */
    public SignResponse createSignResponse() {
        return new SignResponse();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link LoadKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "loadKey")
    public JAXBElement<LoadKey> createLoadKey(LoadKey value) {
        return new JAXBElement<LoadKey>(_LoadKey_QNAME, LoadKey.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Sign }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "sign")
    public JAXBElement<Sign> createSign(Sign value) {
        return new JAXBElement<Sign>(_Sign_QNAME, Sign.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SKCEException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "SKCEException")
    public JAXBElement<SKCEException> createSKCEException(SKCEException value) {
        return new JAXBElement<SKCEException>(_SKCEException_QNAME, SKCEException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveKeyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "removeKeyResponse")
    public JAXBElement<RemoveKeyResponse> createRemoveKeyResponse(RemoveKeyResponse value) {
        return new JAXBElement<RemoveKeyResponse>(_RemoveKeyResponse_QNAME, RemoveKeyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "signResponse")
    public JAXBElement<SignResponse> createSignResponse(SignResponse value) {
        return new JAXBElement<SignResponse>(_SignResponse_QNAME, SignResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoadKeyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "loadKeyResponse")
    public JAXBElement<LoadKeyResponse> createLoadKeyResponse(LoadKeyResponse value) {
        return new JAXBElement<LoadKeyResponse>(_LoadKeyResponse_QNAME, LoadKeyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signxml }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "signxml")
    public JAXBElement<Signxml> createSignxml(Signxml value) {
        return new JAXBElement<Signxml>(_Signxml_QNAME, Signxml.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignxmlResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "signxmlResponse")
    public JAXBElement<SignxmlResponse> createSignxmlResponse(SignxmlResponse value) {
        return new JAXBElement<SignxmlResponse>(_SignxmlResponse_QNAME, SignxmlResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Verifyxml }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "verifyxml")
    public JAXBElement<Verifyxml> createVerifyxml(Verifyxml value) {
        return new JAXBElement<Verifyxml>(_Verifyxml_QNAME, Verifyxml.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerifyxmlResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "verifyxmlResponse")
    public JAXBElement<VerifyxmlResponse> createVerifyxmlResponse(VerifyxmlResponse value) {
        return new JAXBElement<VerifyxmlResponse>(_VerifyxmlResponse_QNAME, VerifyxmlResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RemoveKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sksews.strongauth.com/", name = "removeKey")
    public JAXBElement<RemoveKey> createRemoveKey(RemoveKey value) {
        return new JAXBElement<RemoveKey>(_RemoveKey_QNAME, RemoveKey.class, null, value);
    }

}
