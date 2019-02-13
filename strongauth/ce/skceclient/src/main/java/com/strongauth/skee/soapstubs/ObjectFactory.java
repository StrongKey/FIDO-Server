
package com.strongauth.skee.soapstubs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.strongauth.skee.soapstubs package. 
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

    private final static QName _DecryptResponse_QNAME = new QName("http://skeews.strongauth.com/", "decryptResponse");
    private final static QName _EncryptResponse_QNAME = new QName("http://skeews.strongauth.com/", "encryptResponse");
    private final static QName _DecryptFromCloud_QNAME = new QName("http://skeews.strongauth.com/", "decryptFromCloud");
    private final static QName _Encrypt_QNAME = new QName("http://skeews.strongauth.com/", "encrypt");
    private final static QName _Ping_QNAME = new QName("http://skeews.strongauth.com/", "ping");
    private final static QName _SKCEException_QNAME = new QName("http://skeews.strongauth.com/", "SKCEException");
    private final static QName _EncryptToCloud_QNAME = new QName("http://skeews.strongauth.com/", "encryptToCloud");
    private final static QName _DecryptFromCloudResponse_QNAME = new QName("http://skeews.strongauth.com/", "decryptFromCloudResponse");
    private final static QName _EncryptToCloudResponse_QNAME = new QName("http://skeews.strongauth.com/", "encryptToCloudResponse");
    private final static QName _PingResponse_QNAME = new QName("http://skeews.strongauth.com/", "pingResponse");
    private final static QName _Decrypt_QNAME = new QName("http://skeews.strongauth.com/", "decrypt");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.strongauth.skee.soapstubs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DecryptResponse }
     * 
     */
    public DecryptResponse createDecryptResponse() {
        return new DecryptResponse();
    }

    /**
     * Create an instance of {@link EncryptResponse }
     * 
     */
    public EncryptResponse createEncryptResponse() {
        return new EncryptResponse();
    }

    /**
     * Create an instance of {@link Encrypt }
     * 
     */
    public Encrypt createEncrypt() {
        return new Encrypt();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link DecryptFromCloud }
     * 
     */
    public DecryptFromCloud createDecryptFromCloud() {
        return new DecryptFromCloud();
    }

    /**
     * Create an instance of {@link EncryptToCloud }
     * 
     */
    public EncryptToCloud createEncryptToCloud() {
        return new EncryptToCloud();
    }

    /**
     * Create an instance of {@link SKCEException }
     * 
     */
    public SKCEException createSKCEException() {
        return new SKCEException();
    }

    /**
     * Create an instance of {@link Decrypt }
     * 
     */
    public Decrypt createDecrypt() {
        return new Decrypt();
    }

    /**
     * Create an instance of {@link DecryptFromCloudResponse }
     * 
     */
    public DecryptFromCloudResponse createDecryptFromCloudResponse() {
        return new DecryptFromCloudResponse();
    }

    /**
     * Create an instance of {@link EncryptToCloudResponse }
     * 
     */
    public EncryptToCloudResponse createEncryptToCloudResponse() {
        return new EncryptToCloudResponse();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link SkceReturnObject }
     * 
     */
    public SkceReturnObject createSkceReturnObject() {
        return new SkceReturnObject();
    }

    /**
     * Create an instance of {@link SKCEInputFileInfoType }
     * 
     */
    public SKCEInputFileInfoType createSKCEInputFileInfoType() {
        return new SKCEInputFileInfoType();
    }

    /**
     * Create an instance of {@link SKCEStorageInfoType }
     * 
     */
    public SKCEStorageInfoType createSKCEStorageInfoType() {
        return new SKCEStorageInfoType();
    }

    /**
     * Create an instance of {@link SKCEEncryptionKeyInfoType }
     * 
     */
    public SKCEEncryptionKeyInfoType createSKCEEncryptionKeyInfoType() {
        return new SKCEEncryptionKeyInfoType();
    }

    /**
     * Create an instance of {@link SKCEServiceInfoType }
     * 
     */
    public SKCEServiceInfoType createSKCEServiceInfoType() {
        return new SKCEServiceInfoType();
    }

    /**
     * Create an instance of {@link SKCEAuthorizationInfoType }
     * 
     */
    public SKCEAuthorizationInfoType createSKCEAuthorizationInfoType() {
        return new SKCEAuthorizationInfoType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "decryptResponse")
    public JAXBElement<DecryptResponse> createDecryptResponse(DecryptResponse value) {
        return new JAXBElement<DecryptResponse>(_DecryptResponse_QNAME, DecryptResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "encryptResponse")
    public JAXBElement<EncryptResponse> createEncryptResponse(EncryptResponse value) {
        return new JAXBElement<EncryptResponse>(_EncryptResponse_QNAME, EncryptResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptFromCloud }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "decryptFromCloud")
    public JAXBElement<DecryptFromCloud> createDecryptFromCloud(DecryptFromCloud value) {
        return new JAXBElement<DecryptFromCloud>(_DecryptFromCloud_QNAME, DecryptFromCloud.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Encrypt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "encrypt")
    public JAXBElement<Encrypt> createEncrypt(Encrypt value) {
        return new JAXBElement<Encrypt>(_Encrypt_QNAME, Encrypt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SKCEException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "SKCEException")
    public JAXBElement<SKCEException> createSKCEException(SKCEException value) {
        return new JAXBElement<SKCEException>(_SKCEException_QNAME, SKCEException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptToCloud }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "encryptToCloud")
    public JAXBElement<EncryptToCloud> createEncryptToCloud(EncryptToCloud value) {
        return new JAXBElement<EncryptToCloud>(_EncryptToCloud_QNAME, EncryptToCloud.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptFromCloudResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "decryptFromCloudResponse")
    public JAXBElement<DecryptFromCloudResponse> createDecryptFromCloudResponse(DecryptFromCloudResponse value) {
        return new JAXBElement<DecryptFromCloudResponse>(_DecryptFromCloudResponse_QNAME, DecryptFromCloudResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptToCloudResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "encryptToCloudResponse")
    public JAXBElement<EncryptToCloudResponse> createEncryptToCloudResponse(EncryptToCloudResponse value) {
        return new JAXBElement<EncryptToCloudResponse>(_EncryptToCloudResponse_QNAME, EncryptToCloudResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Decrypt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://skeews.strongauth.com/", name = "decrypt")
    public JAXBElement<Decrypt> createDecrypt(Decrypt value) {
        return new JAXBElement<Decrypt>(_Decrypt_QNAME, Decrypt.class, null, value);
    }

}
