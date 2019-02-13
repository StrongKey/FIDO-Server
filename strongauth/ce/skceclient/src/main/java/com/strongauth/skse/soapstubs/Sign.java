
package com.strongauth.skse.soapstubs;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sign complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sign">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="svcinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEServiceInfoType" minOccurs="0"/>
 *         &lt;element name="keystoretype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="signaturetype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="digesttype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="csainput" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sign", propOrder = {
    "svcinfo",
    "keystoretype",
    "data",
    "signaturetype",
    "digesttype",
    "csainput"
})
public class Sign {

    protected SKCEServiceInfoType svcinfo;
    protected String keystoretype;
    @XmlMimeType("application/octet-stream")
    protected DataHandler data;
    protected String signaturetype;
    protected String digesttype;
    protected String csainput;

    /**
     * Gets the value of the svcinfo property.
     * 
     * @return
     *     possible object is
     *     {@link SKCEServiceInfoType }
     *     
     */
    public SKCEServiceInfoType getSvcinfo() {
        return svcinfo;
    }

    /**
     * Sets the value of the svcinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SKCEServiceInfoType }
     *     
     */
    public void setSvcinfo(SKCEServiceInfoType value) {
        this.svcinfo = value;
    }

    /**
     * Gets the value of the keystoretype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeystoretype() {
        return keystoretype;
    }

    /**
     * Sets the value of the keystoretype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeystoretype(String value) {
        this.keystoretype = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setData(DataHandler value) {
        this.data = value;
    }

    /**
     * Gets the value of the signaturetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignaturetype() {
        return signaturetype;
    }

    /**
     * Sets the value of the signaturetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignaturetype(String value) {
        this.signaturetype = value;
    }

    /**
     * Gets the value of the digesttype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDigesttype() {
        return digesttype;
    }

    /**
     * Sets the value of the digesttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDigesttype(String value) {
        this.digesttype = value;
    }

    /**
     * Gets the value of the csainput property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsainput() {
        return csainput;
    }

    /**
     * Sets the value of the csainput property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsainput(String value) {
        this.csainput = value;
    }

}
