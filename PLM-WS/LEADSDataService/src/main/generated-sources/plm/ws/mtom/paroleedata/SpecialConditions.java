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
 *         &lt;element name="drug-test-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="no-alc-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="psych-outpatient-clinic-flag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "drugTestFlag",
    "noAlcFlag",
    "psychOutpatientClinicFlag"
})
@XmlRootElement(name = "special-conditions")
public class SpecialConditions {

    @XmlElement(name = "drug-test-flag")
    protected String drugTestFlag;
    @XmlElement(name = "no-alc-flag")
    protected String noAlcFlag;
    @XmlElement(name = "psych-outpatient-clinic-flag")
    protected String psychOutpatientClinicFlag;

    /**
     * Gets the value of the drugTestFlag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrugTestFlag() {
        return drugTestFlag;
    }

    /**
     * Sets the value of the drugTestFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrugTestFlag(String value) {
        this.drugTestFlag = value;
    }

    /**
     * Gets the value of the noAlcFlag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoAlcFlag() {
        return noAlcFlag;
    }

    /**
     * Sets the value of the noAlcFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoAlcFlag(String value) {
        this.noAlcFlag = value;
    }

    /**
     * Gets the value of the psychOutpatientClinicFlag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPsychOutpatientClinicFlag() {
        return psychOutpatientClinicFlag;
    }

    /**
     * Sets the value of the psychOutpatientClinicFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPsychOutpatientClinicFlag(String value) {
        this.psychOutpatientClinicFlag = value;
    }

}