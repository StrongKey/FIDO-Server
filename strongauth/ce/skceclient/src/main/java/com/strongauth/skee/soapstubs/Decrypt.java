
package com.strongauth.skee.soapstubs;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for decrypt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="decrypt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="svcinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEServiceInfoType" minOccurs="0"/>
 *         &lt;element name="fileinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEInputFileInfoType" minOccurs="0"/>
 *         &lt;element name="filedata" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="authzinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEAuthorizationInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "decrypt", propOrder = {
    "svcinfo",
    "fileinfo",
    "filedata",
    "authzinfo"
})
public class Decrypt {

    protected SKCEServiceInfoType svcinfo;
    protected SKCEInputFileInfoType fileinfo;
    @XmlMimeType("application/octet-stream")
    protected DataHandler filedata;
    protected SKCEAuthorizationInfoType authzinfo;

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
     * Gets the value of the fileinfo property.
     * 
     * @return
     *     possible object is
     *     {@link SKCEInputFileInfoType }
     *     
     */
    public SKCEInputFileInfoType getFileinfo() {
        return fileinfo;
    }

    /**
     * Sets the value of the fileinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SKCEInputFileInfoType }
     *     
     */
    public void setFileinfo(SKCEInputFileInfoType value) {
        this.fileinfo = value;
    }

    /**
     * Gets the value of the filedata property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getFiledata() {
        return filedata;
    }

    /**
     * Sets the value of the filedata property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setFiledata(DataHandler value) {
        this.filedata = value;
    }

    /**
     * Gets the value of the authzinfo property.
     * 
     * @return
     *     possible object is
     *     {@link SKCEAuthorizationInfoType }
     *     
     */
    public SKCEAuthorizationInfoType getAuthzinfo() {
        return authzinfo;
    }

    /**
     * Sets the value of the authzinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SKCEAuthorizationInfoType }
     *     
     */
    public void setAuthzinfo(SKCEAuthorizationInfoType value) {
        this.authzinfo = value;
    }

}
