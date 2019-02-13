
package com.strongauth.skce.ldap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getuserinfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getuserinfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="svcinfo" type="{http://xml.strongauth.com/schema/SKCEXMLSchema}SKCEServiceInfoType" minOccurs="0"/>
 *         &lt;element name="basedn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="searchkey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="searchvalue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getuserinfo", propOrder = {
    "svcinfo",
    "basedn",
    "searchkey",
    "searchvalue"
})
public class Getuserinfo {

    protected SKCEServiceInfoType svcinfo;
    protected String basedn;
    protected String searchkey;
    protected String searchvalue;

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
     * Gets the value of the basedn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasedn() {
        return basedn;
    }

    /**
     * Sets the value of the basedn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasedn(String value) {
        this.basedn = value;
    }

    /**
     * Gets the value of the searchkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchkey() {
        return searchkey;
    }

    /**
     * Sets the value of the searchkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchkey(String value) {
        this.searchkey = value;
    }

    /**
     * Gets the value of the searchvalue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchvalue() {
        return searchvalue;
    }

    /**
     * Sets the value of the searchvalue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchvalue(String value) {
        this.searchvalue = value;
    }

}
