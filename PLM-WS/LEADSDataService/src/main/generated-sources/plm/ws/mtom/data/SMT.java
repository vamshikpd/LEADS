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
 *         &lt;element name="TypeOrLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PictureOfTattoo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TextOfTattoo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "typeOrLocation",
    "pictureOfTattoo",
    "textOfTattoo"
})
@XmlRootElement(name = "SMT")
public class SMT {

    @XmlElement(name = "TypeOrLocation")
    protected String typeOrLocation;
    @XmlElement(name = "PictureOfTattoo")
    protected String pictureOfTattoo;
    @XmlElement(name = "TextOfTattoo")
    protected String textOfTattoo;

    /**
     * Gets the value of the typeOrLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeOrLocation() {
        return typeOrLocation;
    }

    /**
     * Sets the value of the typeOrLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeOrLocation(String value) {
        this.typeOrLocation = value;
    }

    /**
     * Gets the value of the pictureOfTattoo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPictureOfTattoo() {
        return pictureOfTattoo;
    }

    /**
     * Sets the value of the pictureOfTattoo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPictureOfTattoo(String value) {
        this.pictureOfTattoo = value;
    }

    /**
     * Gets the value of the textOfTattoo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextOfTattoo() {
        return textOfTattoo;
    }

    /**
     * Sets the value of the textOfTattoo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextOfTattoo(String value) {
        this.textOfTattoo = value;
    }

}
