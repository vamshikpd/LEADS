//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.25 at 10:43:25 PM PDT 
//


package plm.ws.mtom.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for internalParoleeDataRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="internalParoleeDataRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.plm/ws/mtom/data}SearchCriteria"/>
 *         &lt;element name="CaseNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Reason" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IncludeAttachment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ResponseFieldsReturned" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IncludeStateWidePAL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "internalParoleeDataRequest", propOrder = {
    "searchCriteria",
    "caseNumber",
    "reason",
    "includeAttachment",
    "username",
    "responseFieldsReturned",
    "includeStateWidePAL"
})
public class InternalParoleeDataRequest {

    @XmlElement(name = "SearchCriteria", required = true)
    protected SearchCriteria searchCriteria;
    @XmlElement(name = "CaseNumber", required = true)
    protected String caseNumber;
    @XmlElement(name = "Reason", required = true)
    protected String reason;
    @XmlElement(name = "IncludeAttachment")
    protected String includeAttachment;
    @XmlElement(name = "Username", required = true)
    protected String username;
    @XmlElement(name = "ResponseFieldsReturned")
    protected String responseFieldsReturned;
    @XmlElement(name = "IncludeStateWidePAL")
    protected String includeStateWidePAL;

    /**
     * Gets the value of the searchCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link SearchCriteria }
     *     
     */
    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * Sets the value of the searchCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchCriteria }
     *     
     */
    public void setSearchCriteria(SearchCriteria value) {
        this.searchCriteria = value;
    }

    /**
     * Gets the value of the caseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * Sets the value of the caseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaseNumber(String value) {
        this.caseNumber = value;
    }

    /**
     * Gets the value of the reason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Gets the value of the includeAttachment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeAttachment() {
        return includeAttachment;
    }

    /**
     * Sets the value of the includeAttachment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeAttachment(String value) {
        this.includeAttachment = value;
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
     * Gets the value of the responseFieldsReturned property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseFieldsReturned() {
        return responseFieldsReturned;
    }

    /**
     * Sets the value of the responseFieldsReturned property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseFieldsReturned(String value) {
        this.responseFieldsReturned = value;
    }

    /**
     * Gets the value of the includeStateWidePAL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeStateWidePAL() {
        return includeStateWidePAL;
    }

    /**
     * Sets the value of the includeStateWidePAL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeStateWidePAL(String value) {
        this.includeStateWidePAL = value;
    }

}
