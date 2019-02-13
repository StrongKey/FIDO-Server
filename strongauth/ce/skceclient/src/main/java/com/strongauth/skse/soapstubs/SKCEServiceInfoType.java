
package com.strongauth.skse.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SKCEServiceInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SKCEServiceInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="did" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="svcusername" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="svcpassword" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="protocol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SKCEServiceInfoType", namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", propOrder = {
    "did",
    "svcusername",
    "svcpassword",
    "protocol"
})
public class SKCEServiceInfoType {

    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected int did;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", required = true)
    protected String svcusername;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", required = true)
    protected String svcpassword;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", required = true)
    protected String protocol;

    /**
     * Gets the value of the did property.
     * 
     */
    public int getDid() {
        return did;
    }

    /**
     * Sets the value of the did property.
     * 
     */
    public void setDid(int value) {
        this.did = value;
    }

    /**
     * Gets the value of the svcusername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvcusername() {
        return svcusername;
    }

    /**
     * Sets the value of the svcusername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvcusername(String value) {
        this.svcusername = value;
    }

    /**
     * Gets the value of the svcpassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvcpassword() {
        return svcpassword;
    }

    /**
     * Sets the value of the svcpassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvcpassword(String value) {
        this.svcpassword = value;
    }

    /**
     * Gets the value of the protocol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the value of the protocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocol(String value) {
        this.protocol = value;
    }

}
