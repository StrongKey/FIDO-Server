
package com.strongauth.skse.soapstubs;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for skceReturnObject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="skceReturnObject">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorkey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errormsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="exceptionmsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messagekey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="outDataHandler" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="response" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnval" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="rid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="strId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="txtime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skceReturnObject", propOrder = {
    "errorkey",
    "errormsg",
    "exceptionmsg",
    "hash",
    "messagekey",
    "outDataHandler",
    "response",
    "returnval",
    "rid",
    "sid",
    "strId",
    "txtime"
})
public class SkceReturnObject {

    protected String errorkey;
    protected String errormsg;
    protected String exceptionmsg;
    protected String hash;
    protected String messagekey;
    @XmlMimeType("application/octet-stream")
    protected DataHandler outDataHandler;
    protected String response;
    protected Object returnval;
    protected Long rid;
    protected Long sid;
    protected String strId;
    protected long txtime;

    /**
     * Gets the value of the errorkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorkey() {
        return errorkey;
    }

    /**
     * Sets the value of the errorkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorkey(String value) {
        this.errorkey = value;
    }

    /**
     * Gets the value of the errormsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrormsg() {
        return errormsg;
    }

    /**
     * Sets the value of the errormsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrormsg(String value) {
        this.errormsg = value;
    }

    /**
     * Gets the value of the exceptionmsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExceptionmsg() {
        return exceptionmsg;
    }

    /**
     * Sets the value of the exceptionmsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExceptionmsg(String value) {
        this.exceptionmsg = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
    }

    /**
     * Gets the value of the messagekey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessagekey() {
        return messagekey;
    }

    /**
     * Sets the value of the messagekey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessagekey(String value) {
        this.messagekey = value;
    }

    /**
     * Gets the value of the outDataHandler property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getOutDataHandler() {
        return outDataHandler;
    }

    /**
     * Sets the value of the outDataHandler property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setOutDataHandler(DataHandler value) {
        this.outDataHandler = value;
    }

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponse(String value) {
        this.response = value;
    }

    /**
     * Gets the value of the returnval property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getReturnval() {
        return returnval;
    }

    /**
     * Sets the value of the returnval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setReturnval(Object value) {
        this.returnval = value;
    }

    /**
     * Gets the value of the rid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRid() {
        return rid;
    }

    /**
     * Sets the value of the rid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRid(Long value) {
        this.rid = value;
    }

    /**
     * Gets the value of the sid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSid() {
        return sid;
    }

    /**
     * Sets the value of the sid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSid(Long value) {
        this.sid = value;
    }

    /**
     * Gets the value of the strId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrId() {
        return strId;
    }

    /**
     * Sets the value of the strId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrId(String value) {
        this.strId = value;
    }

    /**
     * Gets the value of the txtime property.
     * 
     */
    public long getTxtime() {
        return txtime;
    }

    /**
     * Sets the value of the txtime property.
     * 
     */
    public void setTxtime(long value) {
        this.txtime = value;
    }

}
