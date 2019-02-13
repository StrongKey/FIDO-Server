
package com.strongauth.skee.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SKCEStorageInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SKCEStorageInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="storetype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cloudtype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cloudname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accesskey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="secretkey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cloudcontainer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cloudcredentialid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SKCEStorageInfoType", namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", propOrder = {
    "storetype",
    "cloudtype",
    "cloudname",
    "accesskey",
    "secretkey",
    "cloudcontainer",
    "cloudcredentialid"
})
public class SKCEStorageInfoType {

    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", required = true)
    protected String storetype;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String cloudtype;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String cloudname;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String accesskey;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String secretkey;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String cloudcontainer;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String cloudcredentialid;

    /**
     * Gets the value of the storetype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoretype() {
        return storetype;
    }

    /**
     * Sets the value of the storetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoretype(String value) {
        this.storetype = value;
    }

    /**
     * Gets the value of the cloudtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloudtype() {
        return cloudtype;
    }

    /**
     * Sets the value of the cloudtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloudtype(String value) {
        this.cloudtype = value;
    }

    /**
     * Gets the value of the cloudname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloudname() {
        return cloudname;
    }

    /**
     * Sets the value of the cloudname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloudname(String value) {
        this.cloudname = value;
    }

    /**
     * Gets the value of the accesskey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccesskey() {
        return accesskey;
    }

    /**
     * Sets the value of the accesskey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccesskey(String value) {
        this.accesskey = value;
    }

    /**
     * Gets the value of the secretkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecretkey() {
        return secretkey;
    }

    /**
     * Sets the value of the secretkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecretkey(String value) {
        this.secretkey = value;
    }

    /**
     * Gets the value of the cloudcontainer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloudcontainer() {
        return cloudcontainer;
    }

    /**
     * Sets the value of the cloudcontainer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloudcontainer(String value) {
        this.cloudcontainer = value;
    }

    /**
     * Gets the value of the cloudcredentialid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloudcredentialid() {
        return cloudcredentialid;
    }

    /**
     * Sets the value of the cloudcredentialid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloudcredentialid(String value) {
        this.cloudcredentialid = value;
    }

}
