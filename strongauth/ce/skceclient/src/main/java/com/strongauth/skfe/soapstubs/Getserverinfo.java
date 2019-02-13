
package com.strongauth.skfe.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getserverinfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getserverinfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="svcinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEServiceInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getserverinfo", propOrder = {
    "svcinfo"
})
public class Getserverinfo {

    protected SKCEServiceInfoType svcinfo;

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

}
