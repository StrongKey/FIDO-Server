
package com.strongauth.skee.soapstubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SKCEInputFileInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SKCEInputFileInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filename" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="filedigest" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="filedigestalgo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SKCEInputFileInfoType", namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", propOrder = {
    "filename",
    "filedigest",
    "filedigestalgo"
})
public class SKCEInputFileInfoType {

    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema", required = true)
    protected String filename;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String filedigest;
    @XmlElement(namespace = "http://xml.strongauth.com/schema/SKCEXMLSchema")
    protected String filedigestalgo;

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * Gets the value of the filedigest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFiledigest() {
        return filedigest;
    }

    /**
     * Sets the value of the filedigest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFiledigest(String value) {
        this.filedigest = value;
    }

    /**
     * Gets the value of the filedigestalgo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFiledigestalgo() {
        return filedigestalgo;
    }

    /**
     * Sets the value of the filedigestalgo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFiledigestalgo(String value) {
        this.filedigestalgo = value;
    }

}
