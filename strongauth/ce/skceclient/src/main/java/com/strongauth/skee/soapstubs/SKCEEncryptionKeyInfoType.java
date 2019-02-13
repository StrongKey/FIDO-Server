
package com.strongauth.skee.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SKCEEncryptionKeyInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SKCEEncryptionKeyInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uniquekey" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="algorithm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="keysize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SKCEEncryptionKeyInfoType", namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", propOrder = {
    "uniquekey",
    "algorithm",
    "keysize"
})
public class SKCEEncryptionKeyInfoType {

    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected Boolean uniquekey;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String algorithm;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected Integer keysize;

    /**
     * Gets the value of the uniquekey property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUniquekey() {
        return uniquekey;
    }

    /**
     * Sets the value of the uniquekey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUniquekey(Boolean value) {
        this.uniquekey = value;
    }

    /**
     * Gets the value of the algorithm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the value of the algorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlgorithm(String value) {
        this.algorithm = value;
    }

    /**
     * Gets the value of the keysize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKeysize() {
        return keysize;
    }

    /**
     * Sets the value of the keysize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeysize(Integer value) {
        this.keysize = value;
    }

}
