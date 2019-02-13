
package com.strongauth.skse.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loadKey complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="loadKey">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="svcinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEServiceInfoType" minOccurs="0"/>
 *         &lt;element name="keystoretype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "loadKey", propOrder = {
    "svcinfo",
    "keystoretype",
    "csainput"
})
public class LoadKey {

    protected SKCEServiceInfoType svcinfo;
    protected String keystoretype;
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
