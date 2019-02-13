
package com.strongauth.skee.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for decryptFromCloud complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="decryptFromCloud">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="svcinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEServiceInfoType" minOccurs="0"/>
 *         &lt;element name="fileinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEInputFileInfoType" minOccurs="0"/>
 *         &lt;element name="authzinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEAuthorizationInfoType" minOccurs="0"/>
 *         &lt;element name="storageinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEStorageInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "decryptFromCloud", propOrder = {
    "svcinfo",
    "fileinfo",
    "authzinfo",
    "storageinfo"
})
public class DecryptFromCloud {

    protected SKCEServiceInfoType svcinfo;
    protected SKCEInputFileInfoType fileinfo;
    protected SKCEAuthorizationInfoType authzinfo;
    protected SKCEStorageInfoType storageinfo;

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

    /**
     * Gets the value of the storageinfo property.
     * 
     * @return
     *     possible object is
     *     {@link SKCEStorageInfoType }
     *     
     */
    public SKCEStorageInfoType getStorageinfo() {
        return storageinfo;
    }

    /**
     * Sets the value of the storageinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SKCEStorageInfoType }
     *     
     */
    public void setStorageinfo(SKCEStorageInfoType value) {
        this.storageinfo = value;
    }

}
