
package com.strongauth.skee.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SKCEAuthorizationInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SKCEAuthorizationInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userdn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="authgroups" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requiredauthorization" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="payload" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SKCEAuthorizationInfoType", namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", propOrder = {
    "userdn",
    "username",
    "authgroups",
    "requiredauthorization",
    "payload"
})
public class SKCEAuthorizationInfoType {

    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String userdn;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String username;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String authgroups;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected Integer requiredauthorization;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String payload;

    /**
     * Gets the value of the userdn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserdn() {
        return userdn;
    }

    /**
     * Sets the value of the userdn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserdn(String value) {
        this.userdn = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the authgroups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthgroups() {
        return authgroups;
    }

    /**
     * Sets the value of the authgroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthgroups(String value) {
        this.authgroups = value;
    }

    /**
     * Gets the value of the requiredauthorization property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRequiredauthorization() {
        return requiredauthorization;
    }

    /**
     * Sets the value of the requiredauthorization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRequiredauthorization(Integer value) {
        this.requiredauthorization = value;
    }

    /**
     * Gets the value of the payload property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Sets the value of the payload property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayload(String value) {
        this.payload = value;
    }

}
