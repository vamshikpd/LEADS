//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.25 at 11:09:54 PM PDT 
//

package plm.ws.mtom.photo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the plm.ws.mtom.photo package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _InternalParoleePhotoResponse_QNAME = new QName(
			"http://www.plm/ws/mtom/photo", "InternalParoleePhotoResponse");
	private final static QName _InternalParoleePhotoRequest_QNAME = new QName(
			"http://www.plm/ws/mtom/photo", "InternalParoleePhotoRequest");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: plm.ws.mtom.photo
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Error }
	 * 
	 */
	public Error createError() {
		return new Error();
	}

	/**
	 * Create an instance of {@link SMT }
	 * 
	 */
	public SMT createSMT() {
		return new SMT();
	}

	/**
	 * Create an instance of {@link ParoleeReleaseDate }
	 * 
	 */
	public ParoleeReleaseDate createParoleeReleaseDate() {
		return new ParoleeReleaseDate();
	}

	/**
	 * Create an instance of {@link SearchCriteria }
	 * 
	 */
	public SearchCriteria createSearchCriteria() {
		return new SearchCriteria();
	}

	/**
	 * Create an instance of {@link DataAttachment }
	 * 
	 */
	public DataAttachment createDataAttachment() {
		return new DataAttachment();
	}

	/**
	 * Create an instance of {@link VehicleYear }
	 * 
	 */
	public VehicleYear createVehicleYear() {
		return new VehicleYear();
	}

	/**
	 * Create an instance of {@link InternalParoleePhotoRequest }
	 * 
	 */
	public InternalParoleePhotoRequest createInternalParoleePhotoRequest() {
		return new InternalParoleePhotoRequest();
	}

	/**
	 * Create an instance of {@link InternalParoleePhotoResponse }
	 * 
	 */
	public InternalParoleePhotoResponse createInternalParoleePhotoResponse() {
		return new InternalParoleePhotoResponse();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link InternalParoleePhotoResponse }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.plm/ws/mtom/photo", name = "InternalParoleePhotoResponse")
	public JAXBElement<InternalParoleePhotoResponse> createInternalParoleePhotoResponse(
			InternalParoleePhotoResponse value) {
		return new JAXBElement<InternalParoleePhotoResponse>(
				_InternalParoleePhotoResponse_QNAME,
				InternalParoleePhotoResponse.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}
	 * {@link InternalParoleePhotoRequest }{@code >}
	 * 
	 */
	@XmlElementDecl(namespace = "http://www.plm/ws/mtom/photo", name = "InternalParoleePhotoRequest")
	public JAXBElement<InternalParoleePhotoRequest> createInternalParoleePhotoRequest(
			InternalParoleePhotoRequest value) {
		return new JAXBElement<InternalParoleePhotoRequest>(
				_InternalParoleePhotoRequest_QNAME,
				InternalParoleePhotoRequest.class, null, value);
	}

}