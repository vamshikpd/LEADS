//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.01.19 at 04:14:50 PM PST 
//


package plm.ws.mtom.paroleedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hs-11590-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hs-11590-reg-date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pc-290-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pc-290-reg-date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pc-457.1-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pc-457.1-reg-date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pc-3058.6-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pc-3058.6-reg-date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "hs11590Flag",
    "hs11590RegDate",
    "pc290Flag",
    "pc290RegDate",
    "pc4571Flag",
    "pc4571RegDate",
    "pc30586Flag",
    "pc30586RegDate"
})
@XmlRootElement(name = "registration-notice")
public class RegistrationNotice {

    @XmlElement(name = "hs-11590-flag")
    protected String hs11590Flag;
    @XmlElement(name = "hs-11590-reg-date")
    protected String hs11590RegDate;
    @XmlElement(name = "pc-290-flag")
    protected String pc290Flag;
    @XmlElement(name = "pc-290-reg-date")
    protected String pc290RegDate;
    @XmlElement(name = "pc-457.1-flag")
    protected String pc4571Flag;
    @XmlElement(name = "pc-457.1-reg-date")
    protected String pc4571RegDate;
    @XmlElement(name = "pc-3058.6-flag")
    protected String pc30586Flag;
    @XmlElement(name = "pc-3058.6-reg-date")
    protected String pc30586RegDate;

    /**
     * Gets the value of the hs11590Flag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHs11590Flag() {
        return hs11590Flag;
    }

    /**
     * Sets the value of the hs11590Flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHs11590Flag(String value) {
        this.hs11590Flag = value;
    }

    /**
     * Gets the value of the hs11590RegDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHs11590RegDate() {
        return hs11590RegDate;
    }

    /**
     * Sets the value of the hs11590RegDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHs11590RegDate(String value) {
        this.hs11590RegDate = value;
    }

    /**
     * Gets the value of the pc290Flag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPc290Flag() {
        return pc290Flag;
    }

    /**
     * Sets the value of the pc290Flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPc290Flag(String value) {
        this.pc290Flag = value;
    }

    /**
     * Gets the value of the pc290RegDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPc290RegDate() {
        return pc290RegDate;
    }

    /**
     * Sets the value of the pc290RegDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPc290RegDate(String value) {
        this.pc290RegDate = value;
    }

    /**
     * Gets the value of the pc4571Flag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPc4571Flag() {
        return pc4571Flag;
    }

    /**
     * Sets the value of the pc4571Flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPc4571Flag(String value) {
        this.pc4571Flag = value;
    }

    /**
     * Gets the value of the pc4571RegDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPc4571RegDate() {
        return pc4571RegDate;
    }

    /**
     * Sets the value of the pc4571RegDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPc4571RegDate(String value) {
        this.pc4571RegDate = value;
    }

    /**
     * Gets the value of the pc30586Flag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPc30586Flag() {
        return pc30586Flag;
    }

    /**
     * Sets the value of the pc30586Flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPc30586Flag(String value) {
        this.pc30586Flag = value;
    }

    /**
     * Gets the value of the pc30586RegDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPc30586RegDate() {
        return pc30586RegDate;
    }

    /**
     * Sets the value of the pc30586RegDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPc30586RegDate(String value) {
        this.pc30586RegDate = value;
    }

}
