//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.25 at 11:09:54 PM PDT 
//

package plm.ws.mtom.photo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for internalParoleePhotoResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="internalParoleePhotoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TxnStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://www.plm/ws/mtom/photo}DataAttachment" minOccurs="0"/>
 *         &lt;element ref="{http://www.plm/ws/mtom/photo}Error" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "internalParoleePhotoResponse", propOrder = { "txnStatus",
		"dataAttachment", "error" })
public class InternalParoleePhotoResponse {

	@XmlElement(name = "TxnStatus", required = true)
	protected String txnStatus;
	@XmlElement(name = "DataAttachment")
	protected DataAttachment dataAttachment;
	@XmlElement(name = "Error")
	protected Error error;

	/**
	 * Gets the value of the txnStatus property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTxnStatus() {
		return txnStatus;
	}

	/**
	 * Sets the value of the txnStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTxnStatus(String value) {
		this.txnStatus = value;
	}

	/**
	 * Gets the value of the dataAttachment property.
	 * 
	 * @return possible object is {@link DataAttachment }
	 * 
	 */
	public DataAttachment getDataAttachment() {
		return dataAttachment;
	}

	/**
	 * Sets the value of the dataAttachment property.
	 * 
	 * @param value
	 *            allowed object is {@link DataAttachment }
	 * 
	 */
	public void setDataAttachment(DataAttachment value) {
		this.dataAttachment = value;
	}

	/**
	 * Gets the value of the error property.
	 * 
	 * @return possible object is {@link Error }
	 * 
	 */
	public Error getError() {
		return error;
	}

	/**
	 * Sets the value of the error property.
	 * 
	 * @param value
	 *            allowed object is {@link Error }
	 * 
	 */
	public void setError(Error value) {
		this.error = value;
	}

}
